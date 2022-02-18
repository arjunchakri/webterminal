package com.sherlock.webterminal.spring.core;

import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;

import com.sherlock.webterminal.core.workspace.ActionsManager;
import com.sherlock.webterminal.core.workspace.FormulaManager;
import com.sherlock.webterminal.core.workspace.WorkspaceConstants;
import com.sherlock.webterminal.utils.RestUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.sherlock.webterminal.commandexec.AsyncRunner;
import com.sherlock.webterminal.commandexec.CommandExecutionManager;
import com.sherlock.webterminal.commandexec.data.ExecutionResponse;
import com.sherlock.webterminal.commandexec.executor.TerminalExecutorRepository;
import com.sherlock.webterminal.filemanager.FileManager;
import com.sherlock.webterminal.formulas.CustomFormulaExecutor;
import com.sherlock.webterminal.logdownload.DownloadFileRepository;
import com.sherlock.webterminal.logdownload.LogDownloadRequestProcessor;
import com.sherlock.webterminal.logreader.LogReaderProvider;
import com.sherlock.webterminal.logreader.configuration.TempConstants;
import com.sherlock.webterminal.logreader.services.directory.FileNamesProvider;
import com.sherlock.webterminal.logreader.services.directory.data.DirectoryContents;
import com.sherlock.webterminal.logreader.services.readerkey.ReaderKeyRepository;

@RestController
public class RestControllers {
	private static final String ORIGINS = "*";

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/logAlert")
	public String logAlert(@RequestParam(value = "hostname", required = true) String hostname,
			@RequestParam(value = "content", required = true) String content) throws Exception {
		Gson gson = new Gson();
		File file = getAlertFile(hostname);
		String timeStamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance()
				.getTime());
		String filecontent = timeStamp + "||" + content + System.lineSeparator();
		FileUtils.writeStringToFile(file, filecontent, Charset.defaultCharset()
				.toString(), true);
		return gson.toJson(TerminalExecutorRepository.listProcesses());
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/getAlert")
	public String getAlert(@RequestParam(value = "hostname", required = true) String hostname) throws Exception {
		Gson gson = new Gson();
		File file = getAlertFile(hostname);
		return gson.toJson(FileUtils.readFileToString(file, Charset.defaultCharset()
				.toString()));
	}

