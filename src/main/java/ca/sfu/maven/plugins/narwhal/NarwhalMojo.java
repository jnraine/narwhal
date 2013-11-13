package ca.sfu.maven.plugins.narwhal;

import ca.sfu.cq.narwhal.annotations.Narwhal;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;
import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.PluginExecution;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@Mojo(name = "generate-sources", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class NarwhalMojo extends AbstractMojo {
	JavaDocBuilder javaDocBuilder;

	@Parameter(defaultValue = "${project}", readonly = true)
	private MavenProject project;

	@Parameter(defaultValue = "${basedir}", readonly = true)
	private String sourcePath;
	/**
	 * @parameter defaultValue="${project.build.sourceEncoding}"
	 */
	private String sourceEncoding;

	@Parameter
	private boolean showWarnings = true;

	@Parameter
	private boolean logOnlyOnError = false;

	@Parameter(defaultValue = "${project.build.directory}/generated-sources", required = true)
	File outputDirectory;

	public File getOutputDirectory() {
		return outputDirectory;
	}

	public void execute() {
		try {
			getLog().info("This is the source path: " + sourcePath);
			this.javaDocBuilder = new JavaDocBuilder();
			javaDocBuilder.addSourceTree(new File(sourcePath));

			generate();

			project.addCompileSourceRoot(outputDirectory.getAbsolutePath());

			// configureDefaultNarwhalBuild();

		} catch (Exception e) {
			throw new RuntimeException("General error", e);
		}

		getLog().info("Narwhal done.");
	}

	private void configureDefaultNarwhalBuild() {
		getLog().info("Modifying build!");
		Build build = project.getBuild();

		// Initialize plugin
		Plugin scrPlugin = new Plugin();
		scrPlugin.setGroupId("org.apache.felix");
		scrPlugin.setArtifactId("maven-scr-plugin");
		scrPlugin.setVersion("1.4.4");

		// Setup goal executions
		List<PluginExecution> executions = new ArrayList<PluginExecution>();
		PluginExecution scrExecution = new PluginExecution();
		List<String> scrGoals = new ArrayList<String>() {{
			add("scr");
		}};
		scrExecution.setGoals(scrGoals);
		executions.add(scrExecution);
		scrPlugin.setExecutions(executions);

		// Add plugin to build
		List<Plugin> plugins = build.getPlugins();
		plugins.add(2, scrPlugin);
		build.setPlugins(plugins);
		project.setBuild(build);

		for(Plugin plugin : build.getPlugins()) {
			getLog().info("Plugin: " + plugin.getGroupId() + ":" + plugin.getArtifactId() + ":" + plugin.getVersion() + ":" + StringUtils.join(plugin.getExecutions(), ","));
		}
	}

	public void generate() throws Exception {
		for (JavaClass klass : javaDocBuilder.getClasses()) {
			if (hasNarwhalAnnotation(klass)) {
				getLog().info("Generating servlet for " + klass);
				generateServletFor(klass);
			} else {
				getLog().debug(klass + " does not have @Narwhal annotation. Skipping servlet generation.");
			}
		}
	}

	private void generateServletFor(JavaClass klass) {
		NarwhalSpec spec = new NarwhalSpec(klass);
		try {
			writeServletFile(spec);
		} catch (IOException e) {
			throw new RuntimeException("A problem occurred while writing servlet file for " + klass + " class", e);
		}
	}

	/**
	 * Write servlet source files for annotated narwhal controller
	 * <p/>
	 * Narwhal controller spec should contain:
	 * - method				e.g., GET
	 * - resourceType			e.g., clf/components/pages/basic
	 * - extension				e.g., html
	 * - controllerClassName	e.g., BasicController
	 * - servletClassName		e.g., BasicServlet
	 * - package				e.g., ca.sfu.cq.clf
	 *
	 * @param spec for use in NarwhalServlet template
	 */
	private void writeServletFile(NarwhalSpec spec) throws IOException {
		URL templateUrl = getClass().getClassLoader().getResource("narwhal-servlet.vm");
		VelocityContext context = new VelocityContext(spec.getMap());

		// Make sure parent dirs exist
		File pd = new File(outputDirectory, spec.getPackage().replaceAll("\\.", "/"));
		pd.mkdirs();

		FileWriter out = new FileWriter(new File(pd, spec.getServletName() + ".java"));
		InputStreamReader reader = new InputStreamReader(templateUrl.openStream());
		try {
			Velocity.evaluate(context, out, templateUrl.toString(), reader);
		} finally {
			reader.close();
			out.close();
		}
	}

	private boolean hasNarwhalAnnotation(JavaClass klass) {
		Annotation[] annotations = klass.getAnnotations();
		for (Annotation annotation : annotations) {
			if (annotation.getType().isA(getNarwhalAnnotationType())) {
				return true;
			} else {
				getLog().info(annotation.getType() + " isn't equals to " + Narwhal.class);
			}
		}

		return false;
	}

	public static Type getNarwhalAnnotationType() {
		return new Type(Narwhal.class.getCanonicalName());
	}
}