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

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.font.LineMetrics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.config.OutputConfig;
import ca.quadrilateral.wallgenerator.config.SingleFileOutputConfig;
import ca.quadrilateral.wallgenerator.ui.WallGeneratorFrame;

public class SingleFileOutputGenerator extends OutputGenerator {
	private static final Logger log = LoggerFactory.getLogger(WallGeneratorFrame.class);

    @Override
    public void generate(OutputConfig outputConfig, File targetDirectory, String fileBaseName, String outputFormat, List<Map<String, BufferedImage>> imageMaps, boolean useTransparency) throws IOException {
        final SingleFileOutputConfig config = (SingleFileOutputConfig)outputConfig;
        final BufferedImage outputImage = new BufferedImage(config.getOutputWidth(), config.getOutputHeight(),        		
                useTransparency
                    ? BufferedImage.TYPE_INT_ARGB
                    : BufferedImage.TYPE_INT_RGB);

        if (useTransparency) {
        	log.info("Using alpha transparency...");
        } else {
        	log.info("Not using alpha transparency...");
        }
                
        final File targetFile = generateOutputFile(targetDirectory, fileBaseName, outputFormat);

        generateOutput(imageMaps, outputImage, config, useTransparency);

        log.info("Writing output file...");
        
        ImageIO.write(outputImage, outputFormat, targetFile);
        
        log.info("Done writing output file.");

    }

    private File generateOutputFile(File targetDirectory, String fileBaseName, String outputFormat) {
        File outputFile =  new File(targetDirectory, fileBaseName.replace(' ', '_') + "." + outputFormat.toLowerCase());

        log.info("Using Output Filename: " + outputFile.getAbsolutePath());
        
        return outputFile;
    }
    
    private Rectangle getBorderRectangle(Rectangle transparentRegion, int thickness) {
    	return new Rectangle(transparentRegion.x - (1 * thickness), transparentRegion.y - (1 * thickness), transparentRegion.width + (2 * thickness) -1, transparentRegion.height + (2 * thickness) -1);
    }	

    private void generateOutput(List<Map<String, BufferedImage>> images, BufferedImage outputImage, SingleFileOutputConfig config, boolean useTransparency) {
        final Graphics2D graphics = outputImage.createGraphics();
        
        log.info("Drawing Background...");
        
        graphics.setColor(WallGenerator.getConfigManager().getColor("Background"));
        graphics.fillRect(0,  0, outputImage.getWidth(), outputImage.getHeight());
        
        log.info("Drawing Transparent Regions...");
        
        final Color transparencyColor = WallGenerator.getConfigManager().getColor("Transparency");
        final Color borderColor = WallGenerator.getConfigManager().getColor("Border");
                
        Collection<Rectangle> transparentRegions = config.getTransparentRegions();
        for(Rectangle transparentRegion : transparentRegions) {
        	log.debug("Drawing Transparent Region: " + transparentRegion.toString());
        	graphics.setColor(transparencyColor);
        	Composite originalComposite = graphics.getComposite();
        	if (useTransparency) {
        		graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.CLEAR));
        	}        	
        	graphics.fill(transparentRegion);
        	graphics.setComposite(originalComposite);        	
        	        	
        	graphics.setColor(borderColor);
        	graphics.draw(getBorderRectangle(transparentRegion, 1));
        	graphics.draw(getBorderRectangle(transparentRegion, 2));
        	
        }
        
        log.info("Drawing Images...");
        
        Map<String, Dimension> outputPositions = config.getOutputPositions();       
        
        for(String key : outputPositions.keySet()) {
        	Rectangle clipRegion = config.getTransparentRegionByPosition(key);
        	if (clipRegion != null) {
        		graphics.setClip(clipRegion);
        	} else {
        		graphics.setClip(null);
        	}

            Dimension outputPosition = outputPositions.get(key);
        
            for(int i = 0; i < images.size(); i++) {
            	final Map<String, BufferedImage> imageSet = images.get(i);
        		final BufferedImage image = imageSet.get(key);
        		
        		int verticalOffset = calculateVerticalOffset(outputPosition.height, images.get(0).get(key).getHeight(), image.getHeight());
        		
                graphics.drawImage(image, outputPosition.width, verticalOffset, null);
        	}
        }

        graphics.setClip(null);
        
        writeAppSignature(graphics, outputImage);

        if (!useTransparency) {
        	outputImage.setRGB(0, 0, transparencyColor.getRGB());
        }
        
        graphics.dispose();
    }
    
    private int calculateVerticalOffset(int initialOutputPosition, int firstStoryImageHeight, int currentStoryImageHeight) {
    	return initialOutputPosition - (currentStoryImageHeight - firstStoryImageHeight);
    }
    
    private void writeAppSignature(Graphics2D graphics, BufferedImage outputImage) {
    	final Color initialColor = graphics.getColor();
    	final String appSignatureLine1 = "Wallset generated using Wall Generator by Daniel Morton";
    	final String appSignatureLine2 = "(www.quadrilateral.ca/wallgen)";
    	final LineMetrics line1Metrics = graphics.getFontMetrics().getLineMetrics(appSignatureLine1, graphics);
    	final LineMetrics line2Metrics = graphics.getFontMetrics().getLineMetrics(appSignatureLine2, graphics);
    	
    	final float line1Height = line1Metrics.getHeight();
    	final float line2Height = line2Metrics.getHeight();
    	
    	
    	graphics.setColor(WallGenerator.getConfigManager().getColor("Text"));
    	graphics.drawString(appSignatureLine1, 5.0f, (outputImage.getHeight() - line2Height - line1Height) - 5);
    	graphics.drawString(appSignatureLine2, 5.0f, (outputImage.getHeight() - line2Height) - 5);
    	graphics.setColor(initialColor);
    }
}