	private static File getAlertFile(String hostname) {
		return new File(WorkspaceConstants.APP_TEMP_WORKSPACE, hostname + "alerts.log");
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/getRunningActions")
	public String getRunningActions() throws Exception {
		Gson gson = new Gson();
		return gson.toJson(TerminalExecutorRepository.listProcesses());
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/killAction")
	public String killAction(@RequestParam(value = "id", required = true) String id) throws Exception {
		Gson gson = new Gson();
		TerminalExecutorRepository.removeProcess(id);
		return gson.toJson("OK");
	}

	@CrossOrigin(origins = ORIGINS)
	@PostMapping("/uploadfile")
	public String uploadfile(@RequestParam("file") MultipartFile uploadfile,
			@RequestParam(value = "directory", required = true) String directory) throws Exception {
		File inputDirectory = new File(directory);
		if (!inputDirectory.exists() || !inputDirectory.isDirectory()) {

			System.out.println("Not a valid directory. Saving in server workspace.");
			inputDirectory = new File(getUploadDirectory());
		}

		File serverSideUploaded = new File(inputDirectory, uploadfile.getOriginalFilename());
		byte[] bytes = uploadfile.getBytes();
		FileUtils.writeByteArrayToFile(serverSideUploaded, bytes, false);

		return "Successfully uploaded to " + serverSideUploaded.getAbsolutePath();
	}

	public static synchronized String getUploadDirectory() {

		String directory = System.getProperty("java.io.tmpdir");

		if (directory == null || directory.isEmpty()) {
			directory = System.getProperty("user.dir");
		}

		return directory;
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/getCurrentDir")
	public String getCurrentDir() throws Exception {
		Gson gson = new Gson();
		// TODO cache this to disk and return that
		return gson.toJson(FileNamesProvider.getCurrentDir());
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/getCurrentDirContents")
	public String getCurrentDirContents() throws Exception {
		Gson gson = new Gson();
		// TODO cache this to disk

		return gson.toJson(FileNamesProvider.getFolderContents(FileNamesProvider.getCurrentDir()));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "getParentDirectoryPath")
	public String getParentDirectoryPath(@RequestParam(value = "path", required = true) String path) throws Exception {
		File file = new File(path);
		File parentFile = file.getParentFile();
		if (parentFile == null) {
			return path;
		}
		return new Gson().toJson(parentFile.getAbsolutePath()
				.replace("\\", "/"));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "getDirContents")
	public ResponseEntity<String> getDirContents(@RequestParam(value = "path", required = true) String path)
			throws Exception {
		try {
			DirectoryContents folderContents = FileNamesProvider.getFolderContents(path);
			FileNamesProvider.saveDirToCache(path);
			return new ResponseEntity<String>(new Gson().toJson(folderContents), HttpStatus.OK);
		}
		catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "backupAndDelete")
	public String backupAndDelete(@RequestParam(value = "path", required = true) String path) throws Exception {
		return new Gson().toJson(FileManager.backupAndDelete(path));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "downloadLog")
	@ResponseBody
	public String downloadLog(@RequestParam(value = "fileName", required = true) String fileName,
			@RequestParam(value = "dirPath", required = true) String dirPath,
			@RequestParam(value = "readerKey", required = true) String readerKey) {
		try {

			Map<String, String> requestMap = new HashMap<String, String>();

			requestMap.put("action", "download");
			requestMap.put("fileName", fileName);
			requestMap.put("dirPath", dirPath);
			requestMap.put("readerKey", readerKey);
			Gson gson = new Gson();

			List<String> processWebLogs = LogDownloadRequestProcessor.process(gson.toJson(requestMap));

			return gson.toJson(processWebLogs);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// OLD ENDPOINT - NOT IN USE NOW
	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "getDirectoryContents")
	public String getFavouritesNames() throws Exception {
		return new Gson().toJson(FileNamesProvider.getFolderNames(null, null));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "addDir")
	public String setDirs(@RequestParam(value = "path", required = true) String path,
			@RequestParam(value = "displayName", required = true) String displayName) throws Exception {
		return new Gson().toJson(FileNamesProvider.getFolderNames(path, displayName));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "clearFavDirs")
	public String clearFavDirs() throws Exception {
		FileNamesProvider.clearFavDirs();
		return new Gson().toJson(FileNamesProvider.getFolderNames(null, null));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "getRemoteDirectoryContents")
	public String getRemoteDirectoryContents() throws Exception {
		return new Gson().toJson(FileNamesProvider.getFilesList(TempConstants.DIRS_TO_READ));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/getAllActions")
	public String getAllActions() throws Exception {
		Gson gson = new Gson();
		return gson.toJson(ActionsManager.getAllActions());
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/addAction")
	public String addAction(@RequestParam(value = "actionId", required = true) String actionId,
			@RequestParam(value = "command", required = true) String command,
			@RequestParam(value = "workspace", required = true) String workspace) throws Exception {
		Gson gson = new Gson();
		return gson.toJson(ActionsManager.addAction(actionId, command, workspace));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/exportAction")
	public FileSystemResource exportAction(@RequestParam(value = "actionId", required = true) String actionId,
			HttpServletResponse response) throws Exception {
		File exportAction = ActionsManager.exportAction(actionId);
		response.setHeader("Content-Disposition", "attachment; filename=" + exportAction.getName());
		return new FileSystemResource(exportAction);
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/executeAction")
	public ResponseEntity<ExecutionResponse> executeAction(@RequestParam(value = "actionId") String actionId)
			throws Exception {

		return new ResponseEntity<ExecutionResponse>(
				CommandExecutionManager.executeCommand(ActionsManager.getAction(actionId)), HttpStatus.OK);

	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/executeActionAsync")
	public String executeActionAsync(@RequestParam(value = "actionId") String actionId) throws Exception {
		if (actionId == null || actionId.isEmpty()) {
			return "Please provide an action id as /executeAction?actionId={ACTIONID}";
		}

		Gson gson = new Gson();

		Thread t1 = new Thread(new AsyncRunner(actionId));
		t1.start();
		return gson.toJson("OK");
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/executeFormula")
	public String executeFormula(@RequestParam(value = "id") String id) throws Exception {
		if (id == null || id.isEmpty()) {
			return "Please provide an id as /executeFormula?id={FORMULANAME}";
		}

		Gson gson = new Gson();
		return gson.toJson(FormulaManager.executeFormula(id));
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/resetFormulae")
	public String resetFormulae() throws Exception {

		Gson gson = new Gson();
		CustomFormulaExecutor.setupFormulaeCrons();
		return gson.toJson("OK");
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/statusCheck")
	public String getStatusCheck() throws Exception {
		return "OK";
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "remoteRequest")
	@ResponseBody
	public String tailLog(@RequestParam(value = "url", required = true) String url) {
		try {
			String response = RestUtils.getResponse(url, "GET", null);
			return response;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/screenshot", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public FileSystemResource screenshot(HttpServletResponse response) {
		try {

			File screenshotsDir = new File(System.getProperty("java.io.tmpdir"), "webterminalscreenshots");
			screenshotsDir.mkdirs();

			String timestamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
			File screenshotFile = new File(screenshotsDir, "screenshot-" + timestamp + ".png");

			BufferedImage image = new Robot().createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit()
					.getScreenSize()));
			ImageIO.write(image, "png", screenshotFile);

			System.out.println("Screenshot created at " + screenshotFile.getAbsolutePath());

			response.setHeader("Content-Disposition", "attachment; filename=" + screenshotFile.getName());
			return new FileSystemResource(screenshotFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	// @CrossOrigin(origins = ORIGINS)
	// @RequestMapping(value = "/cursor")
	// @ResponseBody
	// public String cursor(@RequestParam(value = "x", required = true) String
	// x,
	// @RequestParam(value = "y", required = true) String y, HttpServletResponse
	// response) {
	// try {
	//
	// new Robot().mouseMove(Integer.parseInt(x), Integer.parseInt(y));
	//
	// return "OK";
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @CrossOrigin(origins = ORIGINS)
	// @RequestMapping(value = "/cursor1")
	// @ResponseBody
	// public String cursor1(HttpServletResponse response) {
	// try {
	// Robot robot = new Robot();
	//
	// robot.mouseMove(377, 1058);
	// robot.delay(500);
	// robot.mouseMove(265, 967);
	// robot.delay(500);
	// robot.mousePress(InputEvent.BUTTON1_MASK);
	// robot.delay(1);
	// robot.mouseRelease(InputEvent.BUTTON1_MASK);
	//
	// return "OK";
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @CrossOrigin(origins = ORIGINS)
	// @RequestMapping(value = "/click")
	// @ResponseBody
	// public String click(HttpServletResponse response) {
	// try {
	//
	// Robot robot = new Robot();
	//
	// robot.mousePress(InputEvent.BUTTON1_MASK);
	// robot.delay(1);
	// robot.mouseRelease(InputEvent.BUTTON1_MASK);
	//
	// return "OK";
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return null;
	// }

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "/download", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@ResponseBody
	public FileSystemResource downloadFile(@RequestParam(value = "key", required = true) String key,
			HttpServletResponse response) {
		try {
			File downloadFile = DownloadFileRepository.getFile(key);
			response.setHeader("Content-Disposition", "attachment; filename=" + downloadFile.getName());
			return new FileSystemResource(downloadFile);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "tailLog")
	@ResponseBody
	public String tailLog(@RequestParam(value = "fileName", required = true) String fileName,
			@RequestParam(value = "dirPath", required = true) String dirPath,
			@RequestParam(value = "readerKey", required = true) String readerKey) {
		try {
			File logFile = new File(dirPath, fileName);
			List<String> logLines = LogReaderProvider.readLogHTTP(logFile.getAbsolutePath(), 500, readerKey);
			Gson gson = new Gson();
			return gson.toJson(logLines);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "getReaderKey")
	@ResponseBody
	public String getReaderKey(@RequestParam(value = "readerKey", required = true) String readerKey) {
		try {
			return ReaderKeyRepository.generateReaderKey();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "handshake")
	public String handshake() throws Exception {
		return "OK";
	}

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "status")
	public String status() throws Exception {
		return "OK";
	}

	/*
	 * Experimental
	 */
	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "testcrash")
	public String crash() throws Exception {
		System.exit(0);
		return "OK";
	}

	static int testcount = 0;

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "testcount")
	public String testcount() throws Exception {
		testcount++;
		return testcount + "";
	}

	static String rancomStr = UUID.randomUUID()
			.toString();

	@CrossOrigin(origins = ORIGINS)
	@RequestMapping(value = "testrandom")
	public String testrandom() throws Exception {
		return rancomStr;
	}

}
