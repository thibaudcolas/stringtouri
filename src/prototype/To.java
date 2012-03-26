package prototype;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.openrdf.model.Statement;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.model.impl.URIImpl;

public abstract class To {
	
	protected Jeu amodif;
	protected LinkedList<Statement> maj;
	protected String output;
	
	public To(Jeu j) {
		amodif = j;
		maj = new LinkedList<Statement>();
		output = "";
	}
	
	public To(Jeu j, LinkedList<Statement> m) {
		amodif = j;
		maj = m;
		output = "";
	}
	
	public void addStatement(String s, String p, String o) {
		maj.add(new StatementImpl(new URIImpl(s), new URIImpl(p), new URIImpl(o)));
	}

	public LinkedList<Statement> getMaj() {
		return maj;
	}

	public void setMaj(LinkedList<Statement> maj) {
		this.maj = maj;
	}

	public abstract String getOutput();
	
	public void writeToFile(String chemin) {
		File f = new File(chemin);
		if (f.isFile() && f.canWrite()) {
			try {
				BufferedWriter res = new BufferedWriter(new FileWriter(chemin));
				res.write(getOutput());
				res.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			System.err.println("Fichier inutilisable");
		}
	}
}
