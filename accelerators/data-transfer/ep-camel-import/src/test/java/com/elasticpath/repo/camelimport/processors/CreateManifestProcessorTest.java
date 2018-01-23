package com.elasticpath.repo.camelimport.processors;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.w3c.dom.Document;

import com.elasticpath.importexport.common.manifest.ManifestBuilder;
import com.elasticpath.importexport.common.types.JobType;

/**
 * Tests for CreateManifestProcessor.
 */
@RunWith(MockitoJUnitRunner.class)
public class CreateManifestProcessorTest {

	@Mock
	private ManifestBuilder mockedManifestBuilder;

	private final CreateManifestProcessor stubbedProcessor = createStubbedCreateManifestProcessor();

	private CreateManifestProcessor createStubbedCreateManifestProcessor() {
		return new CreateManifestProcessor() {
			@Override
			protected File[] listXmlFiles(final File xmlDataDir) {
				return (File[]) Arrays.asList(
							new File("catalogs.xml"),
							new File("amounts.xml"),
							new File("promotions.xml")
						).toArray();
			}

			@Override
			protected ManifestBuilder createNewManifestBuilderInstance() {
				return mockedManifestBuilder;
			}

			@Override
			protected JobType inferJobTypeFromXmlFile(final File xmlFile) {
				Map<String, JobType> jobTypeMap = new HashMap<String, JobType>();
				jobTypeMap.put("catalogs.xml", JobType.CATALOG);
				jobTypeMap.put("amounts.xml", JobType.BASEAMOUNT);
				jobTypeMap.put("promotions.xml", JobType.PROMOTION);

				return jobTypeMap.get(xmlFile.getName());
			}
		};
	}

	@Test
	public final void testBuildManifest() throws Exception {
		stubbedProcessor.buildManifest(new File("unused"));
		verify(mockedManifestBuilder, times(1)).addResource(JobType.CATALOG, "catalogs.xml");
		verify(mockedManifestBuilder, times(1)).addResource(JobType.BASEAMOUNT, "amounts.xml");
		verify(mockedManifestBuilder, times(1)).addResource(JobType.PROMOTION, "promotions.xml");
	}

	@Test
	public final void testGetRootElementName() throws Exception {
		final String expectedRootElementName = "theRootElement";

		Document document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
				new ByteArrayInputStream(String.format("<?xml version=\"1.0\"?><%s><yadda><meow/></yadda></%s>",
						expectedRootElementName, expectedRootElementName).getBytes()));

		assertEquals(expectedRootElementName, stubbedProcessor.getRootElementName(document));
	}
}
