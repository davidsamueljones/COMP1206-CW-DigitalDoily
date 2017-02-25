import java.awt.EventQueue;
import java.awt.Graphics;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.border.TitledBorder;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.border.EtchedBorder;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSlider;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JSeparator;
import javax.swing.border.BevelBorder;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

public class Main {
	private DoilySettings settings;
	private static final int MAX_GALLERY_IMAGES=12;
	private JFrame frmDigitalDoily;
	private JLabel lblSectorCount;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Main window = new Main();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Main() {
		settings = new DoilySettings();
		settings.setSectors(3);
		settings.setShowSeperators(true);
		settings.setPenSize(10);
		settings.setPenColor(Color.RED);
		settings.setReflect(false);
		
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmDigitalDoily = new JFrame();
		frmDigitalDoily.setTitle("Digital Doily [dsj1n15]");
		frmDigitalDoily.setBounds(100, 100, 580, 504);
		frmDigitalDoily.setMinimumSize(new Dimension(580, 504));
		
		frmDigitalDoily.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 200, 0};
		gridBagLayout.rowHeights = new int[] {0, 200, 0};
		gridBagLayout.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		frmDigitalDoily.getContentPane().setLayout(gridBagLayout);
		
		DoilyPanel pnlDisplay = new DoilyPanel(settings);
		pnlDisplay.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		pnlDisplay.setBackground(Color.BLACK);
		GridBagConstraints gbc_pnlDisplay = new GridBagConstraints();
		gbc_pnlDisplay.insets = new Insets(5, 5, 5, 0);
		gbc_pnlDisplay.fill = GridBagConstraints.BOTH;
		gbc_pnlDisplay.gridx = 0;
		gbc_pnlDisplay.gridy = 0;
		frmDigitalDoily.getContentPane().add(pnlDisplay, gbc_pnlDisplay);
		
		GalleryPanel pnlGallery = new GalleryPanel(MAX_GALLERY_IMAGES);
		GridBagConstraints gbc_pnlGallery = new GridBagConstraints();
		gbc_pnlGallery.insets = new Insets(0, 5, 5, 0);
		gbc_pnlGallery.fill = GridBagConstraints.BOTH;
		gbc_pnlGallery.gridx = 0;
		gbc_pnlGallery.gridy = 1;
		frmDigitalDoily.getContentPane().add(pnlGallery, gbc_pnlGallery);
		
		JPanel pnlControl = new JPanel();
		GridBagConstraints gbc_pnlControl = new GridBagConstraints();
		gbc_pnlControl.gridheight = 2;
		gbc_pnlControl.insets = new Insets(5, 5, 0, 5);
		gbc_pnlControl.fill = GridBagConstraints.BOTH;
		gbc_pnlControl.gridx = 1;
		gbc_pnlControl.gridy = 0;
		frmDigitalDoily.getContentPane().add(pnlControl, gbc_pnlControl);
		GridBagLayout gbl_pnlControl = new GridBagLayout();
		gbl_pnlControl.columnWidths = new int[]{0, 0};
		gbl_pnlControl.rowHeights = new int[]{0, 0, 0, 0};
		gbl_pnlControl.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_pnlControl.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		pnlControl.setLayout(gbl_pnlControl);
		
		JPanel pnlDrawingControls = new JPanel();
		pnlDrawingControls.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Drawing Controls", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_pnlDrawingControls = new GridBagConstraints();
		gbc_pnlDrawingControls.insets = new Insets(5, 5, 5, 0);
		gbc_pnlDrawingControls.fill = GridBagConstraints.BOTH;
		gbc_pnlDrawingControls.gridx = 0;
		gbc_pnlDrawingControls.gridy = 0;
		pnlControl.add(pnlDrawingControls, gbc_pnlDrawingControls);
		GridBagLayout gbl_pnlDrawingControls = new GridBagLayout();
		gbl_pnlDrawingControls.columnWidths = new int[]{0, 0};
		gbl_pnlDrawingControls.rowHeights = new int[]{0, 0, 0, 0};
		gbl_pnlDrawingControls.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_pnlDrawingControls.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
		pnlDrawingControls.setLayout(gbl_pnlDrawingControls);
		
