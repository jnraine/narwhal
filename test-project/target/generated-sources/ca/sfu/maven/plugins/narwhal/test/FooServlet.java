package ca.sfu.maven.plugins.narwhal.test;

import ca.sfu.maven.plugins.narwhal.test.FooController;
import ca.sfu.cq.narwhal.NarwhalServlet;
import org.apache.felix.scr.annotations.Component;
import org.apache.felix.scr.annotations.Properties;
import org.apache.felix.scr.annotations.Property;
import org.apache.felix.scr.annotations.Service;

@Component
@Service
@Properties({
	@Property(name = "sling.servlet.resourceTypes", value = "my-app/components/foo"),
	@Property(name = "sling.servlet.extensions", value = "html"),
	@Property(name = "sling.servlet.methods", value = "GET")
})
public class FooServlet extends NarwhalServlet {
	public Class controllerClass() { return FooController.class; }
}