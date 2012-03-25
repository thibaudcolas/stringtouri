package prototype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import org.openrdf.model.Namespace;
import org.openrdf.repository.RepositoryException;

public class ToRDF extends To {

	protected String chemino;
	
	
	private static final String baliseXML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
	
	public ToRDF(Jeu j, String p) {
		super(j, p);
		chemino = "";
	}

	public ToRDF(Jeu j, String p, LinkedList<Maj> m) {
		super(j, p, m);
		chemino = "";
	}
	
	public ToRDF(Jeu j, String p, String c) {
		super(j, p);
		chemino = c;
	}

	public ToRDF(Jeu j, String p, LinkedList<Maj> m, String c) {
		super(j, p, m);
		chemino = c;
	}
	
	public void writeFile(String chemin) {
		File f = new File(chemin);
		if (f.isFile() && f.canWrite()) {
			BufferedWriter res;
			
			String rdf = "";
			for (Maj m : modifs) {
				for (String suj : m.getSujets()) {
					rdf += writeDesc(suj, writeProp(prop, m.getObjet(), true));
				}
				rdf += "\n";
			}
			try {
				List<Namespace> namespaces = amodif.getNamespaceList();
				String tmpns = "";
				for(Namespace n : namespaces) {
					tmpns += "xmlns:" + n.getPrefix() + "=\"" + n.getName() + "\"\n";
				}
				
				res = new BufferedWriter(new FileWriter(chemin));
				res.write(baliseXML + writeRDF(tmpns, rdf));
				res.close();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RepositoryException e) {
				e.printStackTrace();
			}
		}
	}
	
	public String writeRDF(String namespaces, String contenu) {
		return "<rdf:RDF \n"+namespaces+">\n" + contenu + "</rdf:RDF>\n";
	}
	
	public String writeDesc(String suj, String props) {
		String ret = "<rdf:Description rdf:about=\""+suj+"\">\n"
				+ props + "</rdf:Description>\n";
		return ret;
	}
	
	public String writeProp(String prop, String obj, boolean isresource) {
		return "\t" + (isresource ? writeResourceProp(prop, obj) : writeTextProp(prop, obj)) + "\n";
	}
	
	public String writeResourceProp(String prop, String obj) {
		return "<"+prop+" rdf:resource=\""+obj+"\"/>";
	}
	
	public String writeTextProp(String prop, String obj) {
		return "<"+prop+">"+obj+"</"+prop+">";
	}

}
