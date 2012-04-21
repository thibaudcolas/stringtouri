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
	
	private static final void benchEmptySesameDataSetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataSet("http://localhost:8080/openrdf-sesame/", "geo-insee-all");
		LOG.trace("Creating a SesameDataSet from a local server with an empty repository (0) - " + millisLength(start));
	}
	
	private static final void benchSmallSesameDataSetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataSet("http://localhost:8080/openrdf-sesame/", "continents");
		LOG.trace("Creating a SesameDataSet from a local server with a small repository (146) - " + millisLength(start));
	}
	
	private static final void benchMediumSesameDataSetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataSet("http://localhost:8080/openrdf-sesame/", "countries");
		LOG.trace("Creating a SesameDataSet from a local server with a medium repository (12k) - " + millisLength(start));
	}
	
	private static final void benchBigSesameDataSetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataSet("http://localhost:8080/openrdf-sesame/", "geo-insee-all");
		LOG.trace("Creating a SesameDataSet from a local server with a big repository (560k) - " + millisLength(start));
	}
	
	private static final void benchLinkageCreation() {
		
	}
	
	private static final void benchOutputCreation() {
		
	}
	
	private static final void benchAppCreation() {
		
	}
	
	private static final void benchEmptySesameDataImport() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source.addTuples("/Users/Will/Desktop/empty.rdf", "", "http://test.test/empty/");
		LOG.trace("Adding 0 tuples from 1 RDFXML file - " + millisLength(start));
	}
	
	private static final void benchMediumSesameDataImport() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source.addTuples("/Users/Will/Desktop/countries-tolink.rdf", "", "http://telegraphis.net/countries/");
		LOG.trace("Adding 12K tuples from 1 RDFXML file - " + millisLength(start));
	}
	
	private static final void benchSmallSesameDataImport() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source.addTuples("/Users/Will/Desktop/continents.rdf", "", "http://telegraphis.net/continents/");
		LOG.trace("Adding 146 tuples from 1 RDFXML file - " + millisLength(start));
	}
	
	private static final void benchBigSesameDataImport() throws RepositoryException, RDFParseException, IOException {
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

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		LOG.trace("End benchmarking - " + end + "ms");
		
		LOG.trace("Benchmarking length - " + (end - start) + "ms");
	}

}
