import java.awt.Color;

/**
 * DoilySettings class. Handles doily settings, providing defaults.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DoilySettings {
	// Default setting boundaries
	public static final int MIN_SECTORS = 1;
	public static final int MAX_SECTORS = 100;
	public static final int MIN_PEN_SIZE = 1;
	public static final int MAX_PEN_SIZE = 100;
	// Default settings
	private static final int DEFAULT_SECTORS = 20;
	private static final boolean DEFAULT_SHOW_SEPARATORS = true;
	private static final boolean DEFAULT_SHOW_RINGS = true;
	private static final boolean DEFAULT_USE_IMAGE = false;	
	private static final boolean DEFAULT_ANTI_ALIAS = false;	
	private static final int DEFAULT_PEN_SCALE = 10;
	private static final Color DEFAULT_PEN_COLOR = Color.WHITE;
	private static final boolean DEFAULT_REFLECT = false;
	private static final boolean DEFAULT_CIRCLE_BOUNDED = true;
	private static final boolean DEFAULT_INTERPOLATE = true;

	// Drawing Settings
	private int sectors;                // Number of sectors
	private boolean showSeparators;     // Whether to show sector separators
	private boolean showRings;          // Whether to show concentric rings
	private boolean useImage;           // Whether to render drawings as images
	private boolean antiAlias;          // Whether anti-aliasing is enabled

	// Pen Settings
	private int penScale;               // Scale of pen for new lines
	private Color penColor;             // Colour of the pen for new lines
	private boolean reflect;            // Whether new lines should be reflected
	private boolean circleBounded;      // Whether lines should be bound by the circle radius
	private boolean interpolate;        // Whether lines should use linear interpolation

	/**
	 * Instantiates a new doily settings with defaults.
	 */
	public DoilySettings() {
		this(DEFAULT_SECTORS, DEFAULT_SHOW_SEPARATORS, DEFAULT_SHOW_RINGS, 
				DEFAULT_USE_IMAGE, DEFAULT_ANTI_ALIAS, DEFAULT_PEN_SCALE, 
				DEFAULT_PEN_COLOR, DEFAULT_REFLECT, DEFAULT_CIRCLE_BOUNDED, 
				DEFAULT_INTERPOLATE);
	}

	/**
	 * Instantiates a new doily settings with given values.
	 * @param sectors Number of sectors
	 * @param showSeperators Whether to show sector separators
	 * @param showRings Whether to show concentric rings
	 * @param useImage Whether to render drawings as images
	 * @param antiAlias Whether anti-aliasing is enabled
	 * @param penScale Scale of pen for new lines
	 * @param penColor Colour of the pen for new lines
	 * @param reflect Whether new lines should be reflected
	 * @param circleBounded Whether lines should be bound by the circle radius
	 * @param interpolate Whether lines should use linear interpolation
	 */
	public DoilySettings(int sectors, boolean showSeperators, boolean showRings, 
			boolean useImage, boolean antiAlias, int penScale, Color penColor, 
			boolean reflect, boolean circleBounded, boolean interpolate) {
		// Assign setting arguments
		this.sectors = sectors;
		this.showSeparators = showSeperators;
		this.showRings = showRings;
		this.useImage = useImage;
		this.antiAlias = antiAlias;
		this.penScale = penScale;
		this.penColor = penColor;
		this.reflect = reflect;
		this.circleBounded = circleBounded;
		this.interpolate = interpolate;
	}


	/**
	 * Gets the number of sectors.
	 * @return The number of sectors
	 */
	public int getSectors() {
		return sectors;
	}

	/**
	 * Sets the number of sectors.
	 * @param sectors The new number of sectors
	 */
	public void setSectors(int sectors) {
		this.sectors = sectors;
	}

	/**
	 * Checks value of show separators.
	 * @return true, if show separators
	 */
	public boolean isShowSeparators() {
		return showSeparators;
	}

	/**
	 * Sets whether to show separators.
	 * @param showSeparators The new value for show separators
	 */
	public void setShowSeparators(boolean showSeparators) {
		this.showSeparators = showSeparators;
	}

	/**
	 * Checks value of show rings.
	 * @return true, if show rings
	 */
	public boolean isShowRings() {
		return showRings;
	}

	/**
	 * Sets whether to show rings.
	 * @param showRings the new show rings
	 */
	public void setShowRings(boolean showRings) {
		this.showRings = showRings;
	}

	/**
	 * Checks value of use image
	 * @return true, if use image
	 */
	public boolean isUseImage() {
		return useImage;
	}

	/**
	 * Sets the value of use image
	 * @param useImage The new value for use image
	 */
	public void setUseImage(boolean useImage) {
		this.useImage = useImage;
	}

	/**
	 * Checks value of anti alias.
	 * @return true, if anti alias
	 */
	public boolean isAntiAlias() {
		return antiAlias;
	}

	/**
	 * Sets the value of anti alias.
	 * @param antiAlias The new value for anti alias
	 */
	public void setAntiAlias(boolean antiAlias) {
		this.antiAlias = antiAlias;
	}

	/**
	 * Gets the pen scale.
	 * @return the pen scale
	 */
	public int getPenScale() {
		return penScale;
	}

	/**
	 * Sets the pen scale.
	 * @param penScale the new pen scale
	 */
	public void setPenScale(int penScale) {
		this.penScale = penScale;
	}

	/**
	 * Gets the pen colour.
	 * @return The pen colour
	 */
	public Color getPenColor() {
		return penColor;
	}

	/**
	 * Sets the pen colour.
	 * @param penColor The new pen colour
	 */
	public void setPenColor(Color penColor) {
		this.penColor = penColor;
	}

	/**
	 * Checks value of reflect.
	 * @return true, if should reflect
	 */
	public boolean isReflect() {
		return reflect;
	}

	/**
	 * Sets whether to reflect.
	 * @param reflect The new reflect value
	 */
	public void setReflect(boolean reflect) {
		this.reflect = reflect;
	}

	/**
	 * Checks value of circle bound.
	 * @return true, if circle bounded
	 */
	public boolean isCircleBounded() {
		return circleBounded;
	}

	/**
	 * Sets whether to circle bound.
	 * @param circleBounded The new circle bounded value
	 */
	public void setCircleBounded(boolean circleBounded) {
		this.circleBounded = circleBounded;
	}

	/**
	 * Checks value of interpolate.
	 * @return true, if interpolate
	 */
	public boolean isInterpolate() {
		return interpolate;
	}

	/**
	 * Sets whether to interpolate.
	 * @param circleBounded The new interpolate value
	 */
	public void setInterpolate(boolean interpolate) {
		this.interpolate = interpolate;
	}

	@Override
	public DoilySettings clone() {
		return new DoilySettings(sectors, showSeparators, showRings, useImage, 
				antiAlias, penScale, penColor, reflect, circleBounded, interpolate);
	}

}
