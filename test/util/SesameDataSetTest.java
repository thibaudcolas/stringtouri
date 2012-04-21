package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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

/**
 * JUnit test cases on SesameDataSet.
 * @author Thibaud Colas.
 * @version 07042012
 * @see SesameDataSet
 */
public class SesameDataSetTest {
	
	/**
	 * Data set to use during tests.
	 */
	private SesameDataSet j;
	/**
	 * Name of the test repository.
	 */
	private static final String defdep = "test";
	/**
	 * URL of the Sesame server.
	 */
	// Needs the Sesame server to be on.
	private static final String defurl = "http://localhost:8080/openrdf-sesame";
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
		j = new SesameDataSet(defurl, defdep);
		j.addTuples("./test/util/rdf/", "continents", defurl);
	}

	@After
	public void tearDown() throws Exception {
		j.shutdown();
	}

	@Test (expected = NoSuchElementException.class)
	public void testConstructor() {
		assertEquals(j.getName(), defurl + " - " + defdep);
		assertEquals(j.getRepositoryID(), defdep);
		assertEquals(j.getServerURL(), defurl);
		
		try {
			String tmpurl = "http://localhost:8080/openrdf-sesame/repositories/test";
			SesameDataSet jbis = new SesameDataSet(tmpurl);
			assertEquals(jbis.getName(), tmpurl);
			assertEquals(jbis.getRepositoryID(), tmpurl);
			assertEquals(jbis.getServerURL(), tmpurl);
		} catch (RepositoryException e) {
			fail();
		}
	}
	
	@Test
	public void testNamespaceHandling() {
		try {
			assertFalse(j.getNamespaceList().isEmpty());
			// Compté manuellement.
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
			j.resetNamespaces();
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
			
			j.resetNamespaces();
			assertEquals("", j.getPrefixes());
		} catch (RepositoryException e) {
			fail();
		} 
	}
	
	@Test
	public void testSPARQLSelect() {
		try {
			TupleQueryResult tpq = j.selectQuery(defreq);
			
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
	
	@Test (expected = QueryEvaluationException.class)
	public void testSPARQLSelectError() throws QueryEvaluationException {
		try {
			j.selectQuery("SELECT ?s WHERE {");
			
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		}
	}
	
	@Test
	public void testSPARQLUpdate() {
		try {
			j.updateQuery("DELETE DATA {?s ?p ?o}");
			
			assertEquals(j.getAllStatements().size(), 0);
			
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		} catch (UpdateExecutionException e) {
			fail();
		}
	}
	
	@Test (expected = UpdateExecutionException.class)
	public void testSPARQLUpdateError() throws UpdateExecutionException {
		try {
			j.updateQuery("DELETE DATA ");
			
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
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
			
			assertEquals(j.getAllStatements().size(), 0);
			
			j.rollback();
			
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
