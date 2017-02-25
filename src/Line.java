import java.awt.Color;
import java.util.ArrayList;

public class Line {
	private int scaleFactor;
	private Color colour;
	private boolean reflect;
	
	public ArrayList<Dot> dots;
	
	public Line(int scaleFactor, Color colour, boolean reflect) {
		this.scaleFactor = scaleFactor;
		this.colour = colour;
		this.reflect = reflect;
		dots = new ArrayList<Dot>();
	}
	
	public int getScaleFactor() {
		return scaleFactor;
	}
	
	public void setScaleFactor(int scaleFactor) {
		this.scaleFactor = scaleFactor;
	}
	public Color getColour() {
		return colour;
	}
	
	public void setColor(Color color) {
		this.colour = color;
	}
	
	public boolean isReflect() {
		return reflect;
	}
	
	public void setReflect(boolean reflect) {
		this.reflect = reflect;
	}
	
}
