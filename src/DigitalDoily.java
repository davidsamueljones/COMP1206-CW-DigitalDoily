import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.BevelBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * DigitalDoily class. Handles main GUI for creating and managing doilys.
 * 
 * Digital Doily - COMP1206 Coursework
 * @author David Jones [dsj1n15]
 */
public class DigitalDoily extends JFrame {
	// GUI Defaults
	private static final String GUI_NAME = "Digital Doily [dsj1n15]";
	private static final int GUI_DEFAULT_DOILY_SIZE = 350; 
	private static final int GUI_CONTROLS_WIDTH = 323; 
	private static final int GUI_GALLERY_HEIGHT = 200;
	private static final double GUI_DEFAULT_SCALE = 1.5;
	private static final Dimension GUI_MINIMUM_SIZE = new Dimension(
			GUI_CONTROLS_WIDTH+GUI_DEFAULT_DOILY_SIZE, GUI_GALLERY_HEIGHT+GUI_DEFAULT_DOILY_SIZE);
	private static final Dimension GUI_START_SIZE = new Dimension(
			(int)Math.round(GUI_MINIMUM_SIZE.width*GUI_DEFAULT_SCALE), 
			(int)Math.round(GUI_MINIMUM_SIZE.height*GUI_DEFAULT_SCALE));

	// Gallery/Saving Defaults
	private static final int MAX_GALLERY_IMAGES = 12;
	private static final double GALLERY_IMG_RATIO = 1; // Ratio of Width->Height

	// Instance variables
	DoilyState doily;

	// GUI Setting Objects
	JSlider sldSectors;
	FixedWidthLabel lblSectorCount;
	JCheckBox chkShowSeparators;
	JCheckBox chkShowRings;
	JCheckBox chkUseImage;
	JCheckBox chkAntiAlias;
	JSlider sldPenScale;
	FixedWidthLabel lblPenScaleValue;
	JCheckBox chkReflect;
	JCheckBox chkCircleBound;
	JCheckBox chkInterpolate;

	/**
	 * Instantiates a new doily frame.
	 */
	public DigitalDoily() {
		// Explicit super() call
		super();	
		// Check if running on EDT
		if (!SwingUtilities.isEventDispatchThread()) {
			throw new IllegalThreadStateException("DigitalDoily not being loaded on event dispatch thread");
		}	

		// Set frame properties
		this.setTitle(GUI_NAME);
		this.setBounds(100, 100, GUI_START_SIZE.width, GUI_START_SIZE.height);
		this.setMinimumSize(GUI_MINIMUM_SIZE);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Setup layout and functionality of GUI elements
		initialise();

		// Show frame
		this.setVisible(true);
	}

