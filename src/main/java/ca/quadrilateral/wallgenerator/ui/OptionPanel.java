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

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.Controller;
import ca.quadrilateral.wallgenerator.OptionPanelEvent;
import ca.quadrilateral.wallgenerator.OptionPanelEventListener;
import ca.quadrilateral.wallgenerator.WallGenerator;
import ca.quadrilateral.wallgenerator.config.ConfigManager;
import ca.quadrilateral.wallgenerator.config.OutputFileConfig;
import ca.quadrilateral.wallgenerator.config.PostProcessingConfig;
import ca.quadrilateral.wallgenerator.filechooser.PresetsFileChooser;

import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;

public class OptionPanel extends OptionPanelUIBase {
	private static final Logger log = LoggerFactory.getLogger(OptionPanel.class);
    private static final long serialVersionUID = 1L;

    private Collection<ActionListener> previewWallsActionListeners = new ArrayList<ActionListener>();
    private Collection<ActionListener> generateWallsActionListeners = new ArrayList<ActionListener>();
    private Collection<ActionListener> changeGenerationSettingsListeners = new ArrayList<ActionListener>();
    
    private Collection<OptionPanelEventListener> optionPanelEventListeners = new ArrayList<OptionPanelEventListener>();

    public OptionPanel() {
        super();
        this.setupContainerPanels();
        this.attachListeners();
    }

    public void addOptionPanelEventListener(OptionPanelEventListener optionPanelEventListener) {
    	if (!optionPanelEventListeners.contains(optionPanelEventListener)) {
    		optionPanelEventListeners.add(optionPanelEventListener);
    	}
    }
    
    public void removeOptionPanelEventListener(OptionPanelEventListener optionPanelEventListener) {
    	if (!optionPanelEventListeners.contains(optionPanelEventListener)) {
    		optionPanelEventListeners.remove(optionPanelEventListener);
    	}
    }
    
    private void setupContainerPanels() {
    	updateBasicOptionValues();
    	refreshColorPanelOptions();
    }
    
    private void updateBasicOptionValues() {
		final Collection<String> availableResolutions = WallGenerator.getConfigManager().getAvailableResolutions();
		resolutionCombo.setModel(new DefaultComboBoxModel(new Vector<String>(availableResolutions)));
		
		final Collection<String> availableOutputFormats = WallGenerator.getConfigManager().getAvailableOutputFormats();
		outputFileFormatCombo.setModel(new DefaultComboBoxModel(new Vector<String>(availableOutputFormats)));
		
		final Collection<String> availableOutputFileTypes = WallGenerator.getConfigManager().getAvailableOutputFileTypes();
		outputFileTypeCombo.setModel(new DefaultComboBoxModel(new Vector<String>(availableOutputFileTypes)));
	
		updateTransparencyEnabledView();
    }
    
    public void refreshColorPanelOptions() {
    	ConfigManager configManager = WallGenerator.getConfigManager();
    	selectedTextColorPanel.setBackground(configManager.getColor(ConfigManager.TEXT_COLOR_KEY));
    	selectedBackgroundColorPanel.setBackground(configManager.getColor(ConfigManager.BACKGROUND_COLOR_KEY));
    	selectedTransparencyColorPanel.setBackground(configManager.getColor(ConfigManager.TRANSPARENCY_COLOR_KEY));
    	selectedBorderColorPanel.setBackground(configManager.getColor(ConfigManager.BORDER_COLOR_KEY));
    	selectedDistanceBlendColorPanel.setBackground(configManager.getColor(ConfigManager.DISTANCE_BLENDING_COLOR_KEY));
    }
    
    public void addChangeGenerationSettingsListener(ActionListener actionListener) {
        this.changeGenerationSettingsListeners.add(actionListener);
    }

    public void addPreviewWallsActionListener(ActionListener actionListener) {
        this.previewWallsActionListeners.add(actionListener);
    }

    public void addGenerateWallsActionListeners(ActionListener actionListener) {
        this.generateWallsActionListeners.add(actionListener);
    }

