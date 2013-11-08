//package ca.sfu.maven.plugins.narwhal;
//
//import com.sun.mirror.apt.*;
//import com.sun.mirror.declaration.*;
//
//import java.util.*;
//
//public class NarwhalAnnotationProcessorFactory implements AnnotationProcessorFactory {
//	public Collection<String> supportedAnnotationTypes() {
//		return Arrays.asList("Property");
//	}
//
//	public Collection<String> supportedOptions() {
//		return Arrays.asList(new String[0]);
//	}
//
//	public AnnotationProcessor getProcessorFor(Set<AnnotationTypeDeclaration> atds, AnnotationProcessorEnvironment env) {
//		return new NarwhalAnnotationProcessor(env);
//	}
//}