package evolution;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.web.bind.annotation.RestController;

public class FileUtilTest {
	@Test
	public void testPath2PackageName() {
		String basePath = "/Users/chenli/Desktop/Playground/File/File/src/main/java";
		String path = "/Users/chenli/Desktop/Playground/File/File/src/main/java/evolution/controller/AnyController.java";
//		String path = "/Users/chenli/Desktop/Playground/File/File/src/main/java/evolution/controller";
		System.out.println(FileUtil.path2PackageOrClassName(path, basePath));
	}
	
	@Test
	public void testClasses() {
		String basePath = "/Users/chenli/Desktop/Playground/File/File/src/main/java";
		String path = "/Users/chenli/Desktop/Playground/File/File/src/main/java/evolution";
		List<Class<?>> classes = null;
		classes = FileUtil.classes(path, basePath, Arrays.asList(RestController.class, RestController.class), classes);
		System.out.println(classes);
	}
}
