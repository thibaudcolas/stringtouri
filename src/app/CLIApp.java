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
	
	/**
	 * Parameters to control the application.
	 */
	protected static Options opt = new Options();
	/**
	 * Parser which will check submitted values against parameters.
	 */
	protected static BasicParser parser = new BasicParser();
	/**
	 * Receiver of the parameters / values.
	 */
	protected static CommandLine cl;
	/**
	 * Help designer component.
	 */
	protected static HelpFormatter f = new HelpFormatter();
	
	/**
	 * Name of the terminal tool.
	 */
	protected static String terminalname;
	/**
	 * Name for display purposes.
	 */
	protected static String displayname;
	/**
	 * Version of the software.
	 */
	protected static String version;
	/**
	 * Information about the software.
	 */
	protected static String about;
	/**
	 * Logging level to be set at runtime.
	 */
	protected static Level logginglevel;
	/**
	 * Interfaced application.
	 */
	protected static App app;
	 
	/**
	 * Adds standard CLI options to the application.
	 */
	protected static void addDefaultOptions() {
		opt.addOption("h", "help", false, "print this message");
		opt.addOption("version", false, "print the version number");
		opt.addOption("about", false, "print related information");
		opt.addOption("quiet", false, "be quiet");
		opt.addOption("verbose", false, "be verbose - default");
		opt.addOption("debug", false, "print debugging information");
	}
	
	/**
	 * Parses the arguments using the options.
	 * @param args : Application execution arguments.
	 */
	protected static void parseArguments(String[] args) {
		try {
			cl = parser.parse(opt, args);
		} catch (ParseException e) {
			printHelp();
		}
	}
	
	/**
	 * Handle rules for the default options.
	 */
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
	}
	
	/**
	 * Handle rules for the display options (number of prints to be done).
	 */
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
	
	/**
	 * Prints our application's help.
	 */
	protected static void printHelp() {
		String header = displayname + ", a RDF link generation tool.";
		String footer = "NB: don't forget to check that every URL / filepath is reachable.";
		f.printHelp("java -jar " + terminalname, header, opt, footer, true);
		System.exit(0);
	}
	
	/**
	 * Prints our application's version number.
	 */
	protected static void printVersion() {
		System.out.println(displayname + " version " + version);
		System.exit(0);
	}
	
	/**
	 * Prints our application's information.
	 */
	protected static void printAbout() {
		System.out.println(about);
		System.exit(0);
	}
}
