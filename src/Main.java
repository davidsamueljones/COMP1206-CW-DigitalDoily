import java.awt.EventQueue;

/**
 * Main class. Used to start a Doily GUI.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class Main {

	/**
	 * The main method. Starts a new Doily frame using the Event Dispatch Thread.
	 * @param args Passed arguments [Program uses no arguments]
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					new DigitalDoily();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

}
