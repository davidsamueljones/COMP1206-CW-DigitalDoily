import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JPanel;

public class DoilyPanel extends JPanel implements MouseListener, MouseMotionListener {
    private static final int INDENT=10;

    private DoilySettings settings;
    private ArrayList<Line> lines;
    
    public DoilyPanel(DoilySettings settings) {
    	this.settings = settings;
    	lines = new ArrayList<Line>();
    	addMouseListener(this);
    	addMouseMotionListener(this);
    }
    
    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        drawDoily(g, this.getSize());
    }
    
    private void drawDoily(Graphics gr, Dimension d) {
    	// Get panel dimensions
    	Graphics2D g = (Graphics2D)gr;
    	AffineTransform preRotate = g.getTransform();
    	
    	// Determine formatting constraints
    	int max = getMaxSquareDisplay();
    	Point centre = getCentre();
    	int radius = max/2;
    	
    	// Sector sizing
    	int sectors = settings.getSectors();
    	double sectorAngle = getSectorAngle();
    	
    	// Draw border (start top left corner)
    	g.setColor(Color.DARK_GRAY);
    	g.drawOval((d.width - max)/2,(d.height - max)/2,max,max);
    	// !!! Border test lines
    	for (int i=1; i <=10; i++) {
    		int size = i*max/10;
        	g.drawOval((d.width - i*max/10)/2, (d.height - i*max/10)/2, size, size);
    	}

    	// !!! Border test lines

    	
    	// Map dots across all sectors
    	for (int i=0; i < sectors; i++) {
    		// Draw dots for each line
    		for (Line line : lines) {
    			g.setColor(line.getColour());
    			int size = line.getScaleFactor()*radius/1000;
        		for (Dot dot : line.dots) {
        			// Convert scaled points to absolute points
                	double angle = dot.getClockwiseScale()*Math.toRadians(sectorAngle);
                	double orbit = radius*dot.getOrbitScale();
                	int posX = (int)Math.round(Math.sin(angle)*orbit - size / 2.0);
                	int posY = (int)Math.round(Math.cos(angle)*orbit + size / 2.0);
                	// Draw dot
                	g.fillOval(centre.x+posX, centre.y-posY,  size,  size);
        		}
    		}
        	// Rotate for next sector
        	g.rotate(Math.toRadians(sectorAngle), centre.x, centre.y);
    	}
    	g.setTransform(preRotate);
    	
    	
		// Draw separators (Top layer)
    	if (settings.isShowSeperators() && sectors != 1) {
			g.setColor(Color.WHITE);
	    	for (int i=0; i < sectors; i++) {	    	
		    		g.drawLine(centre.x, centre.y, centre.x, centre.y-radius);
		    	// Rotate for next sector
		    	g.rotate(Math.toRadians(sectorAngle), centre.x, centre.y);
	    	}	
    	}
    	
    	g.dispose();
    }
    
    

    private void drawDot(Line line, Point loc) {
   
    	int max = getMaxSquareDisplay();
    	int radius = max/2;
    	Point centre = getCentre();
    	Point relative = new Point(loc.x-centre.x, loc.y-centre.y);
    	double sectorAngle = getSectorAngle();
    	
    	// Calculate angular position and distance from centre
    	double distance = Math.sqrt(Math.pow(relative.x, 2)+Math.pow(relative.y, 2));    	
    	double radPos = Math.atan2(relative.y, relative.x);
    	double degPos = (radPos * 180 / Math.PI + 450) % 360;
    	
    	// Find sector in which point resides
    	for (int i=0; i < settings.getSectors(); i++) {
    		double lowerAngle = sectorAngle*i;
    		double upperAngle = lowerAngle + sectorAngle;
    		// If point is within sector bounds
    		if (lowerAngle < degPos && upperAngle > degPos && distance <= radius) {
    			// Determine relative scaling
    			double orbitScale = distance/radius;
    			double clockwiseScale = (degPos - lowerAngle)/sectorAngle;
    			// Create a new dot using relative point
    			Dot dot = new Dot();
    	    	dot.setOrbitScale(orbitScale);
    	    	dot.setClockwiseScale(clockwiseScale);
    	    	line.dots.add(dot);
    	    	this.repaint();
    		}

    	}
    }
    
    public BufferedImage getDoily(int size) {
    	BufferedImage doilyImage = new BufferedImage(size, size, BufferedImage.TYPE_3BYTE_BGR);
    	Graphics g = doilyImage.createGraphics();
    	drawDoily(g, new Dimension(size, size));
    	return doilyImage;
    }

    public void removeLastLine() {
    	if (lines.size() > 0) {
        	lines.remove(lines.size()-1);
        	repaint();	
    	}
    }
    
    public void removeAllLines() {
    	lines.clear();
    	repaint();
    }
    
    private double getSectorAngle() {
    	return  (360.0/settings.getSectors());
    }
    
    private Point getCentre() {
     	Dimension d = this.getSize();
    	return new Point(d.width/2, d.height/2);
    }

    private int getMaxSquareDisplay() {
    	Dimension d = this.getSize();
     	return (int) Math.round(Math.min(d.width, d.height)*0.95);
    }
    
	@Override
	public void mouseClicked(MouseEvent e) {

	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Add a dot to the current line
		Line line = lines.get(lines.size()-1);
		drawDot(line, e.getPoint());
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		// Create a new line
		Line line = new Line(settings.getPenSize(), settings.getPenColor(), settings.isReflect());
		lines.add(line);
		// Add an initial dot
		drawDot(line, e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {}

	@Override
	public void mouseEntered(MouseEvent e) {}

	@Override
	public void mouseExited(MouseEvent e) {}

	@Override
	public void mouseMoved(MouseEvent e) {}
    
}