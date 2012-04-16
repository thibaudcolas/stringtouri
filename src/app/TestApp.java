package app;

import util.RDFApp;

public class TestApp extends CLIApp {

	private static void initApp() {
		consolename = "testapp.jar";
		displayname = "TestApp";
		version = "1.0.0";
		about = "A cool app";
	}
	

	private static void addUsefulOptions() {
		opt.addOption("s", true, "source file/folder path - required");
		opt.addOption("t", true, "target file/folder path - required");
        opt.addOption("sp", true, "source data set predicate - required");
        opt.addOption("tp", true, "target data set predicate - required");
        opt.addOption("st", true, "source data set objects type - optional");
        opt.addOption("tt", true, "target data set objects type - optional");
	}
	
	private static void handleUsefulOptions() {
		if (cl.hasOption("s") && cl.hasOption("t") && cl.hasOption("sp") && cl.hasOption("tp")) {

			if (!cl.hasOption("quiet")) System.out.println("Hello sir, I'm " + displayname + ", let's get started.");
			String sourcetype = cl.hasOption("st") ? cl.getOptionValue("st") : "";
			String targettype = cl.hasOption("tt") ? cl.getOptionValue("tt") : "";
			app = new RDFApp(cl.getOptionValue("s"), cl.getOptionValue("t"), "", "", logginglevel);
			app.useTypedLinkage(cl.getOptionValue("sp"), cl.getOptionValue("tp"), sourcetype, targettype);
			if (!cl.hasOption("quiet")) System.out.println("Creating new links using my best algorithms, sir.");
			app.useRDFOutput();
			app.generateNewLinks(true);
			System.out.println(app.getOutput());
			if (!cl.hasOption("quiet")) System.out.println("Oh boy ! Your orders have been executed, sir.");
		}
		else {
			if (!cl.hasOption("quiet")) System.out.println("Oh dear, looks like you forgot something.");
			printHelp();
		}
	}
	
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
