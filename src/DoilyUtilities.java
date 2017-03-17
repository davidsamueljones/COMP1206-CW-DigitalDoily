import java.awt.Dimension;
import java.awt.Point;

/**
 * Utility class. Holds static methods for use in other classes.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DoilyUtilities {
	// Utility options
	private static final double USEABLE_AREA = 0.95;
	private static final double PEN_SCALE = 1/1000.0;

	/**
	 * Gets the absolute pen size for the current pen scale on the current display.
	 * @param penScale The size to scale
	 * @param d The dimension
	 * @return The absolute pen size
	 */
	public static int getPenSize(int penScale, Dimension d) {
		return (int) Math.round(penScale*getRadius(d)*PEN_SCALE);
	}    

	/**
	 * Gets the angle of a sector for a given number of sectors.
	 * @return The sector angle
	 */
	public static double getSectorAngle(int sectors) {
		return (2*Math.PI/sectors);
	}

	/**
	 * Gets the centre point of a given dimension.
	 * @param d The dimension
	 * @return The centre point
	 */
	public static Point getCentre(Dimension d) {
		return new Point(d.width/2, d.height/2);
	}

	/**
	 * Orient a point around a given dimensions centre.
	 * @param p The point
	 * @param d The dimension
	 * @return The centred point
	 */
	public static Point centrePoint(Point p, Dimension d) {
		Point centre = getCentre(d);
		return new Point(p.x-centre.x, p.y-centre.y);
	}

	/**
	 * Gets the maximum display area of a dimension if it were considered square.
	 * @param d The dimension
	 * @return The size of either dimension (Width == Height)
	 */
	public static int getMaxSquareDisplay(Dimension d) {
		return (int) Math.round(Math.min(d.width, d.height)*USEABLE_AREA);
	}

	/**
	 * Gets the radius of a Doily for a given dimension.
	 * @param d The dimension
	 * @return The radius
	 */
	public static int getRadius(Dimension d) {
		int max = getMaxSquareDisplay(d);
		return max/2;
	}

}
