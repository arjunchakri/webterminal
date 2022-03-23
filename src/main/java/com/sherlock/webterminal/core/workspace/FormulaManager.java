package com.sherlock.webterminal.core.workspace;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.web.socket.WebSocketSession;

import com.sherlock.webterminal.formulas.data.CustomFormula;
import com.sherlock.webterminal.formulas.data.FormulaWebInput;
import com.sherlock.webterminal.spring.core.socket.LogSocketMessenger;

public class FormulaManager {

	public final static String FORMULAE_WORKSPACE = WorkspaceConstants.APP_WORKSPACE + "formulae/";

	public final static String FORMULA_WS = FORMULAE_WORKSPACE + "%s/";
	public final static String FORMULA_IMPL = FORMULA_WS + "Formula.java";
	public final static String FORMULA_FREQ = FORMULA_WS + "frequency.txt";

	public static void main(String[] args) throws Exception {

		CustomFormula customFormula = getCustomFormulaBean("classload");

		long t1 = System.currentTimeMillis();

		System.out.println(executeFormula(customFormula));
		long t2 = System.currentTimeMillis();

		System.out.println("here at " + (t2 - t1));

	}

	public static FormulaWebInput executeFormula(String formulaName) throws Exception {
		CustomFormula customFormula = getCustomFormulaBean(formulaName);
		if (customFormula == null) {
			return null;
		}

		return executeFormula(customFormula);
	}

