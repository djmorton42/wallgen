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
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.PerspectiveTransform;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.config.Config;
import ca.quadrilateral.wallgenerator.config.PostProcessingConfig;

import com.mortennobel.imagescaling.AdvancedResizeOp;
import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;
import com.mortennobel.imagescaling.MultiStepRescaleOp;
import com.mortennobel.imagescaling.ResampleOp;

public class Generator {
	private static final Logger log = LoggerFactory.getLogger(Generator.class);
	private static final float ZERO_PERCENT_THRESHOLD = 0.009f;
	private static final PostProcessingConfig noOpPostProcessingConfig = new PostProcessingConfig();
	
	static {
		noOpPostProcessingConfig.distanceBlendColor = Color.BLACK;
		noOpPostProcessingConfig.farDarkening = 0f;
		noOpPostProcessingConfig.nearDarkening = 0f;
		noOpPostProcessingConfig.postProcessingOrder = 0;
		noOpPostProcessingConfig.scalingMethod = RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
		noOpPostProcessingConfig.softening = 0f;
		noOpPostProcessingConfig.unsharpenMask = UnsharpenMask.None;
	}
	
    private final Config config;
    private final Map<String, BufferedImage> stackingBaselines;    

    private PostProcessingConfig postProcessingConfig;
    
    public Generator(Config config, PostProcessingConfig postProcessingConfig) {
        this.config = config;
        
        this.postProcessingConfig = noOpPostProcessingConfig;
        
        this.stackingBaselines = generate(generateBaselineSourceImage(), 1);
        
        this.postProcessingConfig = postProcessingConfig;
    }
    
    private final BufferedImage generateBaselineSourceImage() {
    	final Dimension wallSize = config.getWallSize("CloseFront");
    	final BufferedImage baselineSourceImage = new BufferedImage(wallSize.width, wallSize.height, BufferedImage.TYPE_INT_ARGB);
    	final Graphics2D graphics = baselineSourceImage.createGraphics();
    	graphics.setColor(Color.BLACK);
    	graphics.fillRect(0, 0, wallSize.width, wallSize.height);
    	
    	graphics.dispose();
    	
    	return baselineSourceImage;
    }

