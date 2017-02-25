import javax.swing.JPanel;
import java.awt.Dimension;
import java.awt.GridLayout;
import javax.swing.JScrollPane;

public class GalleryPanel extends JPanel {
	private static final int INDENT=10;
	private static final int SPACING=10;
	
	JPanel pnlContent;
	JScrollPane scrollPane;
	
	int maxImgCount;
	
	public GalleryPanel(int maxImgCount) {
		// Set max number of images
		if (maxImgCount < 0) {
			throw new IllegalArgumentException("Gallery cannot have a negative image count maximum");
		}
		this.maxImgCount = maxImgCount;
		
		// Make objects fill panel
		this.setLayout(new GridLayout(1, 1));
		
		// Create pane for gallery
		pnlContent = new JPanel(null);
		// Make scrollable object of pnlContent
		scrollPane = new JScrollPane(pnlContent, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        this.add(scrollPane);
	}
	
	public void loadImages(double ratio) {
        // !!! Find images to determine actual, replace 'max'
		
		// Scale scroll area to pane size
		Dimension paneSize = this.getSize();
		
        // Calculate sizing
    	int imgHeight = paneSize.height - INDENT*2 - scrollPane.getHorizontalScrollBar().getHeight();
    	int imgWidth = (int)Math.round(imgHeight * ratio);    	
    	int reqWidth = INDENT*2+imgWidth*maxImgCount+SPACING*(maxImgCount-1);
		pnlContent.setPreferredSize(new Dimension(reqWidth, paneSize.height));
		
		// Draw gallery images 
        for (int i=0; i<maxImgCount; i++) {
        	GalleryImage image = new GalleryImage();
        	int x = INDENT+(imgWidth+SPACING)*i;
        	image.setBounds(x, INDENT, imgWidth, imgHeight);
        	pnlContent.add(image);
        }
        
        // Scale scroll area to pane size
		scrollPane.setPreferredSize(paneSize);
	}
	
}
