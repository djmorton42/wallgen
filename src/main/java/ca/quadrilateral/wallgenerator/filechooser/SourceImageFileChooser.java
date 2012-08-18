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

package ca.quadrilateral.wallgenerator.filechooser;

import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.filefilter.ImageIOSupportedImageTypeFileFilter;
import ca.quadrilateral.wallgenerator.ui.ImagePreviewPanel;

public class SourceImageFileChooser extends JFileChooser {
	private static final Logger log = LoggerFactory.getLogger(SourceImageFileChooser.class);
	
	private final ImagePreviewPanel imagePreviewPanel;
	
	public SourceImageFileChooser() {
		final String executionDirectory = System.getProperty("user.dir");
		
		this.setCurrentDirectory(new File(executionDirectory));
		this.setMultiSelectionEnabled(false);
		imagePreviewPanel = new ImagePreviewPanel(112, 134);
		this.setAccessory(imagePreviewPanel);
        this.setDialogType(JFileChooser.OPEN_DIALOG);
        this.setFileSelectionMode(JFileChooser.FILES_ONLY);
        this.setDialogTitle("Select Source File");
        this.setFileFilter(new ImageIOSupportedImageTypeFileFilter());
        
        this.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				final String propertyName = evt.getPropertyName();
				BufferedImage newImage = null;
				boolean doUpdate = false;
				if (JFileChooser.DIRECTORY_CHANGED_PROPERTY.equals(propertyName)) {
					doUpdate = true;
				} else if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY.equals(propertyName)) {
					final File selectedFile = (File)evt.getNewValue();
					try {
						if (selectedFile != null) {
							newImage = ImageIO.read(selectedFile);							
						} else {
							newImage = null;
						}
					} catch (IOException ioe) {
						log.warn("IO Exception loading image file for preview in file chooser.");
						newImage = null;
						
					}
					doUpdate = true;
				}
				
				if (doUpdate) {
					if (newImage != null) {
						imagePreviewPanel.setImage(newImage);
					}
				}
			}
        });      

	}
}

/*
        
        

 */
