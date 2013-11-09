# Narwhal

Narwhal is a library that allows you to create CQ components in a pseudo-MVC style.

## Getting Started

See the test-project for an example of how to use Narwhal.

## Quick Tour

Instead of using JSPs, you create components using Java objects and view files.

```java
package ca.sfu.maven.plugins.narwhal.test;

import ca.sfu.cq.narwhal.NarwhalController;
import ca.sfu.cq.narwhal.annotations.Narwhal;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

@Narwhal(resourceType = "my-app/components/foo")
public class FooController extends NarwhalController {
	public FooController(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		super(request, response);
	}

	@Override
	public void getHtml() throws IOException {
		getViewLocals().put("name", "world");
	}
}
```

```
<p>Hello, $name</p>
```

*Note: This isn't telling the whole story. You also need to create a dialog and a component node. I'll cover that later.*