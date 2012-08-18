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

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import ca.quadrilateral.wallgenerator.WallGenerator;
import ca.quadrilateral.wallgenerator.config.ConfigManager;
import ca.quadrilateral.wallgenerator.ui.OptionPanel;

public class SettingsSerializer {
	public static final String TEXT_COLOR_ATTRIBUTE_NAME = "textColor";
	public static final String DISTANCE_BLEND_COLOR_ATTRIBUTE_NAME = "distanceBlendColor";
	public static final String BACKGROUND_COLOR_ATTRIBUTE_NAME = "backgroundColor";
	public static final String BORDER_COLOR_ATTRIBUTE_NAME = "borderColor";
	public static final String TRANSPARENCY_COLOR_ATTRIBUTE_NAME = "transparencyColor";
	public static final String USE_ALPHA_ATTRIBUTE_NAME = "useAlphaTransparency";
	
	private Element createBasicOptionsNode(Document doc, OptionPanel optionPanel) {
		final Element node = doc.createElement("basicOptions");
		node.setAttribute("resolution", optionPanel.getSelectedResolution());
		node.setAttribute("outputFormat", optionPanel.getSelectedOutputType());
		node.setAttribute("outputFileType", optionPanel.getSelectedFileType());
		return node;
	}
	
	private String getConfigColor(String key) {
		final ConfigManager configManager = WallGenerator.getConfigManager();
		return "#" + Integer.toHexString((configManager.getColor(key).getRGB() & 0x00FFFFFF));
	}
	
	private Element createColorOptionsNode(Document doc, OptionPanel optionPanel) {
		final Element node = doc.createElement("colorOptions");
	
		node.setAttribute(TEXT_COLOR_ATTRIBUTE_NAME, getConfigColor(ConfigManager.TEXT_COLOR_KEY));
		node.setAttribute(DISTANCE_BLEND_COLOR_ATTRIBUTE_NAME, getConfigColor(ConfigManager.DISTANCE_BLENDING_COLOR_KEY));
		node.setAttribute(BACKGROUND_COLOR_ATTRIBUTE_NAME, getConfigColor(ConfigManager.BACKGROUND_COLOR_KEY));
		node.setAttribute(BORDER_COLOR_ATTRIBUTE_NAME, getConfigColor(ConfigManager.BORDER_COLOR_KEY));
		node.setAttribute(TRANSPARENCY_COLOR_ATTRIBUTE_NAME, getConfigColor(ConfigManager.TRANSPARENCY_COLOR_KEY));
		node.setAttribute(USE_ALPHA_ATTRIBUTE_NAME, Boolean.toString(optionPanel.useTransparency()));
		
		return node;
	}
	
	private Element createProcessingOptionsNode(Document doc, OptionPanel optionPanel) {
		final Element node = doc.createElement("processingOptions");

		node.setAttribute("distanceBlendNear", Double.toString(optionPanel.getNearWallDarkening()));
		node.setAttribute("distanceBlendFar", Double.toString(optionPanel.getFarWallDarkening()));
		node.setAttribute("imageScalingMethod", optionPanel.getImageScalingMethod());
		node.setAttribute("unsharpMask", optionPanel.getUnsharpMask());
		node.setAttribute("soften", Double.toString(optionPanel.getSoftening()));
		node.setAttribute("filterOrder", optionPanel.getFilterOrder());
		
		return node;
	}
	
	private Document generateXmlDocument(OptionPanel optionPanel) throws Exception {
		final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

		final Document doc = documentBuilder.newDocument();									
		final Node node = doc.createElement("wallgen-presets");
		doc.appendChild(node);
		
		final Element basicOptionsNode = this.createBasicOptionsNode(doc, optionPanel);
		final Element processingOptionsNode = this.createProcessingOptionsNode(doc, optionPanel);
		final Element colorOptionsNode = this.createColorOptionsNode(doc, optionPanel);
		
		node.appendChild(basicOptionsNode);
		node.appendChild(processingOptionsNode);
		node.appendChild(colorOptionsNode);

		return doc;
	}
	
	public void serializeSettings(File file, OptionPanel optionPanel) throws Exception {
		final Document doc = generateXmlDocument(optionPanel);
		
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		
		final DOMSource source = new DOMSource(doc);
		
		final StreamResult result =  new StreamResult(file);
		
		transformer.transform(source, result);
	}
}
