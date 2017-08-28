package suggestiongenerator.services;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class PdfFormFillService {

	public PdfFormFillService () {
		
	}
	
	public int formFill(String pathToDocxSched, String destinationDirectory) {
		
		int exitValue = 1;
		
		// Windows machine
		boolean isWindows = System.getProperty("os.name")
				  .toLowerCase().startsWith("windows");
		
		String cmd = "cmd.exe";  // only needed on windows
		String reminderSlipPopulator = "ReminderSlipPopulator.exe";
		String option = "--populate";
		String[] cmdarray = {cmd, "/c", reminderSlipPopulator, option, pathToDocxSched, destinationDirectory};
		
		ProcessBuilder processBuilder = new ProcessBuilder();
		processBuilder.command(cmdarray);
		processBuilder.inheritIO();
		String classpath = System.getProperty("java.class.path");
		// doesn't work because spring is packaging a .jar file
		// the exe would need to be unzipped
		processBuilder.directory(new File(classpath+"\\BOOT-INF\\classes\\"));
		
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
