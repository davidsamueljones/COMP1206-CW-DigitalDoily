import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;

/**
 * Line class. Holds settings and points for a line.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class Line {
	// Point capture settings
	private static int DEFAULT_INTERPOLATION_RESOLUTION = 100000;
	private static double DEFAULT_INTERPOLATION_SCALE_FACTOR = 0.05;

	// Line settings
	private int scaleFactor;
	private Color color;
	private boolean reflect;
	// Line points
	public ArrayList<LinePoint> points;

	/**
	 * Instantiates a new line with given settings.
	 * @param scaleFactor The pen scale factor
	 * @param colour The pen colour
	 * @param reflect Whether to reflect
	 */
	public Line(int scaleFactor, Color color, boolean reflect) {
		this.scaleFactor = scaleFactor;
		this.color = color;
		this.reflect = reflect;
		points = new ArrayList<LinePoint>();
	}

	/**
	 * Gets the pen scale factor.
	 * @return The pen scale factor
	 */
	public int getScaleFactor() {
		return scaleFactor;
	}

	/**
	 * Sets the pen scale factor.
	 * @param scaleFactor The new pen scale factor
	 */
	public void setScaleFactor(int scaleFactor) {
		this.scaleFactor = scaleFactor;
	}

	/**
	 * Gets the pen colour.
	 * @return The pen colour
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the pen colour.
	 * @param color The new pen colour
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Checks if is reflect.
	 * @return true, if should reflect
	 */
	public boolean isReflect() {
		return reflect;
	}

	/**
	 * Sets whether should be reflected.
	 * @param reflect The new reflect
	 */
	public void setReflect(boolean reflect) {
		this.reflect = reflect;
	}

	/**
	 * Adds an absolute point to a given line as a scaled point.
	 * @param point Point to add
	 * @param d Dimension to scale point to
	 * @param settings The settings to enforce
	 */
	public void addPoint(Point point, Dimension d, DoilySettings settings) {
		// Check for existing points in line
		LinePoint lastPoint = null;
		if (points.size() > 0) {
			// Modify clockwise scaling to take into account wrapping around full circle
			lastPoint = points.get(points.size()-1);
		}		
		// Find scaled point
		LinePoint newPoint = LinePoint.scalePoint(point, d, settings, lastPoint);	

		// Handle interpolation for sector change smoothing
		if (settings.isInterpolate()) {
			interpolateBetweenPoints(lastPoint, newPoint, settings);	
		}
		// Add scaled point
		if (newPoint != null) {
			points.add(newPoint);
		}		
	}

	/**
	 * Use interpolation to add extra points between points in case of large jumps. 
	 * This can be used to improve smoothness when reducing sector count. This function will be
	 * called recursively until distance between reaches target. A fixed plane is used
	 * so panel sizes do not affect number of points drawn.
	 * @param start The point to interpolate from (not null)
	 * @param end The point to interpolate to (not null)
	 * @param settings The settings to enforce
	 */
	private void interpolateBetweenPoints(LinePoint start, LinePoint end, DoilySettings settings) {
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
				addPoint(relPoint, planeResolution, settings);
			}
		}
	}

	@Override
	public Line clone() {
		Line newLine = new Line(scaleFactor, color, reflect);
		for (LinePoint point : points) {
			newLine.points.add(point.clone());
		}
		return newLine;
	}

}
