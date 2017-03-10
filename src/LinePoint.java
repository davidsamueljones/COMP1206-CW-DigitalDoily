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
	 * Gets the absolute position using the scaled values and values provided for positioning.
	 * @param radius The absolute radius [Positioning value]
	 * @param sectorAngle The absolute sector angle in radians [Positioning value]
	 * @return The absolute position
	 */
	public Point getAbsolutePosition(int radius, double sectorAngle) {
		// Convert scaled properties to absolute positioning
		double angle = getClockwiseScale()*sectorAngle;
		double orbit = radius*getOrbitScale();
		// Convert absolute positioning to absolute points
		return new Point((int)Math.round(Math.sin(angle)*orbit),
				(int)Math.round(Math.cos(angle)*orbit));
	}
	
}
