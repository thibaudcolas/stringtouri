package app;

import util.SesameApp;

/**
 * Bundled command line application updating links inside a Sesame server.
 * 
 * @author Thibaud Colas
 * @version 18042012
 */
public class BundledSesameSiri extends CLIApp {

	/**
	 * Initiates the application's information.
	 */
	private static void initApp() {
		terminalname = "bundledsesamesiri.jar";
		displayname = "Bundled(Sesame)Siri";
		version = "1.0.0";
		about = "I'm " + displayname + ", your personal assistant, ask me everything.\n" 
				+ "- Just kidding, in fact I can create links between two Sesame data sets.\n" 
				+ "I use a Sesame server and generate my links between two of its repositories.\n" 
				+ "Please have fun generating RDF links !\n";
	}
	
	/**
	 * Adds customized options for this application.
	 */
	private static void addUsefulOptions() {
		opt.addOption("sesame", true, "server URL - required");
		opt.addOption("s", true, "source repository identifier - required");
		opt.addOption("t", true, "target repository identifier - required");
        opt.addOption("sp", true, "source data set predicate - required");
        opt.addOption("tp", true, "target data set predicate - required");
        opt.addOption("st", true, "source data set objects type");
        opt.addOption("tt", true, "target data set objects type");
        opt.addOption("out", true, "file where the queries will be stored");
        opt.addOption("enc", true, "charset to use when writing queries");
        opt.addOption("update", false, "if you want the queries to be directly executed");
	}
	
	/**
	 * Handles customized options.
	 */
	private static void handleUsefulOptions() {
		if (cl.hasOption("sesame") && cl.hasOption("s") && cl.hasOption("t") && cl.hasOption("sp") && cl.hasOption("tp")) {

			String sesameserver = cl.getOptionValue("sesame");
			String sourcetype = cl.hasOption("st") ? cl.getOptionValue("st") : "";
			String targettype = cl.hasOption("tt") ? cl.getOptionValue("tt") : "";
			
			// We need to give the desired logginglevel to the app, which will dispatch it to its sub classes.
			app = new SesameApp(sesameserver, cl.getOptionValue("s"), cl.getOptionValue("t"), logginglevel);
			app.useTypedLinkage(cl.getOptionValue("sp"), cl.getOptionValue("tp"), sourcetype, targettype);
			
			app.setCharset(cl.hasOption("enc") ? cl.getOptionValue("enc") : "UTF-8");
			app.useSPARQLOutput(false);
			
			if (cl.hasOption("out")) {
				app.storeOutput(cl.getOptionValue("out"));
			}
			else {
				System.out.println("\n\n" + app.getOutput() + "\n\n");
			}
			
			// It is possible to generate the queries without doing any real update.
			if (cl.hasOption("update")) {
				app.updateData();
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
