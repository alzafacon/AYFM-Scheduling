package io.fidelcoria.ayfmPlanner.service;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class PdfFormFillService {

	public PdfFormFillService () {
		
	}
	
	public int formFill(String pathToDocxSched, String destinationDirectory) {
		
		int exitValue = 1;
		
		File exeDirectory = new File("C:\\Program Files\\AYFM\\PdfReminderSlipPopulator");
		
		// Windows machine
		boolean isWindows = System.getProperty("os.name")
				  .toLowerCase().startsWith("windows");
		
		String cmd = "cmd.exe";  // only needed on windows
		String reminderSlipPopulator = "ReminderSlipPopulator.exe";
		String option = "--populate";
		String[] cmdarray;
		if (isWindows) {
			cmdarray = new String[] {cmd, "/c", reminderSlipPopulator, option, pathToDocxSched, destinationDirectory};
		} else {
			cmdarray = new String[] {reminderSlipPopulator, option, pathToDocxSched, destinationDirectory};
		}
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(cmdarray);
		processBuilder.inheritIO();
		
		processBuilder.directory(exeDirectory);
		
		try {
			Process cSharpConsoleApp = processBuilder.start();

			exitValue = cSharpConsoleApp.waitFor();
		}
		catch (IOException | InterruptedException ioEx) {
			System.out.println(ioEx);
		}
		
		return exitValue;
	}
}
