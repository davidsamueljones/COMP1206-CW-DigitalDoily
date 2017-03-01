import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.InputEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

/**
 * GalleryImage class. Handles a single image for GalleryPanels.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class GalleryImage extends JPanel {
	// Default Border Settings
	private final static Border DEFAULT_BORDER = BorderFactory.createLineBorder(Color.BLACK, 1);
	// Selected Border Settings
	private final static Color SELECTED_BORDER_COLOR = new Color(102, 204, 0);
	private final static Border SELECTED_BORDER_STYLE = BorderFactory.createLineBorder(SELECTED_BORDER_COLOR, 3);
	private final static Font SELECTED_BORDER_FONT = new Font(null, Font.BOLD, 20);
	private final static String SELECTED_BORDER_TITLE = "\u2713"; // Unicode Tick
	private final static Border SELECTED_BORDER = BorderFactory.createTitledBorder(
			SELECTED_BORDER_STYLE, SELECTED_BORDER_TITLE, TitledBorder.LEADING, 
			TitledBorder.BELOW_TOP, SELECTED_BORDER_FONT,  SELECTED_BORDER_COLOR); 
	
	// Instance variables
	private BufferedImage imgHQ;  // High quality, exportable image
	private Image imgThumbnail;   // Low quality image
	private GalleryPanel gallery; // Parent gallery
	private boolean selected;     // Whether image is selected
	
	/**
	 * Instantiates a new gallery image.
	 * @param imgHQ A high quality version of the image, used for export
	 * @param imgThumbnail A low quality version of the image used for display
	 * @param gallery The parent gallery
	 */
	public GalleryImage(BufferedImage imgHQ, Image imgThumbnail, GalleryPanel gallery) {
		// Assign arguments
		this.imgHQ = imgHQ;
		this.imgThumbnail = imgThumbnail;
		this.gallery = gallery;
	    
	    // Set selected false to draw border on construction
	    setSelected(false);
	    // Create a new image listener
	    addMouseListener(new SelectListener());
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		// Draw thumbnail
		g.drawImage(imgThumbnail, 0, 0, null);
	}
	
	/**
	 * Checks if image is selected.
	 * @return true, if it is selected
	 */
	public boolean isSelected() {
		return selected;
	}

	/**
	 * Sets whether it is selected and sets an appropriate border.
	 * @param selected the new value of selected
	 */
	public void setSelected(boolean selected) {
		this.selected = selected;
		// Set border to indicate if selected
		if (selected) {
			this.setBorder(SELECTED_BORDER);
		}
		else {
			this.setBorder(DEFAULT_BORDER);
		}
	}
	
	/**
	 * Export the high quality version of the image as a PNG.
	 * @param path the path
	 */
	public void exportHQ(String path) {
		try {
			ImageIO.write(imgHQ, "PNG", new File(path));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * The listener interface for receiving selection events.
	 * Inner class for GalleryImage.
	 */
	class SelectListener extends MouseAdapter {
		 
    	@Override
    	public void mousePressed(MouseEvent e) {
    		// Only acknowledge button1
    		if ((e.getModifiers() & InputEvent.BUTTON1_MASK) == 0) {
    			return;
    		}
    		
    		// Deselect other images if Ctrl or Shift not held
    		int modifierMask = InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK;
    		if ((e.getModifiers() & modifierMask) == 0) {
    			gallery.deselectImages();
    		}
    		// If already selected deselect, else select
    		// Deselect can only occur if modifier is held
    		if (selected) {
    			setSelected(false);
    		}
    		else {
    			setSelected(true);
    		}
    	}
    	
    }
	
}
