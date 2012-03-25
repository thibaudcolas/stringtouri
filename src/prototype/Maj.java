package prototype;

import java.util.LinkedList;

public class Maj {
	private String objet;
	private LinkedList<String> sujets;
	
	public Maj(String obj, LinkedList<String> sujs) {
		objet = obj;
		sujets = sujs;
	}
	
	public Maj(String obj) {
		objet = obj;
		sujets = new LinkedList<String>();
	}
	
	public void add(String obj) {
		sujets.add(obj);
	}
	
	public String getObjet() {
		return objet;
	}
	public void setObjet(String objet) {
		this.objet = objet;
	}
	public LinkedList<String> getSujets() {
		return sujets;
	}
	public void setSujets(LinkedList<String> sujets) {
		this.sujets = sujets;
	}
}
