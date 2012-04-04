package prototype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.UpdateExecutionException;
import org.openrdf.repository.RepositoryException;

public class App {
	
	protected Jeu reference;
	protected Jeu objectif;
	
	protected String datatomaj;
	
	protected Liaison linkage;
	
	protected To sortie;
	
	public App(Jeu ref, Jeu obj) {
		reference = ref;
		objectif = obj;
	}
	
	public App(Jeu ref, Jeu obj, String d, Liaison l, To t, boolean a) throws RepositoryException, QueryEvaluationException, MalformedQueryException {
		reference = ref;
		objectif = obj;
		datatomaj = d;
		linkage = l;
		sortie = t;
		sortie.setMaj(l.getInterconnexion(), a);
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
		sortie = new ToRDF(objectif, datatomaj);
	}
	
	public void setSesameOutput() throws RepositoryException {
		sortie = new ToSesame(objectif, datatomaj);
	}
	
	public void setSPARQLOutput() throws RepositoryException {
		sortie = new ToSPARQL(objectif, datatomaj);
	}
	
	public void initiateInterconnexion(boolean a) throws QueryEvaluationException, MalformedQueryException, RuntimeException, RepositoryException {
		sortie.setMaj(linkage.getInterconnexion(), a);
	}
	
	public String getOutput() {
		return sortie.getOutput();
	}
	
	public void doUpdate() throws RepositoryException, MalformedQueryException, UpdateExecutionException {
		sortie.majStatements();
	}

	public void shutdown() {
		reference.shutdown();
		objectif.shutdown();
	}
	
	/** 
	 * Writes the output to a file.
	 * @param chemin : The path to the file where to write the output.
	 * @throws IOException Error while writing to the filepath.
	 */
	public final void storeOutput(final String chemin) throws IOException {
		File f = new File(chemin);
		if (f.isFile() && f.canWrite()) {
			BufferedWriter res = new BufferedWriter(new FileWriter(chemin));
			res.write(sortie.getOutput());
			res.close();
		}
		else {
			throw new IOException("Fichier inutilisable / introuvable - " + chemin);
		}
	}
}
