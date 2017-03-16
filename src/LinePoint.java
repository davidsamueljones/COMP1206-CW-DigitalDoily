import java.awt.Dimension;
import java.awt.Point;

/**
 * LinePoint class. Holds properties of a single scaled point for Line.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class LinePoint {
	// Scaled positioning
	private double orbitScale;      // Position as a percentage of the radius
	private double clockwiseScale;  // Position as a percentage of the clockwise arc length

	/**
	 * Instantiates a new gallery panel.
	 * @param orbitScale The orbit scale
	 * @param clockwiseScale The clockwise scale
	 */
	public LinePoint(double orbitScale, double clockwiseScale) {
		this.orbitScale = orbitScale;
		this.clockwiseScale = clockwiseScale;
	}

	/**
	 * Gets the orbit scale.
	 * @return The orbit scale
	 */
	public double getOrbitScale() {
		return orbitScale;
	}

	/**
	 * Sets the orbit scale.
	 * @param orbitDistance The new orbit scale
	 */
	public void setOrbitScale(double orbitDistance) {
		this.orbitScale = orbitDistance;
	}

	/**
	 * Gets the clockwise scale.
	 * @return The clockwise scale
	 */
	public double getClockwiseScale() {
		return clockwiseScale;
	}

	/**
	 * Sets the clockwise scale.
	 * @param clockwiseScale The new clockwise scale
	 */
	public void setClockwiseScale(double clockwiseScale) {
		this.clockwiseScale = clockwiseScale;
	}

	/**
	 * Get object absolute position using the scaled values and values provided for positioning.
	 * @param radius The absolute radius [Positioning value]
	 * @param sectorAngle The absolute sector angle in radians [Positioning value]
	 * @return The absolute position
	 */
	public Point getAbsolutePosition(int radius, double sectorAngle) {
		return getAbsolutePosition(orbitScale, clockwiseScale, radius, sectorAngle);
	}

	/**
	 * Static implementation to get the absolute position of a scaled point using 
	 * the scaled values and values provided for positioning.
	 * @param orbitScale Position as a percentage of the radius
	 * @param clockwiseScale Position as a percentage of the clockwise arc length
	 * @param radius The absolute radius [Positioning value]
	 * @param sectorAngle The absolute sector angle in radians [Positioning value]
	 * @return The absolute position
	 */
	public static Point getAbsolutePosition(double orbitScale, double clockwiseScale, int radius, double sectorAngle) {
		// Convert scaled properties to absolute positioning
		double angle = clockwiseScale*sectorAngle;
		double orbit = radius*orbitScale;
		// Convert absolute positioning to absolute points
		return new Point((int)Math.round(Math.sin(angle)*orbit),
				-(int)Math.round(Math.cos(angle)*orbit));
	}
	
	/**
	 * Convert an absolute point (from a centre) to a scaled point using positioning values.
	 * Use settings to determine invalid points and use a previous point to handle wrapped values.
	 * @param absolute Point centred around [0,0]
	 * @param d Dimension point was recorded in
	 * @param settings Settings object used for recording
	 * @param lastPoint An existing point used to calculate point wrapping (null is valid)
	 * @return Scaled point if valid, else null 
	 */
	public static LinePoint scalePoint(Point absolute, Dimension d, 
			DoilySettings settings, LinePoint lastPoint) {
		// Determine capturing constraints
		int radius = DoilyUtilities.getRadius(d);
		int sectors = settings.getSectors();
		double sectorAngle = DoilyUtilities.getSectorAngle(sectors);

		// Calculate angular position and distance from centre
		// Calculate direct distance (radius) using pythagoras
		double distance = Math.sqrt(Math.pow(absolute.x, 2)+Math.pow(absolute.y, 2));    	
		// Find position in radians (convert so clockwise from the vertical)
		double radPos = (Math.atan2(absolute.y, absolute.x) + 2.5*Math.PI) % (2*Math.PI);

		// Find sector in which point resides
		for (int sector=0; sector < sectors; sector++) {
			// Find sector constraints
			double lowerAngle = sectorAngle*sector;
			double upperAngle = lowerAngle + sectorAngle;
			double maxDistance = radius - DoilyUtilities.getPenSize(settings.getPenScale(), d)/2;
			
			// If point is within sector bounds
			if (lowerAngle <= radPos && upperAngle > radPos && 
					(!settings.isCircleBounded() || distance < maxDistance)) {
				// Determine relative scaling
				double orbitScale = distance/radius;
				double clockwiseScale = radPos/sectorAngle;				
				LinePoint newPoint = new LinePoint(orbitScale, clockwiseScale);
				// Handle influence of existing points 
				if (lastPoint != null) {
					// Modify clockwise scaling to take into account wrapping around full circle
					int wrapCount = (int) Math.floor(((lastPoint.getClockwiseScale() - clockwiseScale) / sectors)+0.5);
					newPoint.setClockwiseScale(clockwiseScale+sectors*wrapCount);
				}
				return newPoint;
			}
		}
		// Point does not reside in any sector
		return null;
	}

	@Override
	public LinePoint clone() {
		return new LinePoint(orbitScale, clockwiseScale);
	}
	
}
