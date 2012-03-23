package prototype;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;
import org.openrdf.repository.*;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.sail.memory.MemoryStore;

/**
 * Classe d'administration d'un jeu temporairement créé à l'exécution.
 * 
 * @author Thibaud Colas
 * @version 16032012
 * @see SailRepository, MemoryStore, RepositoryConnection
 */
public class JeuEphemere extends Jeu {
	
	// L'URI de base du jeu, TODO À récupérer autrement.
	private String baseuri;
	
	/**
	 * Constructeur classique.
	 * @param n : nom du jeu.
	 * @param source : répertoire / fichier source.
	 * @param start : filtrage sur les fichiers à importer.
	 * @param uri : URI de base des données.
	 */
	JeuEphemere(String source, String start, String uri) {
		try {
			nom = source;
			baseuri = uri;
			queries = new LinkedList<String>();
			
			rep = new SailRepository(new MemoryStore());
			rep.initialize();
			
			con = rep.getConnection();
			
			addSource(source, start);
		} 
		catch (RepositoryException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Ajoute les fichiers passés en paramètre au dépôt.
	 * @param source
	 * @param start
	 */
	public void addSource(String source, String start) {
		try {
			File src = new File(source);
			int nbimport = 0;
			
			if (src.exists()) {
				// Cas src est un répertoire.
				if (src.isDirectory()) {
					
					// On ne prend que les fichiers rdf.
					FilenameFilter fil = new FilenameFilter() {
					    public boolean accept(File d, String n) {
					        return !n.startsWith(".") && n.endsWith(".rdf");
					    }
					};
					
					File[] rdflist = src.listFiles(fil);
					for (File f : rdflist) if (f.getName().startsWith(start)) {
						// Ajout du fichier au format RDFXML.
						con.add(f, baseuri, RDFFormat.RDFXML);
						nbimport++;
					}
						
				}
				// Cas src est un fichier.
				else {
					// Ajout du fichier au format RDFXML.
					con.add(src, baseuri, RDFFormat.RDFXML);
					nbimport++;
				}
				System.out.println("Importation " + nom + " : " + nbimport + " source(s).");
			}
			else {
				System.err.println("Fichier " + source + " introuvable.");
			}
		}
		catch (Exception e) {
			System.out.println("addSource : " + source + " " + start + e);
		}
	}
	
	public String getBaseURI() {
		return baseuri;
	}
	
	public void setBaseURI(String uri) {
		baseuri = uri;
	}
}