		JButton btnClear = new JButton("Clear");
		btnClear.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int reply = JOptionPane.showConfirmDialog(null, "Are you sure you want to clear your doily?", "Clear?",  JOptionPane.YES_NO_OPTION);
				if (reply == JOptionPane.YES_OPTION)
				{
					pnlDisplay.removeAllLines();
				}
				
			}
		});
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.gridwidth = 3;
		gbc_btnClear.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnClear.insets = new Insets(5, 5, 5, 0);
		gbc_btnClear.gridx = 0;
		gbc_btnClear.gridy = 0;
		pnlDrawingControls.add(btnClear, gbc_btnClear);
		
		JButton btnUndo = new JButton("Undo");
		btnUndo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pnlDisplay.removeLastLine();
			}
		});
		GridBagConstraints gbc_btnUndo = new GridBagConstraints();
		gbc_btnUndo.gridwidth = 3;
		gbc_btnUndo.insets = new Insets(0, 5, 5, 0);
		gbc_btnUndo.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnUndo.gridx = 0;
		gbc_btnUndo.gridy = 1;
		pnlDrawingControls.add(btnUndo, gbc_btnUndo);
		
		JSeparator seperator_1 = new JSeparator();
		GridBagConstraints gbc_seperator_1 = new GridBagConstraints();
		gbc_seperator_1.gridwidth = 3;
		gbc_seperator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_seperator_1.gridx = 0;
		gbc_seperator_1.gridy = 2;
		pnlDrawingControls.add(seperator_1, gbc_seperator_1);
		
		JLabel lblSectors = new JLabel("Sectors:");
		GridBagConstraints gbc_lblSectors = new GridBagConstraints();
		gbc_lblSectors.anchor = GridBagConstraints.WEST;
		gbc_lblSectors.insets = new Insets(5, 5, 5, 5);
		gbc_lblSectors.gridx = 0;
		gbc_lblSectors.gridy = 3;
		pnlDrawingControls.add(lblSectors, gbc_lblSectors);
		
		JSlider sldSectors = new JSlider();
		sldSectors.setMinimum(settings.MIN_SECTORS);
		sldSectors.setMaximum(settings.MAX_SECTORS);
		sldSectors.setValue(settings.getSectors());
		sldSectors.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				int sectors = sldSectors.getValue();
				settings.setSectors(sectors);
				lblSectorCount.setText(String.valueOf(sectors));
				pnlDisplay.repaint();
			}
		});
		GridBagConstraints gbc_sldSectors = new GridBagConstraints();
		gbc_sldSectors.insets = new Insets(5, 0, 5, 0);
		gbc_sldSectors.gridx = 1;
		gbc_sldSectors.gridy = 3;
		pnlDrawingControls.add(sldSectors, gbc_sldSectors);
		
		lblSectorCount = new JLabel() {
			@Override
			public void setText(String str) {
				super.setText(String.format("<html><div WIDTH=%d>%s</div><html>", 25, str));
			}
		};
		lblSectorCount.setText(String.valueOf(settings.getSectors()));
		GridBagConstraints gbc_lblSectorCount = new GridBagConstraints();
		gbc_lblSectorCount.anchor = GridBagConstraints.EAST;
		gbc_lblSectorCount.insets = new Insets(5, 0, 5, 5);
		gbc_lblSectorCount.gridx = 2;
		gbc_lblSectorCount.gridy = 3;
		pnlDrawingControls.add(lblSectorCount, gbc_lblSectorCount);
		
		JCheckBox chkShowSeperators = new JCheckBox("Show Seperators");
		chkShowSeperators.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setShowSeperators(chkShowSeperators.isSelected());
				pnlDisplay.repaint();
			}
		});
		chkShowSeperators.setSelected(settings.isShowSeperators());
		GridBagConstraints gbc_chkShowSeperators = new GridBagConstraints();
		gbc_chkShowSeperators.anchor = GridBagConstraints.WEST;
		gbc_chkShowSeperators.insets = new Insets(0, 0, 5, 0);
		gbc_chkShowSeperators.gridwidth = 3;
		gbc_chkShowSeperators.gridx = 0;
		gbc_chkShowSeperators.gridy = 4;
		pnlDrawingControls.add(chkShowSeperators, gbc_chkShowSeperators);
		
		JPanel pnlPenSettings = new JPanel();
		pnlPenSettings.setBorder(new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null), "Pen Settings", TitledBorder.LEADING, TitledBorder.TOP, null, new Color(0, 0, 0)));
		GridBagConstraints gbc_pnlPenSettings = new GridBagConstraints();
		gbc_pnlPenSettings.insets = new Insets(0, 5, 5, 0);
		gbc_pnlPenSettings.fill = GridBagConstraints.BOTH;
		gbc_pnlPenSettings.gridx = 0;
		gbc_pnlPenSettings.gridy = 1;
		pnlControl.add(pnlPenSettings, gbc_pnlPenSettings);
		GridBagLayout gbl_pnlPenSettings = new GridBagLayout();
		gbl_pnlPenSettings.columnWidths = new int[]{0, 0, 0};
		gbl_pnlPenSettings.rowHeights = new int[]{0, 0, 0, 0};
		gbl_pnlPenSettings.columnWeights = new double[]{0.0, 0.0, 1.0};
		gbl_pnlPenSettings.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0};
		pnlPenSettings.setLayout(gbl_pnlPenSettings);
		
		JLabel lblPenSize = new JLabel("Size:");
		GridBagConstraints gbc_lblPenSize = new GridBagConstraints();
		gbc_lblPenSize.anchor = GridBagConstraints.WEST;
		gbc_lblPenSize.insets = new Insets(5, 5, 5, 5);
		gbc_lblPenSize.gridx = 0;
		gbc_lblPenSize.gridy = 0;
		pnlPenSettings.add(lblPenSize, gbc_lblPenSize);
		
		JSlider sldPenSize = new JSlider();
		sldPenSize.setMinimum(settings.MIN_PEN_SIZE);
		sldPenSize.setMaximum(settings.MAX_PEN_SIZE);
		sldPenSize.setValue(settings.getPenSize());
		sldPenSize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setPenSize(sldPenSize.getValue());
			}
		});
		GridBagConstraints gbc_sldPenSize = new GridBagConstraints();
		gbc_sldPenSize.gridwidth = 2;
		gbc_sldPenSize.insets = new Insets(5, 0, 5, 0);
		gbc_sldPenSize.gridx = 1;
		gbc_sldPenSize.gridy = 0;
		pnlPenSettings.add(sldPenSize, gbc_sldPenSize);
		
		JButton btnSetColour = new JButton("Set Colour");
		btnSetColour.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				 settings.setPenColor(JColorChooser.showDialog(frmDigitalDoily, 
						 "Choose pen Color", settings.getPenColor()));
			}
		});
		GridBagConstraints gbc_btnSetColour = new GridBagConstraints();
		gbc_btnSetColour.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSetColour.gridwidth = 3;
		gbc_btnSetColour.insets = new Insets(0, 0, 5, 0);
		gbc_btnSetColour.gridx = 0;
		gbc_btnSetColour.gridy = 1;
		pnlPenSettings.add(btnSetColour, gbc_btnSetColour);
		
		JCheckBox chkReflect = new JCheckBox("Reflect Drawn Points");
		chkReflect.setSelected(settings.isReflect());
		chkReflect.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				settings.setReflect(chkReflect.isSelected());
			}
		});
		GridBagConstraints gbc_chkReflect = new GridBagConstraints();
		gbc_chkReflect.anchor = GridBagConstraints.WEST;
		gbc_chkReflect.gridwidth = 3;
		gbc_chkReflect.gridx = 0;
		gbc_chkReflect.gridy = 2;
		pnlPenSettings.add(chkReflect, gbc_chkReflect);
		
		JSeparator separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridwidth = 3;
		gbc_separator_2.insets = new Insets(0, 0, 5, 5);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 3;
		pnlPenSettings.add(separator_2, gbc_separator_2);
		
		JLabel lblPreview = new JLabel("Preview:");
		GridBagConstraints gbc_lblPreview = new GridBagConstraints();
		gbc_lblPreview.anchor = GridBagConstraints.WEST;
		gbc_lblPreview.gridwidth = 2;
		gbc_lblPreview.insets = new Insets(0, 5, 5, 5);
		gbc_lblPreview.gridx = 0;
		gbc_lblPreview.gridy = 4;
		pnlPenSettings.add(lblPreview, gbc_lblPreview);
		
		JPanel pnlPreview = new JPanel() {
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				//System.out.println(this.getSize());
			}
		};
		GridBagConstraints gbc_pnlPreview = new GridBagConstraints();
		gbc_pnlPreview.ipady = 25;
		gbc_pnlPreview.insets = new Insets(0, 0, 5, 5);
		gbc_pnlPreview.fill = GridBagConstraints.BOTH;
		gbc_pnlPreview.gridx = 2;
		gbc_pnlPreview.gridy = 4;
		pnlPenSettings.add(pnlPreview, gbc_pnlPreview);
		
		JPanel pnlGalleryControls = new JPanel();
		pnlGalleryControls.setBorder(new TitledBorder(null, "Gallery Controls", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		GridBagConstraints gbc_pnlGalleryControls = new GridBagConstraints();
		gbc_pnlGalleryControls.insets = new Insets(0, 5, 5, 5);
		gbc_pnlGalleryControls.fill = GridBagConstraints.BOTH;
		gbc_pnlGalleryControls.gridx = 0;
		gbc_pnlGalleryControls.gridy = 2;
		pnlControl.add(pnlGalleryControls, gbc_pnlGalleryControls);
		GridBagLayout gbl_pnlGalleryControls = new GridBagLayout();
		gbl_pnlGalleryControls.columnWidths = new int[] {0};
		gbl_pnlGalleryControls.rowHeights = new int[]{0, 0, 0};
		gbl_pnlGalleryControls.columnWeights = new double[]{1.0};
		gbl_pnlGalleryControls.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		pnlGalleryControls.setLayout(gbl_pnlGalleryControls);
		
		JButton btnSave = new JButton("Save To Gallery");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    BufferedImage image = pnlDisplay.getDoily(2000);
			    try {
					ImageIO.write(image, "PNG", new File("filename.png"));
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});
		GridBagConstraints gbc_btnSave = new GridBagConstraints();
		gbc_btnSave.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnSave.insets = new Insets(5, 5, 0, 5);
		gbc_btnSave.gridx = 0;
		gbc_btnSave.gridy = 0;
		pnlGalleryControls.add(btnSave, gbc_btnSave);
		
		JSeparator separator_3 = new JSeparator();
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 1;
		pnlGalleryControls.add(separator_3, gbc_separator_3);
		
		JButton btnRemove = new JButton("Remove Selected");
		GridBagConstraints gbc_btnRemove = new GridBagConstraints();
		gbc_btnRemove.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnRemove.insets = new Insets(0, 5, 5, 5);
		gbc_btnRemove.gridx = 0;
		gbc_btnRemove.gridy = 2;
		pnlGalleryControls.add(btnRemove, gbc_btnRemove);
		
		frmDigitalDoily.setVisible(true);
		pnlGallery.loadImages(1);
	}

}
