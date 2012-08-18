/*
Copyright 2011 Switch Case Technologies Morton. All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are
permitted provided that the following conditions are met:

   1. Redistributions of source code must retain the above copyright notice, this list of
      conditions and the following disclaimer.

   2. Redistributions in binary form must reproduce the above copyright notice, this list
      of conditions and the following disclaimer in the documentation and/or other materials
      provided with the distribution.

THIS SOFTWARE IS PROVIDED BY Switch Case Technologies ''AS IS'' AND ANY EXPRESS OR IMPLIED
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
or implied, of Switch Case Technologies.
*/

package ca.switchcase.commons.util;

import java.util.UUID;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlDomUtilities {

    public static String getAttributeValue(Node node, String attributeName) {
        return node.getAttributes().getNamedItem(attributeName).getTextContent();
    }

    public static String getAttributeValue(Node node, String attributeName, String defaultValue) {
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);
        return attributeNode != null && !StringUtils.isBlank(attributeNode.getTextContent()) ? attributeNode.getTextContent() : defaultValue;
    }

    public static Double getAttributeValueAsDouble(Node node, String attributeName, Double defaultValue) {
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);
        return attributeNode != null ? Double.parseDouble(attributeNode.getTextContent()) : defaultValue;
    }

    public static Double getAttributeValueAsDouble(Node node, String attributeName) {
    	return Double.parseDouble(node.getAttributes().getNamedItem(attributeName).getTextContent());
    }
    

    public static UUID getAttributeValueAsUUID(Node node, String attributeName) {
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);
        return attributeNode != null && !StringUtils.isBlank(attributeNode.getTextContent()) ? UUID.fromString(attributeNode.getTextContent()) : null;
    }

    public static Boolean getAttributeValueAsBoolean(Node node, String attributeName, Boolean defaultValue) {
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);

        return attributeNode != null ? Boolean.parseBoolean(attributeNode.getTextContent()) : defaultValue;
    }

    public static Boolean getAttributeValueAsBoolean(Node node, String attributeName) {
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);

        return attributeNode != null ? Boolean.parseBoolean(attributeNode.getTextContent()) : null;
    }

    public static Integer getAttributeValueAsInteger(Node node, String attributeName, Integer defaultValue) {
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);

        return attributeNode != null ? Integer.parseInt(attributeNode.getTextContent()) : defaultValue;
    }

    public static Integer getAttributeValueAsInteger(Node node, String attributeName) {
        final Node attributeNode = node.getAttributes().getNamedItem(attributeName);

        return attributeNode != null ? Integer.parseInt(attributeNode.getTextContent()) : null;
    }

    public static Node getChildNode(Node node, String childNodeName) {
        final NodeList children = node.getChildNodes();
        for(int i = 0; i < children.getLength(); i++) {
            final Node childNode = children.item(i);
            if (childNode.getNodeName().equals(childNodeName)) {
                return childNode;
            }
        }
        return null;
    }
}
