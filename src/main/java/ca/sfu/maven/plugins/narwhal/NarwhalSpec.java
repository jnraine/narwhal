package ca.sfu.maven.plugins.narwhal;

import ca.sfu.maven.plugins.narwhal.annotation.Narwhal;
import com.sun.mirror.declaration.TypeDeclaration;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Class containing information about a narwhal component. Used when generating servlet class.
 */
public class NarwhalSpec {
	private final TypeDeclaration controllerType;

	public NarwhalSpec(TypeDeclaration narwhalControllerType) {
		this.controllerType = narwhalControllerType;
	}

	public String getServletName() {
		return getName() + "Servlet";
	}

	public String getPackage() {
		return getControllerType().getPackage().getQualifiedName();
	}

	public String getMethod() {
		return "GET";
	}

	public String getFormat() {
		return "html";
	}

	public String getResourceType() {
		return getAnnotation().resourceType();
	}

	public Map<String, Object> getMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("method", getMethod());
		map.put("format", getFormat());
		map.put("resourceType", getResourceType());
		map.put("controllerName", getControllerName());
		map.put("servletName", getServletName());
		map.put("package", getPackage());
		return map;
	}

	public TypeDeclaration getControllerType() {
		return controllerType;
	}

	public Narwhal getAnnotation() {
		return getControllerType().getAnnotation(Narwhal.class);
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
		return getControllerType().getSimpleName();
	}
}

//* Narwhal controller spec should contain:
//* - package				e.g., ca.sfu.cq.clf
//*