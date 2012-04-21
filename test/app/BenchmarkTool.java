package app;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.log4j.Logger;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

import util.DataSet;
import util.Linkage;
import util.Output;
import util.SesameDataSet;

/**
 * Benchmarking class.
 * 
 * @author Thibaud Colas
 * @version 20042012
 */
public class BenchmarkTool {

	/**
	 * Used to record time.
	 */
	private static final Logger LOG = Logger.getLogger(BenchmarkTool.class.getName());
	
	private static DataSet source;
	private static DataSet target;
	private static Linkage linkage;
	private static Output output;
	
	private static final String millisLength(long start) {
		return System.currentTimeMillis() - start + "ms";
	}
	
	private static final void benchDataSetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataSet("http://localhost:8080/openrdf-sesame/", "geo-insee-all");
		LOG.trace("Creating a SesameDataSet from a local server with an empty repository - " + millisLength(start));
	}
	
	private static final void benchLinkageCreation() {
		
	}
	
	private static final void benchOutputCreation() {
		
	}
	
	private static final void benchAppCreation() {
		
	}
	
	private static final void benchDataImport() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source.addTuples("/Users/Will/Desktop/insee-geo/", "", "http://rdf.insee.fr/geo/");
		LOG.trace("Adding 590K tuples from 200 RDFXML files - " + millisLength(start));
	}
	
	private static final void benchQueryEvaluation() {
		
	}
	
	private static final void benchDataRetrieval() {
		
	}
	
	private static final void benchLinkGeneration() {
		
	}
	
	private static final void benchOutputWriting() {
		
	}
	
	private static final void benchUpdateExecution() {
		
	}
	
	private static final void benchOutputStore() {
		
	}


	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		LOG.trace("Start benchmarking - " + start + "ms");
		
		try {
			benchDataSetCreation();
			benchDataImport();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		LOG.trace("End benchmarking - " + end + "ms");
		
		LOG.trace("Benchmarking length - " + (end - start) + "ms");
	}

}
