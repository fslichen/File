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
	public static String createFolders(String... folderPaths) {
		StringBuilder folderPath = new StringBuilder(folderPaths[0]);
		for (int i = 1; i < folderPaths.length; i++) {
			folderPath.append("/").append(folderPaths[i].replace(".", "/"));
		}
		File file = new File(folderPath.toString());
		file.mkdirs();
		return folderPath.toString();
	}
	
	public static String extension(File file) {
		String filename = file.getName();
		return filename.substring(filename.lastIndexOf(".") + 1);
	}

	public static Boolean isValidFolder(String folderPath) {
		File file = new File(folderPath);
		if (!file.exists()) {
			Str.println(folderPath + " does not exist.");
			return false;
		} else if (file.isFile()) {
			Str.println(folderPath + " should be a folder rather than a file.");
			return false;
		}
		return true;
	}
	
	public static Boolean isValidFile(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			Str.println(filePath + " does not exist.");
			return false;
		} else if (file.isDirectory()) {
			Str.println(filePath + " should be a file rather than a folder.");
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
				line = line.replace(oldWord, newWord);
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
			String targetFolderBasePath, String oldWord, String newWord) {
		if (!isValidFolder(sourceFolderBasePath) || !isValidFolder(targetFolderBasePath)) {
			return;
		}
		copy(sourceFolderBasePath, sourceFolderBasePath,
				targetFolderBasePath, targetFolderBasePath, oldWord, newWord);
	}
	
	public static void copy(String sourceFolderPath, String sourceFolderBasePath,
			String targetFolderPath, String targetFolderBasePath, String oldWord, String newWord) {
		File sourceFolder = new File(sourceFolderPath);
		String[] subSourceFileOrFolderRelativePaths = sourceFolder.list();
		for (String subSourceFileOrFolderRelativePath : subSourceFileOrFolderRelativePaths) {
			String subSourceFileOrFolderPath = sourceFolderPath + "/" + subSourceFileOrFolderRelativePath;
			File subSourceFileOrFolder = new File(subSourceFileOrFolderPath);
			String subTargetFileOrFolderPath = targetFolderBasePath + "/" + Str.minus(subSourceFileOrFolderPath, sourceFolderBasePath);
			if (subSourceFileOrFolder.isDirectory()) {
				new File(subTargetFileOrFolderPath).mkdir();
				copy(subSourceFileOrFolderPath, sourceFolderBasePath,
						subTargetFileOrFolderPath, targetFolderBasePath, oldWord, newWord);
			} else if (subSourceFileOrFolder.isFile()) {
				copy(new File(subSourceFileOrFolderPath), new File(subTargetFileOrFolderPath));
				if (oldWord != null && newWord != null) {
					replace(subTargetFileOrFolderPath, oldWord, newWord);
				}
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
