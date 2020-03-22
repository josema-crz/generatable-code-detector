package utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Processes the XML files with the results of Tsantalis' design pattern
 * detection tool
 * (http://users.encs.concordia.ca/~nikolaos/pattern_detection.html). It
 * collects and writes in files all the necessary information.
 *
 */
public class DesignPatternPostprocessor {
	private static int factoryMethodNoGen, prototypeNoGen, singletonNoGen, adapterNoGen, commandNoGen, compositeNoGen,
			decoratorNoGen, observerNoGen, stateNoGen, strategyNoGen, bridgeNoGen, templateMethodNoGen, visitorNoGen,
			proxyNoGen, proxy2NoGen, chainOfResponsibilityNoGen;

	private static int factoryMethodGen, prototypeGen, singletonGen, adapterGen, commandGen, compositeGen, decoratorGen,
			observerGen, stateGen, strategyGen, bridgeGen, templateMethodGen, visitorGen, proxyGen, proxy2Gen,
			chainOfResponsibilityGen;

	private static int totalNoGen, totalGen;

	private static Set<String> artifactsNoGen, artifactsGen;

	public static void main(String[] args) {
		String resultsDir = "C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Design-patterns\\";

		artifactsNoGen = new HashSet<String>();
		artifactsGen = new HashSet<String>();

		Logger.log("Design patterns: postprocessing starting...");

		// Postprocess the 176 result files
		for (int i = 1; i <= 176; i++) {
			try {
				File fXmlFile = new File(resultsDir + String.format("%03d", i) + ".xml");
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(fXmlFile);

				// optional, but recommended
				// read this -
				// http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();

				NodeList nList = doc.getElementsByTagName("instance");
				for (int temp = 0; temp < nList.getLength(); temp++) {
					Node nNode = nList.item(temp);

					processInstance(nNode);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}

			Logger.log("Processed instance " + i + "/176");
		}

		printResults();

		Logger.log("Design patterns: postprocessing finished.");
	}

	private static void processInstance(Node node) {
		NodeList nList = node.getChildNodes();

		// Check whether the instance contains at least one
		// generated/non-generated artifact
		boolean isInstanceGen = false;
		boolean isInstanceNogen = false;

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);

			// Find the artifacts related to the instance
			if (nNode.getNodeName().equals("role")) {
				NamedNodeMap nodeMap = nNode.getAttributes();
				Node elementAtt = nodeMap.getNamedItem("element");

				if (elementAtt != null) {
					String element = elementAtt.getNodeValue();
					if (element.contains("::"))
						element = element.substring(0, element.indexOf("::"));

					String simpleName = element.substring(element.lastIndexOf('.') + 1, element.length());

					// Add the artifact to the corresponding set
					if (isGenerated(element)) {
						artifactsGen.add(simpleName);
						isInstanceGen = true;
					} else {
						artifactsNoGen.add(simpleName);
						isInstanceNogen = true;
					}
				}
			}
		}

		// Get the parent node to check the type of pattern
		if (node.getParentNode().hasAttributes()) {
			NamedNodeMap nodeMap = node.getParentNode().getAttributes();
			Node nameAtt = nodeMap.getNamedItem("name");

			// Count the instance according to the corresponding pattern
			if (isInstanceGen) {
				countPatternGen(nameAtt.getNodeValue());
			}
			if (isInstanceNogen) {
				countPatternNogen(nameAtt.getNodeValue());
			}
		}
	}

	private static boolean isGenerated(String name) {
		// Get the full path
		String path = getPath(name);

		// Check if it is generated or not
		return path.contains("src-gen") || path.contains("cfg-gen");
	}

	private static String getPath(String name) {
		// Look in all the projects in the workspace
		for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			IJavaProject javaProject = JavaCore.create(project);
			try {
				IType type = javaProject.findType(name);

				if (type != null) {
					return type.getPath().toString();
				}

			} catch (JavaModelException e) {

			}
		}

		return null;
	}

	private static void countPatternGen(String name) {
		totalGen++;

		switch (name) {
		case "Factory Method":
			factoryMethodGen++;
			break;
		case "Prototype":
			prototypeGen++;
			break;
		case "Singleton":
			singletonGen++;
			break;
		case "(Object)Adapter":
			adapterGen++;
			break;
		case "Command":
			commandGen++;
			break;
		case "Composite":
			compositeGen++;
			break;
		case "Decorator":
			decoratorGen++;
			break;
		case "Observer":
			observerGen++;
			break;
		case "State":
			stateGen++;
			break;
		case "Strategy":
			strategyGen++;
			break;
		case "Bridge":
			bridgeGen++;
			break;
		case "Template Method":
			templateMethodGen++;
			break;
		case "Visitor":
			visitorGen++;
			break;
		case "Proxy":
			proxyGen++;
			break;
		case "Proxy2":
			proxy2Gen++;
			break;
		case "Chain of Responsibility":
			chainOfResponsibilityGen++;
			break;
		default:
			break;
		}
	}

	private static void countPatternNogen(String name) {
		totalNoGen++;

		switch (name) {
		case "Factory Method":
			factoryMethodNoGen++;
			break;
		case "Prototype":
			prototypeNoGen++;
			break;
		case "Singleton":
			singletonNoGen++;
			break;
		case "(Object)Adapter":
			adapterNoGen++;
			break;
		case "Command":
			commandNoGen++;
			break;
		case "Composite":
			compositeNoGen++;
			break;
		case "Decorator":
			decoratorNoGen++;
			break;
		case "Observer":
			observerNoGen++;
			break;
		case "State":
			stateNoGen++;
			break;
		case "Strategy":
			strategyNoGen++;
			break;
		case "Bridge":
			bridgeNoGen++;
			break;
		case "Template Method":
			templateMethodNoGen++;
			break;
		case "Visitor":
			visitorNoGen++;
			break;
		case "Proxy":
			proxyNoGen++;
			break;
		case "Proxy2":
			proxy2NoGen++;
			break;
		case "Chain of Responsibility":
			chainOfResponsibilityNoGen++;
			break;
		default:
			break;
		}
	}

	private static void printResults() {
		try {
			PrintWriter totalsWriter = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Design-patterns\\__totals.txt",
					"UTF-8");

			totalsWriter.println("Number of instances with one or more non-generated artifact: " + totalNoGen);
			totalsWriter.println("Number of instances with one or more generated artifact: " + totalGen);
			totalsWriter.println("Total different non-generated artifacts in instances: " + artifactsNoGen.size());
			totalsWriter.println("Total different generated artifacts in instances: " + artifactsGen.size());
			totalsWriter.close();

			PrintWriter distGenWriter = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Design-patterns\\__distribution-gen.txt",
					"UTF-8");

			distGenWriter.println("pattern instances");
			distGenWriter.println("{\\shortstack{Factory Method}} " + factoryMethodGen);
			distGenWriter.println("{Prototype} " + prototypeGen);
			distGenWriter.println("{Singleton} " + singletonGen);
			distGenWriter.println("{Adapter} " + adapterGen);
			distGenWriter.println("{Command} " + commandGen);
			distGenWriter.println("{Composite} " + compositeGen);
			distGenWriter.println("{Decorator} " + decoratorGen);
			distGenWriter.println("{Observer} " + observerGen);
			distGenWriter.println("{State} " + stateGen);
			distGenWriter.println("{Strategy} " + strategyGen);
			distGenWriter.println("{Bridge} " + bridgeGen);
			distGenWriter.println("{\\shortstack{Template Method}} " + templateMethodGen);
			distGenWriter.println("{Visitor} " + visitorGen);
			distGenWriter.println("{Proxy} " + proxyGen);
			distGenWriter.println("{Proxy2} " + proxy2Gen);
			distGenWriter.println("{\\shortstack{Chain of Responsiblity}} " + chainOfResponsibilityGen);
			distGenWriter.close();

			PrintWriter distNoGenWriter = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Design-patterns\\__distribution-nogen.txt",
					"UTF-8");

			distNoGenWriter.println("pattern instances");
			distNoGenWriter.println("{\\shortstack{Factory Method}} " + factoryMethodNoGen);
			distNoGenWriter.println("{Prototype} " + prototypeNoGen);
			distNoGenWriter.println("{Singleton} " + singletonNoGen);
			distNoGenWriter.println("{Adapter} " + adapterNoGen);
			distNoGenWriter.println("{Command} " + commandNoGen);
			distNoGenWriter.println("{Composite} " + compositeNoGen);
			distNoGenWriter.println("{Decorator} " + decoratorNoGen);
			distNoGenWriter.println("{Observer} " + observerNoGen);
			distNoGenWriter.println("{State} " + stateNoGen);
			distNoGenWriter.println("{Strategy} " + strategyNoGen);
			distNoGenWriter.println("{Bridge} " + bridgeNoGen);
			distNoGenWriter.println("{\\shortstack{Template Method}} " + templateMethodNoGen);
			distNoGenWriter.println("{Visitor} " + visitorNoGen);
			distNoGenWriter.println("{Proxy} " + proxyNoGen);
			distNoGenWriter.println("{Proxy2} " + proxy2NoGen);
			distNoGenWriter.println("{\\shortstack{Chain of Responsiblity}} " + chainOfResponsibilityNoGen);
			distNoGenWriter.close();

			PrintWriter genArtsWriter = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Design-patterns\\__artifacts-gen.txt",
					"UTF-8");
			for (String artifact : artifactsGen) {
				genArtsWriter.println(artifact);
			}
			genArtsWriter.close();

			PrintWriter nogenArtsWriter = new PrintWriter(
					"C:\\Users\\jza\\workspace-tfm\\maZapataCodeGen\\10.Notizen\\Case-study\\Design-patterns\\__artifacts-nogen.txt",
					"UTF-8");
			for (String artifact : artifactsNoGen) {
				nogenArtsWriter.println(artifact);
			}
			nogenArtsWriter.close();

		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