    public final Map<String, BufferedImage> generate(BufferedImage baseSourceImage, int story, int storiesForSingleImage) {
        final Map<String, BufferedImage> resultMap = new HashMap<String, BufferedImage>();

        final Color blendColor = this.postProcessingConfig.distanceBlendColor;
        
        BufferedImage sourceImage = baseSourceImage;
        
        if (storiesForSingleImage > 1) {
        	final Dimension wallSize = config.getWallSize("CloseFront");
        	int largeSourceHeight = wallSize.height * storiesForSingleImage;
        	final BufferedImage largeSourceImage = scaleImage(baseSourceImage, new Dimension(wallSize.width, largeSourceHeight));
        	sourceImage = largeSourceImage.getSubimage(0, (( (storiesForSingleImage - 1) - (story - 1)) * wallSize.height), wallSize.width, wallSize.height);
        }
        
        final BufferedImage baseImage = scaleImage(sourceImage, "CloseFront");

        resultMap.put("CloseFront", baseImage);

        final Dimension closeFrontSideSize = config.getWallSize("CloseFrontSide");
        final Dimension nearFrontSideSize = config.getWallSize("NearFrontSide");

        final BufferedImage baseImageRightHalf = baseImage.getSubimage(baseImage.getWidth() / 2, 0, baseImage.getWidth() / 2, baseImage.getHeight());
        final BufferedImage closeFrontRight = baseImage.getSubimage(baseImage.getWidth() - closeFrontSideSize.width, 0, closeFrontSideSize.width, baseImage.getHeight());

        resultMap.put("CloseFrontRight", closeFrontRight);

        final BufferedImage baseImageLeftHalf = baseImage.getSubimage(0, 0, baseImage.getWidth() / 2, baseImage.getHeight());
        final BufferedImage closeFrontLeft = baseImage.getSubimage(0, 0, closeFrontSideSize.width, baseImage.getHeight());

        resultMap.put("CloseFrontLeft", closeFrontLeft);

        BufferedImage nearByFront = scaleImage(baseImage, "NearFront");

        final BufferedImage nearByFrontOriginal = new BufferedImage(nearByFront.getWidth(), nearByFront.getHeight(), BufferedImage.TYPE_INT_ARGB);

        final Graphics2D nearByFrontOriginalGraphics = nearByFrontOriginal.createGraphics();
        nearByFrontOriginalGraphics.drawImage(nearByFront, 0, 0, null);
        nearByFrontOriginalGraphics.dispose();
        
        nearByFront = getDarkenedSourceImage(nearByFront, (float)postProcessingConfig.nearDarkening, (float)postProcessingConfig.nearDarkening, true, blendColor);
        
        final BufferedImage nearByFrontRight = nearByFront.getSubimage(nearByFront.getWidth() - nearFrontSideSize.width, 0, nearFrontSideSize.width, nearByFront.getHeight());
        final BufferedImage nearByFrontLeft = nearByFront.getSubimage(0, 0, nearFrontSideSize.width, nearByFront.getHeight());

        resultMap.put("NearFront", nearByFront);
        resultMap.put("NearFrontRight", nearByFrontRight);
        resultMap.put("NearFrontLeft", nearByFrontLeft);

        BufferedImage farFront = scaleImage(nearByFrontOriginal, "FarFront");
        
        final BufferedImage farFrontOriginal = new BufferedImage(farFront.getWidth(), farFront.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        final Graphics2D farFrontOriginalGraphics = farFrontOriginal.createGraphics();
        farFrontOriginalGraphics.drawImage(farFront, 0, 0, null);
        farFrontOriginalGraphics.dispose();
                
        farFront = getDarkenedSourceImage(farFront, (float)postProcessingConfig.farDarkening, (float)postProcessingConfig.farDarkening, true, blendColor);
                        
        resultMap.put("FarFront", farFront);

        resultMap.put("FarFrontSecondary", farFront);

        final BufferedImage closeLeft = generateTransformedImage(baseImageRightHalf, "CloseSide", "CloseLeft");
        resultMap.put("CloseLeft", closeLeft);

        final BufferedImage closeRight = generateTransformedImage(baseImageLeftHalf, "CloseSide", "CloseRight");
        resultMap.put("CloseRight", closeRight);

        final BufferedImage nearLeft = generateTransformedImage(getDarkenedSourceImage(baseImage, 0f, (float)postProcessingConfig.nearDarkening, true, blendColor), "NearSide", "NearLeft");
        resultMap.put("NearLeft", nearLeft);

        final BufferedImage nearRight = generateTransformedImage(getDarkenedSourceImage(baseImage, 0f, (float)postProcessingConfig.nearDarkening, false, blendColor), "NearSide", "NearRight");
        resultMap.put("NearRight", nearRight);

        final BufferedImage farLeft = generateTransformedImage(getDarkenedSourceImage(nearByFrontOriginal, (float)postProcessingConfig.nearDarkening, (float)postProcessingConfig.farDarkening, true, blendColor), "FarSide", "FarLeft");
        resultMap.put("FarLeft", farLeft);

        final BufferedImage farRight = generateTransformedImage(getDarkenedSourceImage(nearByFrontOriginal, (float)postProcessingConfig.nearDarkening, (float)postProcessingConfig.farDarkening, false, blendColor), "FarSide", "FarRight");
        resultMap.put("FarRight", farRight);

        if (story > 1) {
        	return generateStoryImages(resultMap, story);
        } else {
        	return resultMap;
        }
    }

    private Map<String, BufferedImage> generateStoryImages(Map<String, BufferedImage> source, int story) {
    	final Map<String, BufferedImage> resultMap = new HashMap<String, BufferedImage>();

    	for(String key : this.stackingBaselines.keySet()) {
    		final BufferedImage baselineImage = this.stackingBaselines.get(key);
    		final BufferedImage sourceImage = source.get(key);
    		final BufferedImage image = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight() * story, BufferedImage.TYPE_INT_ARGB);
    		Graphics2D graphics = image.createGraphics();
    		log.debug("Key: " + key);
    		log.debug("Height: " + baselineImage.getHeight());
    		for(int x = 0; x < baselineImage.getWidth(); x++) {
    			int startY = -1;
    			int endY = -1;
    			for(int y = 0; y < baselineImage.getHeight(); y++) {
    				if ((baselineImage.getRGB(x, y) & 0xFF000000) != 0) {
    					if (startY == -1) {
    						startY = y;
    					}
    					endY = y;
    				}
    			}
    			log.debug("StartY: " + startY + " EndY: " + endY);
    			BufferedImage slice = sourceImage.getSubimage(x,  startY, 1, endY - startY + 1);
    			int writeOffset = ((story - 1) * sourceImage.getHeight()) + startY;
    			writeOffset -= ((endY - startY + 1) * (story - 1));
    			
    			graphics.drawImage(slice, x, writeOffset, null);
    		}
    		graphics.dispose();
    		resultMap.put(key, image);
    	}
    	
    	return resultMap;
    }
    
