import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * DoilyPanel class. Handles creation and viewing of a doily.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DoilyPanel extends JPanel {
	// Instance variables
	private DoilyState doily;								 // Doily to create and draw
	private DoilyDrawer doilyDrawer;                         // Doily drawer
	private BufferedImage imgDisplay;                        // Current drawing image
	private Deque<Line> redoStack = new ArrayDeque<Line>();  // Stack of last undone lines

	/**
	 * Instantiates a new doily panel with a new doily settings object.
	 */
	public DoilyPanel() {
		this(new DoilySettings());
	}

	/**
	 * Instantiates a new doily panel with a given settings object.
	 * @param settings the settings
	 */
	public DoilyPanel(DoilySettings settings) {
		// Assign arguments
		setDoily(new DoilyState(settings, new ArrayList<Line>()));

		// Add drawing listener 
		DrawListener drawListener = new DrawListener();
		addMouseListener(drawListener);
		addMouseMotionListener(drawListener);
	}

	@Override
	protected void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		Dimension d = this.getSize();

		if (doily.settings.isUseImage()) {
			// Check if image requires redraw or update
			if (imgDisplay == null || 
					!d.equals(new Dimension(imgDisplay.getWidth(), imgDisplay.getHeight()))) {
				imgDisplay = doilyDrawer.getDoilyImage(d);
			}
			else {
				doilyDrawer.updateDoily(imgDisplay);
			}
			// Draw buffered image
			g.drawImage(imgDisplay, 0, 0, null);
		}
		else {
			doilyDrawer.drawDoily(g, d);
		}
	}

	/**
	 * Gets the current doily object.
	 * @return The doily
	 */
	public DoilyState getDoily() {
		return doily;
	}

	/**
	 * Sets the doily object.
	 * @param doily The new doily
	 */
	public void setDoily(DoilyState doily) {
		this.doily = doily;
		// Create doily drawer
		doilyDrawer = new DoilyDrawer(doily);
		clearRedoStack();
		redraw();
	}

	/**
	 * Resets the image object, forcing a full redraw on paint.
	 * Triggers repaint to apply updates.
	 */
	public void redraw() {
		imgDisplay = null;
		repaint();
	}

	/**
	 * Adds a new line using the current settings.
	 * @return The new line
	 */
	private Line addLine() {
		// Create a new line
		Line line = new Line(doily.settings.getPenScale(), 
				doily.settings.getPenColor(), doily.settings.isReflect());
		doily.lines.add(line);
		return line;
	}

	/**
	 * Gets the last line to be added.
	 * @return The last line
	 */
	private Line getLastLine() {
		return doily.lines.get(doily.lines.size()-1);
	}

	/**
	 * Clear all lines and the redo stack.
	 */
	public void clear() {
		doily.lines.clear();
		clearRedoStack();
		redraw();
	}

	/**
	 * Undo state by removing the last line. Store on redo stack so it can be retrieved.
	 */
	public void undo() {
		if (doily.lines.size() > 0) {
			Line line = getLastLine();
			doily.lines.remove(line);
			redoStack.push(line);
			redraw();
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Undo Failed - Nothing to undo", 
					"Undo", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Return to previous state by appending the last line in the stack to the current lines.
	 */
	public void redo() {
		if (redoStack.size() > 0) {
			doily.lines.add(redoStack.pop());
			repaint();
		}
		else {
			JOptionPane.showMessageDialog(null,
					"Redo Failed - Nothing to redo", 
					"Redo", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Clear the redo stack.
	 */
	private void clearRedoStack() {
		redoStack.clear();
	}

	/**
	 * The listener interface for receiving draw events.
	 * Inner class for DoilyPanel.
	 */
	class DrawListener extends MouseAdapter {

		@Override
		public void mousePressed(MouseEvent e) {
			// Only acknowledge button1
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}

			// Add a point to a new line
			Line line = addLine();
			Point point = DoilyUtilities.centrePoint(e.getPoint(), getSize());
			line.addPoint(point, getSize(), doily.settings);

			// Forget lines from undo
			clearRedoStack();
			repaint();
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			// Only acknowledge button1
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}

			// Add a point to the current line
			Line line = getLastLine();
			Point point = DoilyUtilities.centrePoint(e.getPoint(), getSize());
			line.addPoint(point, getSize(), doily.settings);

			repaint();
		}	

	}

}