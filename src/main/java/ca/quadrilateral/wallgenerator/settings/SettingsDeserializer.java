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

package ca.quadrilateral.wallgenerator.settings;

import java.awt.Color;
import java.io.File;
import java.text.MessageFormat;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ca.quadrilateral.wallgenerator.WallGenerator;
import ca.quadrilateral.wallgenerator.config.ConfigManager;
import ca.quadrilateral.wallgenerator.ui.OptionPanel;
import ca.switchcase.commons.util.XmlDomUtilities;

public class SettingsDeserializer {
	private static final Logger log = LoggerFactory.getLogger(SettingsDeserializer.class);
	
	public void deserializeSettings(File file, OptionPanel optionPanel) throws Exception {
		
		final Document doc = getXmlDocument(file);
		loadBasicOptions(doc, optionPanel);
		loadColorOptions(doc, optionPanel);
		loadProcessingOptions(doc, optionPanel);
		optionPanel.setSettingsHaveChanged(this, "LoadPreset:" + file.getAbsolutePath());
		
	}
	
	private Document getXmlDocument(File file) throws Exception {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		final Document doc = documentBuilder.parse(file);									

		validateSettings(doc);
		
		return doc;
	}
	
	private void validateSettings(Document doc) {
		Node rootLevelNode = doc.getFirstChild();
		while (rootLevelNode != null) {
			log.debug("Testing Root Level Node Name: " + rootLevelNode.getNodeName());
			if ("wallgen-presets".equals(rootLevelNode.getNodeName())) {
				return;
			} else {
				rootLevelNode = rootLevelNode.getNextSibling();
			}
		}
		
		throw new IllegalArgumentException("Invalid Presets File!  Expected root element not found!");
				
	}

	private Node getOptionNode(Document doc, String nodeName) {
		final NodeList optionsNodeList = doc.getElementsByTagName(nodeName);
		if (optionsNodeList.getLength() == 0) {
			throw new IllegalArgumentException(MessageFormat.format("Invalid Presets File!  No {0} element found!", nodeName));
		} else if (optionsNodeList.getLength() > 1) {
			throw new IllegalArgumentException(MessageFormat.format("Invalid Presets FIle!  Too many {0} elements found!", nodeName));
		}
		
		return optionsNodeList.item(0);		
	}
	
	private void loadBasicOptions(Document doc, OptionPanel optionPanel) throws Exception {
		final Node basicOptionsNode = getOptionNode(doc, "basicOptions");
		
		optionPanel.setSelectedResolution(XmlDomUtilities.getAttributeValue(basicOptionsNode, "resolution"));
		optionPanel.setSelectedOutputType(XmlDomUtilities.getAttributeValue(basicOptionsNode, "outputFormat"));
		optionPanel.setSelectedFileType(XmlDomUtilities.getAttributeValue(basicOptionsNode, "outputFileType"));				
	}
	
	private void loadColorOptions(Document doc, OptionPanel optionPanel) throws Exception {				
		final Node colorOptionsNode = getOptionNode(doc, "colorOptions");
		
		setConfigColor(
				ConfigManager.TEXT_COLOR_KEY, 
				XmlDomUtilities.getAttributeValue(
						colorOptionsNode, 
						SettingsSerializer.TEXT_COLOR_ATTRIBUTE_NAME));
		
		setConfigColor(
				ConfigManager.DISTANCE_BLENDING_COLOR_KEY, 
				XmlDomUtilities.getAttributeValue(
						colorOptionsNode, 
						SettingsSerializer.DISTANCE_BLEND_COLOR_ATTRIBUTE_NAME));
		
		setConfigColor(
				ConfigManager.BACKGROUND_COLOR_KEY, 
				XmlDomUtilities.getAttributeValue(
						colorOptionsNode, 
						SettingsSerializer.BACKGROUND_COLOR_ATTRIBUTE_NAME));
		
		setConfigColor(
				ConfigManager.BORDER_COLOR_KEY, 
				XmlDomUtilities.getAttributeValue(
						colorOptionsNode, 
						SettingsSerializer.BORDER_COLOR_ATTRIBUTE_NAME));
		
		setConfigColor(
				ConfigManager.TRANSPARENCY_COLOR_KEY, 
				XmlDomUtilities.getAttributeValue(
						colorOptionsNode, 
						SettingsSerializer.TRANSPARENCY_COLOR_ATTRIBUTE_NAME));
		
		optionPanel.setUseTransparency(
				XmlDomUtilities.getAttributeValueAsBoolean(
						colorOptionsNode, 
						SettingsSerializer.USE_ALPHA_ATTRIBUTE_NAME));
		
		optionPanel.refreshColorPanelOptions();
	}
	
	private void setConfigColor(String key, String nodeValue) {
		final ConfigManager configManager = WallGenerator.getConfigManager();
		final int colorValue = (Integer.parseInt(nodeValue.replace("#",""), 16) & 0x00FFFFFF);
		configManager.setColor(key, new Color(colorValue));
	}
	
	private void loadProcessingOptions(Document doc, OptionPanel optionPanel) throws Exception {
		final Node processingOptionsNode = getOptionNode(doc, "processingOptions");
		
		optionPanel.setNearWallDarkening(XmlDomUtilities.getAttributeValueAsDouble(processingOptionsNode, "distanceBlendNear"));
		optionPanel.setFarWallDarkening(XmlDomUtilities.getAttributeValueAsDouble(processingOptionsNode, "distanceBlendFar"));
		optionPanel.setSoftening(XmlDomUtilities.getAttributeValueAsDouble(processingOptionsNode, "soften"));
		
		optionPanel.setImageScalingMethod(XmlDomUtilities.getAttributeValue(processingOptionsNode, "imageScalingMethod"));
		optionPanel.setUnsharpMask(XmlDomUtilities.getAttributeValue(processingOptionsNode, "unsharpMask"));
		optionPanel.setFilterOrder(XmlDomUtilities.getAttributeValue(processingOptionsNode, "filterOrder"));
	}
}
