package app;

import util.RDFApp;

/**
 * Bundled command line application to process RDFXML data sets and create new RDFXML.
 * 
 * @author Thibaud Colas
 * @version 17042012
 */
public class BundledRDFSiri extends CLIApp {

	/**
	 * Initiates the application's information.
	 */
	private static void initApp() {
		terminalname = "bundledrdfsiri.jar";
		displayname = "Bundled(RDF)Siri";
		version = "1.0.0";
		about = "I'm " + displayname + ", your personal assistant, ask me everything.\n" 
				+ "- Just kidding, in fact I can create links between two RDF data sets.\n" 
				+ "I use RDFXML files and generate my links in RDFXML.\n" 
				+ "Please have fun generating RDF links !\n";
	}
	
	/**
	 * Adds customized options for this application.
	 */
	private static void addUsefulOptions() {
		opt.addOption("s", true, "source file/folder path - required");
		opt.addOption("t", true, "target file/folder path - required");
        opt.addOption("sp", true, "source data set predicate - required");
        opt.addOption("tp", true, "target data set predicate - required");
        opt.addOption("st", true, "source data set objects type");
        opt.addOption("tt", true, "target data set objects type");
        opt.addOption("enc", true, "charset to use when writing data");
        opt.addOption("out", true, "file to store the result at");
	}
	
	/**
	 * Handles customized options.
	 */
	private static void handleUsefulOptions() {
		if (cl.hasOption("s") && cl.hasOption("t") && cl.hasOption("sp") && cl.hasOption("tp")) {

			String sourcetype = cl.hasOption("st") ? cl.getOptionValue("st") : "";
			String targettype = cl.hasOption("tt") ? cl.getOptionValue("tt") : "";
			
			// We need to give the desired logginglevel to the app, which will dispatch it to its sub classes.
			app = new RDFApp(cl.getOptionValue("s"), cl.getOptionValue("t"), "", "", logginglevel);
			
			app.useTypedLinkage(cl.getOptionValue("sp"), cl.getOptionValue("tp"), sourcetype, targettype);
			// Always set charset before generating links.
			app.setCharset(cl.hasOption("enc") ? cl.getOptionValue("enc") : "UTF-8");
			app.useRDFOutput(cl.getOptionValue("t"));
			
			if (cl.hasOption("out")) {
				app.storeOutput(cl.getOptionValue("out"));
			}
			else {
				System.out.println("\n\n" + app.getOutput() + "\n\n");
			}
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
