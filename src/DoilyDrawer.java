import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;

/**
 * DoilyDrawer class. Holds drawing methods for a doily state.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DoilyDrawer {
	// Graphic display options
	private static final int RING_COUNT = 10;
	private DoilyState doily;                          // Doily object to draw

	/**
	 * Instantiates a new doily drawer with a given doily.
	 * @param doily The doily object to draw
	 */
	public DoilyDrawer(DoilyState doily) {
		this.doily = doily;
	}

	/**
	 * Creates an image of a given size using the given settings and lines.
	 * @param size Size of image to create
	 * @return Image of the doily
	 */
	public BufferedImage getDoilyImage(Dimension size) {
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
	public void drawDoily(BufferedImage image) {
		drawDoily((Graphics2D) image.getGraphics(), new Dimension(image.getWidth(), image.getHeight()));
	}

	/**
	 * Draws the current doily onto a given graphics object - scaled to the given dimensions.
	 * @param gr Graphics object to draw to
	 * @param d Dimension to scale doily to
	 */
	public void drawDoily(Graphics2D g, Dimension d) {
		// Set graphic settings
		setGraphicSettings(g);
		// Draw concentric circles (Bottom Layer)
		drawRings(g, d);
		// Draw lines
		for (Line line : doily.lines) {
			drawLine(g, line, d);
		}	
		// Draw separators (Top Layer)
		drawSeperators(g, d);
	}

	/**
	 * Draws doily updates onto a given buffered image - scaled to image.
	 * @param image Image to draw to
	 */
	public void updateDoily(BufferedImage image) {
		updateDoily((Graphics2D) image.getGraphics(), new Dimension(image.getWidth(), image.getHeight()));
	}

	/**
	 * Draws changes to the current doily onto a given graphics object - scaled to the given dimensions.
	 * Changes are defined as the last line added to lines.
	 * @param gr Graphics object to draw to
	 * @param d Dimension to scale doily changes to
	 */
	public void updateDoily(Graphics2D g, Dimension d) {
		// Set graphic settings
		setGraphicSettings(g);	
		// Draw last line
		int lineCount = doily.lines.size();
		if (lineCount > 0) {
			drawLine(g, doily.lines.get(lineCount-1), d);			
		}
		// Redraw separators (Top Layer)
		drawSeperators(g, d);
	}

	/**
	 * Set rendering hints for given graphics object
	 * @param g Graphics object to draw to
	 */
	private void setGraphicSettings(Graphics2D g) {
		if (doily.settings.isAntiAlias()) {
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
		if (doily.settings.isShowRings()) {
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
		int sectors = doily.settings.getSectors();
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
		int sectors = doily.settings.getSectors();
		if (doily.settings.isShowSeparators() && sectors != 1) {
			// Determine formatting constraints
			Point centre = DoilyUtilities.getCentre(d);
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

}
