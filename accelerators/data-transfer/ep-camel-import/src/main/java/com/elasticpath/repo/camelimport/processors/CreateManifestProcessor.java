package com.elasticpath.repo.camelimport.processors;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathExpressionException;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;

import com.elasticpath.commons.beanframework.BeanFactory;
import com.elasticpath.importexport.common.ImportExportContextIdNames;
import com.elasticpath.importexport.common.manifest.Manifest;
import com.elasticpath.importexport.common.manifest.ManifestBuilder;
import com.elasticpath.importexport.common.marshalling.XMLMarshaller;
import com.elasticpath.importexport.common.types.JobType;
import com.elasticpath.repo.camelimport.CamelImportConstants;

/**
 * Builds a manifest file from the catalog feed data.
 */
@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
public class CreateManifestProcessor implements Processor {

	private static final Logger LOG = Logger.getLogger(CreateManifestProcessor.class);

	private static final String MANIFEST_FILE_NAME = "manifest.xml";

	private BeanFactory beanFactory;

	private DocumentBuilderFactory documentBuilderFactory;

	@Override
	public void process(final Exchange exchange) throws Exception {
		// EXTERNALIZE
		final String catalogFeedDataDirPath = exchange.getProperty(CamelImportConstants.IMPORT_DATA_DIR_PATH, String.class);
		final File manifestFile = new File(catalogFeedDataDirPath, MANIFEST_FILE_NAME);

		if (manifestFile.exists() && !manifestFile.delete()) {
			throw new RuntimeException(String.format("Unable to remove existing manifest file '%s'", manifestFile.getAbsolutePath()));
		}

		Manifest manifest = buildManifest(new File(catalogFeedDataDirPath));

		writeManifestToFile(manifest, manifestFile);
	}

	/**
	 * Builds the manifest object.
	 * 
	 * @param xmlDataDir the xml data dir
	 * @return the manifest
	 */
	protected Manifest buildManifest(final File xmlDataDir) {
		File[] catalogFeedDataXmlFiles = listXmlFiles(xmlDataDir);

		ManifestBuilder manifestBuilder = createNewManifestBuilderInstance();

		for (File xmlFile : catalogFeedDataXmlFiles) {
			JobType jobType = inferJobTypeFromXmlFile(xmlFile);
			if (jobType == null) {
				throw new RuntimeException(String.format("Unable to determine job type from file '%s'", xmlFile.getPath()));
			}
			manifestBuilder.addResource(jobType, xmlFile.getName());
		}

		return manifestBuilder.build();
	}

	/**
	 * List xml files in the given directory.
	 * 
	 * @param xmlDataDir the xml data dir
	 * @return the xml files
	 */
	protected File[] listXmlFiles(final File xmlDataDir) {
		return xmlDataDir.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(final File dir, final String name) {
				return name.endsWith(".xml");
			}
		});
	}

	/**
	 * Infer job type from an xml file.
	 * 
	 * @param xmlFile the xml file
	 * @return the job type
	 */
	protected JobType inferJobTypeFromXmlFile(final File xmlFile) {
		try {
			Document document = getDocumentBuilderFactory().newDocumentBuilder().parse(xmlFile);
			String rootTag = getRootElementName(document);

			return JobType.getJobTypeByTag(rootTag);
		} catch (Exception e) {
			LOG.error("Error parsing XML data file: " + xmlFile.getAbsolutePath(), e);
			return null;
		}
	}

	/**
	 * Write manifest object to file.
	 * 
	 * @param manifest the manifest
	 * @param file the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected void writeManifestToFile(final Manifest manifest, final File file) throws IOException {
		OutputStream outputStream = null;
		try {
			outputStream = new FileOutputStream(file);
			XMLMarshaller xmlMarshaller = new XMLMarshaller(false, Manifest.class);
			xmlMarshaller.marshal(manifest, outputStream);
		} finally {
			if (outputStream != null) {
				outputStream.close();
			}
		}
	}

	/**
	 * Creates a new manifest builder instance.
	 * 
	 * @return the manifest builder
	 */
	protected ManifestBuilder createNewManifestBuilderInstance() {
		return beanFactory.getBean(ImportExportContextIdNames.MANIFEST_BUILDER);
	}

	/**
	 * Gets the root element name.
	 * 
	 * @param document the document
	 * @return the root element name
	 * @throws XPathExpressionException the x path expression exception
	 */
	protected String getRootElementName(final Document document) throws XPathExpressionException {
		return document.getFirstChild().getNodeName();
	}

	/**
	 * Gets the document builder factory.
	 * 
	 * @return the document builder factory
	 */
	protected DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory == null) {
			documentBuilderFactory = DocumentBuilderFactory.newInstance();
		}
		return documentBuilderFactory;
	}

	public void setBeanFactory(final BeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

}
