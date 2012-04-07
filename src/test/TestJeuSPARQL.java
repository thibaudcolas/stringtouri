package test;

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

import prototype.JeuSPARQL;

public class TestJeuSPARQL {
	
	private JeuSPARQL j;
	private static final String defurl = "http://localhost:8080/openrdf-sesame/repositories/test";
	private static final String defreq = "SELECT ?s WHERE {?s ?p ?o}";
	
	@Before
	public void setUp() throws Exception {
		j = new JeuSPARQL(defurl);
	}

	@After
	public void tearDown() throws Exception {
		j.shutdown();
	}

	@Test (expected=NoSuchElementException.class)
	public void testConstructor() {
		assertEquals(j.getNom(), defurl);
		assertEquals(j.getEndPoint(), defurl);
		
		j.getLastQuery();
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
	
	@Test (expected=QueryEvaluationException.class)
	public void testSPARQLSelectError() throws QueryEvaluationException {
		try {
			j.SPARQLQuery("SELECT ?s WHERE {");
			
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		}
	}
}
