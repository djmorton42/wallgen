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

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class PreviewGenerationService {
	public BufferedImage generatePreview(List<Map<String, BufferedImage>> generatedImages) throws FileNotFoundException, IOException {
		if (generatedImages == null || generatedImages.size() == 0) {
			throw new IllegalArgumentException("Invalid generated images passed to generatePreview.");
		}
		final Map<String, BufferedImage> firstStoryImages = generatedImages.get(0);
		final int largestBaseImageHeight = firstStoryImages.get("CloseLeft").getHeight();

		final int previewImageWidth = calculatePreviewImageWidth(generatedImages);

		final BufferedImage previewImage = new BufferedImage(previewImageWidth, largestBaseImageHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = previewImage.createGraphics();

		int drawPosition = 0;

		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "CloseLeft", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "CloseFront", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "NearLeft", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "NearFront", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "FarLeft", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "FarFront", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "FarRight", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "NearRight", drawPosition, largestBaseImageHeight, 0);
		drawPosition += drawPreviewImagePortion(generatedImages, graphics, "CloseRight", drawPosition, largestBaseImageHeight, 0);

		graphics.dispose();

		return previewImage;
	}

	private int drawPreviewImagePortion(List<Map<String, BufferedImage>> generatedImages, Graphics2D graphics, String key, int drawPosition, int previewImageHeight, int verticalOffset) {

		final Map<String, BufferedImage> firstFloorImages = generatedImages.get(0);

		final BufferedImage firstFloorImage = firstFloorImages.get(key);

		final int verticalPosition = ((previewImageHeight - firstFloorImage.getHeight()) / 2) + verticalOffset;
		graphics.drawImage(firstFloorImage, drawPosition, verticalPosition, null);
		int outputWidth = firstFloorImage.getWidth();

		for(int i = 1; i < generatedImages.size(); i++) {
			final BufferedImage storyImage = generatedImages.get(i).get(key);
			graphics.drawImage(storyImage, drawPosition, verticalPosition - (storyImage.getHeight() - firstFloorImage.getHeight()), null);
		}	

		return outputWidth;
	}

	private int calculatePreviewImageWidth(List<Map<String, BufferedImage>> generatedImages) {
		int width = 0;
		Map<String, BufferedImage> firstStoryImages = generatedImages.get(0);

		width += (((BufferedImage)firstStoryImages.get("CloseLeft")).getWidth() * 2);
		width += ((BufferedImage)firstStoryImages.get("CloseFront")).getWidth();
		width += (((BufferedImage)firstStoryImages.get("NearLeft")).getWidth() * 2);
		width += ((BufferedImage)firstStoryImages.get("NearFront")).getWidth();
		width += (((BufferedImage)firstStoryImages.get("FarLeft")).getWidth() * 2);
		width += ((BufferedImage)firstStoryImages.get("FarFront")).getWidth();

		return width;
	}

}
