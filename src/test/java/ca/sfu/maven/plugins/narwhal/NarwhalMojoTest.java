//package ca.sfu.maven.plugins.narwhal;
//
//import org.apache.maven.plugin.testing.AbstractMojoTestCase;
//
//import java.io.File;
//
//public class NarwhalMojoTest extends AbstractMojoTestCase {
//	/**
//	 * {@inheritDoc}
//	 */
//	protected void setUp() throws Exception {
//		super.setUp();
//	}
//
//	/**
//	 * {@inheritDoc}
//	 */
//	protected void tearDown() throws Exception {
//		super.tearDown();
//	}
//
//	/**
//	 * @throws Exception if any
//	 */
//	public void testSomething() throws Exception {
//		File pom = getTestFile("src/test/resources/dummy-project/pom.xml");
//		assertNotNull(pom);
//		assertTrue("pom doesn't exist: " + pom.getAbsolutePath(), pom.exists());
//
//		NarwhalMojo narwhalMojo = (NarwhalMojo) lookupMojo("touch", pom);
//		assertNotNull(narwhalMojo);
//		narwhalMojo.execute();
//	}
//}