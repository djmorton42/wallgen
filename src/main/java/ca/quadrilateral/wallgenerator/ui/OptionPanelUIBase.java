/*
Copyright 2011 Daniel Morton. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY Daniel Morton ''AS IS'' AND ANY EXPRESS OR IMPLIED
WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Daniel Morton OR
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

The views and conclusions contained in the software and documentation are those of the
authors and should not be interpreted as representing official policies, either expressed
or implied, of Daniel Morton.
*/

package ca.quadrilateral.wallgenerator.ui;

import info.clearthought.layout.TableLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.LayoutManager;
import java.awt.RenderingHints;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.InputVerifier;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.WallGenerator;

import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;

public abstract class OptionPanelUIBase extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(OptionPanelUIBase.class);
	
	private static final long serialVersionUID = 1L;
	
	protected double[][] layoutDescriptor = null;
    protected TableLayout layout = null;

    protected JButton previewWallsButton = new JButton("Preview Walls");
    protected JButton generateWallsButton = new JButton("Generate Walls");
    protected JButton savePresetsButton = new JButton("Save Presets");
    protected JButton loadPresetsButton = new JButton("Load Presets");

    protected JSpinner nearByDarkeningSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01));
    protected JSpinner farAwayDarkeningSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01));

    protected JButton changeBackgroundColorButton = new JButton();
    protected JButton changeTransparencyColorButton = new JButton();
    protected JButton changeTextColorButton = new JButton();
    protected JButton changeBorderColorButton = new JButton();
    protected JButton changeDistanceBlendColorButton = new JButton();
    
    protected JPanel selectedBackgroundColorPanel = new JPanel();
    protected JPanel selectedTransparencyColorPanel = new JPanel();
    protected JPanel selectedBorderColorPanel = new JPanel();
    protected JPanel selectedTextColorPanel = new JPanel();
    protected JPanel selectedDistanceBlendColorPanel = new JPanel();
    
    protected JRadioButton useTransparentColorRadioButton = new JRadioButton();
    protected JRadioButton useTransparencyRadioButton = new JRadioButton("Transparent");
    
    protected ButtonGroup transparencyRadioButtonGroup = new ButtonGroup();
    
    protected JComboBox scalingMethodComboBox = new JComboBox();
    protected JComboBox unsharpMaskComboBox = new JComboBox();
    protected JComboBox processOrderComboBox = new JComboBox(new Object[] {"Unsharp then Soften", "Soften then Unsharp"});
    protected JSpinner softeningSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 1, 0.01));
    
    
    protected JComboBox resolutionCombo = new JComboBox();
    protected JComboBox outputFileFormatCombo = new JComboBox();
    protected JComboBox outputFileTypeCombo = new JComboBox();
    
    protected JPanel basicOptionsPanel = new JPanel();
    protected JPanel colorPanel = new JPanel();
    protected JPanel processingPanel = new JPanel();
    protected JPanel buttonPanel = new JPanel();

    public OptionPanelUIBase() {
    	super();
    	this.setupGUI();
    }
    
    public void setupGUI() {    	
    	this.setLayout(createLayout());
    	
    	this.setupButtonPanel();
    	
    	this.placeComponents();
    	this.setBorder(BorderFactory.createEtchedBorder());
    	
    	setupBasicOptionsPanel();
    	setupColorPanel();
    	setupProcessingPanel();
    }
    
    private final TableLayout createLayout() {
        this.layoutDescriptor = new double[][] {
                {   3, TableLayout.PREFERRED, 
                	10, TableLayout.PREFERRED, 
                	10, TableLayout.PREFERRED, 
                	10, TableLayout.PREFERRED, 
                	10, TableLayout.PREFERRED, 
                	3, TableLayout.FILL, 
                	3, TableLayout.PREFERRED, 3},
                {3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3}
        };
        this.layout = new TableLayout(this.layoutDescriptor);
        return this.layout;
    }
    
    private void setupButtonPanel() {
    	final double[][] layoutDescriptor = new double[][] {{TableLayout.FILL, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 3},{3, TableLayout.PREFERRED, 3}};
    	final TableLayout layout = new TableLayout(layoutDescriptor);
    	this.buttonPanel.setLayout(layout);
    	
    	this.buttonPanel.add(this.savePresetsButton, "1, 1");
    	this.buttonPanel.add(this.loadPresetsButton, "3, 1");
    	this.buttonPanel.add(this.previewWallsButton, "5, 1");
    	this.buttonPanel.add(this.generateWallsButton, "7, 1");
    }

    private void setupBasicOptionsPanel() {
    	final double[][] layoutDescriptor = new double[][] {
    			{3, TableLayout.PREFERRED, 5, TableLayout.PREFERRED, 3},
    			{3, TableLayout.PREFERRED, 5, 
    				TableLayout.PREFERRED, 5, 
    				TableLayout.PREFERRED, 5,
    				TableLayout.PREFERRED, 
    			3}};
    	final LayoutManager layout = new TableLayout(layoutDescriptor);
    	basicOptionsPanel.setLayout(layout);
    	basicOptionsPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    	
    	basicOptionsPanel.add(new JLabel("Basic Options"), "1, 1, 3, 1, c, f");
    	    	
    	basicOptionsPanel.add(new JLabel("Resolution:"), "1, 3");
    	basicOptionsPanel.add(resolutionCombo, "3, 3");    	    	
    	
    	basicOptionsPanel.add(new JLabel("Output Format:"), "1, 5");
    	basicOptionsPanel.add(outputFileFormatCombo, "3, 5");
    	
    	basicOptionsPanel.add(new JLabel("Output File Type:"), "1, 7");
    	basicOptionsPanel.add(outputFileTypeCombo, "3, 7");
    }

    protected void setupColorPanel() {
    	colorPanel.setLayout(new BorderLayout());
    	colorPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    	
    	final double[][] colorPanelLayoutDescriptor = new double[][] {
    			{3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, 50, 3, TableLayout.PREFERRED, 3}, 
    			{3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3}
    		};
    	
    	final TableLayout colorPanelLayout = new TableLayout(colorPanelLayoutDescriptor);
    	
    	final JPanel optionSubPanel = new JPanel(colorPanelLayout);
    	
    	final JLabel titleLabel = new JLabel("Colors");
    	titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	
    	colorPanel.add(titleLabel, BorderLayout.NORTH);
    	colorPanel.add(optionSubPanel, BorderLayout.CENTER);

    	final ImageIcon icon = WallGenerator.loadImageIcon("/palette.png");
    	
    	if (icon != null) {
    		changeBackgroundColorButton.setIcon(icon);
    		changeTransparencyColorButton.setIcon(icon);
    		changeTextColorButton.setIcon(icon);
    		changeBorderColorButton.setIcon(icon);
    		changeDistanceBlendColorButton.setIcon(icon);
    	} else {
    		changeBackgroundColorButton.setText("Change");
    		changeTransparencyColorButton.setText("Change");
    		changeTextColorButton.setText("Change");
    		changeBorderColorButton.setText("Change");
    		changeDistanceBlendColorButton.setText("Change");
    	}
        
    	
    	useTransparencyRadioButton.setFont(useTransparencyRadioButton.getFont().deriveFont(Font.PLAIN));
    	
    	optionSubPanel.add(getPlainLabel("Distance Blend Color"), "1, 1");
    	optionSubPanel.add(getPlainLabel("Text Color"), "1, 3");
    	optionSubPanel.add(getPlainLabel("Background Color"), "1, 5");
    	optionSubPanel.add(getPlainLabel("Border Color"), "1, 7");
    	optionSubPanel.add(getPlainLabel("Transparency Color"), "1, 9");
    	    	
    	optionSubPanel.add(useTransparencyRadioButton, "3, 11, 7, 11, l, f");
    	optionSubPanel.add(useTransparentColorRadioButton, "3, 9, l, f");
    	
    	optionSubPanel.add(selectedDistanceBlendColorPanel, "5, 1, f, f");
    	optionSubPanel.add(selectedTextColorPanel, "5, 3, f, f");
    	optionSubPanel.add(selectedBackgroundColorPanel, "5, 5, f, f");
    	optionSubPanel.add(selectedBorderColorPanel, "5, 7, f, f");
    	optionSubPanel.add(selectedTransparencyColorPanel, "5, 9, f, f");
    	
    	optionSubPanel.add(changeDistanceBlendColorButton, "7, 1");    	
    	optionSubPanel.add(changeTextColorButton, "7, 3");
    	optionSubPanel.add(changeBackgroundColorButton, "7, 5");
    	optionSubPanel.add(changeBorderColorButton, "7, 7");
    	optionSubPanel.add(changeTransparencyColorButton, "7, 9");
    	
    	transparencyRadioButtonGroup.add(useTransparencyRadioButton);
    	transparencyRadioButtonGroup.add(useTransparentColorRadioButton);
    	
    	useTransparentColorRadioButton.setSelected(true);
    	
        selectedBackgroundColorPanel.setBorder(BorderFactory.createEtchedBorder());
        selectedTransparencyColorPanel.setBorder(BorderFactory.createEtchedBorder());
        selectedTextColorPanel.setBorder(BorderFactory.createEtchedBorder());
        selectedBorderColorPanel.setBorder(BorderFactory.createEtchedBorder());
        selectedDistanceBlendColorPanel.setBorder(BorderFactory.createEtchedBorder());
    }

    private void setupProcessingPanel() {
    	final JSpinner.NumberEditor nearByEditor = new JSpinner.NumberEditor(nearByDarkeningSpinner, "0%");  
    	nearByDarkeningSpinner.setEditor(nearByEditor);
    	nearByEditor.getTextField().setFont(nearByEditor.getTextField().getFont().deriveFont(Font.PLAIN));
    	
    	final JSpinner.NumberEditor farAwayEditor = new JSpinner.NumberEditor(farAwayDarkeningSpinner, "0%");
    	farAwayDarkeningSpinner.setEditor(farAwayEditor);
    	farAwayEditor.getTextField().setFont(farAwayEditor.getTextField().getFont().deriveFont(Font.PLAIN));
    	
    	final JSpinner.NumberEditor softeningEditor = new JSpinner.NumberEditor(softeningSpinner, "0%");
    	softeningSpinner.setEditor(softeningEditor);
    	softeningEditor.getTextField().setFont(softeningEditor.getTextField().getFont().deriveFont(Font.PLAIN));
    	
    	farAwayEditor.getTextField().setInputVerifier(new PercentageSpinnerInputVerifier());
    	nearByEditor.getTextField().setInputVerifier(new PercentageSpinnerInputVerifier());    	
    	softeningEditor.getTextField().setInputVerifier(new PercentageSpinnerInputVerifier());
    	
    	final double[][] processingSubPanelLayoutDescriptor = new double[][] {
    			{3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3},
    			{10, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, 
    				TableLayout.PREFERRED, 10, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 
    				10, TableLayout.PREFERRED, 3, TableLayout.PREFERRED, 3, TableLayout.PREFERRED,
    				3, TableLayout.PREFERRED, TableLayout.FILL, 3}};
    	final TableLayout processingSubPanelLayout = new TableLayout(processingSubPanelLayoutDescriptor);
    	
    	final JPanel optionSubPanel = new JPanel(processingSubPanelLayout);
    	processingPanel.setLayout(new BorderLayout());
    	processingPanel.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
    	
    	final DefaultComboBoxModel scalingMethodModel = (DefaultComboBoxModel)scalingMethodComboBox.getModel();
    	scalingMethodModel.addElement(new ComboBoxItemContainer("Nearest Neighbour", RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR));
    	scalingMethodModel.addElement(new ComboBoxItemContainer("Bilinear", RenderingHints.VALUE_INTERPOLATION_BILINEAR));
    	scalingMethodModel.addElement(new ComboBoxItemContainer("Bicubic", RenderingHints.VALUE_INTERPOLATION_BICUBIC));
    	scalingMethodModel.addElement(new ComboBoxItemContainer("Resample", "Resample"));
    	    	
    	final DefaultComboBoxModel unsharpMethodModel = (DefaultComboBoxModel)unsharpMaskComboBox.getModel();
    	unsharpMethodModel.addElement(new ComboBoxItemContainer("None", UnsharpenMask.None));
    	unsharpMethodModel.addElement(new ComboBoxItemContainer("Soft", UnsharpenMask.Soft));    	
    	unsharpMethodModel.addElement(new ComboBoxItemContainer("Normal", UnsharpenMask.Normal));
    	unsharpMethodModel.addElement(new ComboBoxItemContainer("Very Sharp", UnsharpenMask.VerySharp));
    	unsharpMethodModel.addElement(new ComboBoxItemContainer("Oversharpened", UnsharpenMask.Oversharpened));
    	
    	scalingMethodComboBox.setFont(scalingMethodComboBox.getFont().deriveFont(Font.PLAIN));
    	unsharpMaskComboBox.setFont(unsharpMaskComboBox.getFont().deriveFont(Font.PLAIN));
    	processOrderComboBox.setFont(processOrderComboBox.getFont().deriveFont(Font.PLAIN));
    	
    	final JLabel titleLabel = new JLabel("Processing");
    	titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
    	
    	processingPanel.add(titleLabel, BorderLayout.NORTH);
    	processingPanel.add(optionSubPanel, BorderLayout.CENTER);
    	
    	final JLabel darkeningLabel = getSectionLabel("Distance Blending");
    	
    	optionSubPanel.add(darkeningLabel, "1, 1, 3, 1");
    	optionSubPanel.add(getPlainLabel("Near By Walls"), "1, 3");
    	optionSubPanel.add(nearByDarkeningSpinner, "3, 3");
    	optionSubPanel.add(getPlainLabel("Far Away Walls"), "1, 5");
    	optionSubPanel.add(farAwayDarkeningSpinner, "3, 5");
    	
    	optionSubPanel.add(getSectionLabel("Image Scaling"), "1, 7, 3, 7");
    	optionSubPanel.add(scalingMethodComboBox, "1, 9, 3, 9, f, f");
    	
    	optionSubPanel.add(getSectionLabel("Filters"), "1, 11, 3, 11");
    	optionSubPanel.add(getPlainLabel("Order"), "1, 13");
    	optionSubPanel.add(processOrderComboBox, "3, 13, f, f");
    	
    	optionSubPanel.add(getPlainLabel("Unsharp Mask"), "1, 15");
    	optionSubPanel.add(unsharpMaskComboBox, "3, 15, f, f");
    	
    	optionSubPanel.add(getPlainLabel("Soften"), "1, 17");
    	optionSubPanel.add(softeningSpinner, "3, 17");
    }

    private JLabel getSectionLabel(String label) {
    	final JLabel newLabel = new JLabel(label);
    	newLabel.setFont(newLabel.getFont().deriveFont(Font.ITALIC | Font.BOLD));
    	return newLabel;
    }

    
    private JLabel getPlainLabel(String label) {
    	final JLabel newLabel = new JLabel(label);    	
    	final Font plainFont = newLabel.getFont().deriveFont(Font.PLAIN);
    	newLabel.setFont(plainFont);
    	return newLabel;
    }
    
    private void placeComponents() {
    	this.add(basicOptionsPanel, "1, 1, 5, 5, f, f");
        this.add(processingPanel, "7, 1, 7, 5, f, f");
        this.add(colorPanel, "9, 1, 9, 5, f, f");
        this.add(buttonPanel, "1, 7, 13, 7, f, f");
    }

    private void setComboBoxValue(JComboBox comboBox, String value, String illegalMessage) {
    	log.debug("Setting combo box to value: " + value);
		final ComboBoxModel model = comboBox.getModel();
		for(int i = 0; i < model.getSize(); i++) {
			log.debug("Testing model value: " + model.getElementAt(i).toString());
			if (model.getElementAt(i).toString().equals(value)) {
				comboBox.setSelectedIndex(i);
				return;
			}
		}
		throw new IllegalArgumentException(illegalMessage);
    	
    }
    
    
	public void setSelectedResolution(String value) {
		setComboBoxValue(resolutionCombo, value, "Invalid Resolution Specified!");
	}

	public void setSelectedOutputType(String value) {
		setComboBoxValue(outputFileFormatCombo, value, "Invalid Output Format Specified!");
	}

	public void setSelectedFileType(String value) {
		setComboBoxValue(outputFileTypeCombo, value, "Invalid File Type Specified!");
	}   
    
	public void setImageScalingMethod(String value) {
		setComboBoxValue(scalingMethodComboBox, value, "Invalid Scaling Method Specified!");
	}

	public void setUnsharpMask(String value) {
		setComboBoxValue(unsharpMaskComboBox, value, "Invalid Unsharp Mask Specified!");
	}

	public void setFilterOrder(String value) {
		setComboBoxValue(processOrderComboBox, value, "Invalid Filter Order Specified!");
	}
	
	
	public void setNearWallDarkening(Double value) {
		nearByDarkeningSpinner.setValue(value);
	}

	public void setFarWallDarkening(Double value) {
		farAwayDarkeningSpinner.setValue(value);
	}

	public void setSoftening(Double value) {
		softeningSpinner.setValue(value);
	}
	
	
    public String getSelectedResolution() {
		return (String)resolutionCombo.getSelectedItem();    	
	}
	
	public String getSelectedFileType() {
		return (String)outputFileTypeCombo.getSelectedItem();        
	}
	
	public String getSelectedOutputType() {
		return (String)outputFileFormatCombo.getSelectedItem();
	}
	
	public double getNearWallDarkening() {
		return (Double)nearByDarkeningSpinner.getValue();
	}   
	
	public double getFarWallDarkening() {
		return (Double)farAwayDarkeningSpinner.getValue();
	}
	
	public double getSoftening() {
		return (Double)softeningSpinner.getValue();
	}
	
	public boolean useTransparency() {
		return this.useTransparencyRadioButton.isSelected();
	}
	
	public void setUseTransparency(Boolean useTransparency) {
		this.useTransparencyRadioButton.setSelected(useTransparency);
	}			
	
	public String getFilterOrder() {
		return this.processOrderComboBox.getSelectedItem().toString();
	}
	
	public String getUnsharpMask() {
		return this.unsharpMaskComboBox.getSelectedItem().toString();
	}
	
	public String getImageScalingMethod() {
		return this.scalingMethodComboBox.getSelectedItem().toString();
	}
	
	public void updateTransparencyEnabledView() {
		boolean supportsTransparency = doesCurrentOutputFileTypeSupportTransparency();
	
		useTransparencyRadioButton.setEnabled(supportsTransparency);
		if (!supportsTransparency && useTransparencyRadioButton.isSelected()) {
			useTransparentColorRadioButton.setSelected(true);
		}
	}
	
	public abstract boolean doesCurrentOutputFileTypeSupportTransparency();

    public class ComboBoxItemContainer {
    	private final String label;
    	private final Object item;
    	
    	public ComboBoxItemContainer(String label, Object item) {
    		this.label = label;
    		this.item = item;
    	}
    	
    	@Override
    	public String toString() {
    		return this.label;
    	}
    	
    	public Object getItem() {
    		return this.item;
    	}
    }

	private class PercentageSpinnerInputVerifier extends InputVerifier {
		@Override
		public boolean verify(JComponent input) {
			final JTextField field = (JTextField)input;
			String textToVerify = field.getText().trim();
			
			String numberValue = null;
			if (textToVerify.endsWith("%")) {
				numberValue = textToVerify.substring(0, textToVerify.length() - 1);
			} else {
				numberValue = textToVerify;
			}
			
			try {
				int value = Integer.parseInt(numberValue);
				if (value >= 0 && value <= 100) {
					field.setText(value + "%");
					return true;
				} else {
					JOptionPane.showMessageDialog(OptionPanelUIBase.this, "Distance Blending value must be between 0% and 100%", "Invalid Darkening Value", JOptionPane.ERROR_MESSAGE);
				}
			} catch (NumberFormatException nfe) {
				JOptionPane.showMessageDialog(OptionPanelUIBase.this, "Invalid distance blending value", "Invalid Distance Blending Value", JOptionPane.ERROR_MESSAGE);
			}
			
			return false;
		}
	}

	
}
