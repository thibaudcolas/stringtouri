package app;

/**
 * Sample code to copy when creating a new command line tool.
 * 
 * @author Thibaud Colas
 * @version 17042012
 */
public class SampleCLIApp extends CLIApp {

	/**
	 * Initiates the application's information.
	 */
	private static void initApp() {
		terminalname = "sample.jar";
		displayname = "Sample";
		version = "1.0.0";
		about = "I'm " + displayname + "\n";
	}
	
	/**
	 * Adds customized options for this application.
	 */
	private static void addUsefulOptions() {
		opt.addOption("s", true, "sample option");
	}
	
	/**
	 * Handles customized options.
	 */
	private static void handleUsefulOptions() {
		if (cl.hasOption("s")) {
			
		}
		else {
			System.out.println("\n\nInternal error - please check the logs.\n");
		}
	}
	
	/**
	 * Groups everything in a readable fashion.
	 * @param args : Application options.
	 */
	public static void main(String[] args) {
		initApp();
		addDefaultOptions();
		addUsefulOptions();
		parseArguments(args);
		handleDefaultOptions();
		handleDisplayOptions();
		handleUsefulOptions();
		app.shutdown();
	}

}
