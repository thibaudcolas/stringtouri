package prototype;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.repository.RepositoryException;

public class App {
	
	protected Jeu reference;
	protected Jeu objectif;
	
	protected String datatomaj;
	
	protected Liaison linkage;
	
	protected To interconnexion;
	
	public App(Jeu ref, Jeu obj) {
		reference = ref;
		objectif = obj;
	}
	
	public App(Jeu ref, Jeu obj, String d, Liaison l, To t) {
		reference = ref;
		objectif = obj;
		datatomaj = d;
		linkage = l;
		interconnexion = t;
	}
	
	public void setLiaisonSimple(String referenceprop, String objectifprop) {
		linkage = new LiaisonSimple(reference, objectif, referenceprop, objectifprop);
		datatomaj = objectifprop;
	}
	
	public void setLiaisonTypee(String referenceprop, String objectifprop, String referencetype, String objectiftype) {
		linkage = new LiaisonTypee(reference, objectif, referenceprop, objectifprop, referencetype, objectiftype);
		datatomaj = objectifprop;
	}
	
	public void setLiaisonLibre(String referenceprop, String objectifprop, String referencequery, String objectifquery) {
		linkage = new LiaisonLibre(reference, objectif, referenceprop, objectifprop, referencequery, objectifquery);
		datatomaj = objectifprop;
	}
	
	public void setRDFOutput() throws RepositoryException {
		interconnexion = new ToRDF(objectif, datatomaj);
	}
	
	public void setSesameOutput() throws RepositoryException {
		interconnexion = new ToSesame(objectif, datatomaj);
	}
	
	public void setSPARQLOutput() throws RepositoryException {
		interconnexion = new ToSPARQL(objectif, datatomaj);
	}
	
	public void initiateInterconnexion(boolean a) throws QueryEvaluationException, MalformedQueryException, RuntimeException, RepositoryException {
		interconnexion.setMaj(linkage.getInterconnexion(), a);
	}
	
	public String getOutput() {
		return interconnexion.getOutput();
	}
	
	public void doUpdate() {
		interconnexion.majStatements();
	}

	public void shutdown() {
		reference.shutdown();
		objectif.shutdown();
	}
}
