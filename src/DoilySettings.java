import java.awt.Color;

public class DoilySettings {
	public static final int MIN_SECTORS=1;
	public static final int MAX_SECTORS=100;
	
	public static final int MIN_PEN_SIZE=1;
	public static final int MAX_PEN_SIZE=100;
	
	
	private int sectors;
	private boolean showSeperators;
	private int penSize;
	private Color penColor;
	private boolean reflect;
	
	public int getSectors() {
		return sectors;
	}
	
	public void setSectors(int sectors) {
		this.sectors = sectors;
	}
	
	public boolean isShowSeperators() {
		return showSeperators;
	}
	
	public void setShowSeperators(boolean showSeperators) {
		this.showSeperators = showSeperators;
	}
	
	public int getPenSize() {
		return penSize;
	}
	
	public void setPenSize(int penSize) {
		this.penSize = penSize;
	}
	
	public Color getPenColor() {
		return penColor;
	}
	
	public void setPenColor(Color penColor) {
		this.penColor = penColor;
	}
	
	public boolean isReflect() {
		return reflect;
	}
	
	public void setReflect(boolean reflect) {
		this.reflect = reflect;
	}
	
}
