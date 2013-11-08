package ca.sfu.maven.plugins.narwhal;

import ca.sfu.cq.narwhal.annotations.Narwhal;
import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.Type;
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

		} catch (Exception e) {
			throw new RuntimeException("General error", e);
		}

		getLog().info(">>> I've finished without problem!");
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