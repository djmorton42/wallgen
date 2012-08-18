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

package ca.quadrilateral.wallgenerator;

import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.config.Config;
import ca.quadrilateral.wallgenerator.config.ConfigManager;
import ca.quadrilateral.wallgenerator.config.FileSelectionConfig;
import ca.quadrilateral.wallgenerator.config.OutputConfig;
import ca.quadrilateral.wallgenerator.config.PostProcessingConfig;
import ca.quadrilateral.wallgenerator.settings.SettingsDeserializer;
import ca.quadrilateral.wallgenerator.settings.SettingsSerializer;
import ca.quadrilateral.wallgenerator.ui.FileChooserPanel;
import ca.quadrilateral.wallgenerator.ui.OptionPanel;
import ca.quadrilateral.wallgenerator.ui.PreviewDialog;
import ca.quadrilateral.wallgenerator.ui.WallGeneratorFrame;

public class Controller {
	public static final String DEFAULT_SETTINGS_FILENAME = "default-settings.xml";

	private static final Logger log = LoggerFactory.getLogger(Controller.class);

	public static final String SAVE_PRESETS_FILE_KEY = "SAVE_PRESETS_FILE";
	public static final String LOAD_PRESETS_FILE_KEY = "LOAD_PRESETS_FILE";
	public static final String SAVE_PRESENTS_FILE_AS_DEFAULT_KEY = "SAVE_PRESETS_FILE_AS_DEFAULT";


	private final FileChooserPanel fileChooserPanel;
	private final OptionPanel optionPanel;
	private final WallGeneratorFrame wallGeneratorFrame;

	private List<Map<String, BufferedImage>> generatedImages = null;

	private ConfigManager configManager = null;
	private PostProcessingConfig baselinePostProcessingConfig = new PostProcessingConfig();