	public static FormulaWebInput executeFormula(CustomFormula customFormula) throws Exception {
		FormulaWebInput formulaOutput = new FormulaWebInput();
		formulaOutput.setName(customFormula.getName());
		formulaOutput.setFrequency(customFormula.getFrequency() + " secs");
		try {
			String compilationIssues = customFormula.getCompilationIssues();
			if (compilationIssues != null) {
				String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance()
						.getTime());
				return new FormulaWebInput(customFormula.getName(), compilationIssues, timeStamp + "", "0 ms",
						customFormula.getFrequency() + " secs");
			}

			long t1 = System.currentTimeMillis();
			String output = executeFormula(customFormula.getClassObject());
			long timetaken = System.currentTimeMillis() - t1;

			String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance()
					.getTime());
			formulaOutput = new FormulaWebInput(customFormula.getName(), output, timeStamp + "", timetaken + " ms",
					customFormula.getFrequency() + " secs");

		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			// compilationLogs.add(e.getMessage());
			// StringWriter sw = new StringWriter();
			// e.printStackTrace(new PrintWriter(sw));
			// String exceptionAsString = sw.toString();
			formulaOutput.setOutput("RUNTIME ISSUE: " + e.getMessage());
		}

		return formulaOutput;
	}

	public static CustomFormula getCustomFormulaBean(String formulaId) throws Exception {
		CustomFormula customFormula = null;
		try {
			String formulaImpl = String.format(FORMULA_IMPL, formulaId);

			customFormula = compileFormulae(formulaId, formulaImpl);
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return customFormula;
	}

	public static CustomFormula compileFormulae(String formulaId, String formulaImpl) {
		CustomFormula customFormula;
		File formulaImplFile;
		formulaImplFile = new File(formulaImpl);
		if (!formulaImplFile.exists()) {
			return null;
		}

		customFormula = new CustomFormula(0, formulaImplFile.getParent(), formulaImplFile.getName(), formulaId, null);

		List<String> compilationLogs = new ArrayList<String>();
		try {
			compilationLogs = compileFileOnly(new File(customFormula.getWorkspace()), customFormula.getSourceCode());
			generateClass(customFormula);

		}
		catch (Exception e) {
			e.printStackTrace();
			// compilationLogs.add(e.getMessage());
			customFormula.setCompilationIssues("COMPILATION ISSUE: " + e.getMessage());
		}

		int frequency = 0;
		try {
			frequency = Integer.parseInt(executeFormula(customFormula.getClassObject(), "getFrequency"));
			System.out.println(customFormula.getName() + " has sheduling enabled at frequency of " + frequency);
		}
		catch (Exception e) {
			// getFrequency method not found or exception while invoking it
			e.printStackTrace();
			frequency = 0;
		}

		customFormula.setFrequency(frequency);
		return customFormula;
	}

	public static List<CustomFormula> getAllFormulae() throws Exception {
		List<CustomFormula> formulaeList = new ArrayList<CustomFormula>();
		File workspace = new File(FORMULAE_WORKSPACE);

		if (!workspace.exists()) {
			return formulaeList;
		}

		File[] listFiles = workspace.listFiles();

		if (listFiles.length == 0) {
			return formulaeList;
		}

		for (File eachFile : listFiles) {
			if (eachFile.isDirectory()) {
				CustomFormula formula;

				try {
					formula = getCustomFormulaBean(eachFile.getName());

				}
				catch (Exception e) {
					System.out.println(
							"FormulaeScraping - Error occured for the formula directory: " + eachFile.getName());
					continue;
				}
				if (formula == null) {
					continue;
				}

				formulaeList.add(formula);
			}
		}

		Collections.sort(formulaeList, new Comparator<CustomFormula>() {
			public int compare(CustomFormula o1, CustomFormula o2) {
				return o1.getName()
						.compareTo(o2.getName());
			};
		});

		return formulaeList;
	}

	public static CustomFormula generateClass(CustomFormula customFormula) throws Exception {

		URLClassLoader urlClassLoader = new URLClassLoader(new URL[] { new File(customFormula.getWorkspace()).toURI()
				.toURL() }, null);
		File pluginLibrary = new File(customFormula.getWorkspace(), "libs");
		if (pluginLibrary.isDirectory()) {
			File[] listFiles = pluginLibrary.listFiles();
			for (File eachFile : listFiles) {
				if (eachFile.isFile() && eachFile.canRead()) {
					String jarPath = eachFile.getAbsolutePath();
					if (jarPath.endsWith("jar")) {
						addJarsToClassLoader(urlClassLoader, new File(jarPath));
					}
				}
			}
		}

		Class<?> loadClass = urlClassLoader.loadClass("Formula");
		customFormula.setClassObject(loadClass);

		return customFormula;
	}

	public static String executeFormula(Class loadedClass) throws Exception {
		return executeFormula(loadedClass, "execute");
	}

	public static String executeFormula(Class loadedClass, String methodName) throws Exception {
		Object newInstance = loadedClass.newInstance();

		Method method = loadedClass.getMethod(methodName);

		Object invoke = method.invoke(newInstance);

		return (String) invoke;
	}

	private static void addJarsToClassLoader(URLClassLoader urlClassLoader, File jarFile) {

		try {
			java.net.URL url = jarFile.toURI()
					.toURL();
			for (java.net.URL it : java.util.Arrays.asList(urlClassLoader.getURLs())) {
				if (it.equals(url)) {
					return;
				}
			}
			java.lang.reflect.Method method =
					java.net.URLClassLoader.class.getDeclaredMethod("addURL", new Class[] { java.net.URL.class });
			method.setAccessible(true);
			method.invoke(urlClassLoader, new Object[] { url });
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static List<String> compileFileOnly(File workspace, String fileName) throws Exception {
		File javaSourceFile = new File(workspace, fileName);
		if (!javaSourceFile.exists() || !javaSourceFile.canRead()) {
			return new ArrayList<String>(Arrays.asList("NO_FILE"));
		}
		List<String> logs = new ArrayList<String>();

		boolean isWindows = System.getProperty("os.name")
				.toLowerCase()
				.contains("windows");
		String classpathSeparator = (isWindows) ? ";" : ":";

		File pluginLibrary = new File(workspace.getAbsolutePath(), "libs");
		StringBuilder classpathArgBuilder = new StringBuilder();
		List<String> classpathJarList = new ArrayList<String>();
		if (pluginLibrary.isDirectory()) {
			File[] listFiles = pluginLibrary.listFiles();

			for (File eachFile : listFiles) {
				if (eachFile.isFile() && eachFile.canRead()) {
					String jarPath = eachFile.getAbsolutePath();
					if (jarPath.endsWith("jar")) {
						classpathJarList.add("libs/" + eachFile.getName());
					}
				}
			}

			classpathArgBuilder.append(" -cp ");
			classpathArgBuilder.append(String.join(classpathSeparator, classpathJarList));
		}

		String classpath = classpathArgBuilder.toString();

		try {

			if (classpath.isEmpty()) {
				classpath = "-cp .";
			}
			else {
				classpath = classpath + classpathSeparator + ".";
			}
			// TODO Refactor custom java path - Point 2
			String configuredJavaHome = ActionsManager.getConfiguredJavaHome();
			String compileCommand = configuredJavaHome + "javac " + classpath + " " + javaSourceFile.getName();
			System.out.println(" >> " + compileCommand);
			logs = execute(compileCommand, workspace, null);

		}
		catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		return logs;
	}

	public static List<String> execute(String commandExec, File contextDirectory, WebSocketSession session)
			throws Exception {
		LogSocketMessenger.sendMessage("<span style='color:grey'>[ PRE - running " + commandExec + " ]</span> ",
				session);
		List<String> logs = new ArrayList<String>();
		ProcessBuilder pb = null;
		boolean isWindows = System.getProperty("os.name")
				.toLowerCase()
				.contains("windows");
		if (isWindows) {
			pb = new ProcessBuilder("cmd", "/c", commandExec);
		}
		else {
			pb = new ProcessBuilder("bash", "-c", commandExec);
		}
		pb.directory(contextDirectory);
		Process p = pb.start();
		InputStream inputstream = p.getInputStream();
		InputStreamReader inputstreamreader = new InputStreamReader(inputstream);
		BufferedReader bufferedreader = new BufferedReader(inputstreamreader);
		String line;
		while ((line = bufferedreader.readLine()) != null) {
			System.out.println(line);
			logs.add(line);

		}

		boolean failed = false;

		BufferedReader errorBufferedStream = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		while ((line = errorBufferedStream.readLine()) != null) {
			System.out.println("ERROR>> " + line);
			logs.add("ERROR>> " + line);
			failed = true;
		}

		if (failed) {
			throw new Exception(commandExec + " execution failed with - " + logs.toString());
		}

		return logs;
	}

}
