package nz.ac.massey.cs.utils.odem2graphml;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	

	private Map<Integer, String> nodes;
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

		nodes = new HashMap<Integer,String>();
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
		int nodeId1=0;
		NodeList containerList = doc.getElementsByTagName("container");
		for (int i = 0; i < containerList.getLength(); i++) {
			Node container = containerList.item(i);
			NamedNodeMap containerAttr = container.getAttributes(); 
			if (container.getNodeType() == Node.ELEMENT_NODE) {
				Element containerElement = (Element) container;
				NodeList namespaces = containerElement.getElementsByTagName("namespace");
				Set<String> allNameSpaceNames = new HashSet<String>();
				for(int counter=0;counter<namespaces.getLength();counter++){
					Node ns = namespaces.item(counter);
					NamedNodeMap attr=ns.getAttributes();
					allNameSpaceNames.add(attr.getNamedItem("name").getNodeValue());
				}
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
								if(!nodes.containsValue(typeStr)){
									out.write("<node id=\"");
									out.write(String.valueOf(nodeId));
//										out.write("\" class.id=\"");
//										out.write(String.valueOf(nodeId));
									out.write("\" container=\"");
									out.write(containerStr.substring(containerStr
											.lastIndexOf('/') + 1));
									out.write("\" namespace=\"");
									out.write(namespaceStr);
									out.write("\" name=\"");
									out.write(typeStr.substring(typeStr.lastIndexOf('.') + 1));
									out.write("\" isInterface=\"");
									if(typeAttr.getNamedItem("classification")!=null 
											&& typeAttr.getNamedItem("classification").getNodeValue()
											.equals("interface")){
										out.write("true");
									}
									else
										out.write("false");
									out.write("\" type=\"");
									out.write(typeStr1);
//										if (typeAttr.getNamedItem("classification") != null)
//											out.write(String
//													.valueOf(typeAttr.getNamedItem(
//															"classification")
//															.getNodeValue().equals(
//																	"interface")));
//										else
//											out.write("null");

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

									out.write("\" isBoundry=\"");
									String s = typeStr.substring(0,typeStr.indexOf('.'));
									if(allNameSpaceNames.contains(s))
										out.write("false\"");
									else
										out.write("true\"");
									//out.write("\" isSelected=\"false\" ");
//										nodes.add(nodeId, typeStr);
									if (typeStr.contains(namespaceStr))
										nodes.put(nodeId, typeStr);
									else
										nodes.put(nodeId, namespaceStr + '.'
												+ typeStr);
											
									out.write("/>");
									out.write('\n');
									
									/* to test
									TempEdge tempEdge = new TempEdge();
									tempEdge.setSource(String
											.valueOf(nodeId));
									tempEdge.setType(typeAttr.getNamedItem("classification")
											.getNodeValue());
									tempEdge.setTarget(typeAttr.getNamedItem("name")
											.getNodeValue()); 
									tempEdges.add(tempEdge);
									nodeId++; */
								}
								else
									nodeId--;
								
								//depends-on nodes
								StringBuffer buffer = new StringBuffer();
								if (type.getNodeType() == Node.ELEMENT_NODE) {
									Element typeElement = (Element) type;
									NodeList relationships = typeElement
											.getElementsByTagName("depends-on");
									for (int e = 0; e < relationships.getLength(); e++) {
										Node relationship = relationships.item(e);
										NamedNodeMap relationshipAttr = relationship.getAttributes();
										String relationshipStr = relationshipAttr.getNamedItem("name").getNodeValue();
										String relationshipStr1 = relationshipAttr.getNamedItem("classification").getNodeValue();
										
										if(!nodes.containsValue(relationshipStr)){
											nodeId1+=nodeId+1;
											out.write("<node id=\"");
											out.write(String.valueOf(nodeId1));
//												out.write("\" class.id=\"");
//												out.write(String.valueOf(nodeId));
											out.write("\" container=\"");
											out.write(containerStr.substring(containerStr
													.lastIndexOf('/') + 1));
											out.write("\" namespace=\"");
											out.write(relationshipStr.substring(0, relationshipStr.lastIndexOf('.')));
											out.write("\" name=\"");
											out.write(relationshipStr.substring(relationshipStr
													.lastIndexOf('.') + 1));
											out.write("\" isInterface=\"");
											if(relationshipAttr.getNamedItem("classification")!=null 
													&& relationshipAttr.getNamedItem("classification").getNodeValue()
													.equals("interface")){
												out.write("true");
											}
											else
												out.write("false");
											out.write("\" type=\"");
											out.write(typeStr1);//TODO:
//												if (typeAttr.getNamedItem("classification") != null)
//													out.write(String
//															.valueOf(typeAttr.getNamedItem(
//																	"classification")
//																	.getNodeValue().equals(
//																			"interface")));
//												else
//													out.write("null");

											out.write("\" isAbstract=\"");
											if (relationshipAttr.getNamedItem("isAbstract") != null
													&& relationshipAttr.getNamedItem("isAbstract")
															.getNodeValue().equals("yes"))
												out.write("true");
											else
												out.write("false");
											out.write("\" isException=\"");
											out.write(String.valueOf(relationshipStr
													.endsWith("Exception")));
											out.write("\" access=\"");

											if (relationshipAttr.getNamedItem("visibility") != null)
												out.write(typeAttr.getNamedItem(
														"visibility").getNodeValue());
											else
												out.write("null");

											out.write("\" isBoundry=\"");
											String s = relationshipStr.substring(0,relationshipStr.indexOf('.'));
											if(allNameSpaceNames.contains(s))
												out.write("false\"");
											else
												out.write("true\"");
											//out.write("\" isSelected=\"false\" ");

											nodes.put(nodeId1, relationshipStr);
//												if (relationshipStr.contains(namespaceStr))
//													nodes.add(nodeId, relationshipStr);
//												else
//													nodes.add(nodeId, namespaceStr + '.'
//															+ relationshipStr);
													
											out.write("/>");
											out.write('\n');
											//nodeId++;
											
											TempEdge tempEdge1 = new TempEdge();
											//tempEdge1.setSource(String.valueOf(nodeId));
											tempEdge1.setSource(typeStr);
											tempEdge1.setType(relationshipAttr.getNamedItem("classification")
													.getNodeValue());
											tempEdge1.setTarget(relationshipAttr.getNamedItem("name").getNodeValue()); 
											tempEdges.add(tempEdge1);
//												buffer.append(relationshipAttr
//														.getNamedItem("name")
//														.getNodeValue());
//												buffer.append('|');

											nodeId=nodeId1;
											nodeId1=0;
										}
										else {
											TempEdge tempEdge2 = new TempEdge();
											//tempEdge2.setSource(String.valueOf(nodeId));
											tempEdge2.setSource(typeStr);
											tempEdge2.setType(relationshipAttr.getNamedItem("classification")
													.getNodeValue());
											tempEdge2.setTarget(relationshipAttr.getNamedItem("name").getNodeValue()); 
											tempEdges.add(tempEdge2);
										}
											
										
									}
								}
//									if (buffer.length() > 0) {
//										out.write("reference=\"");
//										out.write(buffer.toString());
//										out.write("\" ");
//									}
//									out.write("classification=\"null\" ");*/
//									out.write("/>");
//									out.write('\n');
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
			String sourceID=null;
			for(int k=0;k<nodes.size();k++){
				if(nodes.get(k).equals(te.getSource()))
					sourceID=String.valueOf(k);
			}
			for (int j = 0; j < nodes.size(); j++) {
				if (te.getTarget().equals(nodes.get(j))) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("<edge id=\"edge-");
					buffer.append(i);
					buffer.append("\" source=\"");
					buffer.append(sourceID);
					buffer.append("\" target=\"");
					buffer.append(j);
//					buffer.append("\" sourceId=\"");
//					buffer.append(sourceID);
//					buffer.append("\" targetId=\"");
//					buffer.append(j);
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
