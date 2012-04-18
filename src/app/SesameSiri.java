package app;

import util.SesameApp;

/**
 * Command Line application combined with a Sesame server.
 * 
 * @author Thibaud Colas
 * @version 16042012
 */
public class SesameSiri extends CLIApp {

	/**
	 * Initiates the application's information.
	 */
	private static void initApp() {
		terminalname = "sesamesiri.jar";
		displayname = "(Sesame)Siri";
		version = "1.0.0";
		about = "I'm " + displayname + ", your personal assistant, ask me everything.\n" 
				+ "- Just kidding, in fact I can create links between two RDF data sets.\n" 
				+ "I use a Sesame server and generate my links between two of its repositories.\n" 
				+ "Please have fun generating RDF links !\n";
	}
	
	/**
	 * Adds customized options for this application.
	 */
	private static void addUsefulOptions() {
		opt.addOption("ses", true, "server URL - required");
		opt.addOption("s", true, "source repository identifier - required");
		opt.addOption("t", true, "target repository identifier - required");
        opt.addOption("sp", true, "source data set predicate - required");
        opt.addOption("tp", true, "target data set predicate - required");
        opt.addOption("st", true, "source data set objects type");
        opt.addOption("tt", true, "target data set objects type");
        opt.addOption("out", true, "to supply if you just want to export the SPARQL queries");
        opt.addOption("all", false, "process everything, not just new links (to ");
	}
	
	/**
	 * Handles customized options.
	 */
	private static void handleUsefulOptions() {
		if (cl.hasOption("ses") && cl.hasOption("s") && cl.hasOption("t") && cl.hasOption("sp") && cl.hasOption("tp")) {

			String sesameserver = cl.getOptionValue("ses");
			if (!cl.hasOption("quiet")) System.out.println("\n-- Processing data from " + sesameserver + "\n");
			String sourcetype = cl.hasOption("st") ? cl.getOptionValue("st") : "";
			String targettype = cl.hasOption("tt") ? cl.getOptionValue("tt") : "";
			
			// We need to give the desired logginglevel to the app, which will dispatch it to its sub classes.
			app = new SesameApp(sesameserver, cl.getOptionValue("s"), cl.getOptionValue("t"), logginglevel);
			app.useTypedLinkage(cl.getOptionValue("sp"), cl.getOptionValue("tp"), sourcetype, targettype);
			
			if (!cl.hasOption("quiet")) System.out.println("-- Interlinking on " + sourcetype + " - " + targettype + "\n");
			app.useSPARQLOutput();
			app.generateNewLinks(cl.hasOption("all"));
			
			if (cl.hasOption("out")) {
				app.storeOutput(cl.getOptionValue("out"));
				if (!cl.hasOption("quiet")) System.out.println("-- SPARQL queries stored in " + cl.getOptionValue("out") + "\n");
			}
			else {
				app.updateData();
				if (!cl.hasOption("quiet")) System.out.println("-- Data updated in " + cl.getOptionValue("t") + "\n");
			}
		}
		else {
			if (!cl.hasOption("quiet")) System.out.println("Error - not enough parameters\n");
			printHelp();
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
