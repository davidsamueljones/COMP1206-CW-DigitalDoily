import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

/**
 * GalleryPanel class. Handles the layout and interaction between multiple GalleryImages.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class GalleryPanel extends JPanel {
	// Display Constants
	private static final int INDENT = 10;
	private static final int SPACING = 10;
	// Export Constants
	private static final String EXPORT_DIR = "export"; 

	// GUI Objects
	JPanel pnlContent;
	JScrollPane scrContent;

	// Instance variables
	int maxImgCount;                 // Maximum number of images that can be stored
	double imgRatio;                 // Ratio of width to height
	ArrayList<GalleryImage> images;  // Images that belong to the gallery

	/**
	 * Instantiates a new gallery panel.
	 * @param maxImgCount Maximum number of images that can be stored
	 * @param imgRatio Ratio of width to height
	 */
	public GalleryPanel(int maxImgCount, double imgRatio) {
		// Verify arguments
		if (maxImgCount < 0) {
			throw new IllegalArgumentException("Gallery maximum image count must be zero or positive");
		}
		if (imgRatio <= 0) {
			throw new IllegalArgumentException("Gallery image ratio must be positive");
		}
		// Set instance variables
		this.maxImgCount = maxImgCount;
		this.imgRatio = imgRatio;
		this.images = new ArrayList<GalleryImage>();

		// Set layout so objects fill panel
		this.setLayout(new GridLayout(1, 1));
		// Create pane for gallery
		pnlContent = new JPanel(null);
		// Make scrollable object of pnlContent
		scrContent = new JScrollPane(pnlContent, ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrContent.getHorizontalScrollBar().setUnitIncrement(16);
		scrContent.setPreferredSize(new Dimension(0,0));
		this.add(scrContent);
	}

	/**
	 * Layout image array in gallery.
	 * This method modifies swing components and should therefore only be run on the 
	 * event dispatch thread
	 */
	public void reloadImages() {
		// Calculate sizing of content pane
		int imgCount = images.size();
		Dimension imgDimensions = getImageDimensions();
		int reqWidth = INDENT*2+imgDimensions.width*imgCount+SPACING*(imgCount-1);
		pnlContent.setPreferredSize(new Dimension(reqWidth, this.getSize().height));

		// Draw gallery images 
		for (int i=0; i < imgCount; i++) {
			// Reverse image order
			GalleryImage image = images.get(imgCount-i-1);
			// Set image position and size
			image.setBounds(INDENT+(imgDimensions.width+SPACING)*i, INDENT, 
					imgDimensions.width, imgDimensions.height);
			// Add panel to pnlContent if new
			if (image.getParent() == null) {
				pnlContent.add(image);
			}
		}

		// Move scroll bar to view newest image
		scrContent.revalidate();
		scrContent.getHorizontalScrollBar().setValue(1);
		repaint();
	}

	/**
	 * Adds a new image to the gallery.
	 * @param imgHQ High quality image for GalleryImage
	 * @param imgThumbnail Low quality image for GalleryImage
	 */
	public void addImage(BufferedImage imgHQ, Image imgThumbnail) {
		// Add image if maximum not reached
		if (images.size() >= maxImgCount) {
			JOptionPane.showMessageDialog(null,
					String.format("Maximum of %d images allowed in the gallery", maxImgCount), 
					"Add Image", JOptionPane.ERROR_MESSAGE);
		}
		else {
			images.add(new GalleryImage(imgHQ, imgThumbnail, this));
			reloadImages();
		}
	}

	/**
	 * Removes the selected images.
	 * This method modifies swing components and should therefore only be run on the 
	 * event dispatch thread
	 */
	public void removeSelected() {
		// Use Iterator so array removal is safe 
		Iterator<GalleryImage> imagesIterator = images.iterator();
		while (imagesIterator.hasNext()) {
			GalleryImage image = imagesIterator.next();
			if (image.isSelected()) {
				// Remove from image array and content panel
				imagesIterator.remove();
				pnlContent.remove(image);
			}
		}
		reloadImages();
	}
	
	/**
	 * Exports all selected images to respective files in an export folder.
	 * Uses batch naming when appropriate (appends -# for each file).
	 * @param filename the filename
	 */
	public void exportSelected(String filename) {
		Boolean alwaysOverwrite = false;
		// Find all selected files
		ArrayList<GalleryImage> selected = getSelected();

		// If multiple selected files, toggle suffixing
		Boolean batchNaming;
		if (selected.size() > 1) {
			batchNaming = true;
		}
		else {
			batchNaming = false;
		}

		// Loop for all selected images
		for (int i=0; i < selected.size(); i++) {
			// Create appropriate path
			String path;
			if (batchNaming) {
				path = String.format("%s/%s-%d.png", EXPORT_DIR, filename, i+1);
			}
			else {
				path = String.format("%s/%s.png", EXPORT_DIR, filename);
			}

			// If path exists already, ask for overwrite permission
			File file = new File(path);
			boolean export = false;
			if (file.exists() && !alwaysOverwrite) {
				int res = new OverwriteMessageBox(path).showOptionDialog();
				// Overwrite for this image and future images
				if (res == OverwriteMessageBox.OPTION_YES_TO_ALL) {
					alwaysOverwrite = true;
					export = true;
				}
				// Overwrite for this image
				else if (res == OverwriteMessageBox.OPTION_YES) {
					export = true;
				}
				// Do not export, stop exporting images
				else if (res == OverwriteMessageBox.OPTION_CANCEL) {
					break;
				}
				// Default, do not export
			}
			else {
				export = true;
			}
			// Export image, creating path if required
			if (export) {
				file.getParentFile().mkdirs();
				selected.get(i).exportHQ(path);
			}	
		}		
	}

	/**
	 * Gets the selected images.
	 * @return The selected images
	 */
	private ArrayList<GalleryImage> getSelected() {	
		ArrayList<GalleryImage> selected = new ArrayList<GalleryImage>();
		for (GalleryImage image : images){
			if (image.isSelected()) {
				selected.add(image);
			}
		}
		return selected;
	}

	/**
	 * Checks if any images are selected.
	 *
	 * @return true, if any selected
	 */
	public boolean isAnySelected() {	
		if (getSelected().size() > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Deselect all images.
	 */
	public void deselectImages() {
		for (GalleryImage image : images){
			image.setSelected(false);
		}
	}

	/**
	 * Gets an images dimensions (Width & Height).
	 * @return The image dimensions
	 */
	public Dimension getImageDimensions() {
		return new Dimension(getImageWidth(), getImageHeight());
	}

	/**
	 * Calculates an images width using the height and ratio.
	 * @return The image width
	 */
	private int getImageWidth() {
		return (int)Math.round(getImageHeight() * imgRatio);    
	}

	/**
	 * Calculates an images height using gallery size.
	 * @return The image height
	 */
	private int getImageHeight() {
		return this.getSize().height - INDENT*2 - 
				scrContent.getHorizontalScrollBar().getHeight();
	}

	/**
	 * The Class OverwriteMessageBox.
	 * Used to show option dialogs with the options "Yes, No, Yes To All and Cancel".
	 */
	public class OverwriteMessageBox extends JOptionPane {
		// Return codes
		public static final int OPTION_YES = 0;
		public static final int OPTION_NO = 1;
		public static final int OPTION_YES_TO_ALL = 2;
		public static final int OPTION_CANCEL = 3;
		// Answerable Options
		private final Object[] options = {"Yes", "No", "Yes To All", "Cancel"};
		// Instance variables
		private String path;

		/**
		 * Instantiates a new overwrite message box.
		 * @param path The path to check
		 */
		public OverwriteMessageBox(String path) {
			this.path = path;
		}

		/**
		 * Show option dialog with presets.
		 * @return The selected option
		 */
		public int showOptionDialog() {
			return showOptionDialog(null,String.format(
					"'%s' exists.\n Do you wish to overwrite?", path) , "Overwrite", 
					JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, 
					null, options, options[3]);
		}
	}

}
