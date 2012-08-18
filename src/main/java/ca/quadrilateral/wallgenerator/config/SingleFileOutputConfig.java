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

import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


public class SingleFileOutputConfig extends OutputConfig {
    private int outputHeight = 0;
    private int outputWidth = 0;
    private Map<String, Dimension> outputPositions = new HashMap<String, Dimension>();
    private Map<String, Rectangle> transparentRegions = new HashMap<String, Rectangle>();

    public SingleFileOutputConfig(String name) {
        super.setName(name);
    }

    public int getOutputHeight() {
        return outputHeight;
    }

    public void setOutputHeight(int outputHeight) {
        this.outputHeight = outputHeight;
    }

    public int getOutputWidth() {
        return outputWidth;
    }

    public void setOutputWidth(int outputWidth) {
        this.outputWidth = outputWidth;
    }

    public Map<String, Dimension> getOutputPositions() {
        return new HashMap<String, Dimension>(this.outputPositions);
    }

    public Dimension getOutputPosition(String type) {
        return outputPositions.get(type);
    }

    public void addOutputPosition(String type, Dimension location) {
        outputPositions.put(type, location);
    }
    
    public void addTransparentRegion(int x, int y, int width, int height) {
    	transparentRegions.put(x + "," + y, new Rectangle(x, y, width, height));
    }
    public void addTransparentRegion(String position, int x, int y, int width, int height) {
    	transparentRegions.put(position, new Rectangle(x, y, width, height));
    }
    
    public Collection<Rectangle> getTransparentRegions() {
    	return this.transparentRegions.values();
    }	
    
    public Rectangle getTransparentRegionByPosition(String position) {
    	return transparentRegions.get(position);
    }

}