    private void fireChangeGenerationSettingsListeners(ActionEvent event) {
        for(ActionListener listener : this.changeGenerationSettingsListeners) {
        	log.debug("Invalidating Wall Cache...");
            listener.actionPerformed(event);
        }
    }

    public void setSettingsHaveChanged(Object source, String command) {
    	fireChangeGenerationSettingsListeners(new ActionEvent(source, 0, command));
    }

    private void attachListeners() {
    	this.loadPresetsButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			final OptionPanelEvent optionPanelEvent = new OptionPanelEvent(e.getSource());
    			
    			final JFileChooser fileChooser = new PresetsFileChooser();
    			if (JFileChooser.APPROVE_OPTION != fileChooser.showOpenDialog(OptionPanel.this)) {
    				return;
    			}
    			
    			final File file = fileChooser.getSelectedFile();
    			optionPanelEvent.addAttribute(Controller.LOAD_PRESETS_FILE_KEY, file);
    			
    			try {
	    			for(OptionPanelEventListener listener : optionPanelEventListeners) {
	   					listener.requestLoadPresets(optionPanelEvent);
	    			}
    			} catch (Exception ex) {
    				log.error("There was an error loading presets from file " + file.getAbsolutePath(), ex);
    				JOptionPane.showMessageDialog(OptionPanel.this, "There was an error loading presets from the file " + file.getAbsolutePath(), "Preset File Load Error", JOptionPane.ERROR_MESSAGE);
    			}
    		}
    	});
    	
    	this.savePresetsButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			final OptionPanelEvent optionPanelEvent = new OptionPanelEvent(e.getSource());
    			
    			final PresetsFileChooser fileChooser = new PresetsFileChooser();    			    	
    			if (JFileChooser.APPROVE_OPTION != fileChooser.showSaveDialog(OptionPanel.this)) {
    				return;
    			}
    			
    			final File file = fileChooser.getSelectedFile();

    			optionPanelEvent.addAttribute(Controller.SAVE_PRESETS_FILE_KEY, file);
    			optionPanelEvent.addAttribute(Controller.SAVE_PRESENTS_FILE_AS_DEFAULT_KEY, fileChooser.useAsDefault());
				try {
	    			for(OptionPanelEventListener listener : optionPanelEventListeners) {
	   					listener.requestSavePresets(optionPanelEvent);
	    			}
				} catch (Exception ex) {
					log.error("There was an error saving presets to file " + file.getAbsolutePath(), ex);
					JOptionPane.showMessageDialog(OptionPanel.this, "There was an error saving presets to the file " + file.getAbsolutePath(), "Preset File Save Error", JOptionPane.ERROR_MESSAGE);
				}    			
    		}
    	});
    	
    	this.previewWallsButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			final OptionPanelEvent optionPanelEvent = new OptionPanelEvent(e.getSource());
    			
				try {
	    			for(OptionPanelEventListener listener : optionPanelEventListeners) {
	   					listener.requestGeneratePreview(optionPanelEvent);
	    			}
				} catch (Exception ex) {
					log.error("There was an previewing walls", ex);
					JOptionPane.showMessageDialog(OptionPanel.this, "There was an error generating the wall preview!", "Preview Error", JOptionPane.ERROR_MESSAGE);
				}
    		}
    	});
    	
    	this.generateWallsButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			final OptionPanelEvent optionPanelEvent = new OptionPanelEvent(e.getSource());
    			
				try {
	    			for(OptionPanelEventListener listener : optionPanelEventListeners) {
	   					listener.requestGenerateWalls(optionPanelEvent);
	    			}
				} catch (Exception ex) {
					log.error("There was an previewing walls", ex);
					JOptionPane.showMessageDialog(OptionPanel.this, "There was an error generating the wallset!", "Wall Generation Error", JOptionPane.ERROR_MESSAGE);
				}    			    			
    		}
    	});
    	
    	this.changeDistanceBlendColorButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			handleColorChange("Distance Blending", selectedDistanceBlendColorPanel.getBackground(), selectedDistanceBlendColorPanel);
    		}
    	});
    	
    	this.changeDistanceBlendColorButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		fireChangeGenerationSettingsListeners(e);
        	}
        });
    	
    	this.changeBackgroundColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleColorChange("Background", selectedBackgroundColorPanel.getBackground(), selectedBackgroundColorPanel);
			}
    	});

    	this.changeTextColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleColorChange("Text", selectedTextColorPanel.getBackground(), selectedTextColorPanel);				
			}
    	});

    	this.changeTransparencyColorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				handleColorChange("Transparency", selectedTransparencyColorPanel.getBackground(), selectedTransparencyColorPanel);				
			}
    	});

    	this.changeBorderColorButton.addActionListener(new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			handleColorChange("Border", selectedBorderColorPanel.getBackground(), selectedBorderColorPanel);
    		}
    	});
    	
        
        final ActionListener outputTypeTransparencySupportListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateTransparencyEnabledView();
			}        	
        };

        outputFileTypeCombo.addActionListener(outputTypeTransparencySupportListener);               
        
        final ChangeListener invalidateGeneratedWallsChangeListener = new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				fireChangeGenerationSettingsListeners(new ActionEvent(e.getSource(), 0, null));
			}        	
        }; 
        
        final ActionListener invalidateGeneratedWallsActionListener = new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		fireChangeGenerationSettingsListeners(e);
        	}
        };
        
        nearByDarkeningSpinner.addChangeListener(invalidateGeneratedWallsChangeListener);
        farAwayDarkeningSpinner.addChangeListener(invalidateGeneratedWallsChangeListener);
        softeningSpinner.addChangeListener(invalidateGeneratedWallsChangeListener);
        
        scalingMethodComboBox.addActionListener(invalidateGeneratedWallsActionListener);
        unsharpMaskComboBox.addActionListener(invalidateGeneratedWallsActionListener);
        processOrderComboBox.addActionListener(invalidateGeneratedWallsActionListener);
        
        final ChangeGenerationSettingActionListener changeListener = new ChangeGenerationSettingActionListener();
        
        outputFileFormatCombo.addActionListener(changeListener);
        outputFileTypeCombo.addActionListener(changeListener);
        resolutionCombo.addActionListener(changeListener);
    }
    
    private void handleColorChange(String type, Color initialColor, JPanel targetPanel) {
    	Color newColor = JColorChooser.showDialog(this, "Select " + type + " Color", initialColor);
    	if (newColor != null) {
    		WallGenerator.getConfigManager().setColor(type, newColor);
    		targetPanel.setBackground(newColor);
    	}
    }
    
    @Override
    public boolean doesCurrentOutputFileTypeSupportTransparency() {
		final OutputFileConfig outputFileConfig = WallGenerator.getConfigManager().getOutputFileConfig(super.getSelectedFileType());
		return outputFileConfig.isSupportsTransparency();
    }

    public class ChangeGenerationSettingActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            fireChangeGenerationSettingsListeners(event);
        }
    }

	
	public PostProcessingConfig getPostProcessingConfig() {
		final PostProcessingConfig config = new PostProcessingConfig();
		
		config.farDarkening = ((Double)this.farAwayDarkeningSpinner.getValue()).floatValue();
		config.nearDarkening = ((Double)this.nearByDarkeningSpinner.getValue()).floatValue();
		config.scalingMethod = ((ComboBoxItemContainer)scalingMethodComboBox.getSelectedItem()).getItem();
		config.softening = ((Double)this.softeningSpinner.getValue()).floatValue();
		config.unsharpenMask = (UnsharpenMask)((ComboBoxItemContainer)unsharpMaskComboBox.getSelectedItem()).getItem();
		config.postProcessingOrder = this.processOrderComboBox.getSelectedIndex();
		config.distanceBlendColor = WallGenerator.getConfigManager().getColor("Distance Blending");
		
		return config;
	}

}