    public Map<String, BufferedImage> generate(BufferedImage sourceImage, int story) {
    	return generate(sourceImage, story, 1);
    }
    
    private BufferedImage getDarkenedSourceImage(BufferedImage sourceImage, float startAlpha, float endAlpha, boolean leftToRight, Color blendColor) {
    	if (startAlpha > ZERO_PERCENT_THRESHOLD || endAlpha > ZERO_PERCENT_THRESHOLD) {
    		final BufferedImage blendSource = new BufferedImage(1, sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    		final BufferedImage modifiedSourceImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    		
    		final Graphics2D blendSourceGraphics = blendSource.createGraphics();
    		blendSourceGraphics.setColor(blendColor);
    		blendSourceGraphics.fillRect(0, 0, 1, sourceImage.getHeight());
    		blendSourceGraphics.dispose();
    
    		final Graphics2D graphics = modifiedSourceImage.createGraphics();
    		graphics.drawImage(sourceImage, 0, 0, null);
    		
    		final float delta = (endAlpha - startAlpha) / sourceImage.getWidth();
    		
    		if (leftToRight) {
    			for(int i = 0; i < sourceImage.getWidth(); i++) {
    				final float alpha = startAlpha + (delta * i);
    				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    				graphics.drawImage(blendSource, i, 0, null);
    			}
    		} else {
    			for(int i = sourceImage.getWidth() - 1; i >= 0; i--) {
    				final float alpha = startAlpha + (delta * ((sourceImage.getWidth() - 1) - i));
    				graphics.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));
    				graphics.drawImage(blendSource, i, 0, null);    				
    			}
    		}    		    		    		
    		graphics.dispose();
    		
    		for(int x = 0; x < sourceImage.getWidth(); x++) {
    			for(int y = 0; y < sourceImage.getHeight(); y++) {
    				int sourceAlphaValue = (sourceImage.getRGB(x, y) & 0xFF000000);
    				int targetPixelColor = (modifiedSourceImage.getRGB(x, y) & 0x00FFFFFF);
    				modifiedSourceImage.setRGB(x, y, (targetPixelColor | sourceAlphaValue));
    			}
    		}
    		
    		return modifiedSourceImage;
    	} else {
    		return sourceImage;
    	}
    }

    private BufferedImage generateTransformedImage(BufferedImage sourceImage, String wallSizeKey, String transformKey) {
        final PerspectiveTransform transform = config.getTransform(transformKey);

        final Dimension wallSize = config.getWallSize(wallSizeKey);

        final BufferedImage resultImage = new BufferedImage(wallSize.width, wallSize.height, BufferedImage.TYPE_INT_ARGB);

        applyTransform(sourceImage, resultImage, transform);
        return resultImage;
    }

    private AdvancedResizeOp getImageScaler(int targetWidth, int targetHeight) {
    	AdvancedResizeOp result = null;
    	if (postProcessingConfig.scalingMethod.getClass().equals(String.class)) {
    		result = new ResampleOp(targetWidth, targetHeight);
    	} else {
    		result = new MultiStepRescaleOp(targetWidth, targetHeight, postProcessingConfig.scalingMethod);
    	}
    	log.info("Using Image Scaler: " + result);
    	return result;
    }	
    
    private BufferedImage scaleImage(BufferedImage sourceImage, Dimension targetDimension) {
        BufferedImage scaledImage = null;
        
        final int targetWidth = targetDimension.width;
        final int targetHeight = targetDimension.height;
        
        if (sourceImage.getHeight() == targetHeight && sourceImage.getWidth() == targetWidth) {
        	return sourceImage;
        }	
        
       	scaledImage = getImageScaler(targetWidth, targetHeight).filter(sourceImage, null);
       
        return postProcess(scaledImage);    	
    }
    
    private BufferedImage scaleImage(BufferedImage sourceImage, String configKey) {
        final Dimension wallSize = config.getWallSize(configKey);
        return scaleImage(sourceImage, wallSize);
    }
    
    private BufferedImage postProcess(BufferedImage image) {
    	BufferedImage processedImage = image;
    	if (postProcessingConfig.softening == null || postProcessingConfig.unsharpenMask == UnsharpenMask.None) {
    		processedImage = unsharpMask(processedImage, postProcessingConfig.unsharpenMask);
    		processedImage = soften(processedImage, postProcessingConfig.softening);
    	} else {
    		if (postProcessingConfig.postProcessingOrder == PostProcessingConfig.SOFTEN_BEFORE_UNSHARPEN) {
        		processedImage = soften(processedImage, postProcessingConfig.softening);    			
        		processedImage = unsharpMask(processedImage, postProcessingConfig.unsharpenMask);    			
    		} else if (postProcessingConfig.postProcessingOrder == PostProcessingConfig.UNSHARPEN_BEFORE_SOFTEN) {
        		processedImage = unsharpMask(processedImage, postProcessingConfig.unsharpenMask);
        		processedImage = soften(processedImage, postProcessingConfig.softening);    			
    		}
    	}
    	
    	return processedImage;
    }
    
    private BufferedImage unsharpMask(BufferedImage sourceImage, UnsharpenMask maskValue) {
    	if (maskValue == UnsharpenMask.None) {
    		return sourceImage;
    	}
    	log.info("Applying Unsharpen Mask: " + maskValue);
    	ResampleOp resampleOp = new ResampleOp(sourceImage.getWidth(), sourceImage.getHeight());
    	resampleOp.setUnsharpenMask(maskValue);
    	return resampleOp.filter(sourceImage, null);
    }
    
    public BufferedImage soften(BufferedImage sourceImage, Float softenFactor) {    	
        if (softenFactor == null || softenFactor <= ZERO_PERCENT_THRESHOLD)
            return sourceImage;
        else {
        	BufferedImage image = convertToPreMultipliedAlpha(sourceImage);
        	
        	log.info("Applying Softening...");
            float[] softenArray = {0, softenFactor, 0, softenFactor, 1-(softenFactor*4), softenFactor, 0, softenFactor, 0};
            Kernel kernel = new Kernel(3, 3, softenArray);
            ConvolveOp cOp = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
            return convertFromPreMultipliedAlpha(cOp.filter(image, null));
        }
    }
    
    private BufferedImage convertToPreMultipliedAlpha(BufferedImage sourceImage) {
    	BufferedImage premultipliedAlphaImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
    	Graphics2D graphics = premultipliedAlphaImage.createGraphics();
    	graphics.drawImage(sourceImage,  0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
    	graphics.dispose();
    	return premultipliedAlphaImage;
    }
    
    private BufferedImage convertFromPreMultipliedAlpha(BufferedImage sourceImage) {
    	BufferedImage nonPremultipliedAlphaImage = new BufferedImage(sourceImage.getWidth(), sourceImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
    	Graphics2D graphics = nonPremultipliedAlphaImage.createGraphics();
    	graphics.drawImage(sourceImage,  0, 0, sourceImage.getWidth(), sourceImage.getHeight(), null);
    	graphics.dispose();
    	return nonPremultipliedAlphaImage;
    }
    

    private void applyTransform(BufferedImage sourceImage, BufferedImage destinationImage, PerspectiveTransform transform) {
        for(int x = 0; x < destinationImage.getWidth(); x++) {
            for(int y = 0; y < destinationImage.getHeight(); y++) {
                try {
                    final Point2D sourcePoint = new Point2D.Double(x, y);
                    final Point2D destPoint = new Point2D.Double();
                    transform.inverseTransform(sourcePoint, destPoint);

                    int destPointX = (int)destPoint.getX();
                    int destPointY = (int)destPoint.getY();

                    if (destPointX < 0 ||
                            destPointY < 0 ||
                            destPointX >= sourceImage.getWidth() ||
                            destPointY >= sourceImage.getHeight()) {
                        continue;
                    }

                    final int color = sourceImage.getRGB(destPointX, destPointY);
                    destinationImage.setRGB((int)sourcePoint.getX(), (int)sourcePoint.getY(), color);
                } catch (Exception e) {
                    throw new RuntimeException("Error transforming image!", e);
                }
            }
        }
    }
}
