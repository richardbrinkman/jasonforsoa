package jason.compiler;

import jason.annotation.AccessibleTo;
import jason.annotation.Authentic;
import jason.annotation.AvailablePolicies;
import jason.annotation.Confidential;
import jason.annotation.Fresh;
import jason.annotation.Integrity;
import jason.annotation.Logged;
import jason.annotation.Policies;
import jason.annotation.Policy;
import jason.annotation.Roles;
import jason.framework.Constants;
import java.io.IOException;
import java.lang.annotation.IncompleteAnnotationException;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import javax.tools.StandardLocation;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * The <code>JasonProcessor</code> is a plugin for javac. If javac is invoked as:
 * <pre>
 *	<code>javac -processor jason.compiler.JasonProcessor -cp ... &lt;source_files&gt;</code>
 * </pre>
 * Then the <code>JasonProcessor</code> will read all the Jason annotation from
 * the {@link jason.annotation} package and generate the policy files from them.
 * @author dr. ir. R. Brinkman <r.brinkman@cs.ru.nl>
 */
@SupportedAnnotationTypes({"jason.annotation.*"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class JasonProcessor extends AbstractProcessor {
	private static final String NOT_YET_IMPLEMENTED = " not yet implemented";
	private Messager messager;

	@Override
	public void init(ProcessingEnvironment processingEnvironment) {
		super.init(processingEnvironment);
		messager = processingEnvironment.getMessager();
	}

//	protected String processElement(Element element, String policyVersion) {
//		boolean isAnnotated = false;
//		StringBuilder parameterOutput = new StringBuilder();
//		Authentic authentic = element.getAnnotation(Authentic.class);
//		Confidential confidential = element.getAnnotation(Confidential.class);
//		Integrity integrity = element.getAnnotation(Integrity.class);
//		//@TODO: handle multiple policies correctly
//		if (authentic != null) {
//			isAnnotated = true;
//			parameterOutput.append("\t\t\t<authentic signedby=\"" + authentic.signedBy() + "\"/>\n");
//		}
//		if (confidential != null) {
//			isAnnotated = true;
//			parameterOutput.append("\t\t\t<confidential encryptedby=\"" + confidential.encryptedFor() + "\"/>\n");
//		}
//		if (integrity != null) {
//			messager.printMessage(Kind.WARNING, "De @Integrity annotation is nog niet ingebouwd");
//		}
//		return isAnnotated ? parameterOutput.toString() : null;
//	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		boolean processed = false;
		try {
			messager.printMessage(Kind.NOTE, "---Start processing---");
			if (!annotations.isEmpty()) {
				Document wsdlDocument = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
				org.w3c.dom.Element policyRoot = wsdlDocument.createElementNS(
					Constants.WS_POLICY_NAMESPACE,
					Constants.WS_POLICY_PREFIX + ":Policy"
				);
				policyRoot.setAttribute(
					"xmlns:" + Constants.WS_SECURITY_POLICY_PREFIX,
					Constants.WS_SECURITY_POLICY_NAMESPACE
				);
				policyRoot.setAttribute(
					"xmlns:" + Constants.WS_SECURITY_UTILITY_PREFIX,
					Constants.WS_SECURITY_UTILITY_NAMESPACE
				);
				Node defaultPolicy = policyRoot.appendChild(
					wsdlDocument.createElement(Constants.WS_POLICY_PREFIX + ":ExactlyOne")
				).appendChild(wsdlDocument.createElement(Constants.WS_POLICY_PREFIX + ":All"));
				org.w3c.dom.Element alternativePolicies = null;
				String packageName = null; 
				String serviceName = null; 
				Elements elements = processingEnv.getElementUtils();
				for (TypeElement annotation : annotations) {
					messager.printMessage(Kind.NOTE, "Processing annotation " + annotation.getQualifiedName());
					for (Element element : roundEnv.getElementsAnnotatedWith(annotation)) {
						if (packageName == null) 
							packageName = elements.getPackageOf(element).getQualifiedName().toString();
						if (serviceName == null)
							serviceName = extractServiceName(element);
						messager.printMessage(Kind.NOTE, "  Element: " + element);
						if (annotation.getQualifiedName().toString().equals(AccessibleTo.class.getCanonicalName()))
							messager.printMessage(Kind.MANDATORY_WARNING, "@AccessibleTo" + NOT_YET_IMPLEMENTED);

						//@Authentic
						if (annotation.getQualifiedName().toString().equals(Authentic.class.getCanonicalName()))
							addSecurityParts(defaultPolicy, wsdlDocument, element, Constants.WS_SECURITY_POLICY_PREFIX + ":SignedElements");

						//@AvailablePolicies
						if (annotation.getQualifiedName().toString().equals(AvailablePolicies.class.getCanonicalName())) {
							AvailablePolicies availablePolicies = element.getAnnotation(AvailablePolicies.class);
							if (alternativePolicies == null && availablePolicies.value().length > 0) {
								alternativePolicies = (org.w3c.dom.Element) defaultPolicy.appendChild(
									wsdlDocument.createElement(
										Constants.WS_POLICY_PREFIX + ":ExactlyOne"
									)
								);
							}
							for (String policyName : availablePolicies.value()) {
								org.w3c.dom.Element policy = wsdlDocument.createElement(
									Constants.WS_POLICY_PREFIX + ":Policy"
								);
								policy.setAttribute(Constants.WS_SECURITY_UTILITY_PREFIX + ":Id", policyName);
								alternativePolicies.appendChild(
									wsdlDocument.createElement(
										Constants.WS_POLICY_PREFIX + ":All"
									)
								).appendChild(policy).appendChild(
									wsdlDocument.createElement(
										Constants.WS_POLICY_PREFIX + ":ExactlyOne"
									)
								).appendChild(
									wsdlDocument.createElement(
										Constants.WS_POLICY_PREFIX + ":All"
									)
								);
							}
						}

						//@Confidential
						if (annotation.getQualifiedName().toString().equals(Confidential.class.getCanonicalName()))
							addSecurityParts(defaultPolicy, wsdlDocument, element, Constants.WS_SECURITY_POLICY_PREFIX + ":EncryptedParts");

						//@Fresh
						if (annotation.getQualifiedName().toString().equals(Fresh.class.getCanonicalName()))
							messager.printMessage(Kind.MANDATORY_WARNING, "@Fresh" + NOT_YET_IMPLEMENTED);

						//@Integrity
						if (annotation.getQualifiedName().toString().equals(Integrity.class.getCanonicalName()))
							messager.printMessage(Kind.MANDATORY_WARNING, "@Integrity" + NOT_YET_IMPLEMENTED);

						//@Logged
						if (annotation.getQualifiedName().toString().equals(Logged.class.getCanonicalName()))
							messager.printMessage(Kind.MANDATORY_WARNING, "@Logged" + NOT_YET_IMPLEMENTED);

						//@Policies
						if (annotation.getQualifiedName().toString().equals(Policies.class.getCanonicalName())) {
							XPath xpath = XPathFactory.newInstance().newXPath();
							Policies policies = element.getAnnotation(Policies.class);
							for (Policy policy : policies.value()) {
								Node policyElement = (Node) xpath.evaluate(
									"All/Policy[@Id=\"" + policy.name() +"\"]/ExactlyOne/All",
									alternativePolicies,
									XPathConstants.NODE
								);
								if (policyElement == null)
									messager.printMessage(Kind.ERROR, "Policyname " + policy.name() + " should be specified as one the @AvailablePolicies");
								else {
									try {
										if (policy.accessibleTo() != null)
											messager.printMessage(Kind.MANDATORY_WARNING, "@Policy(accessibleTo=...)" + NOT_YET_IMPLEMENTED);
									} catch (IncompleteAnnotationException ex) {}
									try {
										Authentic authentic = policy.authentic();
										addSecurityParts(policyElement, wsdlDocument, element, Constants.WS_SECURITY_POLICY_PREFIX + ":SignedElements");
									} catch (IncompleteAnnotationException ex) {}
									try {
										Confidential confidential = policy.confidential();
										addSecurityParts(policyElement, wsdlDocument, element, Constants.WS_SECURITY_POLICY_PREFIX + ":EncryptedParts");
									} catch (IncompleteAnnotationException ex) {}
									try {
										if (policy.integrity() != null)
											messager.printMessage(Kind.MANDATORY_WARNING, "@Policy(integrity=..)" + NOT_YET_IMPLEMENTED);
									} catch (IncompleteAnnotationException ex) {}
									try {
										if (policy.logged() != null)
											messager.printMessage(Kind.MANDATORY_WARNING, "@Policy(logged=...)" + NOT_YET_IMPLEMENTED);
									} catch (IncompleteAnnotationException ex) {}
									try {
										if (policy.roles() != null)
											messager.printMessage(Kind.MANDATORY_WARNING, "@Policy(roles=...)" + NOT_YET_IMPLEMENTED);
									} catch (IncompleteAnnotationException ex) {}
								}
							}
						}

						//@Policy
						if (annotation.getQualifiedName().toString().equals(Policy.class.getCanonicalName()))
							messager.printMessage(Kind.ERROR, "@Policy can only be used inside a @Policies annotation");

						//@Roles
						if (annotation.getQualifiedName().toString().equals(Roles.class.getCanonicalName()))
							messager.printMessage(Kind.MANDATORY_WARNING, "@Roles" + NOT_YET_IMPLEMENTED);
					}
				}

				//Output security policy in WSDL file
				TransformerFactory factory = TransformerFactory.newInstance();
				factory.setAttribute("indent-number", Integer.valueOf(2));
				Transformer transformer = factory.newTransformer();
				transformer.setOutputProperty(OutputKeys.INDENT, "yes");
				transformer.transform(
					new DOMSource(policyRoot),
					new StreamResult(
						processingEnv.getFiler().createResource(
							StandardLocation.SOURCE_OUTPUT,
							packageName, 
							serviceName + ".wsp"
						).openWriter()
					)
				);
			}
			messager.printMessage(Kind.NOTE, "---Finished processing---");
			processed = true;
		} catch (TransformerException ex) {
			messager.printMessage(Kind.ERROR, ex.getLocalizedMessage());
		} catch (ParserConfigurationException ex) {
			messager.printMessage(Kind.ERROR, ex.getLocalizedMessage());
		} catch (IOException ex) {
			messager.printMessage(Kind.ERROR, ex.getLocalizedMessage());
		} catch (XPathExpressionException ex) {
			messager.printMessage(Kind.ERROR, ex.getLocalizedMessage());
		}
		return processed;
//		try {
//			for (Element serviceClass : roundEnv.getElementsAnnotatedWith(AvailablePolicies.class)) {
//				AvailablePolicies policies = serviceClass.getAnnotation(AvailablePolicies.class);
//				for (String policyVersion : policies.value()) {
//					Name packageName = ((PackageElement) serviceClass.getEnclosingElement()).getQualifiedName();
//					Name className = serviceClass.getSimpleName();
//					FileObject resource = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName, className + "_" + policyVersion + ".xml", serviceClass);
//					Writer writer = resource.openWriter();
//					writer.write("<?xml version=\"1.0\"?>\n");
//					writer.write("<service xmlns=\"" + Constants.JASON_NAMESPACE + "\" name=\"" + className + "\" package=\"" + packageName + "\" version=\"" + policyVersion + "\">\n");
//					for (Element method : serviceClass.getEnclosedElements())
//						if (method.getKind() == ElementKind.METHOD) {
//							boolean methodIsAnnotated = false;
//							StringBuilder methodOutput = new StringBuilder();
//							methodOutput.append("\t<method name=\"" + method.getSimpleName() + "\">\n");
//
//							//Parameters
//							StringBuilder annotationOutput = new StringBuilder();
//							for (Element parameter : ((ExecutableElement) method).getParameters())
//								if (parameter.getKind() == ElementKind.PARAMETER) {
//									String parameterAnnotationOutput = processElement(parameter, policyVersion);
//									String tag = "\t\t<param name=\"" + parameter.getSimpleName() + "\"";
//									if (parameterAnnotationOutput != null) {
//										methodIsAnnotated = true;
//										annotationOutput.append(tag + ">\n" + parameterAnnotationOutput + "\t\t</param>\n");
//									} else
//										annotationOutput.append(tag + "/>\n");
//								}
//							methodOutput.append(annotationOutput.toString());
//
//							//Result
//							String resultAnnotationOutput = processElement(method, policyVersion);
//							if (resultAnnotationOutput != null) {
//								methodIsAnnotated = true;
//								methodOutput.append("\t\t<result>\n");
//								methodOutput.append(resultAnnotationOutput);
//								methodOutput.append("\t\t</result>\n");
//							}
//
//							methodOutput.append("\t</method>\n");
//							if (methodIsAnnotated)
//								writer.append(methodOutput.toString());
//						}
//
//					writer.write("</service>");
//					writer.close();
//				}
//			}
//		} catch (IOException ex) {
//			messager.printMessage(Kind.ERROR, ex.getMessage());
//		}
//		return true;
//		try {
//			for (Element serviceClass : roundEnv.getElementsAnnotatedWith(AvailablePolicies.class)) {
//				AvailablePolicies policies = serviceClass.getAnnotation(AvailablePolicies.class);
//				for (String policyVersion : policies.value()) {
//					Name packageName = ((PackageElement) serviceClass.getEnclosingElement()).getQualifiedName();
//					Name className = serviceClass.getSimpleName();
//					FileObject resource = processingEnv.getFiler().createResource(StandardLocation.SOURCE_OUTPUT, packageName, className + "_" + policyVersion + ".xml", serviceClass);
//					Writer writer = resource.openWriter();
//					writer.write("<?xml version=\"1.0\"?>\n");
//					writer.write("<service xmlns=\"" + Constants.JASON_NAMESPACE + "\" name=\"" + className + "\" package=\"" + packageName + "\" version=\"" + policyVersion + "\">\n");
//					for (Element method : serviceClass.getEnclosedElements())
//						if (method.getKind() == ElementKind.METHOD) {
//							boolean methodIsAnnotated = false;
//							StringBuilder methodOutput = new StringBuilder();
//							methodOutput.append("\t<method name=\"" + method.getSimpleName() + "\">\n");
//
//							//Parameters
//							StringBuilder annotationOutput = new StringBuilder();
//							for (Element parameter : ((ExecutableElement) method).getParameters())
//								if (parameter.getKind() == ElementKind.PARAMETER) {
//									String parameterAnnotationOutput = processElement(parameter, policyVersion);
//									String tag = "\t\t<param name=\"" + parameter.getSimpleName() + "\"";
//									if (parameterAnnotationOutput != null) {
//										methodIsAnnotated = true;
//										annotationOutput.append(tag + ">\n" + parameterAnnotationOutput + "\t\t</param>\n");
//									} else
//										annotationOutput.append(tag + "/>\n");
//								}
//							methodOutput.append(annotationOutput.toString());
//
//							//Result
//							String resultAnnotationOutput = processElement(method, policyVersion);
//							if (resultAnnotationOutput != null) {
//								methodIsAnnotated = true;
//								methodOutput.append("\t\t<result>\n");
//								methodOutput.append(resultAnnotationOutput);
//								methodOutput.append("\t\t</result>\n");
//							}
//
//							methodOutput.append("\t</method>\n");
//							if (methodIsAnnotated)
//								writer.append(methodOutput.toString());
//						}
//
//					writer.write("</service>");
//					writer.close();
//				}
//			}
//		} catch (IOException ex) {
//			messager.printMessage(Kind.ERROR, ex.getMessage());
//		}
//		return true;
	}

	private void addSecurityParts(Node policy, Document wsdlDocument, Element element, String partType) throws DOMException {
		policy.appendChild(
			wsdlDocument.createElement(
				partType
			)
		).appendChild(
			wsdlDocument.createElement(Constants.WS_SECURITY_POLICY_PREFIX + ":XPath")
		).setTextContent(
			extractXPath(element)
		);
	}

	private String extractServiceName(Element element) {
		String result;
		if (element.getKind() == ElementKind.CLASS)
			result = element.getSimpleName().toString();
		else
			result = extractServiceName(element.getEnclosingElement());
		return result;
	}

	private String extractXPath(Element element) {
		String result;
		switch (element.getKind()) {
			case METHOD:
				result = extractServiceName(element) + "Response/return"; break;
			case PARAMETER: 
				result = extractServiceName(element) +
				         "/" +
								 element.getEnclosingElement().getSimpleName().toString() + //method name
								 "/" +
								 element.getSimpleName().toString();
				break;
			default:
				result = null;
		}
		return result;
	}
}
