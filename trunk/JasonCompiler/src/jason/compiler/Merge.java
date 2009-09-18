package jason.compiler;

import java.io.FileWriter;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class Merge {
	private static void mergePolicy(String wsdlLocation, String policyLocation, String mergedLocation) throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException, TransformerException {
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document wsdlDocument = builder.parse(wsdlLocation);
		wsdlDocument.getDocumentElement().getElementsByTagName("binding").item(0).appendChild(
			wsdlDocument.importNode(
				builder.parse(policyLocation).getDocumentElement(),
				true
			)
		);
		TransformerFactory factory = TransformerFactory.newInstance();
		factory.setAttribute("indent-number", Integer.valueOf(2));
		Transformer transformer = factory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.transform(
			new DOMSource(wsdlDocument),
			new StreamResult(
				new FileWriter(mergedLocation)
			)
		);
	}

	public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException, TransformerConfigurationException, TransformerException {
		if (args.length == 3)
			mergePolicy(args[0], args[1], args[2]);
		else
			System.err.println("Syntax: java jason.compiler.Merge <WSDL> <policy> <merged>");
	}
}