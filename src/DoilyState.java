import java.util.ArrayList;

/**
 * DoilyState class. Holds the state of a doily.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DoilyState {
	public DoilySettings settings;                          // DoilySettings object to use
	public ArrayList<Line> lines;                           // Lines of current drawing

	/**
	 * Instantiates a new doily state using given settings and lines
	 * @param settings Settings related to doily
	 * @param lines Set of lines
	 */
	public DoilyState(DoilySettings settings, ArrayList<Line> lines) {
		this.settings = settings;
		this.lines = lines;
	}

	@Override
	public DoilyState clone() {	
		ArrayList<Line> newLines = new ArrayList<Line>();
		for (Line line : lines) {
			newLines.add(line.clone());
		}
		return new DoilyState(settings.clone(), newLines);
	}

}
