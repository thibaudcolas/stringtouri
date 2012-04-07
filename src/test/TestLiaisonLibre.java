package test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openrdf.model.Statement;
import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryException;

import prototype.JeuRDF;
import prototype.Liaison;
import prototype.LiaisonLibre;

/**
 * JUnit test cases on LiaisonLibre.
 * @author Thibaud Colas.
 * @version 07042012
 * @see LiaisonLibre
 */
public class TestLiaisonLibre {
	
	/**
	 * Source data set.
	 */
	private JeuRDF s;
	/**
	 * Target data set.
	 */
	private JeuRDF c;
	/**
	 * A linkage tool to test.
	 */
	private LiaisonLibre l;
	
	/**
	 * Default URI to use when importing data.
	 */
	private static final String defuri = "defuri";
	/**
	 * Predicate to be used on the source side.
	 */
	private static final String defprops = "gn:name";
	/**
	 * Predicate to be used on the target side.
	 */
	private static final String defpropc = "geographis:onContinent";
	/**
	 * Query to be used on the source side.
	 */
	private static final String defqs = "SELECT ?s ?o WHERE {?s a geographis:Continent . ?s gn:name ?o}";
	/**
	 * Query to be used on the target side.
	 */
	private static final String defqc = "SELECT ?s ?o WHERE {?s geographis:onContinent ?o . ?s geographis:currency <http://telegraphis.net/data/currencies/EUR#EUR>}";



	@Before
	public void setUp() throws Exception {
		s = new JeuRDF("./src/test/rdf/continents.rdf", "", defuri);
		c = new JeuRDF("./src/test/rdf/countries-tolink.rdf", "", defuri);
		l = new LiaisonLibre(s, c, defprops, defpropc, defqs, defqc);
	}

	@After
	public void tearDown() throws Exception {
		l.shutdown();
	}

	@Test
	public void testConstructor() {
		assertEquals(l.getMaxLiens(), 0);
		assertEquals(l.getNom(), defprops + " - " + defpropc);
		assertEquals(l.getPropCible(), defpropc);
		assertEquals(l.getPropSource(), defprops);
		assertEquals(l.getQueryCible(), defqc);
		assertEquals(l.getQuerySource(), defqs);
	}
	
	@Test
	public void testOtherConstructor() {
		final int maxliens = 100;
		
		LiaisonLibre lbis = new LiaisonLibre(s, c, defprops, defpropc, defqs, defqc, maxliens);
		assertEquals(lbis.getMaxLiens(), maxliens);
		assertEquals(lbis.getNom(), defprops + " - " + defpropc);
		assertEquals(lbis.getPropCible(), defpropc);
		assertEquals(lbis.getPropSource(), defprops);
		assertEquals(l.getQueryCible(), defqc);
		assertEquals(l.getQuerySource(), defqs);
	}
	
	@Test
	public void testBindingCheck() {
		try {
			assertTrue(l.hasCorrectBindingNames(s.SPARQLQuery(l.getQuerySource())));
			assertFalse(l.hasCorrectBindingNames(s.SPARQLQuery("SELECT ?" + Liaison.OVAR + " WHERE {?" + Liaison.SVAR + " " + defpropc + " ?" + Liaison.OVAR + "}")));
			assertFalse(l.hasCorrectBindingNames(s.SPARQLQuery("SELECT ?" + Liaison.SVAR + " WHERE {?" + Liaison.SVAR + " " + defpropc + " ?" + Liaison.OVAR + "}")));
			assertFalse(l.hasCorrectBindingNames(s.SPARQLQuery("SELECT ?" + Liaison.SVAR + " ?" + Liaison.OVAR + " ?" + Liaison.PVAR + " WHERE {?" + Liaison.SVAR + " " + defpropc + " ?" + Liaison.OVAR + "}")));
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		} catch (QueryEvaluationException e) {
			fail();
		}
	}
	
	@Test
	public void testSourceData() {
		try {
			HashMap<String, String> result = l.getSourceData();
			assertTrue(result.size() > 0);
			for (String k : result.keySet()) {
				assertFalse(k.startsWith("http://"));
				assertTrue(result.get(k).startsWith("http://"));
			}
			
			TupleQueryResult tpqr = s.SPARQLQuery(l.getQuerySource());
			int cpt = 0;
			while (tpqr.hasNext()) {
				BindingSet bs = tpqr.next();
				assertTrue(result.containsKey(bs.getBinding(Liaison.OVAR).getValue().stringValue()));
				assertTrue(result.containsValue(bs.getBinding(Liaison.SVAR).getValue().stringValue()));

				cpt++;
			}
			assertEquals(cpt, result.size());
			tpqr.close();
			
		} catch (QueryEvaluationException e) {
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		}
	}
	
	@Test
	public void testCibleData() {
		try {
			HashMap<String, LinkedList<String>> result = l.getCibleData();
			assertTrue(result.size() > 0);
			for (String k : result.keySet()) {
				assertFalse(k.startsWith("http://"));
				assertFalse(result.get(k).isEmpty());
			}
			
			TupleQueryResult tpqr = c.SPARQLQuery(l.getQueryCible());
			while (tpqr.hasNext()) {
				BindingSet bs = tpqr.next();
				assertTrue(result.containsKey(bs.getBinding(Liaison.OVAR).getValue().stringValue()));
			}
			tpqr.close();
			
		} catch (QueryEvaluationException e) {
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		}
	}

	@Test
	public void testInterconnexion() {
		try {
			HashMap<String, LinkedList<Statement>> result = l.getInterconnexion();
			for (String suj : result.keySet()) {
				assertTrue(suj.startsWith("http://"));
				for (Statement st : result.get(suj)) {
					assertEquals(st.getPredicate().stringValue(), defpropc);
					assertEquals(st.getSubject().stringValue(), suj);
				}
			}
		} catch (QueryEvaluationException e) {
			fail();
		} catch (RepositoryException e) {
			fail();
		} catch (MalformedQueryException e) {
			fail();
		}
	}
}
