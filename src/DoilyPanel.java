import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 * DoilyPanel class. Handles creation and viewing of doilys
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DoilyPanel extends JPanel {
	// Graphic display options
	private static final int RING_COUNT = 10;
	// Point capture settings
	private static int DEFAULT_INTERPOLATION_RESOLUTION = 100000;
	private static double DEFAULT_INTERPOLATION_SCALE_FACTOR = 0.05;
	
	// Instance variables
	private BufferedImage pnlImage;                          // Current drawing image
	private DoilySettings settings;                          // DoilySettings object to use
	private ArrayList<Line> lines;                           // Lines of current drawing
	private Deque<Line> redoStack = new ArrayDeque<Line>();  // Stack of last undone lines

	/**
	 * Instantiates a new doily panel with a new settings object.
	 */
	public DoilyPanel() {
		this(new DoilySettings());
		// Attach listener to detect panel resizing
		this.addComponentListener(new ComponentAdapter(){
			@Override
			public void componentResized(ComponentEvent e) {
				// Redraw if image is being used so it can be scaled
				if (settings.isUseImage()) {
					redraw();    	
				}
			}
		});
	}

	/**
	 * Instantiates a new doily panel with a given settings object.
	 * @param settings the settings
	 */
	public DoilyPanel(DoilySettings settings) {
		// Assign arguments
		this.settings = settings;
		lines = new ArrayList<Line>();

		// Add drawing listener 
		DrawListener drawListener = new DrawListener();
		addMouseListener(drawListener);
		addMouseMotionListener(drawListener);
	}

	@Override
	protected void paintComponent(Graphics gr){
		super.paintComponent(gr);
		Graphics2D g = (Graphics2D) gr;
		Dimension size = this.getSize();
		if (settings.isUseImage()) {
			// Check if image requires redraw or update
			if (pnlImage == null) {
				pnlImage = getDoily(size);
			}
			else {
				updateDoily(pnlImage);
			}
			// Draw buffered image
			g.drawImage(pnlImage, 0, 0, null);
		}
		else {
			drawDoily(g, size);
		}
	}
	
	/**
	 * Resets the image object, forcing a full redraw on paint.
	 * Triggers repaint to apply updates.
	 */
	public void redraw() {
		pnlImage = null;
		repaint();
	}

	/**
	 * Creates an image of a given size using the current doily.
	 * @param size Size of image to create
	 * @return Image of the doily
	 */
	public BufferedImage getDoily(Dimension size) {
		// Create a new image of given size
		BufferedImage doilyImage = new BufferedImage(size.width, size.height, BufferedImage.TYPE_3BYTE_BGR);
		// Use BufferedImage's size and graphics object to draw the scaled Doily
		drawDoily(doilyImage);
		return doilyImage;
	}

	/**
	 * Draws the current doily onto a given buffered image - scaled to image.
	 * @param image Image to draw to
	 */
	private void drawDoily(BufferedImage image) {
		drawDoily((Graphics2D) image.getGraphics(), new Dimension(image.getWidth(), image.getHeight()));
	}

	/**
	 * Draws the current doily onto a given graphics object - scaled to the given dimensions.
	 * @param gr Graphics object to draw to
	 * @param d Dimension to scale doily to
	 */
	private void drawDoily(Graphics2D g, Dimension d) {
		// Set graphic settings
		setGraphicSettings(g);
		// Draw concentric circles (Bottom Layer)
		drawRings(g, d);
		// Draw lines
		for (Line line : lines) {
			drawLine(g, line, d);
		}	
		// Draw separators (Top Layer)
		drawSeperators(g, d);
	}

	/**
	 * Draws doily updates onto a given buffered image - scaled to image.
	 * @param image Image to draw to
	 */
	private void updateDoily(BufferedImage image) {
		updateDoily((Graphics2D) image.getGraphics(), new Dimension(image.getWidth(), image.getHeight()));
	}

	/**
	 * Draws changes to the current doily onto a given graphics object - scaled to the given dimensions.
	 * Changes are defined as the last line added to lines.
	 * @param gr Graphics object to draw to
	 * @param d Dimension to scale doily changes to
	 */
	private void updateDoily(Graphics2D g, Dimension d) {
		// Set graphic settings
		setGraphicSettings(g);	
		// Draw last line
		if (lines.size() > 0) {
			drawLine(g, lines.get(lines.size()-1), d);			
		}
		// Redraw separators (Top Layer)
		drawSeperators(g, d);
	}

	/**
	 * Set rendering hints for given graphics object
	 * @param g Graphics object to draw to
	 */
	private void setGraphicSettings(Graphics2D g) {
		if (settings.isAntiAlias()) {
			// Set graphic object rendering hints
			g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		}
	}

	/**
	 * Draw concentric rings to a given graphics object scaled to a given dimension.
	 * @param g Graphics object to draw to
	 * @param d Dimension to scale rings to
	 */
	private void drawRings(Graphics2D g, Dimension d) {
		if (settings.isShowRings()) {
			// Determine formatting constraints
			int max = DoilyUtilities.getMaxSquareDisplay(d);
			// Reset stroke and set colour
			g.setColor(Color.DARK_GRAY);
			g.setStroke(new BasicStroke());
			// Draw concentric rings
			for (int i=1; i <= RING_COUNT; i++) {
				int size = i*max/RING_COUNT;
				g.drawOval((d.width - i*max/RING_COUNT)/2, (d.height - i*max/RING_COUNT)/2, size, size);
			}
		}
	}

	/**
	 * Draw a line to a given graphics object scaled to a given dimension.
	 * @param g Graphics object to draw to
	 * @param line Line object to draw
	 * @param d Dimension to scale line to
	 */
	private void drawLine(Graphics2D g, Line line, Dimension d) {
		// Determine formatting constraints
		Point centre = DoilyUtilities.getCentre(d);
		int radius = DoilyUtilities.getRadius(d);		
		int sectors = settings.getSectors();
		double sectorAngle = DoilyUtilities.getSectorAngle(sectors);
		AffineTransform preRotate = g.getTransform();

		// Set pen using line settings
		int size = DoilyUtilities.getPenSize(line.getScaleFactor(), d);
		g.setColor(line.getColor());
		g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));	

		// Create paths for points positioned normally and reflected
		// Reflected path may not be required but performance impact is minimal
		Path2D pathNormal = new Path2D.Float();
		Path2D pathReflected = new Path2D.Float();
		boolean isFirst = true;
		for (LinePoint point : line.points) {
			// Get positional coordinates of point
			Point posAbsolute = point.getAbsolutePosition(radius, sectorAngle);
			Point posNormal = new Point(centre.x+posAbsolute.x, centre.y+posAbsolute.y);
			Point posReflected = new Point(centre.x-posAbsolute.x, centre.y+posAbsolute.y);
			// Create paths
			if (isFirst) {
				pathNormal.moveTo(posNormal.x, posNormal.y);
				pathReflected.moveTo(posReflected.x, posReflected.y);
				isFirst = false;
			} else {
				pathNormal.lineTo(posNormal.x, posNormal.y);
				pathReflected.lineTo(posReflected.x, posReflected.y);
			} 
		}

		// Draw points for all sectors
		for (int i=0; i < sectors; i++) {
			// Draw paths
			g.draw(pathNormal);
			if (line.isReflect()) {
				g.draw(pathReflected);
			}	
			// Rotate for next sector
			g.rotate(sectorAngle, centre.x, centre.y);
		}
		// Reset rotation (in case of any offset from double accuracy)
		g.setTransform(preRotate);
		// Reset stroke
		g.setStroke(new BasicStroke());
	}

	/**
	 * Draw sector separators to a given graphics object scaled to a given dimension.
	 * @param g Graphics object to draw to
	 * @param d Dimension to scale separators to
	 */
	private void drawSeperators(Graphics2D g, Dimension d) {
		if (settings.isShowSeparators()) { //&& settings.getSectors() != 1) {
			// Determine formatting constraints
			Point centre = DoilyUtilities.getCentre(d);
			int sectors = settings.getSectors();
			int radius = DoilyUtilities.getRadius(d);
			AffineTransform preRotate = g.getTransform();
			// Reset stroke and set colour
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke());
			// Draw separator for each sector
			for (int i=0; i < sectors; i++) {	    
				g.drawLine(centre.x, centre.y, centre.x, centre.y-radius);
				g.rotate(DoilyUtilities.getSectorAngle(sectors), centre.x, centre.y);
			}
			// Reset transform (in case of any offset from double accuracy)
			g.setTransform(preRotate);
		}
	}
	
	/**
	 * Gets the last line to be added.
	 * @return The last line
	 */
	private Line getLastLine() {
		return lines.get(lines.size()-1);
	}
	
	/**
	 * Adds a new line using the current settings.
	 * @return The new line
	 */
	private Line addLine() {
		// Create a new line
		Line line = new Line(settings.getPenScale(), settings.getPenColor(), settings.isReflect());
		lines.add(line);
		return line;
	}

	/**
	 * Adds an absolute point to a given line as a scaled point.
	 * @param line The line to add to
	 * @param point Point to add
	 * @param d Dimension to scale point to
	 */
	private void addPointToLine(Line line, Point point, Dimension d) {
		// Check for existing points in line
		LinePoint lastPoint = null;
		if (line.points.size() > 0) {
			// Modify clockwise scaling to take into account wrapping around full circle
			 lastPoint = line.points.get(line.points.size()-1);
		}		
		// Find scaled point
		LinePoint newPoint = LinePoint.scalePoint(point, d, settings, lastPoint);	
		
		// Handle interpolation for sector change smoothing
		if (settings.isInterpolate()) {
			interpolateBetweenPoints(lastPoint, newPoint, line);	
		}
		// Add scaled point
		if (newPoint != null) {
			line.points.add(newPoint);
		}		
	}

	/**
	 * Use interpolation to add extra points between points in case of large jumps. 
	 * This can be used to improve smoothness when reducing sector count. This function will be
	 * called recursively until distance between reaches target. A fixed plane is used
	 * so panel sizes do not affect number of points drawn.
	 * @param start The point to interpolate from (not null)
	 * @param end The point to interpolate to (not null)
	 * @param line The line to add new points to
	 */
	private void interpolateBetweenPoints(LinePoint start, LinePoint end, Line line) {
		// Validate exit conditions
		if (start == null || end == null) {
			return;
		}
		
		// Configure interpolation settings
		int planeSize = DEFAULT_INTERPOLATION_RESOLUTION;
		double scaleFactor = DEFAULT_INTERPOLATION_SCALE_FACTOR;
		double sectorAngle = DoilyUtilities.getSectorAngle(settings.getSectors());
		Dimension planeResolution = new Dimension(planeSize, planeSize);
		int planeRadius = DoilyUtilities.getRadius(planeResolution);
	
		// Recreate points in interpolation plane
		Point startPoint = start.getAbsolutePosition(planeRadius, sectorAngle);
		Point endPoint = end.getAbsolutePosition(planeRadius, sectorAngle);
		// Calculate midpoint
		Point difPoints = new Point(endPoint.x - startPoint.x, endPoint.y - startPoint.y);
		Point relPoint = new Point(startPoint.x + difPoints.x/2, startPoint.y + difPoints.y/2);
		// Recreate point as a scaled position
		LinePoint scaledpoint = LinePoint.scalePoint(relPoint, planeResolution, settings, start);	

		// If new point is valid and not zero
		if ((difPoints.x != 0 || difPoints.y != 0) && scaledpoint != null) {
			LinePoint scaledDif = new LinePoint(Math.abs(scaledpoint.getOrbitScale() - start.getOrbitScale()),
					Math.abs(scaledpoint.getClockwiseScale() - start.getClockwiseScale()));
			// Add point if separation is high
			if (scaledDif.getOrbitScale() > scaleFactor || scaledDif.getClockwiseScale() > scaleFactor) {
				// Add point, this will result in the interpolate function being recalled
				addPointToLine(line, relPoint, planeResolution);
			}
		}
	}

	/**
	 * Gets the current settings object.
	 * @return The settings
	 */
	public DoilySettings getSettings() {
		return settings;
	}

	/**
	 * Sets the settings object.
	 * @param settings The new settings
	 */
	public void setSettings(DoilySettings settings) {
		this.settings = settings;
	}
	
	/**
	 * Clear all lines and the redo stack.
	 */
	public void clear() {
		lines.clear();
		clearRedoStack();
		redraw();
	}

	/**
	 * Undo state by removing the last line. Store on redo stack so it can be retrieved.
	 */
	public void undo() {
		if (lines.size() > 0) {
			Line line = getLastLine();
			lines.remove(line);
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
			lines.add(redoStack.pop());
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
			addPointToLine(addLine(), DoilyUtilities.centrePoint(e.getPoint(), getSize()), getSize());
			
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
			addPointToLine(getLastLine(), DoilyUtilities.centrePoint(e.getPoint(), getSize()), getSize());
			repaint();
		}	

	}

}