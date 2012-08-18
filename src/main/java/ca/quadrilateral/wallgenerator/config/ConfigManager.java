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
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.switchcase.commons.util.XmlDomUtilities;

public class ConfigManager {
	public final static String TEXT_COLOR_KEY = "Text";
	public final static String BACKGROUND_COLOR_KEY = "Background";
	public final static String TRANSPARENCY_COLOR_KEY = "Transparency";
	public final static String BORDER_COLOR_KEY = "Border";
	public final static String DISTANCE_BLENDING_COLOR_KEY = "Distance Blending";
	
    private Map<String, Config> configs = new HashMap<String, Config>();
    private Map<String, OutputFileConfig> outputFileConfigs = new HashMap<String, OutputFileConfig>();
    private Map<String, Color> colorMap = new HashMap<String, Color>();
    final XPathFactory factory = XPathFactory.newInstance();

    public ConfigManager() {
    	colorMap.put(TEXT_COLOR_KEY, Color.RED);
    	colorMap.put(BACKGROUND_COLOR_KEY, Color.BLACK);
    	colorMap.put(TRANSPARENCY_COLOR_KEY, Color.CYAN);
    	colorMap.put(BORDER_COLOR_KEY, Color.MAGENTA);
    	colorMap.put(DISTANCE_BLENDING_COLOR_KEY, Color.BLACK);
    }

    private void clearConfig() {
        configs.clear();
        outputFileConfigs.clear();
    }

    public void reloadConfig() throws Exception {
        clearConfig();
        loadConfig();
    }

    public void loadConfig() throws Exception {
        final File configFile = new File("config.xml");
        if (!configFile.canRead()) {
            throw new RuntimeException("Can not load config.xml file!");
        }
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        final Document doc = documentBuilder.parse(configFile);

        final NodeList outputFileTypeNodes = (NodeList)factory.newXPath().evaluate("/wallGeneratorConfig/outputFileTypes/outputFileType", doc, XPathConstants.NODESET);
        for(int n = 0; n < outputFileTypeNodes.getLength(); n++) {
            final Node outputFileTypeNode = outputFileTypeNodes.item(n);
            final OutputFileConfig config = new OutputFileConfig();

            config.setType(XmlDomUtilities.getAttributeValue(outputFileTypeNode, "type"));
            config.setEnabled(XmlDomUtilities.getAttributeValueAsBoolean(outputFileTypeNode, "enabled", false));
            config.setSupportsTransparency(XmlDomUtilities.getAttributeValueAsBoolean(outputFileTypeNode, "supportsTransparency", false));

            if (config.isEnabled()) {
                outputFileConfigs.put(config.getType(), config);
            }
        }

        final NodeList configNodes = (NodeList)factory.newXPath().evaluate("/wallGeneratorConfig/configs/config", doc, XPathConstants.NODESET);
        for(int n = 0; n < configNodes.getLength(); n++) {
            createConfig(configNodes.item(n));
        }
    }

    public OutputFileConfig getOutputFileConfig(String type) {
        return outputFileConfigs.get(type);
    }

    public void addConfig(Config config) {
        configs.put(config.getResolution(), config);
    }

    public Config getConfig(String resolution) {
        return configs.get(resolution);
    }

    public void createConfig(Node configNode) throws Exception {
        final String resolution = XmlDomUtilities.getAttributeValue(configNode, "resolution");
        final int sortOrder = XmlDomUtilities.getAttributeValueAsInteger(configNode, "sortOrder", Integer.MAX_VALUE);
        final Config config = new Config(resolution, sortOrder);

        final NodeList wallSizeNodes = (NodeList)factory.newXPath().evaluate("wallSizes/wallSize", configNode, XPathConstants.NODESET);

        for(int n = 0; n < wallSizeNodes.getLength(); n++) {
            final Node wallSizeNode = wallSizeNodes.item(n);
            config.addWallSize(wallSizeNode);
        }

        final NodeList transformNodes = (NodeList)factory.newXPath().evaluate("transforms/transform", configNode, XPathConstants.NODESET);

        for(int n = 0; n < transformNodes.getLength(); n++) {
            final Node transformNode = transformNodes.item(n);
            config.addTransform(transformNode);
        }

        final NodeList outputConfigNodes = (NodeList)factory.newXPath().evaluate("outputs/output", configNode, XPathConstants.NODESET);

        for(int n = 0; n < outputConfigNodes.getLength(); n++) {
            final Node outputConfigNode = outputConfigNodes.item(n);
            config.addOutputConfig(outputConfigNode);
        }

        addConfig(config);
    }

    public Collection<String> getAvailableResolutions() {
        final List<Config> resolutionConfigs = new ArrayList<Config>(configs.values());
        final List<String> resolutionConfigStrings = new ArrayList<String>();
    	Collections.sort(resolutionConfigs, new Comparator<Config>() {
			@Override
			public int compare(Config config, Config otherConfig) {
				return config.getSortOrder().compareTo(otherConfig.getSortOrder());
			}
    	});
    	
    	for(Config config : resolutionConfigs) {
    		resolutionConfigStrings.add(config.getResolution());
    	}
    	
    	return resolutionConfigStrings;
    }

    public Collection<String> getAvailableOutputFormats() {
        final Set<String> outputFormats = new TreeSet<String>();
        for(String configResolution : configs.keySet()) {
            outputFormats.addAll(configs.get(configResolution).getOutputConfigTypes());
        }
        return outputFormats;
    }

    public Collection<String> getAvailableOutputFileTypes() {
        return new TreeSet<String>(outputFileConfigs.keySet());
    }

    public Color getColor(String type) {
    	return this.colorMap.get(type);
    }
    
    public void setColor(String type, Color color) {
    	this.colorMap.put(type, color);    	
    }
    
}
