package prototype;

import org.openrdf.repository.RepositoryException;

public abstract class App {
	
	protected Jeu reference;
	protected Jeu objectif;
	
	protected String datatomaj;
	
	protected Liaison linkage;
	
	protected To interconnexion;
	
	public void initLiaisonSimple(String referenceprop, String objectifprop) {
		linkage = new LiaisonSimple(reference, objectif, referenceprop, objectifprop);
		datatomaj = objectifprop;
	}
	
	public void initLiaisonTypee(String referenceprop, String objectifprop, String referencetype, String objectiftype) {
		linkage = new LiaisonTypee(reference, objectif, referenceprop, objectifprop, referencetype, objectiftype);
		datatomaj = objectifprop;
	}
	
	public void initLiaisonLibre(String referenceprop, String objectifprop, String referencequery, String objectifquery) {
		linkage = new LiaisonLibre(reference, objectif, referenceprop, objectifprop, referencequery, objectifquery);
		datatomaj = objectifprop;
	}
	
	public void setSPARQLOutput() throws RepositoryException {
		interconnexion = new ToSPARQL(objectif, datatomaj);
	}
	
	public void setRDFOutput() {
		
	}

}