	public Controller(final WallGeneratorFrame wallGeneratorFrame, final FileChooserPanel fileChooserPanel, final OptionPanel optionPanel, final ConfigManager configManager) {
		this.wallGeneratorFrame = wallGeneratorFrame;
		this.configManager = configManager;
		this.optionPanel = optionPanel;
		this.fileChooserPanel = fileChooserPanel;               

		this.optionPanel.addChangeGenerationSettingsListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearGeneratedImages();
				updateCurrentGenerationValuesInFileChooserPanel();
			}
		});

		this.optionPanel.addOptionPanelEventListener(new OptionPanelEventListener() {

			@Override
			public void requestSavePresets(OptionPanelEvent event) {
				final SettingsSerializer settingsSerializer = new SettingsSerializer();
				final File outputFile = (File)event.getAttribute(SAVE_PRESETS_FILE_KEY);
				if (outputFile == null) {
					throw new IllegalStateException("No Output File");
				}
				try {					
					if (outputFile.exists()) {
						if (JOptionPane.NO_OPTION == 
								JOptionPane.showConfirmDialog(
										optionPanel, 
										"File " + outputFile.getAbsolutePath() + " exists.  Overwrite?", 
										"Confirm Overwrite", 
										JOptionPane.YES_NO_OPTION, 
										JOptionPane.QUESTION_MESSAGE)) {
							return;
						}
					} else {
						if (!outputFile.createNewFile()) {
							JOptionPane.showMessageDialog(optionPanel, "Unable to write to file " + outputFile.getAbsolutePath() + "!", "Unable To Write", JOptionPane.ERROR_MESSAGE);
							return;
						}
					}

					settingsSerializer.serializeSettings(outputFile, optionPanel);
					final Boolean useSettingsAsDefault = (Boolean)event.getAttribute(SAVE_PRESENTS_FILE_AS_DEFAULT_KEY);
					if (useSettingsAsDefault != null && useSettingsAsDefault) {
						log.info("Saving presets file as default filename: " + DEFAULT_SETTINGS_FILENAME);
						settingsSerializer.serializeSettings(new File(System.getProperty("user.dir"), DEFAULT_SETTINGS_FILENAME), optionPanel);
					}
					JOptionPane.showMessageDialog(optionPanel, "Preset File Saved!", "Save Successful", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public void requestLoadPresets(OptionPanelEvent event) {
				final SettingsDeserializer settingsDeserializer = new SettingsDeserializer();
				final File inputFile = (File)event.getAttribute(LOAD_PRESETS_FILE_KEY);
				if (inputFile == null) {
					throw new IllegalStateException("No Input File");
				}
				try {
					if (!inputFile.canRead()) {
						JOptionPane.showMessageDialog(optionPanel, "Unable to read from file " + inputFile.getAbsolutePath() + "!", "Unable To Read", JOptionPane.ERROR_MESSAGE);
						return;
					}

					settingsDeserializer.deserializeSettings(inputFile, optionPanel);
					JOptionPane.showMessageDialog(optionPanel, "Preset File Loaded!", "Load Successful", JOptionPane.INFORMATION_MESSAGE);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

			}

			@Override
			public void requestGeneratePreview(OptionPanelEvent event) {
				try {
					if (validateSourceFile(true)) {
						final BufferedImage previewImage = generatePreview();
						final PreviewDialog previewDialog = new PreviewDialog(previewImage, wallGeneratorFrame);
						previewDialog.setVisible(true);
					}
				} catch (FileNotFoundException fnfe) {
					log.error("Error generating preview", fnfe);
					JOptionPane.showMessageDialog(wallGeneratorFrame, "Source image could not be found!", "Source Image Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ioe) {
					log.error("Error generating preview", ioe);
					JOptionPane.showMessageDialog(wallGeneratorFrame, "IO error generating images.  Check file permissions", "IO Error", JOptionPane.ERROR_MESSAGE);
				} catch (ArrayIndexOutOfBoundsException iob) {
					log.error("ArrayIndexOutOfBoundsException generating preview images", iob);
					JOptionPane.showMessageDialog(wallGeneratorFrame, "The source image could not be scaled.  This may occur for very small images.  Try a different source image or scaling method.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					log.error("Error generating preview", ex);
					throw new RuntimeException(ex);
				}
			}

			@Override
			public void requestGenerateWalls(OptionPanelEvent event) {
				try {
					if (StringUtils.isBlank(fileChooserPanel.outputFileNameBase())) {
						JOptionPane.showMessageDialog(wallGeneratorFrame, "No base output file name specified!", "Target Image Error", JOptionPane.ERROR_MESSAGE);
					} else {
						if (validateSourceFile(true)) {
							FileSelectionConfig fileSelectionConfig = fileChooserPanel.getFileSelectionConfig();
							String selectedOutputType = optionPanel.getSelectedOutputType();
							String resolution = optionPanel.getSelectedResolution();
							if (fileSelectionConfig.getStories() > 1) {
								if (!WallGenerator
										.getConfigManager()
										.getConfig(resolution)
										.getOutputConfig(selectedOutputType)
										.isSupportsMultiStory()) {
									JOptionPane.showMessageDialog(wallGeneratorFrame, "The selected output configuration does not support multiple stories!", "Multi-Story Not Supported", JOptionPane.WARNING_MESSAGE);
									return;
								}
							}
							generateOutput();
							JOptionPane.showMessageDialog(wallGeneratorFrame, "Generation Completed!", "Complete", JOptionPane.INFORMATION_MESSAGE);
						}
					}
				} catch (FileNotFoundException fnfe) {
					log.error("Error generating output", fnfe);
					JOptionPane.showMessageDialog(wallGeneratorFrame, "Source image could not be found!", "Source Image Error", JOptionPane.ERROR_MESSAGE);
				} catch (IOException ioe) {
					log.error("Error generating output", ioe);
					JOptionPane.showMessageDialog(wallGeneratorFrame, "IO error generating images.  Check file permissions", "IO Error", JOptionPane.ERROR_MESSAGE);
				} catch (ArrayIndexOutOfBoundsException iob) {
					log.error("ArrayIndexOutOfBoundsException generating output images", iob);
					JOptionPane.showMessageDialog(wallGeneratorFrame, "The source image could not be scaled.  This may occur for very small images.  Try a different source image or scaling method.", "Error", JOptionPane.ERROR_MESSAGE);
				} catch (Exception ex) {
					log.error("Error generating output", ex);
					JOptionPane.showMessageDialog(wallGeneratorFrame, "An unanticipated error has ocurred.  See log file for details", "Error", JOptionPane.ERROR_MESSAGE);
				}


			}
		});

		this.fileChooserPanel.addChangeGenerationSettingsListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clearGeneratedImages();
			}
		});

		baselinePostProcessingConfig.nearDarkening = 0f;
		baselinePostProcessingConfig.farDarkening = 0f;
		baselinePostProcessingConfig.softening = 0f;
		baselinePostProcessingConfig.scalingMethod = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;

		updateCurrentGenerationValuesInFileChooserPanel();
	}
	
    private boolean validateSourceFile(boolean showErrorDialog) {
    	final FileSelectionConfig fileSelectionConfig = fileChooserPanel.getFileSelectionConfig();
    	if (!fileSelectionConfig.hasValidSourceFiles()) {
    		if (showErrorDialog) {
    			displayMissingSourceFileErrorDialog();
    		}
    		return false;
    	}
    	return true;
    }	
    
    private void displayMissingSourceFileErrorDialog() {
    	JOptionPane.showMessageDialog(wallGeneratorFrame, "Source file name is required.", "Invalid Source File", JOptionPane.ERROR_MESSAGE);
    }

    public void loadDefaultSettingsFile() {
    	final SettingsDeserializer settingsDeserializer = new SettingsDeserializer();
    	final File inputFile = new File(System.getProperty("user.dir"), DEFAULT_SETTINGS_FILENAME);
    	try {
    		if (!inputFile.exists()) {
    			log.info("No default settings file present: " + DEFAULT_SETTINGS_FILENAME);
    			return;
    		}
    		if (!inputFile.canRead()) {
    			log.info("Unable to read default setting file: " + DEFAULT_SETTINGS_FILENAME);
    			JOptionPane.showMessageDialog(wallGeneratorFrame, "Can not read default settings file: " + DEFAULT_SETTINGS_FILENAME, "Unable to Read Settings File", JOptionPane.WARNING_MESSAGE);
    			return;
    		}

    		settingsDeserializer.deserializeSettings(inputFile, optionPanel);
    		log.info("Default settings file " + DEFAULT_SETTINGS_FILENAME + " loaded.");
    	} catch (Exception e) {
    		throw new RuntimeException(e);
    	}
    }

	private void updateCurrentGenerationValuesInFileChooserPanel() {
		fileChooserPanel.setCurrentResolution(optionPanel.getSelectedResolution());
		fileChooserPanel.setCurrentOutputFormat(optionPanel.getSelectedOutputType());    	
	}

	public void generateOutput() throws FileNotFoundException, IOException {
		if (!hasImages()) {
			this.generatedImages = new WallGenerationService().fetchImages(
					fileChooserPanel.getFileSelectionConfig(), 
					optionPanel.getPostProcessingConfig(), 
					optionPanel.getSelectedResolution());
		}

		final Config config = this.configManager.getConfig(this.optionPanel.getSelectedResolution());
		final OutputConfig outputConfig = config.getOutputConfig(optionPanel.getSelectedOutputType());

		final OutputGenerator generator = new OutputGeneratorFactory().createOutputGenerator(outputConfig);
		generator.generate(
				outputConfig, 
				fileChooserPanel.getTargetDirectory(), 
				fileChooserPanel.outputFileNameBase(), 
				optionPanel.getSelectedFileType(), 
				this.generatedImages,
				optionPanel.useTransparency());
	}

	public BufferedImage generatePreview() throws FileNotFoundException, IOException {    	
		if (!hasImages()) {
			this.generatedImages = new WallGenerationService().fetchImages(
					fileChooserPanel.getFileSelectionConfig(), 
					optionPanel.getPostProcessingConfig(), 
					optionPanel.getSelectedResolution());
		}

		return new PreviewGenerationService().generatePreview(this.generatedImages);
	}

	private boolean hasImages() {
		return this.generatedImages != null;
	}

	public void clearGeneratedImages() {
		this.generatedImages = null;
	}
}
