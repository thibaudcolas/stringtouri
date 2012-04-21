package util;

import java.io.IOException;

import org.apache.log4j.Level;
import org.openrdf.repository.RepositoryException;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParseException;

/**
 * Helps handling the interlinking with RDF data.
 * 
 * @author Thibaud Colas
 * @version 05042012
 * @see App
 */
public class RDFApp extends App {

	/**
	 * Default constructor.
	 * @param rpath : Filepath to source data.
	 * @param gpath : Filepath to target data.
	 * @param rfilter : Name filter.
	 * @param gfilter : Name filter.
	 */
	public RDFApp(String rpath, String gpath, String rfilter, String gfilter) {
		super();
		
		try {
			reference = new RDFDataSet(rpath, rfilter, "", RDFFormat.RDFXML);
			goal = new RDFDataSet(gpath, gfilter, "", RDFFormat.RDFXML);
			
			name = reference.getName() + " - " + goal.getName();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created RDFApp " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		} catch (RDFParseException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODEIO);
		} catch (IOException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODEIO);
		}
	}
	
	/**
	 * Default constructor with logging selection
	 * @param rpath : Filepath to source data.
	 * @param gpath : Filepath to target data.
	 * @param rfilter : Name filter.
	 * @param gfilter : Name filter.
	 * @param logging : Log level to use.
	 */
	public RDFApp(String rpath, String gpath, String rfilter, String gfilter, Level logging) {
		super(logging);
		
		try {
			reference = new RDFDataSet(rpath, rfilter, "", RDFFormat.RDFXML, logging);
			goal = new RDFDataSet(gpath, gfilter, "", RDFFormat.RDFXML, logging);
			
			name = reference.getName() + " - " + goal.getName();
			
			if (LOG.isDebugEnabled()) {
				LOG.debug("Created RDFApp " + name);
			}
		}
		catch (RepositoryException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODERE);
		} catch (RDFParseException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODEIO);
		} catch (IOException e) {
			LOG.fatal(e);
			shutdown();
			System.exit(CODEIO);
		}
	}
}
