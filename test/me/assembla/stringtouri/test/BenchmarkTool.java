package me.assembla.stringtouri.test;

import java.io.IOException;

import me.assembla.stringtouri.dataset.Dataset;
import me.assembla.stringtouri.dataset.SesameDataset;
import me.assembla.stringtouri.linkage.Linkage;
import me.assembla.stringtouri.linkage.TypedLinkage;
import me.assembla.stringtouri.output.Output;
import me.assembla.stringtouri.output.SPARQLOutput;

import org.apache.log4j.Logger;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

/**
 * Benchmarking class.
 * 
 * @author Thibaud Colas
 * @version 2013-05-27
 */
public class BenchmarkTool {

	/**
	 * Used to record time.
	 */
	private static final Logger LOG = Logger.getLogger(BenchmarkTool.class.getName());
	
	private static Dataset source;
	private static Dataset target;
	private static Linkage linkage;
	private static Output output;
	
	private static final String millisLength(long start) {
		return System.currentTimeMillis() - start + "ms";
	}
	
	private static final void benchEmptySesameDatasetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/empty", "empty");
		LOG.trace("Creating a SesameDataset from a local server with an empty repository (0) - " + millisLength(start));
	}
	
	private static final void benchSmallSesameDatasetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/continents", "file://continents.rdf");
		LOG.trace("Creating a SesameDataset from a local server with a small repository (146) - " + millisLength(start));
	}
	
	private static final void benchMediumSesameDatasetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/countries", "countries");
		LOG.trace("Creating a SesameDataset from a local server with a medium repository (12k) - " + millisLength(start));
	}
	
	private static final void benchBigSesameDatasetCreation() throws RepositoryException, RDFParseException, IOException {
		long start = System.currentTimeMillis();
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/geo-insee-all", "geo-insee-all");
		LOG.trace("Creating a SesameDataset from a local server with a big repository (560k) - " + millisLength(start));
	}
	
	private static final void benchSmallLinkageCreation() throws RepositoryException {
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/continents", "file://continents.rdf");
		target = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/continents", "file://continents.rdf");
		long start = System.currentTimeMillis();
		linkage = new TypedLinkage(source, target, "gn:name", "gn:name", "geographis:Continent", "geographis:Continent");
		LOG.trace("Creating a TypedLinkage from a local server with a small repository (146) - " + millisLength(start));
	}
	
	private static final void benchMediumLinkageCreation() throws RepositoryException {
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/countries", "countries");
		target = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/countries", "countries");
		long start = System.currentTimeMillis();
		linkage = new TypedLinkage(source, target, "geographis:isoShortName", "geographis:isoShortName", "gn:Country", "gn:Country");
		LOG.trace("Creating a TypedLinkage from a local server with a medium repository (12k) - " + millisLength(start));
	}
	
	private static final void benchBigLinkageCreation() throws RepositoryException {
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/geo-insee-all", "geo-insee-all");
		target = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/geo-insee-all", "geo-insee-all");
		long start = System.currentTimeMillis();
		linkage = new TypedLinkage(source, target, "geo:nom", "geo:nom", "", "");
		LOG.trace("Creating a TypedLinkage from a local server with a big repository (560k) - " + millisLength(start));
	}
	
	private static final void benchSmallSPARQLOutputCreation() throws RepositoryException {
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/continents", "file://continents.rdf");
		target = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/continents", "file://continents.rdf");
		linkage = new TypedLinkage(source, target, "gn:name", "gn:name", "geographis:Continent", "geographis:Continent");
		long start = System.currentTimeMillis();
		output = new SPARQLOutput(target, "continents", "gn:name", "gn:name");
		LOG.trace("Creating a SPARQLOutput from a small linkage (7) - " + millisLength(start));
	}
	
	private static final void benchMediumSPARQLOutputCreation() throws RepositoryException {
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/countries", "countries");
		target = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/countries", "countries");
		linkage = new TypedLinkage(source, target, "geographis:isoShortName", "geographis:isoShortName", "gn:Country", "gn:Country");
		long start = System.currentTimeMillis();
		output = new SPARQLOutput(target, "countries", "geographis:isoShortName", "geographis:isoShortName");
		LOG.trace("Creating a SPARQLOutput from a medium linkage (500) - " + millisLength(start));
	}
	
	private static final void benchBigSPARQLOutputCreation() throws RepositoryException {
		source = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/geo-insee-all", "geo-insee-all");
		target = new SesameDataset("http://localhost:8080/openrdf-sesame/repositories/geo-insee-all", "geo-insee-all");
		linkage = new TypedLinkage(source, target, "geo:nom", "geo:nom", "", "");
		long start = System.currentTimeMillis();
		output = new SPARQLOutput(target, "geo-insee-all", "geo:nom", "geo:nom");
		LOG.trace("Creating a SPARQLOutput from a big linkage (40k) - " + millisLength(start));
	}
	
	private static final void benchEmptySesameQueryEvaluation() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		long start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s ?p ?o}");
		LOG.trace("Querying 0 tuples from within 0 tuples - " + millisLength(start));
	}
	
	private static final void benchSmallSesameQueryEvaluation() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		long start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s ?p \"notinsidethedata\"}");
		LOG.trace("Querying 0 tuples from within 146 tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s gn:name \"Africa\"}");
		LOG.trace("Querying 1 tuple from within 146 tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s a geographis:Continent}");
		LOG.trace("Querying 7 tuples from within 146 tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s ?p ?o}");
		LOG.trace("Querying 146 tuples from within 146 tuples - " + millisLength(start));
	}
	
	private static final void benchMediumSesameQueryEvaluation() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		long start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s ?p \"notinsidethedata\"}");
		LOG.trace("Querying 0 tuples from within 12K tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s geographis:onContinent \"Africa\"}");
		LOG.trace("Querying 50 tuples from within 12K tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s geographis:isoShortName ?o }");
		LOG.trace("Querying 500 tuples from within 12K tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s ?p ?o}");
		LOG.trace("Querying 12k tuples from within 12K tuples - " + millisLength(start));
	}
	
	private static final void benchBigSesameQueryEvaluation() throws RepositoryException, MalformedQueryException, QueryEvaluationException {
		long start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s ?p \"notinsidethedata\"}");
		LOG.trace("Querying 0 tuples from within 590K tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s a geo:Departement}");
		LOG.trace("Querying 100 tuples from within 590K tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s a geo:Commune}");
		LOG.trace("Querying 36k tuples from within 590K tuples - " + millisLength(start));
		start = System.currentTimeMillis();
		source.selectQuery("SELECT ?s WHERE {?s ?p ?o}");
		LOG.trace("Querying 590k tuples from within 590K tuples - " + millisLength(start));
	}
	
	private static final void benchSmallDataRetrieval() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		long start = System.currentTimeMillis();
		linkage.getSourceData();
		linkage.getTargetData();
		LOG.trace("Retrieving data from both data sources of a small linkage (7 - 7) - " + millisLength(start));
	}
	
	private static final void benchMediumDataRetrieval() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		long start = System.currentTimeMillis();
		linkage.getSourceData();
		linkage.getTargetData();
		LOG.trace("Retrieving data from both data sources of a medium linkage (500 - 500) - " + millisLength(start));
	}
	
	private static final void benchBigDataRetrieval() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		long start = System.currentTimeMillis();
		linkage.getSourceData();
		linkage.getTargetData();
		LOG.trace("Retrieving data from both data sources of a big linkage (40k - 40k) - " + millisLength(start));
	}
	
	private static final void benchSmallLinkGeneration() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		long start = System.currentTimeMillis();
		linkage.generateLinks();
		LOG.trace("Matching data from both data sources of a small linkage (7 - 7) - " + millisLength(start));
	}
	
	private static final void benchMediumLinkGeneration() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		long start = System.currentTimeMillis();
		linkage.generateLinks();
		LOG.trace("Matching data from both data sources of a medium linkage (500 - 500) - " + millisLength(start));
	}
	
	private static final void benchBigLinkGeneration() throws QueryEvaluationException, RepositoryException, MalformedQueryException {
		long start = System.currentTimeMillis();
		linkage.generateLinks();
		LOG.trace("Matching data from both data sources of a medium linkage (40k - 40k) - " + millisLength(start));
	}
	
	public static void main(String[] args) {
		long start = System.currentTimeMillis();
		LOG.trace("Start benchmarking - " + start + "ms");
		
		try {
			benchEmptySesameDatasetCreation();
			benchEmptySesameQueryEvaluation();
			benchSmallSesameDatasetCreation();
			benchSmallSesameQueryEvaluation();
			benchSmallLinkageCreation();
			benchSmallDataRetrieval();
			benchSmallLinkGeneration();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		long end = System.currentTimeMillis();
		LOG.trace("End benchmarking - " + end + "ms");
		
		LOG.trace("Benchmarking length - " + (end - start) + "ms");
	}

}
