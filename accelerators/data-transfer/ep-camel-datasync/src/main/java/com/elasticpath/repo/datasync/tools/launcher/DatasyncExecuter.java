package com.elasticpath.repo.datasync.tools.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.apache.log4j.Logger;

/**
 * The executer starts the sync tool process and provides the result summary.
 */
public class DatasyncExecuter {
	private static final Logger LOG = Logger.getLogger(DatasyncExecuter.class);
	
	/**
	 * A path of a script to run DST tool.
	 */
	private String scriptPath;

	/**
	 * Working dir of a script.
	 */
	private String workingDir;

	/**
	 * The Summary of the DST process.
	 */
	private DatasyncSummary datasyncSummary;
	

	/**
	 * Executes the data sync tool client to process a specific <object>ChangeSet</object>.
	 * 
	 * @param changeSetGuid the GUID of the changeset to process.
	 * @return the summary of the DST process, includes success\error messages.
	 * @throws Exception exception
	 */
	public DatasyncSummary execute(final String changeSetGuid) throws Exception {				
		// Get the GUID of the ChangeSet to be processed 
		LOG.info("DataSync publish start: GUID=" + changeSetGuid);

		DatasyncSummary summary = executeScript(changeSetGuid);
		
		LOG.info("DataSync publish end: GUID=" + changeSetGuid);
		return summary;
	}

	/**
	 * Executes DST script.
	 * 
	 * @param String Change Set Guid
	 * @return the summary of the DST process
	 * @throws Exception exception
	 */
	private DatasyncSummary executeScript(final String changeSetGuid) throws Exception {
		
		createDatasyncSummary(changeSetGuid);
		
		// Prepare the command to execute
		String[] commandArray = null;
		
		if (!scriptPath.startsWith("cmd") && scriptPath.endsWith(".bat")) {
			commandArray = new String[] { "cmd", "/c", scriptPath, "-f", "-p", changeSetGuid };
		} else {
			if (workingDir == null) {
				File parentDir = new File(scriptPath).getParentFile();
				if (parentDir == null) {
					workingDir = new File(".").getAbsolutePath();
				} else {
					workingDir = parentDir.getAbsolutePath();
				}
			}
			commandArray = new String[] { scriptPath, "-f", "-p", changeSetGuid };
		}
		LOG.debug("Executing commandArray= " + Arrays.toString(commandArray));
		try {
			// Run the command
			int exitCode = executeRuntime(commandArray, workingDir, changeSetGuid);
			
			// Log the execution results
			if (exitCode == 0 && !getDatasyncSummary().getErrorMessage().isEmpty()) {
				LOG.error("Error messages detected in the stream.");
			}
			LOG.debug("Command executed, exitCode=" + exitCode);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			getDatasyncSummary().addError(e.getMessage());
		}
		
		return getDatasyncSummary();
	}

	private int executeRuntime(final String[] commandArray, final String workingDir, final String threadName)
			throws IOException, InterruptedException {
		File workingDirLocation = null;
		if (workingDir != null) {
			workingDirLocation = new File(workingDir);
		}
		
		ProcessBuilder builder = new ProcessBuilder(commandArray);
		builder.directory(workingDirLocation);

		Process process = getProcess(builder);
		
		LOG.debug("Creating reader for OUTPUT stream.");
		createReader(process.getInputStream(), threadName, false, getDatasyncSummary());		
		LOG.debug("Creating reader for ERROR stream.");
		createReader(process.getErrorStream(), threadName + "Error", true, getDatasyncSummary());
		return process.waitFor();
	}


	private void createReader(final InputStream inStream, final String threadName, final boolean isErrorStream,
			final DatasyncSummary datasyncSummary) {
		// Start a separate thread for each log reader
		new Thread(new Runnable() {
			public void run() {
				LOG.debug("Starting reader thread, isErrorStream=" + isErrorStream);
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new InputStreamReader(inStream));
					String line = null;
					boolean summary = false;
					while ((line = reader.readLine()) != null) {
						if (!summary && line.contains("Summary")) {
							summary = true;
						}
						if (isErrorStream) {
							LOG.error("ERROR: " + line);
							datasyncSummary.addError(line);
						} else {
							LOG.debug("OUTPUT: " + line);
							if (summary) {
								datasyncSummary.addSummary(line);
							}
						}
					}
				} catch (IOException e) {
					LOG.error("Error reading process stream. IOException: ", e);
				} finally {
					LOG.debug("Stopping reader thread, isErrorStream=" + isErrorStream);
					closeReader(reader);
				}
			}
		}, threadName).start();
	}

	private void closeReader(final BufferedReader reader) {
		if (reader != null) {
			try {
				reader.close();
			} catch (IOException e) {
				LOG.error("Error closing process stream. IOException: ", e);
			}
		}
	}

	protected String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(final String scriptPath) {
		this.scriptPath = scriptPath;
	}

	protected String getWorkingDir() {
		return workingDir;
	}

	public void setWorkingDir(final String workingDir) {
		this.workingDir = workingDir;
	}

	/**
	 * Creates a new DatasyncSummary.
	 * 
	 * @param changesetGuid the GUID
	 */
	protected void createDatasyncSummary(final String changesetGuid) {
		this.datasyncSummary = new DatasyncSummary(changesetGuid);
	}
	
	/**
	 * Gets a DatasyncSummary.
	 * 
	 * @return datasyncSummary
	 */
	public DatasyncSummary getDatasyncSummary() {
		return datasyncSummary;
	}
	
	/**
	 * Gets a process to execute the DST. Used in tests. 
	 *
	 * @param builder the builder
	 * @return a process
	 * @throws IOException IOException
	 * @throws InterruptedException InterruptedException
	 */
	protected Process getProcess(final ProcessBuilder builder) throws IOException, InterruptedException {
		return builder.start();
	}
}
