package app;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.log4j.Level;

import util.App;

/**
 * Abstract Command Line Interface application.
 * 
 * @author Thibaud Colas
 * @version 15042012
 */
public abstract class CLIApp {
	
	protected static Options opt = new Options();
	protected static BasicParser parser = new BasicParser();
	protected static CommandLine cl;
	protected static HelpFormatter f = new HelpFormatter();
	
	protected static String consolename;
	protected static String displayname;
	protected static String version;
	protected static String about;
	
	protected static Level logginglevel;
	protected static App app;
	
	protected static void addDefaultOptions() {
		opt.addOption("h", "help", false, "print this message");
		opt.addOption("version", false, "print the version number");
		opt.addOption("about", false, "print related information");
		opt.addOption("quiet", false, "be quiet");
		opt.addOption("verbose", false, "be verbose (default)");
		opt.addOption("debug", false, "print debugging information");
	}
	
	protected static void parseArguments(String[] args) {
		try {
			cl = parser.parse(opt, args);
		} catch (ParseException e) {
			printHelp();
		}
	}
	
	protected static void handleDefaultOptions() {
		if (cl.hasOption("h") || cl.hasOption("help")) {
			printHelp();
		}
		else if (cl.hasOption("version")) {
			printVersion();
		}
		else if (cl.hasOption("about")) {
			printAbout();
		}
		
		handleDisplayOptions();
	}
	
	protected static void handleDisplayOptions() {
		if(cl.hasOption("verbose")) {
			logginglevel = Level.INFO;
		}
		else if (cl.hasOption("debug")) {
			logginglevel = Level.DEBUG;
		}
		else if (cl.hasOption("quiet")) {
			logginglevel = Level.FATAL;
		}
		else {
			logginglevel = Level.INFO;
		}
	}
	
	protected static void printHelp() {
		String header = displayname + ", a RDF link generation tool.";
		String footer = "NB: don't forget to check that every URL / filepath is reachable.";
		f.printHelp("java -jar " + consolename, header, opt, footer, true);
		System.exit(0);
	}
	
	protected static void printVersion() {
		System.out.println(displayname + " version " + version);
		System.exit(0);
	}
	
	protected static void printAbout() {
		System.out.println(about);
		System.exit(0);
	}
}
