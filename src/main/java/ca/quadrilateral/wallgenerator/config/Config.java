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
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.media.jai.PerspectiveTransform;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.switchcase.commons.util.XmlDomUtilities;

public class Config {
    private final String resolution;
    private final int sortOrder;
    private Map<String, Dimension> wallSizes = new HashMap<String, Dimension>();
    private Map<String, PerspectiveTransform> transforms = new HashMap<String, PerspectiveTransform>();
    private Map<String, OutputConfig> outputConfigs = new HashMap<String, OutputConfig>();
    
    public Config(String resolution, int sortOrder) {
        this.resolution = resolution;
        this.sortOrder = sortOrder;
    }

    public Collection<String> getOutputConfigTypes() {
        return outputConfigs.keySet();
    }

    public String getResolution() {
        return this.resolution;
    }
    
    public Integer getSortOrder() {
    	return this.sortOrder;
    }

    public Map<String, Dimension> getWallSizes() {
        return new HashMap<String, Dimension>(this.wallSizes);
    }

    public Map<String, PerspectiveTransform> getTransforms() {
        return new HashMap<String, PerspectiveTransform>(transforms);
    }

    public OutputConfig getOutputConfig(String name) {
        return this.outputConfigs.get(name);
    }

    public Dimension getWallSize(String wallType) {
        return wallSizes.get(wallType);
    }

    public PerspectiveTransform getTransform(String transformType) {
        return transforms.get(transformType);
    }

    public void addWallSize(Node wallSizeNode) {
        final String wallType = XmlDomUtilities.getAttributeValue(wallSizeNode, "type");
        final Dimension wallSize = new Dimension(
                                XmlDomUtilities.getAttributeValueAsInteger(wallSizeNode, "width"),
                                XmlDomUtilities.getAttributeValueAsInteger(wallSizeNode, "height")
                            );
        wallSizes.put(wallType, wallSize);
    }

    public void addTransform(Node transformNode) {
        final String transformType = XmlDomUtilities.getAttributeValue(transformNode, "type");
        final String[] transformPoints = StringUtils.split(
                        transformNode.getTextContent().replaceAll("[^0-9.,]", ""), ",");

        final PerspectiveTransform transform = PerspectiveTransform.getQuadToQuad(
                Double.parseDouble(transformPoints[0]),
                Double.parseDouble(transformPoints[1]),
                Double.parseDouble(transformPoints[2]),
                Double.parseDouble(transformPoints[3]),
                Double.parseDouble(transformPoints[4]),
                Double.parseDouble(transformPoints[5]),
                Double.parseDouble(transformPoints[6]),
                Double.parseDouble(transformPoints[7]),
                Double.parseDouble(transformPoints[8]),
                Double.parseDouble(transformPoints[9]),
                Double.parseDouble(transformPoints[10]),
                Double.parseDouble(transformPoints[11]),
                Double.parseDouble(transformPoints[12]),
                Double.parseDouble(transformPoints[13]),
                Double.parseDouble(transformPoints[14]),
                Double.parseDouble(transformPoints[15])
            );

        transforms.put(transformType, transform);
    }

    public void addOutputConfig(Node outputConfigNode) {
        String configType = XmlDomUtilities.getAttributeValue(outputConfigNode, "name");
        boolean supportsMultiStory = XmlDomUtilities.getAttributeValueAsBoolean(outputConfigNode,  "supportsMultiStory", false);
        this.outputConfigs.put(configType, createOutputConfig(configType, supportsMultiStory, outputConfigNode));
    }

    private OutputConfig createOutputConfig(String configType, boolean supportsMultiStory, Node outputConfigNode) {
        Node outputNode = XmlDomUtilities.getChildNode(outputConfigNode, "singleFileOutput");
        if (outputNode != null) {
            return createSingleFileOutputConfig(configType, supportsMultiStory, outputNode);
        }

        outputNode = XmlDomUtilities.getChildNode(outputConfigNode, "individualFileOutput");
        if (outputNode != null) {
            return createIndividualFileOutputConfig(configType, supportsMultiStory, outputNode);
        }

        return null;
    }

    private IndividualFileOutputConfig createIndividualFileOutputConfig(String configType, boolean supportsMultiStory, Node singleFileOutputNode) {
        return new IndividualFileOutputConfig(configType, supportsMultiStory);
    }

    private SingleFileOutputConfig createSingleFileOutputConfig(String configType, boolean supportsMultiStory, Node singleFileOutputNode) {
        try {
            final SingleFileOutputConfig config = new SingleFileOutputConfig(configType);

            config.setSupportsMultiStory(supportsMultiStory);
            config.setOutputHeight(XmlDomUtilities.getAttributeValueAsInteger(singleFileOutputNode, "outputHeight"));
            config.setOutputWidth(XmlDomUtilities.getAttributeValueAsInteger(singleFileOutputNode, "outputWidth"));

            final XPathFactory factory = XPathFactory.newInstance();

            final NodeList positionNodes = (NodeList)factory.newXPath().evaluate("positions/position", singleFileOutputNode, XPathConstants.NODESET);

            for(int n = 0; n < positionNodes.getLength(); n++) {
                final Node positionNode = positionNodes.item(n);
                config.addOutputPosition(
                        XmlDomUtilities.getAttributeValue(positionNode, "type"),
                        new Dimension(
                                XmlDomUtilities.getAttributeValueAsInteger(positionNode, "x"),
                                XmlDomUtilities.getAttributeValueAsInteger(positionNode, "y")
                        )
                    );
            }
            
            final NodeList transparentRegionNodes = (NodeList)factory.newXPath().evaluate("transparentRegions/region", singleFileOutputNode, XPathConstants.NODESET);

            for(int n = 0; n < transparentRegionNodes.getLength(); n++) {
            	final Node transparentRegionNode = transparentRegionNodes.item(n);
            	
            	config.addTransparentRegion(
            			XmlDomUtilities.getAttributeValue(transparentRegionNode, "position", null),
            			XmlDomUtilities.getAttributeValueAsInteger(transparentRegionNode, "x"), 
            			XmlDomUtilities.getAttributeValueAsInteger(transparentRegionNode, "y"), 
            			XmlDomUtilities.getAttributeValueAsInteger(transparentRegionNode, "width"), 
            			XmlDomUtilities.getAttributeValueAsInteger(transparentRegionNode, "height"));
            }
            	
            return config;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

