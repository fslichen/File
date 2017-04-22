package evolution;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

public class FileUtil {
	public static String extension(File file) {
		String filename = file.getName();
		return filename.substring(filename.lastIndexOf(".") + 1);
	}
	
	public static Boolean isValidFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			Str.println(filePath + " does not exist.");
			return false;
		} else if (file.isDirectory()) {
			Str.println(filePath + " represents a folder.");
			return false;
		}
		return true;
	}
	
	public static void replace(String filePath, String oldWord, String newWord) {
		if (!isValidFile(filePath)) {
			return;
		}
		File file = new File(filePath);
		try {
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			File temporaryFile = File.createTempFile(file.getName(), extension(file));
			FileWriter fileWriter = new FileWriter(temporaryFile);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				line = line.replaceAll(oldWord, newWord);
				bufferedWriter.write(line + "\n");
			}
			bufferedReader.close();
			bufferedWriter.close();
			copy(temporaryFile, file);// Overwrite the original file.
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void copy(String sourceFolderBasePath, 
			String targetFolderBasePath) {
		File sourceFolder = new File(sourceFolderBasePath);
		if (!sourceFolder.exists()) {
			Str.println("Source path " + sourceFolderBasePath + " does not exist.");
			return;
		} else if (sourceFolder.isFile()) {
			Str.println(sourceFolderBasePath + " is not a folder.");
			return;
		}
		File targetFolder = new File(targetFolderBasePath);
		if (!targetFolder.exists()) {
			Str.println("Target path " + targetFolderBasePath + " does not exist.");
			return;
		} else if (targetFolder.isFile()) {
			Str.println(targetFolderBasePath + " is not a folder.");
			return;
		}
		copy(sourceFolderBasePath, sourceFolderBasePath,
				targetFolderBasePath, targetFolderBasePath);
	}
	
	public static void copy(String sourceFolderPath, String sourceFolderBasePath,
			String targetFolderPath, String targetFolderBasePath) {
		File sourceFolder = new File(sourceFolderPath);
		String[] subSourceFileOrFolderRelativePaths = sourceFolder.list();
		for (String subSourceFileOrFolderRelativePath : subSourceFileOrFolderRelativePaths) {
			String subSourceFileOrFolderPath = sourceFolderPath + "/" + subSourceFileOrFolderRelativePath;
			File subSourceFileOrFolder = new File(subSourceFileOrFolderPath);
			String subTargetFileOrFolderPath = targetFolderBasePath + "/" + Str.minus(subSourceFileOrFolderPath, sourceFolderBasePath);
			if (subSourceFileOrFolder.isDirectory()) {
				new File(subTargetFileOrFolderPath).mkdir();
				copy(subSourceFileOrFolderPath, sourceFolderBasePath,
						subTargetFileOrFolderPath, targetFolderBasePath);
			} else if (subSourceFileOrFolder.isFile()) {
				copy(new File(subSourceFileOrFolderPath), new File(subTargetFileOrFolderPath));
			}
		}
	}
	
	public static void copy(File file0, File file1) {
		try {
			InputStream in = new FileInputStream(file0);
			OutputStream out = new FileOutputStream(file1);
			byte[] buffer = new byte[1024];
			int length = -1;
			while ((length = in.read(buffer)) != -1) {
				out.write(buffer, 0, length);
			}
			in.close();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
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
