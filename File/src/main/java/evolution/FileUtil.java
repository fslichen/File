package evolution;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {
	public static String path2PackageOrClassName(String path, String basePath) {
		path = path.substring(basePath.length() + 1, path.length()).replace("/", ".");
		int length = path.length();
		if (path.charAt(length - 1) == '.') {
			path = path.substring(0, length - 1);
		}
		length = path.length();
		if ("java".equals(path.substring(length - 4, length))) {
			return path.substring(0, length - 5);
		}
		return path;
	}
	
	public static Class<?> clazz(String className) {
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
			System.out.println(className + " is not found.");
			return null;
		}
	}
	
	public static String extension(String path) {
		return path.substring(path.lastIndexOf('.') + 1, path.length());
	}
	
	public static Boolean isJava(String path) {
		return "java".equals(extension(path));
	}
	
	public static Boolean isProperties(String path) {
		return "properties".equals(extension(path));
	}
	
	@SuppressWarnings("rawtypes")
	public static List<Class<?>> classes(String path, List<Class> annotationClasses, List<Class<?>> classes) {
		return classes(path, path.substring(0, path.lastIndexOf("/")), annotationClasses, classes);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<Class<?>> classes(String path, String basePath, List<Class> annotationClasses, List<Class<?>> classes) {
		classes = classes == null ? new LinkedList<>() : classes;
		File file = new File(path);
		if (file.isDirectory()) {
			String[] fileOrDirectoryNames = file.list();
			for (String fileOrDirectoryName : fileOrDirectoryNames) {
				classes(path + "/" + fileOrDirectoryName, basePath, annotationClasses, classes);
			}
		} else if (isJava(path)) {
			Class<?> clazz = clazz(path2PackageOrClassName(path, basePath));
			if (annotationClasses == null || (annotationClasses != null && annotationClasses.stream().anyMatch(x -> clazz.getAnnotation(x) != null))) {
				classes.add(clazz);
			}
		}
		return classes;
	}
}
