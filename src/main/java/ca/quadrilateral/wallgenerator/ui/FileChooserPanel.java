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

import static info.clearthought.layout.TableLayoutConstants.FILL;
import static info.clearthought.layout.TableLayoutConstants.PREFERRED;
import info.clearthought.layout.TableLayout;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.WallGenerator;
import ca.quadrilateral.wallgenerator.config.FileSelectionConfig;
import ca.quadrilateral.wallgenerator.filechooser.SourceImageFileChooser;
import ca.quadrilateral.wallgenerator.filechooser.TargetDirectoryFileChooser;

public class FileChooserPanel extends JPanel {
	private static final Logger log = LoggerFactory.getLogger(FileChooserPanel.class);
	
	private static final long serialVersionUID = 1L;
	
	private double[][] layoutDescriptor = null;
    private TableLayout layout = null;


    private final JLabel sourceFileLabel = new JLabel("Source File:");
    
    private final JTextField sourceFileNameTextField = new JTextField();
    
    private final JTextField secondStorySourceFileNameTextField = new JTextField();
    private final JTextField thirdStorySourceFileNameTextField = new JTextField();
    
    private final JTextField targetDirectoryTextField = new JTextField();
    private final JTextField outputFileNameBaseTextField = new JTextField();

    private final JButton sourceFileNameButton = new JButton();
    
    private final JButton secondStorySourceFileNameButton = new JButton();
    private final JButton thirdStorySourceFileNameButton = new JButton();
    
    private final JButton targetDirectoryNameButton = new JButton();
    private final JButton generateBaseFileNameButton = new JButton();

    private final JButton clearFirstStoryFileTextButton = new JButton();
    private final JButton clearSecondStoryFileTextButton = new JButton();
    private final JButton clearThirdStoryFileTextButton = new JButton();
    
