//package ca.sfu.maven.plugins.narwhal;
//
//
//import ca.sfu.cq.narwhal.NarwhalController;
//import ca.sfu.cq.narwhal.annotations.Narwhal;
//import com.sun.mirror.apt.AnnotationProcessor;
//import com.sun.mirror.apt.AnnotationProcessorEnvironment;
//import com.sun.mirror.declaration.Modifier;
//import com.sun.mirror.declaration.TypeDeclaration;
//import com.sun.mirror.type.InterfaceType;
//import org.apache.velocity.VelocityContext;
//import org.apache.velocity.app.Velocity;
//
//import java.io.*;
//import java.net.URL;
//import java.util.*;
//
//public class NarwhalAnnotationProcessor implements AnnotationProcessor {
//	private AnnotationProcessorEnvironment env;
//	private org.apache.maven.execution.MavenExecutionRequest whatever;
//
//	NarwhalAnnotationProcessor(AnnotationProcessorEnvironment env) {
//		this.env = env;
//	}
//
//	public void process() {
//		List<NarwhalSpec> narwhalSpecs = getNarwhalSpecs();
//
//		for (NarwhalSpec narwhalSpec : narwhalSpecs) {
//			try {
//				writeServletFile(narwhalSpec);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	/**
//	 * Write servlet source files for annotated narwhal controller
//	 * <p/>
//	 * Narwhal controller spec should contain:
//	 * - method				e.g., GET
//	 * - resourceType			e.g., clf/components/pages/basic
//	 * - extension				e.g., html
//	 * - controllerClassName	e.g., BasicController
//	 * - servletClassName		e.g., BasicServlet
//	 * - package				e.g., ca.sfu.cq.clf
//	 *
//	 * @param narwhalSpec for use in NarwhalServlet template
//	 */
//	private void writeServletFile(NarwhalSpec narwhalSpec) throws IOException {
//		PrintWriter out = env.getFiler().createSourceFile(narwhalSpec.getServletName());
//		URL templateUrl = getClass().getResource("narwhal-servlet.vm");
//		VelocityContext context = new VelocityContext(narwhalSpec.getMap());
//		Velocity.evaluate(context, out, templateUrl.toString(), getReader(templateUrl));
//		out.close();
//	}
//
//	private Reader getReader(URL url) throws IOException {
//		return new InputStreamReader(url.openStream());
//	}
//
//	public List<NarwhalSpec> getNarwhalSpecs() {
//		Collection<TypeDeclaration> types = env.getSpecifiedTypeDeclarations();
//		Collection<TypeDeclaration> narwhalControllerTypes = findPublicNarwhalControllerTypes(types);
//		List<NarwhalSpec> narwhalControllerSpecs = new ArrayList<NarwhalSpec>();
//
//		for(TypeDeclaration narwhalControllerType : narwhalControllerTypes) {
//			narwhalControllerSpecs.add(new NarwhalSpec(narwhalControllerType));
//		}
//
//		return narwhalControllerSpecs;
//	}
//
//	/**
//	 * Filter out non-public types
//	 *
//	 * @param types
//	 * @return public types
//	 */
//	private Collection<TypeDeclaration> findPublicNarwhalControllerTypes(Collection<TypeDeclaration> types) {
//		Collection<TypeDeclaration> narwhalControllerTypes = new ArrayList<TypeDeclaration>();
//		for (TypeDeclaration type : types) {
//			if (isPublic(type) && hasNarwhalAnnotation(type)) {
//				narwhalControllerTypes.add(type);
//			}
//		}
//		return narwhalControllerTypes;
//	}
//
//	private boolean hasNarwhalAnnotation(TypeDeclaration type) {
//		return type.getAnnotation(Narwhal.class) != null;
//	}
//
//	private boolean isPublic(TypeDeclaration type) {
//		return type.getModifiers().contains(Modifier.PUBLIC);
//	}
//
//	/**
//	 * Compares all super interfaces with Narwhal using canonical name.
//	 *
//	 * @param type
//	 * @return true if type extends Narwhal
//	 */
//	private boolean isNarwhalController(TypeDeclaration type) {
//		for (InterfaceType superInterface : type.getSuperinterfaces()) {
//			String superInterfaceCanonicalName = superInterface.getDeclaration().getQualifiedName();
//			String narwhalControllerCanonicalName = NarwhalController.class.getCanonicalName();
//			System.out.print("Checking if " + superInterfaceCanonicalName + " is equal to " + narwhalControllerCanonicalName);
//
//			if(superInterfaceCanonicalName.equals(narwhalControllerCanonicalName)) {
//				return true; // found narwhal controller as super interface
//			}
//		}
//
//		return false; // did not
//	}
//}