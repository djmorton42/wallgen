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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import ca.quadrilateral.wallgenerator.config.Config;
import ca.quadrilateral.wallgenerator.config.FileSelectionConfig;
import ca.quadrilateral.wallgenerator.config.PostProcessingConfig;

public class WallGenerationService {
	
    public List<Map<String, BufferedImage>> fetchImages(final FileSelectionConfig fileSelectionConfig, final PostProcessingConfig postProcessingConfig, String resolution) throws FileNotFoundException, IOException {
    	if (fileSelectionConfig.getStories() == 1) {
    		final File sourceFile = fileSelectionConfig.getFirstStorySourceFile();
            if (sourceFile == null) {
                throw new FileNotFoundException("Source File");
            }

            final BufferedImage sourceImage = ImageIO.read(sourceFile);
            
            final List<Map<String, BufferedImage>> generatedImages = new ArrayList<Map<String, BufferedImage>>();
            generatedImages.add(new Generator(chooseConfig(resolution), postProcessingConfig).generate(sourceImage, 1));
            return generatedImages;
    	} else {
    		if (fileSelectionConfig.hasValidSourceFiles()) {
    			if (fileSelectionConfig.getStoryMethod().equals("Individual Files")) {
    				return generateMultiStoryImages(getSourceImages(fileSelectionConfig.getSourceFiles()), fileSelectionConfig, postProcessingConfig, resolution);
    			} else if (fileSelectionConfig.getStoryMethod().equals("Tiled")) {
    				List<File> fileList = new ArrayList<File>();
    				for(int i = 0; i < fileSelectionConfig.getStories(); i++) {
    					fileList.add(fileSelectionConfig.getFirstStorySourceFile());
    				}
    				return generateMultiStoryImages(getSourceImages(fileList), fileSelectionConfig, postProcessingConfig, resolution);
    			} else {    				    				
    				return generateMultiStoryImages(Arrays.asList(ImageIO.read(fileSelectionConfig.getFirstStorySourceFile())), fileSelectionConfig, postProcessingConfig, resolution);
    			}
    		} else {
    			throw new FileNotFoundException("Source File");
    		}
    	}
    }

    private List<Map<String, BufferedImage>> generateMultiStoryImages(List<BufferedImage> sourceImages, final FileSelectionConfig fileSelectionConfig, final PostProcessingConfig postProcessingConfig, String resolution) throws IOException {
    	List<Map<String, BufferedImage>> results = new ArrayList<Map<String, BufferedImage>>();
    	if (sourceImages.size() == 1) {
    		final int stories = fileSelectionConfig.getStories();    		
    		for(int i = 0; i < stories; i++) {
    			results.add(
    					new Generator(chooseConfig(resolution), postProcessingConfig)
    						.generate(sourceImages.get(0), i + 1, stories));
    		}
    	} else {
	    	for(int i = 0; i < sourceImages.size(); i++) {    		
	    		results.add(new Generator(chooseConfig(resolution), postProcessingConfig).generate(sourceImages.get(i), i + 1));	    		
	    	}
    	}
    	return results;
    }
    
    private List<BufferedImage> getSourceImages(List<File> sourceFiles) throws IOException {
    	final List<BufferedImage> sourceImages = new ArrayList<BufferedImage>();
    	for(int i = 0; i < sourceFiles.size(); i++) {
    		final File sourceFile = sourceFiles.get(i);
    		if (sourceFile != null) {
    			sourceImages.add(ImageIO.read(sourceFile));
    		} else {
    			sourceImages.add(new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB));
    		}
    	}
    	return sourceImages;
    }
    
    private Config chooseConfig(String resolution) {
    	return WallGenerator.getConfigManager().getConfig(resolution);
    }
}
