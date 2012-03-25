package prototype;

import java.util.LinkedList;

public class ToRDF extends To {

	protected String chemin;
	
	public ToRDF(Jeu j, String p) {
		super(j, p);
		chemin = "";
	}

	public ToRDF(Jeu j, String p, LinkedList<Maj> m) {
		super(j, p, m);
		chemin = "";
	}
	
	public ToRDF(Jeu j, String p, String c) {
		super(j, p);
		chemin = c;
	}

	public ToRDF(Jeu j, String p, LinkedList<Maj> m, String c) {
		super(j, p, m);
		chemin = c;
	}
	
	public String writeDescription(String sub, String props) {
		String ret = "<rdf:Description rdf:about=\""+sub+"\">\n"
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
