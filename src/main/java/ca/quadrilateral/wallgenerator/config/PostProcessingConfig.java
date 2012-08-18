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

package ca.quadrilateral.wallgenerator.config;

import java.awt.Color;

import com.mortennobel.imagescaling.AdvancedResizeOp.UnsharpenMask;

public class PostProcessingConfig {
	public static final int UNSHARPEN_BEFORE_SOFTEN = 0;
	public static final int SOFTEN_BEFORE_UNSHARPEN = 1;
	
	public UnsharpenMask unsharpenMask = UnsharpenMask.None;
	public Float softening = null;
	public Object scalingMethod = null;
	public Float nearDarkening = null;
	public Float farDarkening = null;
	public int postProcessingOrder = UNSHARPEN_BEFORE_SOFTEN;
	public Color distanceBlendColor = null;
	
	public PostProcessingConfig duplicate() {
		PostProcessingConfig config = new PostProcessingConfig();
		config.distanceBlendColor = distanceBlendColor;
		config.farDarkening = farDarkening;
		config.nearDarkening = nearDarkening;
		config.postProcessingOrder = postProcessingOrder;
		config.scalingMethod = scalingMethod;
		config.softening = softening;
		config.unsharpenMask = unsharpenMask;
		
		return config;
	}
}