    private final JSpinner storySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 3, 1));
    
    private final ImagePreviewPanel previewPanel = new ImagePreviewPanel(112, 134);

    private final JFileChooser sourceImageFileChooser = new SourceImageFileChooser();
    private final JFileChooser targetDirectoryFileChooser = new TargetDirectoryFileChooser();

    private final JRadioButton storyTiledImageRadioButton = new JRadioButton("Tiled", true);
    private final JRadioButton storySingleFileRadioButton = new JRadioButton("Single File");
    private final JRadioButton storyIndividualFileRadioButton = new JRadioButton("Individual Files");
    
    private final ButtonGroup storyRadioButtonGroup = new ButtonGroup();
    
	private TableLayout storyConfigPanelLayout = null;
	private JPanel storyConfigPanel = null;
    
    private String currentResolution = "";
    private String currentOutputFormat = "";

    private Collection<ActionListener> changeGenerationSettingsListeners = new ArrayList<ActionListener>();

    public FileChooserPanel() {
        super();
        this.setLayout(setupLayout());
        this.setPreferredSize(new Dimension(1, 210));
        this.setBorder(BorderFactory.createEtchedBorder());
        this.setupComponents();
        this.placeComponents();
        this.attachListeners();
    }
    
    public void setCurrentResolution(String currentResolution) {
    	this.currentResolution = currentResolution;
    }
    
    public void setCurrentOutputFormat(String currentOutputFormat) {
    	this.currentOutputFormat = currentOutputFormat;
    }

    public void addChangeGenerationSettingsListener(ActionListener actionListener) {
        this.changeGenerationSettingsListeners.add(actionListener);
    }

    private void fireChangeGenerationSettingsListeners(ActionEvent event) {
        for(ActionListener listener : this.changeGenerationSettingsListeners) {
            listener.actionPerformed(event);
        }
    }
    
    private void setStoryRadioVisibility(boolean isVisible) {
    	storyConfigPanel.invalidate();
    	storyConfigPanelLayout.setColumn(2, isVisible ? PREFERRED : 0);
    	storyConfigPanelLayout.setColumn(3, isVisible ? 3 : 0);
    	storyConfigPanelLayout.setColumn(4, isVisible ? PREFERRED : 0);
    	storyConfigPanelLayout.setColumn(5, isVisible ? 3 : 0);
    	storyConfigPanelLayout.setColumn(6, isVisible ? PREFERRED : 0);
    	storyConfigPanel.validate();
    	
    	if (!isVisible) {
    		storyTiledImageRadioButton.setSelected(true);
    	}
    }
    
    private final void attachListeners() {
    	this.clearFirstStoryFileTextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				sourceFileNameTextField.setText("");
				loadSourcePreview();
			}
    	});

    	this.clearSecondStoryFileTextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				secondStorySourceFileNameTextField.setText("");
				loadSourcePreview();
			}
    	});

    	this.clearThirdStoryFileTextButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thirdStorySourceFileNameTextField.setText("");
				loadSourcePreview();
			}
    	});

    	final ActionListener storyRadioChangeActionListener = new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
   				loadSourcePreview();
    			fireChangeGenerationSettingsListeners(e);
    		}
    	};
    	
    	this.storyTiledImageRadioButton.addActionListener(storyRadioChangeActionListener);
    	this.storyIndividualFileRadioButton.addActionListener(storyRadioChangeActionListener);
    	this.storySingleFileRadioButton.addActionListener(storyRadioChangeActionListener);
    	
    	this.storySpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int stories = (Integer)storySpinner.getValue();
				setStoryRadioVisibility(stories != 1);
				setExtraStoryFieldVisibility();
				loadSourcePreview();
				fireChangeGenerationSettingsListeners(new ActionEvent(e.getSource(), 0, null));
			}
    	});
    	
        this.sourceFileNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (sourceImageFileChooser.showOpenDialog(FileChooserPanel.this) == JFileChooser.APPROVE_OPTION) {
                    sourceFileNameTextField.setText(sourceImageFileChooser.getSelectedFile().getAbsolutePath());
                    loadSourcePreview();
                    fireChangeGenerationSettingsListeners(e);
                }
            }
        });
        
        this.secondStorySourceFileNameButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (sourceImageFileChooser.showOpenDialog(FileChooserPanel.this) == JFileChooser.APPROVE_OPTION) {
        			secondStorySourceFileNameTextField.setText(sourceImageFileChooser.getSelectedFile().getAbsolutePath());
    				loadSourcePreview();
    				fireChangeGenerationSettingsListeners(e);
        		}
        	}
        });
        
        this.thirdStorySourceFileNameButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		if (sourceImageFileChooser.showOpenDialog(FileChooserPanel.this) == JFileChooser.APPROVE_OPTION) {
        			thirdStorySourceFileNameTextField.setText(sourceImageFileChooser.getSelectedFile().getAbsolutePath());
    				loadSourcePreview();
    				fireChangeGenerationSettingsListeners(e);
        			
        		}
        	}
        });

        this.targetDirectoryNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (targetDirectoryFileChooser.showOpenDialog(FileChooserPanel.this) == JFileChooser.APPROVE_OPTION) {
                    targetDirectoryTextField.setText(targetDirectoryFileChooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        this.generateBaseFileNameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	final int stories = (Integer)storySpinner.getValue();            	
                final String sourceFileName = getNameForGeneratedName(stories);
                
                if (!StringUtils.isBlank(sourceFileName)) {                	
                   outputFileNameBaseTextField.setText(
                		   MessageFormat.format(
                				   "wall_{0}_{1}{3}_{2}", 
                				   currentResolution.replace(' ', '_'), 
                				   currentOutputFormat.replace(' ', '_'), 
                				   FilenameUtils.getBaseName(sourceFileName).replace(' ', '_'), 
                				   stories != 1 ? ("_" + stories + "_Story") : ""));
                } else {
                    JOptionPane.showMessageDialog(FileChooserPanel.this, "Can not generate base file name when no source image is selected!", "No Source Image Selected!", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        this.sourceFileNameButton.addActionListener(new ChangeGenerationSettingActionListener());
    }

    private String getNameForGeneratedName(int stories) {
    	String name = null;
    	String storyMethod = this.getStoryMethod();
    	if (stories == 1 || storyMethod.equals("Tiled") || storyMethod.equals("Single File")) {
    		if (!StringUtils.isBlank(sourceFileNameTextField.getText())) {
    			name = sourceFileNameTextField.getText();
    		}
    	} else if (stories == 2) {
    		name = getFirstNameInList(sourceFileNameTextField.getText(), secondStorySourceFileNameTextField.getText());
    	} else if (stories == 3) {
    		name = getFirstNameInList(sourceFileNameTextField.getText(), secondStorySourceFileNameTextField.getText(), thirdStorySourceFileNameTextField.getText());
    	}
    	return name;
    }
    
    private String getFirstNameInList(String... strings) {
    	for(String string : strings) {
    		if (!StringUtils.isBlank(string)) {
    			return string;
    		}
    	}
    	return null;
    }

    private final void setupComponents() {
    	final ImageIcon browseIcon = WallGenerator.loadImageIcon("/folder_explore.png");
    	final ImageIcon generateIcon = WallGenerator.loadImageIcon("/add.png");
    	final ImageIcon clearIcon = WallGenerator.loadImageIcon("/cancel.png");
    	
    	this.sourceFileNameButton.setToolTipText("Select Source File");
    	this.secondStorySourceFileNameButton.setToolTipText("Select Second Story Source File");
    	this.thirdStorySourceFileNameButton.setToolTipText("Select Third Story Source File");
    	this.targetDirectoryNameButton.setToolTipText("Select Target Directory");
    	this.generateBaseFileNameButton.setToolTipText("Generate File Name");
    	this.clearFirstStoryFileTextButton.setToolTipText("Clear");
    	this.clearSecondStoryFileTextButton.setToolTipText("Clear");
    	this.clearThirdStoryFileTextButton.setToolTipText("Clear");
    	
    	if (browseIcon != null) {
    		this.sourceFileNameButton.setIcon(browseIcon);
    		this.targetDirectoryNameButton.setIcon(browseIcon);
    		this.secondStorySourceFileNameButton.setIcon(browseIcon);
    		this.thirdStorySourceFileNameButton.setIcon(browseIcon);
    		this.generateBaseFileNameButton.setIcon(generateIcon);
    		
    		this.clearFirstStoryFileTextButton.setIcon(clearIcon);
    		this.clearSecondStoryFileTextButton.setIcon(clearIcon);
    		this.clearThirdStoryFileTextButton.setIcon(clearIcon);
    	} else {
    		this.sourceFileNameButton.setText("Browse");
    		this.targetDirectoryNameButton.setText("Browse");
    		this.secondStorySourceFileNameButton.setText("Browse");
    		this.thirdStorySourceFileNameButton.setText("Browse");
    		this.generateBaseFileNameButton.setText("Generate Name");
    		
       		this.clearFirstStoryFileTextButton.setText("Clear");
    		this.clearSecondStoryFileTextButton.setText("Clear");
    		this.clearThirdStoryFileTextButton.setText("Clear");    		
    	}
    	
        this.previewPanel.setPreferredSize(new Dimension(112, 134));
        this.sourceFileNameTextField.setEditable(false);
        this.secondStorySourceFileNameTextField.setEditable(false);
        this.thirdStorySourceFileNameTextField.setEditable(false);
        this.targetDirectoryTextField.setEditable(false);
        this.targetDirectoryTextField.setText(System.getProperty("user.dir"));
    }
    
    private void setExtraStoryFieldVisibility() {
    	boolean displayExtra = storyIndividualFileRadioButton.isSelected();
    	boolean displayThird = ((Integer)storySpinner.getValue()) == 3;
    	
    	if (displayExtra) {
    		sourceFileLabel.setText("First Story File:");
    		this.sourceFileNameButton.setToolTipText("Select First Story Source File");
    	} else {
    		sourceFileLabel.setText("Source File:");
    		this.sourceFileNameButton.setToolTipText("Select Source File");
    	}
    	
    	this.invalidate();
    	this.layout.setRow(5, displayExtra ? PREFERRED : 0);
    	this.layout.setRow(6, displayExtra ? 3 : 0);
    	this.layout.setRow(7, (displayExtra && displayThird) ? PREFERRED : 0);
    	this.layout.setRow(8, (displayExtra && displayThird) ? 3 : 0);
    	this.validate();
    }
    
    private void setupStoryConfigPanel() {
    	double[][] layoutDescriptor = new double[][] {{50, 3, 0, 0, 0, 0, 0, TableLayout.FILL}, {PREFERRED}};
    	storyConfigPanelLayout = new TableLayout(layoutDescriptor);
    	storyConfigPanel = new JPanel(storyConfigPanelLayout);
    	
    	storyRadioButtonGroup.add(storyTiledImageRadioButton);
    	storyRadioButtonGroup.add(storySingleFileRadioButton);
    	storyRadioButtonGroup.add(storyIndividualFileRadioButton);

    	final ActionListener storyRadioChangeListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setExtraStoryFieldVisibility();
			}
    	};
    	
    	storyTiledImageRadioButton.addActionListener(storyRadioChangeListener);
    	storySingleFileRadioButton.addActionListener(storyRadioChangeListener);
    	storyIndividualFileRadioButton.addActionListener(storyRadioChangeListener);
    	
        storyTiledImageRadioButton.setFont(storyTiledImageRadioButton.getFont().deriveFont(Font.PLAIN));
        storySingleFileRadioButton.setFont(storySingleFileRadioButton.getFont().deriveFont(Font.PLAIN));
        storyIndividualFileRadioButton.setFont(storyIndividualFileRadioButton.getFont().deriveFont(Font.PLAIN));
    	
    	storyConfigPanel.add(storySpinner, "0, 0, f, f");
    	storyConfigPanel.add(storyTiledImageRadioButton, "2, 0");
    	storyConfigPanel.add(storySingleFileRadioButton, "4, 0");
    	storyConfigPanel.add(storyIndividualFileRadioButton, "6, 0");
    	this.add(storyConfigPanel, "3, 1");
    }

    private final void placeComponents() {
    	this.add(new JLabel("Stories:"), "1, 1");
    	setupStoryConfigPanel();
    	
        this.add(sourceFileLabel, "1, 3");
        
        this.add(new JLabel("Second Story File:"), "1, 5");
        this.add(new JLabel("Third Story File:"), "1, 7");
        
        this.add(new JLabel("Destination Dir:"), "1, 9");
        this.add(new JLabel("Base Output File Name:"), "1, 11");
        this.add(new JLabel("Preview"), "9, 1, c, f");

        this.add(sourceFileNameTextField, "3, 3, f, f");
        
        this.add(secondStorySourceFileNameTextField, "3, 5, f, f");
        this.add(thirdStorySourceFileNameTextField, "3, 7, f, f");
        
        this.add(targetDirectoryTextField, "3, 9, f, f");
        this.add(outputFileNameBaseTextField, "3, 11, f, f");

        this.add(sourceFileNameButton, "5, 3");
        
        this.add(secondStorySourceFileNameButton, "5, 5");
        this.add(thirdStorySourceFileNameButton, "5, 7");
        
        this.add(targetDirectoryNameButton, "5, 9, f, f");
        this.add(generateBaseFileNameButton, "5, 11, f, f");

        this.add(clearFirstStoryFileTextButton, "7, 3, f, f");
        this.add(clearSecondStoryFileTextButton, "7, 5, f, f");
        this.add(clearThirdStoryFileTextButton, "7, 7, f, f");
        
        this.add(this.previewPanel, "9, 3, 9, 15");

    }

    private final LayoutManager setupLayout() {
        this.layoutDescriptor = new double[][] {
                {5, PREFERRED, 5, FILL, 5, PREFERRED, 5, PREFERRED, 5, 112, 5},
                {3, 
                	PREFERRED, 3,
                	PREFERRED, 3,
                	0, 0,                	
                	0, 0,
                	PREFERRED, 3, PREFERRED, 3, PREFERRED, 3, FILL, 3}
        };

        this.layout = new TableLayout(layoutDescriptor);
        return this.layout;
    }

    private void loadSourcePreview() {
    	try { 
	    	List<File> files = new ArrayList<File>();
	    	if (!StringUtils.isBlank(sourceFileNameTextField.getText())) {
	    		files.add(new File(sourceFileNameTextField.getText()));
	    	}
	    	
	    	int stories = (Integer)this.storySpinner.getValue();
	    	
	    	if (stories >= 2 && this.getStoryMethod().equals("Individual Files")) {
	    		if (!StringUtils.isBlank(secondStorySourceFileNameTextField.getText())) {
	    			files.add(new File(secondStorySourceFileNameTextField.getText()));
	    		}
	    	} 
	    	
	    	if (stories == 3 && this.getStoryMethod().equals("Individual Files")) {
	    		if (!StringUtils.isBlank(thirdStorySourceFileNameTextField.getText())) {
	    			files.add(new File(thirdStorySourceFileNameTextField.getText()));
	    		}
	    	}    	    	
	    	
	    	List<Image> previewImages = new ArrayList<Image>();
	    	
	    	for(File file : files) {
	    		if (file.canRead()) {
	    			previewImages.add(ImageIO.read(file));
	    		}
	    	}
	    	
	        previewPanel.setImages(previewImages);

		} catch (Exception ex) {
            JOptionPane.showMessageDialog(FileChooserPanel.this, "Unable to load image preview!", "Load Error", JOptionPane.ERROR_MESSAGE);    				
		}
    }


    public File getSourceFile() {
        final String sourceFileNameText = sourceFileNameTextField.getText();
        if (StringUtils.isBlank(sourceFileNameText)) {
            return null;
        }

        final File file = new File(sourceFileNameTextField.getText());

        if (file.isFile() && file.canRead()) {
            return file;
        } else {
            return null;
        }
    }

    public File getTargetDirectory() {
        final String targetDirectoryText = targetDirectoryTextField.getText();
        if (StringUtils.isBlank(targetDirectoryText)) {
            return null;
        }

        final File file = new File(targetDirectoryTextField.getText());
        if (file.isDirectory()) {
            return file;
        } else {
            return null;
        }
    }

    public String outputFileNameBase() {
        return this.outputFileNameBaseTextField.getText();
    }
    
    public FileSelectionConfig getFileSelectionConfig() {
    	FileSelectionConfig config = new FileSelectionConfig();
    	
    	config.setBaseOutputFileName(this.outputFileNameBaseTextField.getText());
    	config.setDestinationDirectory(getFileFromText(this.targetDirectoryTextField));
    	config.setFirstStorySourceFile(getFileFromText(this.sourceFileNameTextField));
    	config.setSecondStorySourceFile(getFileFromText(this.secondStorySourceFileNameTextField));
    	config.setThirdStorySourceFile(getFileFromText(this.thirdStorySourceFileNameTextField));
    	config.setStories((Integer)storySpinner.getValue());
    	config.setStoryMethod(getStoryMethod());
    	
    	return config;
    }
    
    private String getStoryMethod() {
    	if (this.storyIndividualFileRadioButton.isSelected()) {
    		return "Individual Files";
    	} else if (this.storyTiledImageRadioButton.isSelected()) {
    		return "Tiled";
    	} else if (this.storySingleFileRadioButton.isSelected()) {
    		return "Single File";
    	} else {
    		return null;
    	}
    }
    
    private File getFileFromText(JTextField textField) {
    	String text = textField.getText();
    	if (StringUtils.isBlank(text)) {
    		return null;
    	} else {
    		return new File(text);
    	}
    }

    public class ChangeGenerationSettingActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent event) {
            fireChangeGenerationSettingsListeners(event);
        }
    }
        
    public class InterpolationContainer {
    	private final String label;
    	private final Object renderingHint;
    	
    	public InterpolationContainer(String label, Object renderingHint) {
    		this.label = label;
    		this.renderingHint = renderingHint;
    	}
    	
    	@Override
    	public String toString() {
    		return this.label;
    	}
    	
    	public Object getRenderingHint() {
    		return this.renderingHint;
    	}
    }        

}
