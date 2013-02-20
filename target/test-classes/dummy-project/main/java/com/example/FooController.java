package com.example;

import ca.sfu.cq.narwhal.NarwhalController;
import ca.sfu.cq.narwhal.annotations.Narwhal;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;

@Narwhal(resourceType = "my-app/components/foo")
public class FooController extends NarwhalController {
	public FooController(SlingHttpServletRequest request, SlingHttpServletResponse response) {
		super(request, response);
	}
}