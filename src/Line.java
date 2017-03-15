import java.awt.Color;
import java.util.ArrayList;

/**
 * Line class. Holds settings and points for a line.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class Line {
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

}
