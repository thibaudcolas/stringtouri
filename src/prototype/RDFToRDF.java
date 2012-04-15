package prototype;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

/**
 * Test class with user interface.
 * 
 * @author Thibaud Colas
 * @version 14042012
 * @see App
 */
public class RDFToRDF {

	public static void main(String[] args) {
        Options opt = new Options();

        opt.addOption("h", false, "Print help for this application");
        opt.addOption("s", true, "Absolute path to the source RDFXML file(s)");
        opt.addOption("t", true, "Absolute path to the target RDFXML file(s)");
        opt.addOption("sp", true, "Predicate to look for inside source data set");
        opt.addOption("tp", true, "Predicate to look for inside target data set");	
        opt.addOption("st", false, "Data type inside the source data set");
        opt.addOption("tt", false, "Data type inside the target data set");
        
        BasicParser parser = new BasicParser();
        CommandLine cl;
        HelpFormatter f = new HelpFormatter();
        try {
				cl = parser.parse(opt, args);
	        
	        if (cl.hasOption('h')) {
	            f.printHelp("CLI StringToURI", opt);
	        }
	        else {
	        	App rdfapp = new RDFApp(cl.getOptionValue("s"), cl.getOptionValue("t"), "", "");
	        	rdfapp.useTypedLinkage(cl.getOptionValue("sp"), cl.getOptionValue("tp"), (cl.hasOption("st") ? cl.getOptionValue("st") : ""), (cl.hasOption("tt") ? cl.getOptionValue("tt") : ""));
	        	rdfapp.useRDFOutput();
	        	rdfapp.generateNewLinks(true);
	    		System.out.println(rdfapp.getOutput());
	    		rdfapp.shutdown();
	        }
        
        } catch (ParseException e) {
            f.printHelp("CLI StringToURI", opt);
            //TODO better
		}
	}
}
