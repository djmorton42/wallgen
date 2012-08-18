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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JPanel;
import javax.swing.Timer;

public class ImagePreviewPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    private List<Image> images = null;
    private int width = 0;
    private int height = 0;
    private Timer timer = null;
    private int currentImageCounter = 0;

    public ImagePreviewPanel(BufferedImage image) {
        this(image.getWidth(), image.getHeight());
        this.setImage(image);
    }

    public ImagePreviewPanel(int width, int height) {
        super(true);
        this.width = width;
        this.height = height;
        this.setPreferredSize(new Dimension(width, height));
    }

    public final void setImage(Image image) {
    	setImages(new ArrayList<Image>(Arrays.asList(image)));
    }
    
    public final void setImages(List<Image> images) {
    	this.images = images;
   		this.currentImageCounter = 0;

   		if (timer != null) {
   			timer.stop();
   		}
   		
    	timer = new Timer(1500, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				advanceImage();
				repaint();
			}
    	});
    	timer.start();
    	this.repaint();
    }
    
    private void advanceImage() {
    	if (this.images != null) {
	    	this.currentImageCounter++;
	    	if (this.currentImageCounter >= this.images.size()) {
	    		this.currentImageCounter = 0;
	    	}
    	}
    }

    private Image getCurrentImage() {
    	if (this.images != null && this.currentImageCounter < this.images.size()) {
    		return this.images.get(this.currentImageCounter);
    	} else {
    		return null;
    	}
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final Image image = getCurrentImage();
        if (image != null) {
            ((Graphics2D)g).drawImage(image, 0, 0, width, height, null);
        }
    }
}
