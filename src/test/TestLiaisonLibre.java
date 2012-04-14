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

import prototype.RDFDataSet;
import prototype.Linkage;
import prototype.FreeLinkage;

/**
 * JUnit test cases on LiaisonLibre.
 * @author Thibaud Colas.
 * @version 07042012
 * @see FreeLinkage
 */
public class TestLiaisonLibre {
	
	/**
	 * Source data set.
	 */
	private RDFDataSet s;
	/**
	 * Target data set.
	 */
	private RDFDataSet c;
	/**
	 * A linkage tool to test.
	 */
	private FreeLinkage l;
	
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
		s = new RDFDataSet("./src/test/rdf/continents.rdf", "", defuri);
		c = new RDFDataSet("./src/test/rdf/countries-tolink.rdf", "", defuri);
		l = new FreeLinkage(s, c, defprops, defpropc, defqs, defqc);
	}

	@After
	public void tearDown() throws Exception {
		l.shutdown();
	}

	@Test
	public void testConstructor() {
		assertEquals(l.getMaxLinks(), 0);
		assertEquals(l.getName(), defprops + " - " + defpropc);
		assertEquals(l.getTargetPredicate(), defpropc);
		assertEquals(l.getSourcePredicate(), defprops);
		assertEquals(l.getTargetQuery(), defqc);
		assertEquals(l.getSourceQuery(), defqs);
	}
	
	@Test
	public void testOtherConstructor() {
		final int maxliens = 100;
		
		FreeLinkage lbis = new FreeLinkage(s, c, defprops, defpropc, defqs, defqc, maxliens);
		assertEquals(lbis.getMaxLinks(), maxliens);
		assertEquals(lbis.getName(), defprops + " - " + defpropc);
		assertEquals(lbis.getTargetPredicate(), defpropc);
		assertEquals(lbis.getSourcePredicate(), defprops);
		assertEquals(l.getTargetQuery(), defqc);
		assertEquals(l.getSourceQuery(), defqs);
	}
	
	@Test
	public void testBindingCheck() {
		try {
			assertTrue(l.hasCorrectBindingNames(s.selectQuery(l.getSourceQuery())));
			assertFalse(l.hasCorrectBindingNames(s.selectQuery("SELECT ?" + Linkage.OVAR + " WHERE {?" + Linkage.SVAR + " " + defpropc + " ?" + Linkage.OVAR + "}")));
			assertFalse(l.hasCorrectBindingNames(s.selectQuery("SELECT ?" + Linkage.SVAR + " WHERE {?" + Linkage.SVAR + " " + defpropc + " ?" + Linkage.OVAR + "}")));
			assertFalse(l.hasCorrectBindingNames(s.selectQuery("SELECT ?" + Linkage.SVAR + " ?" + Linkage.OVAR + " ?" + Linkage.PVAR + " WHERE {?" + Linkage.SVAR + " " + defpropc + " ?" + Linkage.OVAR + "}")));
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
			
			TupleQueryResult tpqr = s.selectQuery(l.getSourceQuery());
			int cpt = 0;
			while (tpqr.hasNext()) {
				BindingSet bs = tpqr.next();
				assertTrue(result.containsKey(bs.getBinding(Linkage.OVAR).getValue().stringValue()));
				assertTrue(result.containsValue(bs.getBinding(Linkage.SVAR).getValue().stringValue()));

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
			HashMap<String, LinkedList<String>> result = l.getTargetData();
			assertTrue(result.size() > 0);
			for (String k : result.keySet()) {
				assertFalse(k.startsWith("http://"));
				assertFalse(result.get(k).isEmpty());
			}
			
			TupleQueryResult tpqr = c.selectQuery(l.getTargetQuery());
			while (tpqr.hasNext()) {
				BindingSet bs = tpqr.next();
				assertTrue(result.containsKey(bs.getBinding(Linkage.OVAR).getValue().stringValue()));
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
			HashMap<String, LinkedList<Statement>> result = l.generateLinks();
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
