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

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.quadrilateral.wallgenerator.config.ConfigManager;
import ca.quadrilateral.wallgenerator.ui.WallGeneratorFrame;

public class WallGenerator {
	private static final Logger log = LoggerFactory.getLogger(WallGeneratorFrame.class);
    private static ConfigManager configManager = null;

    public static void main(String[] args) throws Exception {
    	log.info("Wall Generation Application Initialized");
    	
        configManager = new ConfigManager();
        configManager.loadConfig();

        setLookAndFeel();
        
        WallGeneratorFrame frame = new WallGeneratorFrame();
        frame.setVisible(true);
    }
    
    private static void setLookAndFeel() {
    	try {
    	    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
    	        if ("Nimbus".equals(info.getName())) {
    	            UIManager.setLookAndFeel(info.getClassName());
    	            break;
    	        }
    	    }
    	} catch (Exception e) {
    		log.info("Could not set Nimbus Look and Feel... Using default Look and Feel");
    		try {
				UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
			} catch (Exception ex) {
				log.error("Could not set a Look and Feel for the UI.");
				System.exit(1);
			}
    	}
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }
    
    public static ImageIcon loadImageIcon(String fileName) {
    	try {
    		return new ImageIcon(ImageIO.read(WallGenerator.class.getResourceAsStream(fileName)));
    	} catch (Exception e) {
    		log.error("Error loading palette.png icon", e);
    		return null;
    	}
    }

}
