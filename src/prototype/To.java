package prototype;

import java.util.LinkedList;

import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

public abstract class To {
	
	protected Jeu amodif;
	protected String prop;
	protected LinkedList<StatementImpl> maj;
	
	public To(Jeu j, String p) {
		amodif = j;
		prop = p;
		maj = new LinkedList<StatementImpl>();
	}
	
	public To(Jeu j, String p, LinkedList<StatementImpl> m) {
		amodif = j;
		prop = p;
		maj = m;
	}
	
	public void addStatement(String s, String p, String o) {
		maj.add(new StatementImpl(new URIImpl(s), new URIImpl(p), new URIImpl(o)));
	}
}
