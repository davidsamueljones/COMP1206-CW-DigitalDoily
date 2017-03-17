import java.awt.EventQueue;

import javax.swing.UIManager;

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
		// Get natural GUI appearance
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Error setting look and feel");
		}
		// Create new Digital Doily on EDT 
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
