package me.llun.v4amounter.console.core.patcher;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

/**
 * Created by null on 17-12-27.
 * For Android O
 */

public class XmlAudioEffectsPatcher extends AudioEffectsPatcher {
	public XmlAudioEffectsPatcher(String path) throws IOException, SAXException, ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		document = builder.parse(new File(path));
	}

	public XmlAudioEffectsPatcher() throws ParserConfigurationException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		document = builder.newDocument();

		Element root = document.createElement("audio_effects_conf");
		root.setAttribute("version" ,"2.0");
		root.setAttribute("xmlns" ,"http://schemas.android.com/audio/audio_effects_conf/v2_0");
		document.appendChild(root);
	}

	@Override
	public void putEffect(String name, String library, String libraryPath, String uuid, String soundFxDirectory) {
		Element root = document.getDocumentElement();

		NodeList effectsList = root.getElementsByTagName("effects");
		if ( effectsList.getLength() != 1 ) {
			for ( int i = 0 ; i < effectsList.getLength() ; i++ )
				root.removeChild(effectsList.item(i));
			root.appendChild(document.createElement("effects"));
		}
		NodeList librariesList = root.getElementsByTagName("libraries");
		if ( librariesList.getLength() != 1 ) {
			for ( int i = 0 ; i < librariesList.getLength() ; i++ )
				root.removeChild(librariesList.item(i));
			root.appendChild(document.createElement("libraries"));
		}

		Element effects = (Element) root.getElementsByTagName("effects").item(0);
		Element libraries = (Element) root.getElementsByTagName("libraries").item(0);

		Element effectAppend = document.createElement("effect");
		Element libraryAppend = document.createElement("library");

		effectAppend.setAttribute("name" ,name);
		effectAppend.setAttribute("library" ,library);
		effectAppend.setAttribute("uuid" ,uuid);

		libraryAppend.setAttribute("name" ,library);
		libraryAppend.setAttribute("path" ,libraryPath);

		effectAppend.setNodeValue(null);
		libraryAppend.setNodeValue(null);

		effects.appendChild(effectAppend);
		libraries.appendChild(libraryAppend);
	}

	@Override
	public void removeEffects(String... uuid) {
		Element root = document.getDocumentElement();

		NodeList effectsList = root.getElementsByTagName("effects");
		if ( effectsList.getLength() > 0 ) {
			Element effectsElement = (Element) effectsList.item(0);
			NodeList effectElements = effectsElement.getElementsByTagName("effect");
			for ( int i = 0 ; i < effectElements.getLength() ; i++ ) {
				Node n = effectElements.item(i);
				if ( n.getNodeType() != Node.ELEMENT_NODE )
					continue;

				for ( String u : uuid ) {
					if ( u.equals(((Element) n).getAttribute("uuid"))) {
						effectsElement.removeChild(n);
						break;
					}
				}
			}
		}
	}

	@Override
	public void removeRootNodes(String... excludes) {
		Element root = document.getDocumentElement();

		NodeList rootItemList = root.getChildNodes();
		if ( rootItemList.getLength() < 1 )
			return;

		for ( int i = 0 ; i < rootItemList.getLength() ; i++ ) {
			Node node = rootItemList.item(i);
			if ( node.getNodeType() != Node.ELEMENT_NODE )
				continue;

			boolean remove = true;

			for ( String n : excludes ) {
				if ( n.equals(node.getNodeName()) ) {
					remove = false;
					break;
				}
			}

			if ( remove )
				root.removeChild(node);
		}
	}

	@Override
	public void write(String output) throws Exception {
		Transformer transformer = TransformerFactory.newInstance().newTransformer();
		FileOutputStream outputStream = new FileOutputStream(output);

		transformer.transform(new DOMSource(document) ,new StreamResult(outputStream));
		outputStream.flush();
		outputStream.close();
	}

	private Document document;
}
