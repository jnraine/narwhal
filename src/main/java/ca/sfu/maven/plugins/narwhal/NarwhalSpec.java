package ca.sfu.maven.plugins.narwhal;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;

import java.util.HashMap;
import java.util.Map;

/**
 * Class containing information about a narwhal component. Used when generating servlet class.
 */
public class NarwhalSpec {
	private final JavaClass controllerKlass;

	public NarwhalSpec(JavaClass controllerKlass) {
		this.controllerKlass = controllerKlass;
	}

	public String getServletName() {
		return getName() + "Servlet";
	}

	public String getPackage() {
		return getControllerKlass().getPackage().getName();
	}

	public String getMethod() {
		return "GET";
	}

	public String getFormat() {
		return "html";
	}

	public String getResourceType() {
		return (String) getNarwhalAnnotation().getProperty("resourceType").getParameterValue();
	}

	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("method", getMethod());
		map.put("format", getFormat());
		map.put("resourceType", getResourceType());
		map.put("controllerName", getControllerName());
		map.put("servletName", getServletName());
		map.put("package", getPackage());
		map.put("extension", getExtension());
		map.put("controllerCanonicalName", getControllerKlass().getFullyQualifiedName());
		return map;
	}

	public JavaClass getControllerKlass() {
		return controllerKlass;
	}

	public Annotation getNarwhalAnnotation() {
		Annotation[] annotations = getControllerKlass().getAnnotations();
		for(Annotation annotation : annotations) {
			if(annotation.getType().isA(NarwhalMojo.getNarwhalAnnotationType()))
				return annotation;
		}

		throw new RuntimeException(getControllerKlass() + " does not have @Narwhal annotation set");
	}

	/**
	 * Simple implementation with many bugs. Removes "Controller" from controller type simple name.
	 *
	 * Example: BasicController => Basic
	 */
	public String getName() {
		return getControllerName().replace("Controller", "");
	}

	public String getControllerName() {
		return getControllerKlass().getName();
	}

	public String getExtension() {
		return "html";
	}
}

//* Narwhal controller spec should contain:
//* - package				e.g., ca.sfu.cq.clf
//*