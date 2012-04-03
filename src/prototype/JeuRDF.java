package prototype;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.LinkedList;

import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;
import org.openrdf.sail.memory.MemoryStore;

/**
 * A data set stored locally which lasts only for runtime.
 * 
 * @author Thibaud Colas
 * @version 01042012
 * @see SailRepository
 */
public class JeuRDF extends Jeu {
	
	/**
	 * The base data set URI.
	 */
	private String baseuri;
	
	/**
	 * Simple constructor.
	 * @param source : Source file / folder.
	 * @param start : Filter on the filenames to import.
	 * @param uri : Base URI for the data.
	 * @throws RuntimeException The initialization has failed and no recovery is possible.
	 * @throws IOException File/folder error.
	 * @throws RDFParseException File(s) content isn't correct RDFXML.
	 */
	JeuRDF(String source, String start, String uri) throws RuntimeException, IOException, RDFParseException {
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
			throw new RuntimeException("Error while creating new JeuRDF", e);
		} catch (RDFParseException e) {
			throw new RDFParseException(e);
		} catch (IOException e) {
			throw new IOException("File " + source + " isn't usable " + e);
		}
	}
	
	/**
	 * Adds given file(s) to the repository.
	 * @param source : Path to the given folder/file.
	 * @param start : String to filter filenames with.
	 * @throws IOException If the submitted filepath isn't usable.
	 * @throws RDFParseException If the RDF data inside the file(s) isn't well-formed.
	 * @throws RepositoryException Internal repository error.
	 */
	private void addSource(String source, String start) throws RepositoryException, RDFParseException, IOException {
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
				for (File f : rdflist) {
					if (f.getName().startsWith(start)) {
						// Ajout du fichier au format RDFXML.
						con.add(f, baseuri, RDFFormat.RDFXML);
						nbimport++;
					}
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
			throw new FileNotFoundException("File " + source + " does not exist.");
		}
	}
	
	public final String getBaseURI() {
		return baseuri;
	}
}
