package evolution;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.web.bind.annotation.RestController;

public class FileUtilTest {
	@Test
	public void testProjectClasses() {
		List<Class<?>> classes = FileUtil.projectClasses(FileUtil.getSrcMainJava(), "evolution.controller", false);
		System.out.println(classes);
	}
	
	@Test
	public void testAddLineBelowKeyword() {
		FileUtil.addLineBelowKeyword(new File("/Users/chenli/Desktop/anyFile.txt"), "@Test", "Hello World");
	}
	
	@Test
	public void testPath2PackageName() {
		String basePath = "/Users/chenli/Desktop/Playground/File/File/src/main/java";
		String path = "/Users/chenli/Desktop/Playground/File/File/src/main/java/evolution/controller/AnyController.java";
//		String path = "/Users/chenli/Desktop/Playground/File/File/src/main/java/evolution/controller";
		System.out.println(FileUtil.path2PackageOrClassName(path, basePath));
	}
	
	@Test
	public void testClasses() {
		String basePath = "/Users/chenli/Desktop/Playground/Git/File/File/src/main/java";
		String path = "/Users/chenli/Desktop/Playground/Git/File/File/src/main/java/evolution";
		List<Class<?>> classes = null;
		classes = FileUtil.classes(path, basePath, Arrays.asList(RestController.class, RestController.class), classes);
		System.out.println(classes);
	}
	
	@Test
	public void encrypt() {
		FileUtil.encrypt("/Users/chenli/Desktop/Swagger_Creator.zip", "/Users/chenli/Desktop/Swagger_Creator_Encoded.zip");
	}
}
