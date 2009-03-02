package nz.ac.massey.cs.utils.odem2graphml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * Modified BARRIO class originally written by Slava.
 * converts (single) odem files to graph ml
 * @author jens dietrich
 */
public class Odem2GraphML {
	

	private List<String> nodes;
	private List<TempEdge> tempEdges;
	private List<String> edges;

	public void convert(Reader reader,Writer writer) throws IOException, ParserConfigurationException, SAXException {

		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder;
		docBuilderFactory.setValidating(false);		
		docBuilder = docBuilderFactory.newDocumentBuilder();		
		Document doc = docBuilder.parse(new InputSource(reader));
		Writer out =writer;

		out.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		out.write('\n');
		out.write("<graphml xmlns=\"http://graphml.graphdrawing.org/xmlns/graphml\"");
		out.write(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"");
		out.write(" xsi:schemaLocation=\"http://graphml.graphdrawing.org/xmlns/graphml\">");
		out.write('\n');
		out.write("<graph edgedefault=\"");

		// String s = PreferenceRetriver.getGraphChoice();
		// if(!s.equals("directed") && !s.equals("undirected"))
		// s="directed";
		String s = "directed";

		out.write(s);
		out.write("\" file=\"");
		// System.out.println("[OdemReader]: choise = " + s);

		//if (input instanceof File)
		//	out.write(((File) input).getAbsolutePath());
		
		// System.out.println("[OdemReader]: file = "+((File)input).
		// getAbsolutePath());
		out.write("\">\n");

		nodes = new ArrayList<String>();
		tempEdges = new ArrayList<TempEdge>();
		edges = new ArrayList<String>();
		writeNodes(doc, out);
		buildEdgeList();
		writeEdges(out);

		out.write("</graph>");
		out.write('\n');
		out.write("</graphml>");
	}

	private void writeNodes(Document doc, Writer out) {
		int nodeId = 0;
		NodeList containerList = doc.getElementsByTagName("container");
		for (int i = 0; i < containerList.getLength(); i++) {
			Node container = containerList.item(i);
			NamedNodeMap containerAttr = container.getAttributes(); 
			if (container.getNodeType() == Node.ELEMENT_NODE) {
				Element containerElement = (Element) container;
				NodeList namespaces = containerElement.getElementsByTagName("namespace");
				for (int j = 0; j < namespaces.getLength(); j++) {
					Node namespace = namespaces.item(j); 
					NamedNodeMap namespaceAttr = namespace.getAttributes();
					if (namespace.getNodeType() == Node.ELEMENT_NODE) {
						Element namespaceElement = (Element) namespace; 
						NodeList types = namespaceElement
								.getElementsByTagName("type");
						for (int k = 0; k < types.getLength(); k++) {
							Node type = types.item(k); 
							NamedNodeMap typeAttr = type.getAttributes();
							String containerStr = containerAttr.getNamedItem(
									"name").getNodeValue();
							String namespaceStr = namespaceAttr.getNamedItem(
									"name").getNodeValue();
							String typeStr = typeAttr.getNamedItem("name")
									.getNodeValue();
							String typeStr1 = typeAttr.getNamedItem("classification").getNodeValue();
							try {
								out.write("<node id=\"");
								out.write(String.valueOf(nodeId));
//								out.write("\" class.id=\"");
//								out.write(String.valueOf(nodeId));
								out.write("\" jar=\"");
								out.write(containerStr.substring(containerStr
										.lastIndexOf('/') + 1));
								out.write("\" namespace=\"");
								out.write(namespaceStr);
								out.write("\" name=\"");
								out.write(typeStr.substring(typeStr
										.lastIndexOf('.') + 1));
								out.write("\" cluster=\"null\" isInterface=\"");
								if(typeAttr.getNamedItem("classification")!=null 
										&& typeAttr.getNamedItem("classification").getNodeValue()
										.equals("interface")){
									out.write("true");
								}
								else
									out.write("false");
								out.write("\" type=\"");
								out.write(typeStr1);
//								if (typeAttr.getNamedItem("classification") != null)
//									out.write(String
//											.valueOf(typeAttr.getNamedItem(
//													"classification")
//													.getNodeValue().equals(
//															"interface")));
//								else
//									out.write("null");

								out.write("\" isAbstract=\"");
								if (typeAttr.getNamedItem("isAbstract") != null
										&& typeAttr.getNamedItem("isAbstract")
												.getNodeValue().equals("yes"))
									out.write("true");
								else
									out.write("false");
								out.write("\" isException=\"");
								out.write(String.valueOf(typeStr
										.endsWith("Exception")));
								out.write("\" access=\"");

								if (typeAttr.getNamedItem("visibility") != null)
									out.write(typeAttr.getNamedItem(
											"visibility").getNodeValue());
								else
									out.write("null");

								out.write("\" isSelected=\"false\" ");

								if (typeStr.contains(namespaceStr))
									nodes.add(nodeId, typeStr);
								else
									nodes.add(nodeId, namespaceStr + '.'
											+ typeStr);

								StringBuffer buffer = new StringBuffer();
								if (type.getNodeType() == Node.ELEMENT_NODE) {
									Element typeElement = (Element) type;
									NodeList relationships = typeElement
											.getElementsByTagName("depends-on");
									for (int e = 0; e < relationships
											.getLength(); e++) {
										Node relationship = relationships
												.item(e);
										NamedNodeMap relationshipAttr = relationship
												.getAttributes();

										TempEdge tempEdge = new TempEdge();
										tempEdge.setSource(String
												.valueOf(nodeId));
										tempEdge.setType(relationshipAttr
												.getNamedItem("classification")
												.getNodeValue());
										tempEdge.setTarget(relationshipAttr
												.getNamedItem("name")
												.getNodeValue());

										buffer.append(relationshipAttr
												.getNamedItem("name")
												.getNodeValue());
										buffer.append('|');

										tempEdges.add(tempEdge);
									}
								}
								if (buffer.length() > 0) {
									out.write("reference=\"");
									out.write(buffer.toString());
									out.write("\" ");
								}
//								out.write("classification=\"null\" ");
								out.write("/>");
								out.write('\n');
								nodeId++;

							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
						}
					}
				}
			}
		}
	}

	private void buildEdgeList() {
		for (int i = 0; i < tempEdges.size(); i++) {
			TempEdge te = tempEdges.get(i);
			for (int j = 0; j < nodes.size(); j++) {
				if (te.getTarget().equals(nodes.get(j))) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("<edge id=\"edge-");
					buffer.append(i);
					buffer.append("\" source=\"");
					buffer.append(te.getSource());
					buffer.append("\" target=\"");
					buffer.append(j);
					buffer.append("\" sourceId=\"");
					buffer.append(te.getSource());
					buffer.append("\" targetId=\"");
					buffer.append(j);
					buffer.append("\" type=\"");
					buffer.append(te.getType());
					buffer.append("\" isSelected=\"false\" state=\"null");
					buffer.append("\" betweenness=\"null");
					buffer.append("\" separation=\"null\" />");
					edges.add(buffer.toString());
					break;
				}
			}
		}
	}

	private void writeEdges(Writer out) {
		Iterator<String> iter = edges.iterator();
		while (iter.hasNext()) {
			try {
				out.write(iter.next());
				out.write('\n');
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	public void reset() {
		this.edges = null;
		this.nodes = null;
		this.tempEdges = null;
	}
}
