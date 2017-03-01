import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.InputEvent;
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

/**
 * DoilyPanel class. Handles creation and viewing of doilys
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DoilyPanel extends JPanel {
	// Display options
    private static final double USEABLE_AREA = 0.95;
    // Graphic display options
    private static final double PEN_SCALE = 1/1000.0;
    private static final int RING_COUNT = 10;
    
    // Instance variables
    private DoilySettings settings;                          // DoilySettings object to use

	private ArrayList<Line> lines;                           // Lines of current drawing
    private Deque<Line> redoStack = new ArrayDeque<Line>();  // Stack of last undone lines
    
    /**
     * Instantiates a new doily panel with a new settings object.
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
    	this.settings = settings;
    	lines = new ArrayList<Line>();
    	
    	// Add drawing listener 
    	DrawListener drawListener = new DrawListener();
    	addMouseListener(drawListener);
    	addMouseMotionListener(drawListener);
    }
    
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawDoily(g, this.getSize());
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
    	Graphics g = doilyImage.createGraphics();
    	drawDoily(g, size);
    	return doilyImage;
    }
    
    /**
     * Draws the current doily onto a given graphics object - scaled to the given dimensions.
     * @param gr Graphics object to draw to
     * @param d Dimension to scale doily to
     */
    private void drawDoily(Graphics gr, Dimension d) {
    	// Get panel dimensions and remember rotation position
    	Graphics2D g = (Graphics2D)gr;
    	AffineTransform preRotate = g.getTransform();
    	// Set graphic settings
    	if (settings.isAntiAlias()) {
			g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON));
		}
    	
    	// Determine formatting constraints
    	int max = getMaxSquareDisplay(d);
    	Point centre = getCentre(d);
    	int radius = getRadius(d);
    	
    	// Sector sizing
    	int sectors = settings.getSectors();
    	double sectorAngle = getSectorAngle(settings.getSectors());
    	
    	// Draw concentric circles
    	if (settings.isShowRings()) {
    		g.setColor(Color.DARK_GRAY);
        	for (int i=1; i <=RING_COUNT; i++) {
        		int size = i*max/RING_COUNT;
            	g.drawOval((d.width - i*max/RING_COUNT)/2, (d.height - i*max/RING_COUNT)/2, size, size);
        	}
    	}

		// Draw path for each line of points
		for (Line line : lines) {
			// Set pen using line settings
			int size = (int) Math.round(line.getScaleFactor()*radius*PEN_SCALE);
			g.setColor(line.getColor());
			g.setStroke(new BasicStroke(size, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			// Create path objects
			Path2D path = new Path2D.Double();
			Path2D pathReflected = new Path2D.Double();
			boolean isFirst = true;
    		// Loop through all points
			for (LinePoint point : line.points) {
				// Get positional coordinates of point
				Point posAbsolute = point.getAbsolutePosition(radius, sectorAngle);
            	Point posNormal = new Point(centre.x+posAbsolute.x, centre.y-posAbsolute.y);
            	Point posReflected = new Point(centre.x-posAbsolute.x, centre.y-posAbsolute.y);
				// Create paths
            	if (isFirst) {
                    path.moveTo(posNormal.x, posNormal.y);
                    pathReflected.moveTo(posReflected.x, posReflected.y);
                    isFirst = false;
            	} else {
            		path.lineTo(posNormal.x, posNormal.y);
            		pathReflected.lineTo(posReflected.x, posReflected.y);
                } 
    		}
        	// Map points across all sectors
        	for (int i=0; i < sectors; i++) {
        		// Draw paths
        		g.draw(path);
            	if (line.isReflect()) {
            		g.draw(pathReflected);
            	}	
            	// Rotate for next sector
        		g.rotate(Math.toRadians(sectorAngle), centre.x, centre.y);
        	}
        	// Reset rotation (in case of any offset from double accuracy)
        	g.setTransform(preRotate);
		}
    	
		// Draw separators (Top layer)
    	if (settings.isShowSeparators() && sectors != 1) {
			// Reset stroke and set colour
			g.setColor(Color.WHITE);
			g.setStroke(new BasicStroke());
			// Draw separator for each sector
	    	for (int i=0; i < sectors; i++) {	    
		    	g.drawLine(centre.x, centre.y, centre.x, centre.y-radius);
		    	g.rotate(Math.toRadians(sectorAngle), centre.x, centre.y);
	    	}
    	}
    	// Dispose graphics object
    	g.dispose();
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
     * Adds an absolute point to a given line as a relative point.
     * @param line The line to add to
     * @param absolute The absolute position of the point to add
     */
    private void addPointToLine(Line line, Point absolute) {
     	Dimension d = this.getSize();
    	int radius = getRadius(d);
    	Point centre = getCentre(d);
    	Point relative = new Point(absolute.x-centre.x, absolute.y-centre.y);
    	double sectorAngle = getSectorAngle(settings.getSectors());
    	
    	// Calculate angular position and distance from centre
    	// Calculate direct distance (radius) using pythagoras
    	double distance = Math.sqrt(Math.pow(relative.x, 2)+Math.pow(relative.y, 2));    	
    	// Find position in radians (y & x swapped for simpler clockwise rotation calculation)
    	double radPos = Math.atan2(relative.y, relative.x);
    	// Find position in degrees, starting from the vertical
    	double degPos = (radPos * 180 / Math.PI + 450) % 360;
    	
    	// Find sector in which point resides
    	for (int i=0; i < settings.getSectors(); i++) {
    		// Find sector constraints
    		double lowerAngle = sectorAngle*i;
    		double upperAngle = lowerAngle + sectorAngle;
    		// If point is within sector bounds (radius can be ignored)
    		if ((lowerAngle < degPos && upperAngle > degPos) && 
    				(!settings.isCircleBounded() || distance < radius)) {
    			// Determine relative scaling
    			double orbitScale = distance/radius;
    			double clockwiseScale = degPos/sectorAngle;
				// Create a new point with relative scale
    	    	line.points.add(new LinePoint(orbitScale, clockwiseScale));
    	    	this.repaint();
    		}
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
     * Clear all lines and the redo stack.
     */
    public void clear() {
    	lines.clear();
    	clearRedoStack();
    	repaint();
    }
    
    /**
     * Undo state by removing the last line. Store on redo stack so it can be retrieved.
     */
    public void undo() {
    	if (lines.size() > 0) {
    		Line line = getLastLine();
        	lines.remove(line);
        	redoStack.push(line);
        	repaint();
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
     * Gets the absolute pen size for the current pen scale on the current display.
     * @return The absolute pen size
     */
    public int getPenSize(int penScale) {
    	return (int) Math.round(settings.getPenScale()*getRadius(this.getSize())*PEN_SCALE);
    }    
    
    /**
     * Clear the redo stack.
     */
    private void clearRedoStack() {
    	redoStack.clear();
    }
    
    /**
     * Gets the angle of a sector for a given number of sectors.
     * @return The sector angle
     */
    private static double getSectorAngle(int sectors) {
    	return (360.0/sectors);
    }
    
    /**
     * Gets the centre point of a given dimension.
     * @param d The dimension
     * @return The centre point
     */
    private static Point getCentre(Dimension d) {
    	return new Point(d.width/2, d.height/2);
    }

    /**
     * Gets the maximum display area of a dimension if it were considered square.
     * @param d The dimension
     * @return The size of either dimension (Width == Height)
     */
    private static int getMaxSquareDisplay(Dimension d) {
     	return (int) Math.round(Math.min(d.width, d.height)*USEABLE_AREA);
    }
    
    /**
     * Gets the radius of a Doily for a given dimension.
     * @param d The dimension
     * @return The radius
     */
    private static int getRadius(Dimension d) {
    	int max = getMaxSquareDisplay(d);
     	return max/2;
    }

    /**
     * The listener interface for receiving draw events.
     * Inner class for DoilyPanel.
     */
    class DrawListener extends MouseAdapter {
 
	    @Override
    	public void mousePressed(MouseEvent e) {
    		// Only acknowledge button1
    		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
    			return;
    		}
    		// Add a point to a new line
    		addPointToLine(addLine(), e.getPoint());
    		// Forget lines from undo
    		clearRedoStack();
    	}
    	
	    @Override
    	public void mouseDragged(MouseEvent e) {
    		// Only acknowledge button1
    		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
    			return;
    		}
    		// Add a point to the current line
    		addPointToLine(getLastLine(), e.getPoint());
    	}	
	    
    }
    
}