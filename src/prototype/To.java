package prototype;

import java.util.LinkedList;

public abstract class To {
	
	protected Jeu amodif;
	protected String prop;
	protected LinkedList<Maj> modifs;
	
	public To(Jeu j, String p) {
		amodif = j;
		prop = p;
		modifs = new LinkedList<Maj>();
	}
	
	public To(Jeu j, String p, LinkedList<Maj> m) {
		amodif = j;
		prop = p;
		modifs = m;
	}
	
	public void addModif(String obj, LinkedList<String> sujs) {
		modifs.add(new Maj(obj, sujs));
	}
}
