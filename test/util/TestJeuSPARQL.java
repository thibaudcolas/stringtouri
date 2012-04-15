package util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.NoSuchElementException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import util.SPARQLDataSet;


/**
 * JUnit test cases on JeuSPARQL.
 * @author Thibaud Colas.
 * @version 07042012
 * @see SPARQLDataSet
 */
public class TestJeuSPARQL {
	
	/**
	 * Data set to use during tests.
	 */
	private SPARQLDataSet j;
	/**
	 * URL of the SPARQL endpoint.
	 */
	// Needs the Sesame server to be on.
	private static final String defurl = "http://localhost:8080/openrdf-sesame/repositories/test";
	/**
	 * Simple test query retrieving everything.
	 */
	private static final String defreq = "SELECT ?s WHERE {?s ?p ?o}";
	
	@Before
	public void setUp() throws Exception {
		j = new SPARQLDataSet(defurl);
	}
	
	@After
	public void tearDown() throws Exception {
		j.shutdown();
	}

	@Test (expected = NoSuchElementException.class)
	public void testConstructor() {
		assertEquals(j.getName(), defurl);
		assertEquals(j.getEndpointURL(), defurl);
		
		j.getLastQuery();
	}
	
	@Test
	public void testSPARQLSelect() {
		try {
			TupleQueryResult tpq = j.selectQuery(defreq);
			
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
}
