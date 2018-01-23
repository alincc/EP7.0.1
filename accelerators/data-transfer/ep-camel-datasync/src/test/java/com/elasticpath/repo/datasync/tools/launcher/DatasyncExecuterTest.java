package com.elasticpath.repo.datasync.tools.launcher;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Unit tests for DatasyncExecuter.
 */
@RunWith(MockitoJUnitRunner.class)
public class DatasyncExecuterTest {

	private static final String CHANGE_SET_GUID = "123456";
	
	private DatasyncExecuter datasyncExecuter;
	
	@Mock
	private Process process;
	
	/**
	 * Set up test.
	 * @throws InterruptedException 
	 */
	@Before
	public final void setUp() throws InterruptedException {
		
		datasyncExecuter = new DatasyncExecuter() {
			@Override
			protected Process getProcess(final ProcessBuilder builder) {				
				return process;
			}
		};
		
		InputStream inputStream = new ByteArrayInputStream("Summary: test input stream".getBytes());
		when(process.getInputStream()).thenReturn(inputStream);
		
		InputStream errorStream = new ByteArrayInputStream("test error stream".getBytes());
		when(process.getErrorStream()).thenReturn(errorStream);
		
		
		
		File workingDir = new File("/");
		File script = new File(workingDir + "test.bat");
		datasyncExecuter.setScriptPath(script.getAbsolutePath());
		datasyncExecuter.setWorkingDir(workingDir.getAbsolutePath());
	}

	/**
	 * Test executer happy path.
	 * @throws Exception exception
	 */
	@Test
	public void testHappyPath() throws Exception {
		DatasyncSummary summary = datasyncExecuter.execute(CHANGE_SET_GUID);
		
		assertNotNull(summary);
//		System.out.println(summary.getSummaryMessage());
//		System.out.println(summary.getErrorMessage());
	}
}