	/**
	 * Method to setup layout and functionality of GUI elements.
	 * All component initialisation and placement is finalised before 
	 * attaching listeners and functionality.
	 */
	private void initialise() {	
		// ************************************************************************************
		// * GUI HANDLING
		// ************************************************************************************
		// All code pertaining to positioning and layout of GUI elements

		// Set frame to use grid bag layout (Growing main panel, fixed width bottom and side panels)
		GridBagLayout gblFrame = new GridBagLayout();
		gblFrame.columnWidths = new int[] {0, GUI_CONTROLS_WIDTH, 0};
		gblFrame.rowHeights = new int[] {0, GUI_GALLERY_HEIGHT, 0};
		gblFrame.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gblFrame.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		this.getContentPane().setLayout(gblFrame);

		// -------------------------------------------------------------------------------------

		// [Main Panel] <- 'Display' DoilyPanel
		DoilyPanel pnlDisplay = new DoilyPanel();
		pnlDisplay.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlDisplay.setBackground(Color.BLACK);
		GridBagConstraints gbc_pnlDisplay = new GridBagConstraints();
		gbc_pnlDisplay.insets = new Insets(5, 5, 5, 0);
		gbc_pnlDisplay.fill = GridBagConstraints.BOTH;
		gbc_pnlDisplay.gridx = 0;
		gbc_pnlDisplay.gridy = 0;
		this.getContentPane().add(pnlDisplay, gbc_pnlDisplay);

		// -------------------------------------------------------------------------------------

		// [Main Panel] <- 'Gallery' GalleryPanel
		GalleryPanel pnlGallery = new GalleryPanel(MAX_GALLERY_IMAGES, GALLERY_IMG_RATIO);
		GridBagConstraints gbc_pnlGallery = new GridBagConstraints();
		gbc_pnlGallery.insets = new Insets(0, 5, 5, 0);
		gbc_pnlGallery.fill = GridBagConstraints.BOTH;
		gbc_pnlGallery.gridx = 0;
		gbc_pnlGallery.gridy = 1;
		this.getContentPane().add(pnlGallery, gbc_pnlGallery);

		// -------------------------------------------------------------------------------------

		// [Main Panel] <- 'Control' JPanel
		JPanel pnlControl = new JPanel();
		JScrollPane scrControl = new JScrollPane(pnlControl, 
				ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scrControl.setPreferredSize(new Dimension(0,0));
		GridBagConstraints gbc_pnlControl = new GridBagConstraints();
		gbc_pnlControl.gridheight = 2;
		gbc_pnlControl.insets = new Insets(5, 5, 5, 5);
		gbc_pnlControl.fill = GridBagConstraints.BOTH;
		gbc_pnlControl.gridx = 1;
		gbc_pnlControl.gridy = 0;
		this.getContentPane().add(scrControl, gbc_pnlControl);
		GridBagLayout gbl_pnlControl = new GridBagLayout();
		gbl_pnlControl.rowHeights = new int[]{0, 0, 0, 0};
		gbl_pnlControl.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		pnlControl.setLayout(gbl_pnlControl);

		// ---------------------------------------

		// [Side Panel] <- 'Drawing Controls' panel
		JPanel pnlDrawingControls = new JPanel();
		pnlDrawingControls.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), 
				"Drawing Controls", TitledBorder.LEADING, TitledBorder.TOP));
		GridBagConstraints gbc_pnlDrawingControls = new GridBagConstraints();
		gbc_pnlDrawingControls.insets = new Insets(5, 5, 5, 5);
		gbc_pnlDrawingControls.fill = GridBagConstraints.BOTH;
		gbc_pnlDrawingControls.gridx = 0;
		gbc_pnlDrawingControls.gridy = 0;
		pnlControl.add(pnlDrawingControls, gbc_pnlDrawingControls);
		GridBagLayout gbl_pnlDrawingControls = new GridBagLayout();
		gbl_pnlDrawingControls.columnWeights = new double[]{0.5, 0.5};
		pnlDrawingControls.setLayout(gbl_pnlDrawingControls);

		// [Drawing Controls] <- 'Clear' Button
		JButton btnClear = new JButton("Clear");
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.gridwidth = 2;
		gbc_btnClear.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnClear.insets = new Insets(5, 5, 5, 5);
		gbc_btnClear.gridx = 0;
		gbc_btnClear.gridy = 0;
		pnlDrawingControls.add(btnClear, gbc_btnClear);

		// [Drawing Controls] <- 'Undo & Redo' Buttons
		JPanel pnlUndoRedo = new JPanel();
		GridBagConstraints gbc_pnlUndoRedo = new GridBagConstraints();
		gbc_pnlUndoRedo.gridwidth = 2;
		gbc_pnlUndoRedo.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlUndoRedo.insets = new Insets(0, 5, 5, 5);
		gbc_pnlUndoRedo.gridx = 0;
		gbc_pnlUndoRedo.gridy = 1;
		pnlDrawingControls.add(pnlUndoRedo, gbc_pnlUndoRedo);
		GridBagLayout gbl_pnlUndoRedo = new GridBagLayout();
		gbl_pnlUndoRedo.columnWeights = new double[]{0.5, 0.5};
		pnlUndoRedo.setLayout(gbl_pnlUndoRedo);

		JButton btnUndo = new JButton("Undo");
		GridBagConstraints gbc_btnUndo = new GridBagConstraints();
		gbc_btnUndo.insets = new Insets(0, 0, 0, 2);
		gbc_btnUndo.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnUndo.gridx = 0;
		gbc_btnUndo.gridy = 0;
		pnlUndoRedo.add(btnUndo, gbc_btnUndo);

		JButton btnRedo = new JButton("Redo");
		GridBagConstraints gbc_btnRedo = new GridBagConstraints();
		gbc_btnRedo.insets = new Insets(0, 2, 0, 0);
		gbc_btnRedo.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRedo.gridx = 1;
		gbc_btnRedo.gridy = 0;
		pnlUndoRedo.add(btnRedo, gbc_btnRedo);

		// [Drawing Controls] <- Separator
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridwidth = 2;
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 2;
		pnlDrawingControls.add(separator_1, gbc_separator_1);

		// [Drawing Controls] <- 'Sectors' Panel
		JPanel pnlSectors = new JPanel();
		GridBagConstraints gbc_pnlSectors = new GridBagConstraints();
		gbc_pnlSectors.anchor = GridBagConstraints.WEST;
		gbc_pnlSectors.gridwidth = 2;
		gbc_pnlSectors.insets = new Insets(0, 5, 5, 5);
		gbc_pnlSectors.gridx = 0;
		gbc_pnlSectors.gridy = 3;
		pnlDrawingControls.add(pnlSectors, gbc_pnlSectors);
		GridBagLayout gbl_pnlSectors = new GridBagLayout();
		pnlSectors.setLayout(gbl_pnlSectors);

		JLabel lblSectors = new JLabel();
		lblSectors.setText("Sectors:");
		GridBagConstraints gbc_lblSectors = new GridBagConstraints();
		gbc_lblSectors.anchor = GridBagConstraints.WEST;
		gbc_lblSectors.gridx = 0;
		gbc_lblSectors.gridy = 0;
		pnlSectors.add(lblSectors, gbc_lblSectors);

		sldSectors = new JSlider();
		sldSectors.setMinimum(DoilySettings.MIN_SECTORS);
		sldSectors.setMaximum(DoilySettings.MAX_SECTORS);
		sldSectors.setPaintTicks(true);
		sldSectors.setPaintLabels(true);
		sldSectors.setMajorTickSpacing(DoilySettings.MAX_SECTORS-DoilySettings.MIN_SECTORS);
		sldSectors.setMinorTickSpacing(10);
		GridBagConstraints gbc_sldSectors = new GridBagConstraints();
		gbc_sldSectors.gridx = 1;
		gbc_sldSectors.gridy = 0;
		pnlSectors.add(sldSectors, gbc_sldSectors);

		// Displays the current sector count
		lblSectorCount = new FixedWidthLabel(25);
		GridBagConstraints gbc_lblSectorCount = new GridBagConstraints();
		gbc_lblSectorCount.anchor = GridBagConstraints.EAST;
		gbc_lblSectorCount.gridx = 2;
		gbc_lblSectorCount.gridy = 0;
		pnlSectors.add(lblSectorCount, gbc_lblSectorCount);

		// [Drawing Controls] <- 'Show Separators' Check Box
		chkShowSeparators = new JCheckBox("Show Separators");
		GridBagConstraints gbc_chkShowSeparators = new GridBagConstraints();
		gbc_chkShowSeparators.anchor = GridBagConstraints.WEST;
		gbc_chkShowSeparators.insets = new Insets(0, 5, 5, 0);
		gbc_chkShowSeparators.gridx = 0;
		gbc_chkShowSeparators.gridy = 4;
		pnlDrawingControls.add(chkShowSeparators, gbc_chkShowSeparators);

		// [Drawing Controls] <- 'Show Rings' Check Box
		chkShowRings = new JCheckBox("Show Rings");
		GridBagConstraints gbc_chkShowRings = new GridBagConstraints();
		gbc_chkShowRings.anchor = GridBagConstraints.WEST;
		gbc_chkShowRings.insets = new Insets(0, 0, 5, 0);
		gbc_chkShowRings.gridx = 1;
		gbc_chkShowRings.gridy = 4;
		pnlDrawingControls.add(chkShowRings, gbc_chkShowRings);

		// [Drawing Controls] <- Separator
		JSeparator separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.insets = new Insets(0, 5, 5, 5);
		gbc_separator_2.gridwidth = 2;
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 5;
		pnlDrawingControls.add(separator_2, gbc_separator_2);

		// [Drawing Controls] <- 'Use Image' Check Box
		chkUseImage = new JCheckBox("Render as Image");
		GridBagConstraints gbc_chkUseImage = new GridBagConstraints();
		gbc_chkUseImage.anchor = GridBagConstraints.WEST;
		gbc_chkUseImage.insets = new Insets(0, 5, 5, 0);
		gbc_chkUseImage.gridx = 0;
		gbc_chkUseImage.gridy = 6;
		pnlDrawingControls.add(chkUseImage, gbc_chkUseImage);

		// [Drawing Controls] <- 'Anti-Alias' Check Box
		chkAntiAlias = new JCheckBox("Anti-Alias");
		GridBagConstraints gbc_chkAntiAlias = new GridBagConstraints();
		gbc_chkAntiAlias.anchor = GridBagConstraints.WEST;
		gbc_chkAntiAlias.insets = new Insets(0, 0, 5, 0);
		gbc_chkAntiAlias.gridx = 1;
		gbc_chkAntiAlias.gridy = 6;
		pnlDrawingControls.add(chkAntiAlias, gbc_chkAntiAlias);

		// ---------------------------------------

		// [Side Panel] <- 'Pen Settings' panel
		JPanel pnlPenSettings = new JPanel();
		pnlPenSettings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), 
				"Pen Settings", TitledBorder.LEADING, TitledBorder.TOP));
		GridBagConstraints gbc_pnlPenSettings = new GridBagConstraints();
		gbc_pnlPenSettings.insets = new Insets(0, 5, 5, 5);
		gbc_pnlPenSettings.fill = GridBagConstraints.BOTH;
		gbc_pnlPenSettings.gridx = 0;
		gbc_pnlPenSettings.gridy = 1;
		pnlControl.add(pnlPenSettings, gbc_pnlPenSettings);
		GridBagLayout gbl_pnlPenSettings = new GridBagLayout();
		gbl_pnlPenSettings.columnWeights = new double[]{0.5, 0.5};
		pnlPenSettings.setLayout(gbl_pnlPenSettings);

		// [Pen Settings] <- 'Pen Scale' Panel
		JPanel pnlPenScale = new JPanel();
		GridBagConstraints gbc_pnlPenScale = new GridBagConstraints();
		gbc_pnlPenScale.anchor = GridBagConstraints.WEST;
		gbc_pnlPenScale.gridwidth = 2;
		gbc_pnlPenScale.insets = new Insets(0, 5, 5, 5);
		gbc_pnlPenScale.gridx = 0;
		gbc_pnlPenScale.gridy = 0;
		pnlPenSettings.add(pnlPenScale, gbc_pnlPenScale);
		GridBagLayout gbl_pnlPenScale = new GridBagLayout();
		pnlPenScale.setLayout(gbl_pnlPenScale);

		JLabel lblPenScale = new JLabel("Scale:   ");
		GridBagConstraints gbc_lblPenScale = new GridBagConstraints();
		gbc_lblPenScale.anchor = GridBagConstraints.WEST;
		gbc_lblPenScale.gridx = 0;
		gbc_lblPenScale.gridy = 0;
		pnlPenScale.add(lblPenScale, gbc_lblPenScale);

		sldPenScale = new JSlider();
		sldPenScale.setMinimum(DoilySettings.MIN_PEN_SIZE);
		sldPenScale.setMaximum(DoilySettings.MAX_PEN_SIZE);
		sldPenScale.setPaintTicks(true);
		sldPenScale.setPaintLabels(true);
		sldPenScale.setMajorTickSpacing(DoilySettings.MAX_PEN_SIZE-DoilySettings.MIN_PEN_SIZE);
		sldPenScale.setMinorTickSpacing(10);
		GridBagConstraints gbc_sldPenScale = new GridBagConstraints();
		gbc_sldPenScale.gridx = 1;
		gbc_sldPenScale.gridy = 0;
		pnlPenScale.add(sldPenScale, gbc_sldPenScale);

		// Displays the current pen scale
		lblPenScaleValue = new FixedWidthLabel(25);
		GridBagConstraints gbc_lblPenSize = new GridBagConstraints();
		gbc_lblPenSize.anchor = GridBagConstraints.EAST;
		gbc_lblPenSize.gridx = 2;
		gbc_lblPenSize.gridy = 0;
		pnlPenScale.add(lblPenScaleValue, gbc_lblPenSize);

		// [Pen Settings] <- 'Pen Colour' Button 
		JButton btnSetColour = new JButton("Set Colour");
		GridBagConstraints gbc_btnSetColour = new GridBagConstraints();
		gbc_btnSetColour.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSetColour.gridwidth = 2;
		gbc_btnSetColour.insets = new Insets(0, 5, 5, 5);
		gbc_btnSetColour.gridx = 0;
		gbc_btnSetColour.gridy = 1;
		pnlPenSettings.add(btnSetColour, gbc_btnSetColour);

		// [Pen Settings] <- 'Reflect Points' Check Box
		chkReflect = new JCheckBox("Reflect Points");
		GridBagConstraints gbc_chkReflect = new GridBagConstraints();
		gbc_chkReflect.anchor = GridBagConstraints.WEST;
		gbc_chkReflect.insets = new Insets(0, 5, 5, 5);
		gbc_chkReflect.gridwidth = 2;
		gbc_chkReflect.gridx = 0;
		gbc_chkReflect.gridy = 2;
		pnlPenSettings.add(chkReflect, gbc_chkReflect);

		// [Pen Settings] <- 'Bind to Circle' Check Box
		chkCircleBound = new JCheckBox("Bind to Circle");
		GridBagConstraints gbc_chkCircleBound = new GridBagConstraints();
		gbc_chkCircleBound.anchor = GridBagConstraints.WEST;
		gbc_chkCircleBound.insets = new Insets(0, 5, 5, 5);
		gbc_chkCircleBound.gridx = 0;
		gbc_chkCircleBound.gridy = 3;
		pnlPenSettings.add(chkCircleBound, gbc_chkCircleBound);

		// [Pen Settings] <- 'Interpolate' Check Box
		chkInterpolate = new JCheckBox("Interpolate");
		GridBagConstraints gbc_chkInterpolate = new GridBagConstraints();
		gbc_chkInterpolate.anchor = GridBagConstraints.WEST;
		gbc_chkInterpolate.fill = GridBagConstraints.HORIZONTAL;
		gbc_chkInterpolate.insets = new Insets(0, 0, 5, 0);
		gbc_chkInterpolate.gridx = 1;
		gbc_chkInterpolate.gridy = 3;
		pnlPenSettings.add(chkInterpolate, gbc_chkInterpolate);

		// [Pen Settings] <- Separator
		JSeparator separator_3 = new JSeparator();
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_3.gridwidth = 2;
		gbc_separator_3.insets = new Insets(0, 5, 5, 5);
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 4;
		pnlPenSettings.add(separator_3, gbc_separator_3);

		// [Pen Settings] <- 'Preview' Panel
		JPanel pnlPreviewHolder = new JPanel();
		GridBagConstraints gbc_pnlPreviewHolder = new GridBagConstraints();
		gbc_pnlPreviewHolder.anchor = GridBagConstraints.WEST;
		gbc_pnlPreviewHolder.gridwidth = 2;
		gbc_pnlPreviewHolder.insets = new Insets(0, 5, 5, 5);
		gbc_pnlPreviewHolder.fill = GridBagConstraints.HORIZONTAL;
		gbc_pnlPreviewHolder.gridx = 0;
		gbc_pnlPreviewHolder.gridy = 5;
		pnlPenSettings.add(pnlPreviewHolder, gbc_pnlPreviewHolder);
		GridBagLayout gbl_pnlPreviewHolder = new GridBagLayout();
		gbl_pnlPreviewHolder.columnWeights = new double[]{0.0, 1.0};
		pnlPreviewHolder.setLayout(gbl_pnlPreviewHolder);

		JLabel lblPreview = new JLabel("Preview:");
		GridBagConstraints gbc_lblPreview = new GridBagConstraints();
		gbc_lblPreview.anchor = GridBagConstraints.WEST;
		gbc_lblPreview.insets = new Insets(0, 0, 0, 5);
		gbc_lblPreview.gridx = 0;
		gbc_lblPreview.gridy = 0;
		pnlPreviewHolder.add(lblPreview, gbc_lblPreview);

		JPanel pnlPreview = new JPanel() {
			/* Draws an oval using current settings to indicate pen */
			@Override
			public void paintComponent(Graphics gr) {
				super.paintComponent(gr);
				Graphics2D g = (Graphics2D) gr;
				// Get pen properties
				int size = DoilyUtilities.getPenSize(doily.settings.getPenScale(), pnlDisplay.getSize());
				g.setColor(doily.settings.getPenColor());
				g.setRenderingHints(new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
						RenderingHints.VALUE_ANTIALIAS_ON));
				// Draw oval
				g.fillOval(10, (this.getHeight()-size)/2, size, size);
			}
		};
		GridBagConstraints gbc_pnlPreview = new GridBagConstraints();
		gbc_pnlPreview.ipady = 50; // preview box height
		gbc_pnlPreview.fill = GridBagConstraints.BOTH;
		gbc_pnlPreview.gridx = 1;
		gbc_pnlPreview.gridy = 0;
		pnlPreviewHolder.add(pnlPreview, gbc_pnlPreview);

		// ---------------------------------------

		// [Side Panel] <- 'Gallery Controls' panel
		JPanel pnlGalleryControls = new JPanel();
		pnlGalleryControls.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), 
				"Gallery Controls", TitledBorder.LEADING, TitledBorder.TOP));
		GridBagConstraints gbc_pnlGalleryControls = new GridBagConstraints();
		gbc_pnlGalleryControls.insets = new Insets(0, 5, 0, 5);
		gbc_pnlGalleryControls.fill = GridBagConstraints.BOTH;
		gbc_pnlGalleryControls.gridx = 0;
		gbc_pnlGalleryControls.gridy = 2;
		pnlControl.add(pnlGalleryControls, gbc_pnlGalleryControls);
		GridBagLayout gbl_pnlGalleryControls = new GridBagLayout();
		gbl_pnlGalleryControls.columnWeights = new double[]{0.5, 0.5};
		pnlGalleryControls.setLayout(gbl_pnlGalleryControls);

		// [Gallery Controls] <- 'Save' Button	
		JButton btnSave = new JButton("Save to Gallery");
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.gridwidth = 2;
		gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave.insets = new Insets(5, 5, 5, 5);
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 0;
		pnlGalleryControls.add(btnSave, gbc_btnSave);

		// [Gallery Controls] <- 'Load' Button
		JButton btnLoadSelected = new JButton("Load");
		GridBagConstraints gbc_btnLoadSelected = new GridBagConstraints();
		gbc_btnLoadSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnLoadSelected.insets = new Insets(0, 5, 5, 2);
		gbc_btnLoadSelected.gridx = 0;
		gbc_btnLoadSelected.gridy = 1;
		pnlGalleryControls.add(btnLoadSelected, gbc_btnLoadSelected);

		// [Gallery Controls] <- 'Export' Button
		JButton btnExportSelected = new JButton("Export");
		GridBagConstraints gbc_btnExportSelected = new GridBagConstraints();
		gbc_btnExportSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnExportSelected.insets = new Insets(0, 2, 5, 5);
		gbc_btnExportSelected.gridx = 1;
		gbc_btnExportSelected.gridy = 1;
		pnlGalleryControls.add(btnExportSelected, gbc_btnExportSelected);

		// [Gallery Controls] <- 'Remove' Button
		JButton btnRemoveSelected = new JButton("Remove");
		GridBagConstraints gbc_btnRemoveSelected = new GridBagConstraints();
		gbc_btnRemoveSelected.gridwidth = 2;
		gbc_btnRemoveSelected.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemoveSelected.insets = new Insets(0, 5, 5, 5);
		gbc_btnRemoveSelected.gridx = 0;
		gbc_btnRemoveSelected.gridy = 2;
		pnlGalleryControls.add(btnRemoveSelected, gbc_btnRemoveSelected);

		// -------------------------------------------------------------------------------------

		// Get setup
		doily = pnlDisplay.getDoily();
		update();

		// ------------------------------------------------------------------------------------

		// ************************************************************************************
		// * EVENT HANDLING
		// ************************************************************************************
		// All code pertaining to listeners and events

		// [Clear Button]
		// Clear the display panel
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Confirm before clearing
				int res = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear your doily?", 
						"Clear?",  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (res == JOptionPane.YES_OPTION)
				{
					pnlDisplay.clear();
				}			
			}
		});

		// [Undo Button]
		// Undo the last action in the display panel
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnlDisplay.undo();
			}
		});

		// [Redo Button]
		// Redo the last undo in the display panel
		btnRedo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnlDisplay.redo();
			}
		});

		// [Sector Slider]
		// Update Sector Count settings value
		sldSectors.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int sectors = sldSectors.getValue();
				doily.settings.setSectors(sectors);
				lblSectorCount.setText(String.valueOf(sectors));
				pnlDisplay.redraw();
			}
		});

		// [Show Separators Check Box]
		// Update Show Separator settings value
		chkShowSeparators.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doily.settings.setShowSeparators(chkShowSeparators.isSelected());
				pnlDisplay.redraw();
			}
		});

		// [Show Rings Check Box]
		// Update Show Rings settings value
		chkShowRings.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doily.settings.setShowRings(chkShowRings.isSelected());
				pnlDisplay.redraw();
			}
		});

		// [Use Image Check Box]
		// Update Use Image settings value
		chkUseImage.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doily.settings.setUseImage(chkUseImage.isSelected());
				pnlDisplay.redraw();
			}
		});

		// [Anti-Alias Check Box]
		// Update Anti-Aliasing settings value
		chkAntiAlias.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doily.settings.setAntiAlias(chkAntiAlias.isSelected());
				pnlDisplay.redraw();
			}
		});

		// [Pen Scale Slider]
		// Update Pen scale settings value
		sldPenScale.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int penScale = sldPenScale.getValue();
				doily.settings.setPenScale(penScale);
				lblPenScaleValue.setText(String.valueOf(penScale));
				pnlPreview.repaint();
			}
		});

		// [Set Colour Button]
		// Update Pen Colour settings value
		btnSetColour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Use JColorChooser, null returned on cancel
				Color color = JColorChooser.showDialog(null, 
						"Choose pen Color", doily.settings.getPenColor());
				if (color != null) {
					doily.settings.setPenColor(color);
					pnlPreview.repaint();
				}
			}
		});

		// [Reflect Check Box]
		// Update Pen Reflect settings value
		chkReflect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doily.settings.setReflect(chkReflect.isSelected());
			}
		});

		// [Circle Bound Check Box]
		// Update Pen Circle Bound settings value
		chkCircleBound.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doily.settings.setCircleBounded(chkCircleBound.isSelected());
			}
		});

		// [Interpolate Check Box]
		// Update Interpolate settings value
		chkInterpolate.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				doily.settings.setInterpolate(chkInterpolate.isSelected());
			}
		});


		// [Save Button]
		// Save the display to the gallery
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Add the image to the gallery
				pnlGallery.addImage(pnlDisplay.getDoily().clone());
			}
		});

		// [Load Button]
		// Loads a selected item from the gallery
		btnLoadSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Error if no items or more than one are selected
				ArrayList<GalleryImage> selected = pnlGallery.getSelected();
				if (selected.size() != 1) {
					JOptionPane.showMessageDialog(null,
							"Load Failed - Exactly one gallery image must be selected", 
							"Load Selected", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// Get confirmation from the user
				int res = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to load the selected gallery image?", 
						"Load Selected",  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (res == JOptionPane.YES_OPTION)
				{
					// Trigger load
					doily = selected.get(0).getDoily().clone();
					pnlDisplay.setDoily(doily);
					update();
				}
			}
		});

		// [Export Button]
		// Export the selected items to the 'desktop'
		btnExportSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Error if no items are selected
				if (!pnlGallery.isAnySelected()) {
					JOptionPane.showMessageDialog(null,
							"Export Failed - No gallery images selected", 
							"Export Selected", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// Get a filename from the user, null if cancelled, error on empty string
				String filename = JOptionPane.showInputDialog("Please input the export filename: ");
				if (filename != null) {
					if (filename.equals("")) {
						JOptionPane.showMessageDialog(null,
								"Export Failed - No export filename provided", 
								"Export Selected", JOptionPane.ERROR_MESSAGE);
					}
					else {
						// Trigger export
						pnlGallery.exportSelected(filename);
					}
				}
			}
		});

		// [Remove Button]
		// Remove the selected items from the gallery
		btnRemoveSelected.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				// Error if no items are selected
				if (!pnlGallery.isAnySelected()) {
					JOptionPane.showMessageDialog(null,
							"Remove Failed - No gallery images selected", 
							"Remove Selected", JOptionPane.ERROR_MESSAGE);
					return;
				}
				// Get confirmation from the user
				int res = JOptionPane.showConfirmDialog(null, 
						"Are you sure you want to remove the selected gallery images?", 
						"Remove Selected",  JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (res == JOptionPane.YES_OPTION)
				{
					// Trigger removal
					pnlGallery.removeSelected();
				}
			}
		});
	}

	/**
	 * Update values of GUI setting objects 
	 */
	private void update() {
		sldSectors.setValue(doily.settings.getSectors());
		lblSectorCount.setText(String.valueOf(doily.settings.getSectors()));
		chkShowSeparators.setSelected(doily.settings.isShowSeparators());
		chkShowRings.setSelected(doily.settings.isShowRings());
		chkUseImage.setSelected(doily.settings.isUseImage());
		chkAntiAlias.setSelected(doily.settings.isAntiAlias());
		sldPenScale.setValue(doily.settings.getPenScale());
		lblPenScaleValue.setText(String.valueOf(doily.settings.getPenScale()));
		chkReflect.setSelected(doily.settings.isReflect());
		chkCircleBound.setSelected(doily.settings.isCircleBounded());
		chkInterpolate.setSelected(doily.settings.isInterpolate());
	}

	/**
	 * Inner class defined so strings can be displayed in a fixed width.
	 */
	class FixedWidthLabel extends JLabel {
		private int width;

		/**
		 * Constructor for FixedWidthLabel
		 * @param width Width of string
		 */
		public FixedWidthLabel(int width) {
			this.width = width;
		}

		@Override
		public void setText(String str) {
			super.setText(String.format("<html><div WIDTH=%d>%s</div><html>", width, str));
		}

	}

}