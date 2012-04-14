package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFParseException;

import prototype.RDFDataSet;

/**
 * JUnit test cases on JeuRDF.
 * @author Thibaud Colas.
 * @version 07042012
 * @see RDFDataSet
 */
public class TestJeuRDF {
	
	/**
	 * Data set to use during tests.
	 */
	private RDFDataSet j;
	/**
	 * Default file to be imported.
	 */
	private static final String deffile = "./src/test/rdf/countries.rdf";
	/**
	 * Default folder to be imported.
	 */
	private static final String deffolder = "./src/test/rdf/";
	/**
	 * Default prefix while filtering files.
	 */
	private static final String defpre = "continents";
	/**
	 * Default base URI for the new data.
	 */
	private static final String defuri = "defuri";
	/**
	 * Prefix for the rdf namespace.
	 */
	private static final String rdfpre = "rdf";
	/**
	 * Namespace URI for the RDF namespace.
	 */
	private static final String rdfuri = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	/**
	 * Simple test query retrieving everything.
	 */
	private static final String defreq = "SELECT ?s WHERE {?s ?p ?o}";
	
	@Before
	public void setUp() throws Exception {
		j = new RDFDataSet(deffolder, defpre, defuri);
	}
	
	@After
	public void tearDown() throws Exception {
		j.shutdown();
	}

	@Test (expected = NoSuchElementException.class)
	public void testConstructor() {
		assertEquals(j.getNom(), deffolder);
		assertEquals(j.getBaseURI(), defuri);
		j.getLastQuery();
	}
	
	@Test
	public void testAddSourceFolder() {
		try {
			int tmpsize = j.getAllStatements().size();
			j.addSource(deffile, "", defuri);
			assertTrue(tmpsize < j.getAllStatements().size());
			
		} catch (Exception e) {
			fail();
		}
	}
	
	@Test (expected = FileNotFoundException.class)
	public void testAddSourceNotFound() throws IOException {
		try {
			j.addSource(deffolder + "notfound/", "", defuri);
		}
		catch (RepositoryException e) {
			fail();
		} catch (RDFParseException e) {
			fail();
		}
	}
	
	@Test
	public void testNamespaceHandling() {
		try {
			assertFalse(j.getNamespaceList().isEmpty());
			assertEquals(j.getNamespaceList().size(), 8);
			assertEquals(j.getNamespace(rdfpre), rdfuri);
			
		} catch (RepositoryException e) {
			fail();
		}
	}
	
	@Test
	public void testNamespaceAddGet() {
		try {
			assertEquals(null, j.getNamespace(defuri));
			j.addNamespace(defuri, defuri + defuri);
			assertEquals(j.getNamespace(defuri), defuri + defuri);
		} catch (RepositoryException e) {
			fail();
		} 
	}

	@Test
	public void testNamespaceRaz() {
		try {
			j.razNamespaces();
			assertEquals(j.getNamespaceList().size(), 0);
		} catch (RepositoryException e) {
			fail();
		} 
	}
	
	@Test
	public void testNamespacePrefixes() {
		try {
			String tmppref = j.getPrefixes();
			assertTrue(tmppref.startsWith("PREFIX"));
			assertTrue(tmppref.contains(rdfpre));
			assertTrue(tmppref.contains(rdfuri));
			
			j.razNamespaces();
			assertEquals("", j.getPrefixes());
		} catch (RepositoryException e) {
			fail();
		} 
	}
	
	@Test
	public void testSPARQLSelect() {
		try {
			TupleQueryResult tpq = j.SPARQLQuery(defreq);
			
			assertEquals(defreq, j.getLastQuery());
			assertEquals(1, j.getQueries().size());
			assertEquals(tpq.getBindingNames().get(0), "s");
			
			tpq.close();
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		} catch (QueryEvaluationException e) {
			fail();
		}
	}
	
	@Test (expected = MalformedQueryException.class)
	public void testSPARQLSelectError() throws MalformedQueryException {
		try {
			j.SPARQLQuery("SELECT ?s WHERE {");
			
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (QueryEvaluationException e) {
			fail();
		}
	}
	
	@Test
	public void testSPARQLUpdate() {
		try {
			j.updateQuery("DELETE DATA {?s ?p ?o}");
			
			assertEquals("DELETE DATA {?s ?p ?o}", j.getLastQuery());
			assertEquals(1, j.getQueries().size());
			assertEquals(j.getAllStatements().size(), 0);
			
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		} catch (UpdateExecutionException e) {
			fail();
		}
	}
	
	@Test (expected = MalformedQueryException.class)
	public void testSPARQLUpdateError() throws MalformedQueryException {
		try {
			j.updateQuery("DELETE DATA ");
			
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (UpdateExecutionException e) {
			fail();
		}
	}
	
	@Test
	public void testSetCommit() {
		assertTrue(j.isAutoCommit());
		j.setAutoCommit(false);
		assertFalse(j.isAutoCommit());
		j.setAutoCommit(true);
		assertTrue(j.isAutoCommit());
	}
	
	@Test
	public void testCommit() {
		try {
			j.setAutoCommit(false);
			
			j.updateQuery("DELETE DATA {?s ?p ?o}");
			
			assertEquals("DELETE DATA {?s ?p ?o}", j.getLastQuery());
			assertEquals(1, j.getQueries().size());
			assertEquals(j.getAllStatements().size(), 0);
			
			j.commit();
			assertEquals(j.getAllStatements().size(), 0);
			
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		} catch (UpdateExecutionException e) {
			fail();
		}
	}
	
	@Test
	public void testRollback() {
		try {
			j.setAutoCommit(false);
			
			j.updateQuery("DELETE DATA {?s ?p ?o}");
			
			assertEquals("DELETE DATA {?s ?p ?o}", j.getLastQuery());
			assertEquals(1, j.getQueries().size());
			assertEquals(j.getAllStatements().size(), 0);
			
			j.rollback();
			
			assertFalse(j.getAllStatements().size() == 0);
			
			j.commit();
			assertFalse(j.getAllStatements().size() == 0);
			
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		} catch (UpdateExecutionException e) {
			fail();
		}
	}
	
	@Test
	public void testStatements() {
		try {
			LinkedList<Statement> sts = j.getAllStatements();
			assertTrue(sts.size() != 0);
			
			j.addStatement(new StatementImpl(new URIImpl(rdfpre + ":" + defuri), new URIImpl(rdfpre + ":" + defuri), new URIImpl(rdfpre + ":" + defuri)));
			assertEquals(sts.size() + 1, j.getAllStatements(null, null).size());
			
			j.addAllStatements(sts);
			assertEquals(sts.size() + 1, j.getAllStatements().size());
			
			
			j.removeStatements(null, new URIImpl(rdfpre + ":" + defuri));
			assertEquals(sts.size(), j.getAllStatements(null, null).size());
			
		} catch (RepositoryException e) {
			fail();
		}
	}
}
