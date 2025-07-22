package com.ey.advisory.common.web.async;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.APICryptoException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.async.AsyncExecControlParams;
import com.ey.advisory.common.async.JobUrlResolver;
import com.ey.advisory.common.async.TaskFetcherAndDistributor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.async.repositories.master.GroupRepository;
import com.ey.advisory.core.async.repositories.master.PeriodicExecJobRepository;
import com.ey.advisory.domain.client.GlReconSFTPConfigEntity;
import com.ey.advisory.gstnapi.domain.master.GstnAPIGroupConfig;
import com.ey.advisory.gstnapi.repositories.master.GstnAPIGroupConfigRepository;
import com.ey.advisory.repositories.client.GLReconSFTPConfigRepository;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


/**
 * Dummy Web Controller. Monitoring components will be included as REST
 * controllers in this application, in the future.
 * 
 * @author Sai.Pakanati
 *
 */
@RestController
public class WebController {

	@Autowired
	@Qualifier("JsonBasedJobUrlResolverImpl")
	private JobUrlResolver jobUrlResolver;
	
	@Autowired
	@Qualifier("DBTaskFetcherAndDistributor")
	private TaskFetcherAndDistributor fetcherAndDistributor;

	@Autowired
	@Qualifier("EyAsyncMiscIOPool")
	private ThreadPoolTaskExecutor execPool;

	@Autowired
	private AsyncExecControlParams controlParams;

	@Autowired
	private PeriodicExecJobRepository periodicJobRepo;
	
	@Autowired
	private AsyncExecJobRepository asyncJobRepo;
	
	@Autowired
	private GLReconSFTPConfigRepository sftpConfig;
		
	@Autowired
	private Environment env;
	
	@Autowired
	@Qualifier("GstnAPIGroupConfigRepository")
	private GstnAPIGroupConfigRepository gstnAPIGroupConfigRepo;

	@Autowired
	@Qualifier("GroupRepository")
	private GroupRepository groupRepository;
			
	public static final String AES_TRANSFORMATION = "AES/ECB/PKCS7Padding";
	public static final String AES_ALGORITHM = "AES";

	private static final Logger LOGGER = LoggerFactory
			.getLogger(WebController.class);

	/**
	 * Use this method to test the embedded tomcat.
	 * 
	 * @return a dummy string
	 */
	@RequestMapping("/test")
	public String handler() {
		return "{\"msg\": \"App is UP and RUNNING\"}";
	}

	/**
	 * Use this method to suspend the processing of further tasks.
	 * 
	 * @return a dummy string
	 */
	@RequestMapping("/suspend")
	public String suspend() {
		boolean success = controlParams.setSuspended(true);

		String statusMsg = success
				? "Successfully set the " + "AsyncExec app to SUSPENDED state"
				: "AsyncExec App is already in SUSPENDED state. "
						+ "Not changing the status.";

		// Create a dto with null as the user message.
		AsyncExecAppDetailsDto dto = getAsyncDetailsJson(statusMsg);
		Gson gson = JsonUtil.newGsonInstance();

		return gson.toJson(dto);
	}

	/**
	 * Use this method to resume the processing of further tasks.
	 * 
	 * @return a dummy string
	 */
	@RequestMapping("/resume")
	public String resume() {
		boolean success = controlParams.setSuspended(false);

		String statusMsg = success
				? "Successfully set the " + "AsyncExec app to RUNNING state"
				: "AsyncExec App is already in RUNNING state. "
						+ "Not changing the status.";

		// Create a dto with null as the user message.
		AsyncExecAppDetailsDto dto = getAsyncDetailsJson(statusMsg);
		Gson gson = JsonUtil.newGsonInstance();

		return gson.toJson(dto);
	}

	/**
	 * Use this method to get the status of the AsyncExecute application.
	 */
	@RequestMapping("/status")
	public String status() {

		// Create a dto with null as the user message.
		AsyncExecAppDetailsDto dto = getAsyncDetailsJson(null);
		Gson gson = JsonUtil.newGsonInstance();

		return gson.toJson(dto);
	}

	private AsyncExecAppDetailsDto getAsyncDetailsJson(String msg) {

		// status can be 'SUSPENDED' or 'RUNNING'
		String status = controlParams.getStatus();

		// Frame the comma separated string containing the task types.
		List<String> existingTasks = fetcherAndDistributor.getTaskTypeList();
		String taskTypesStr = !existingTasks.isEmpty()
				? StringUtils.join(existingTasks, ",") : "";

		int activeThreadCount = execPool.getActiveCount();
		int maxPoolSize = execPool.getMaxPoolSize();
		int corePoolSize = execPool.getCorePoolSize();

		// Create an async exc dto to later convert to json
		return new AsyncExecAppDetailsDto(status,
				activeThreadCount, maxPoolSize, corePoolSize, taskTypesStr,
				msg);

	}

	@RequestMapping("/replaceTasks")
	public String replaceTasks(@RequestParam String taskTypes) {
		try {

			if (taskTypes == null || taskTypes.trim().isEmpty()) {
				String msg = "The taskTypes Request "
						+ "URL parameter cannot be empty. "
						+ "Not switching taskTypes";
				LOGGER.error(msg);

				// Create a dto with null as the user message.
				AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
				Gson gson = JsonUtil.newGsonInstance();

				return gson.toJson(dto);
			}

			// Get the new tasks by parsing the task types list.
			List<String> newTasks = splitTaskList(taskTypes);

			// Get the existing task type list
			CopyOnWriteArrayList<String> existingTasks = fetcherAndDistributor
					.getTaskTypeList();

			// Get the list as a string for printing message.
			String existingTasksStr = StringUtils.join(existingTasks, ",");

			// Get the new task list as string, for printing message.
			String newTasksStr = StringUtils.join(newTasks, ",");

			// Replace the contents of the existing task list with the
			// new task list. We do this in a consistent manner, such that the
			// task type list in the db fetcher and distributor class will
			// always contain valid entries. i.e. it will eliminate existing
			// entries and add the new entries in a phased manner.
			replaceTaskTypes(newTasks, existingTasks);

			// Print the list of tasks to the logger.
			printListOfTasks(existingTasks);

			String msg = String.format(
					"Replaced existing tasks [%s] " + "with new tasks [%s]",
					existingTasksStr, newTasksStr);

			// Create a dto with null as the user message.
			AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
			Gson gson = JsonUtil.newGsonInstance();

			return gson.toJson(dto);

		} catch (Exception ex) {
			String msg = "Errror while replacing the task types. "
					+ ex.getMessage();
			LOGGER.error(msg, ex);

			// Create a dto with null as the user message.
			AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
			Gson gson = JsonUtil.newGsonInstance();

			return gson.toJson(dto);
		}
	}

	
	@PostMapping(value = "/replaceTasksPost")
	public String replaceTasksPost(@RequestBody String taskTypes) {
		String newTaskTypes = null;
		try {
			JsonObject jsonObject = JsonParser.parseString(taskTypes)
					.getAsJsonObject();

			JsonObject reqObj = jsonObject.getAsJsonObject("req");

			JsonArray taskTypesArray = reqObj.getAsJsonArray("taskTypes");

			if (taskTypesArray == null || taskTypesArray.size() == 0) {
				String msg = "The taskTypes JSON array cannot be empty. Not switching taskTypes";
				LOGGER.error(msg);

				AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
				Gson gson = JsonUtil.newGsonInstance();
				return gson.toJson(dto);
			}

			for (JsonElement element : taskTypesArray) {
				newTaskTypes = element.getAsString();
			}

			// Get the new tasks by parsing the task types list.
			List<String> newTasks = splitTaskList(newTaskTypes);

			// Get the existing task type list
			CopyOnWriteArrayList<String> existingTasks = fetcherAndDistributor
					.getTaskTypeList();

			// Get the list as a string for printing message.
			String existingTasksStr = StringUtils.join(existingTasks, ",");

			// Get the new task list as string, for printing message.
			String newTasksStr = StringUtils.join(newTasks, ",");

			// Replace the contents of the existing task list with the
			// new task list. We do this in a consistent manner, such that the
			// task type list in the db fetcher and distributor class will
			// always contain valid entries. i.e. it will eliminate existing
			// entries and add the new entries in a phased manner.
			replaceTaskTypes(newTasks, existingTasks);

			// Print the list of tasks to the logger.
			printListOfTasks(existingTasks);

			String msg = String.format(
					"Replaced existing tasks [%s] " + "with new tasks [%s]",
					existingTasksStr, newTasksStr);

			// Create a dto with null as the user message.
			AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
			Gson gson = JsonUtil.newGsonInstance();

			return gson.toJson(dto);

		} catch (Exception ex) {
			String msg = "Errror while replacing the task types. "
					+ ex.getMessage();
			LOGGER.error(msg, ex);

			// Create a dto with null as the user message.
			AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
			Gson gson = JsonUtil.newGsonInstance();

			return gson.toJson(dto);
		}
	}

	private void printListOfTasks(List<String> taskNames) {
		// Printing the list of tasks.
		IntStream.range(0, taskNames.size()).forEach(i -> {
			if (LOGGER.isInfoEnabled()) {
				String msg = String.format(
						"Task Type No: '%d' for this "
								+ "AsyncExec instance = '%s'",
						i + 1, taskNames.get(i));
				LOGGER.info(msg); // Print the list of task Types.
			}
		});
	}

	private List<String> splitTaskList(String taskTypes) {
		// Split the string with comma as the delimiter.
		String[] taskNames = taskTypes.split(",");

		return Arrays.stream(taskNames).map(String::trim)
				.collect(Collectors.toCollection(ArrayList::new));
	}

	private void replaceTaskTypes(List<String> newTasks,
			CopyOnWriteArrayList<String> existingTasks) {

		// Find out all the elements that are present in the new task list,
		// but are not present in the existing task list.
		Collection<String> coll = CollectionUtils.subtract(newTasks,
				existingTasks);
		List<String> diffList = new ArrayList<>(coll);

		// First remove the tasks that are not in the new task list. Note that
		// this will be carried out in a separate copy of the array list.
		// We're making sure that the arraylist will hold valid data all the
		// time.
		existingTasks.retainAll(newTasks);

		// At this point, the existing task list will contain all the valid
		// elements of the new list, which was already present in the existing
		// list. Note that at this time the DB Fetcher loop can use the
		// existingTasks, with the partial list. But still it will be in a
		// consistent state in the sense that only a subset of the new tasks
		// will be considered at this point.

		// Once we remove the list of tasks from the existing list, we need
		// to add the new tasks in the new task list to the existing list. This
		// is present in the diff list.
		existingTasks.addAll(diffList);

		// At this point, the task list would have been replaced completely
		// with the new list.
	}

	@GetMapping(value = "/sayHello", produces = "application/json")
	public ResponseEntity<String> sayHello() {

		try {
			String str = "Hello " + TenantContext.getTenantId();
			return new ResponseEntity<>(str, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<>(e.getMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/resetPeriodicJobById", produces = "application/json")
	public ResponseEntity<String> resetPeriodicJobById(
			@RequestParam("jobId") Long jobId) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			int updatedOrNot = periodicJobRepo.resetPeriodicJobById(jobId);
			JsonObject resp = new JsonObject();
			if (updatedOrNot > 0) {
				resp.add("hdr", gson.toJsonTree("S"));
				resp.add("resp",
						gson.toJsonTree("Job Reset has been Successful"));
			} else {
				resp.add("hdr", gson.toJsonTree("E"));
				resp.add("resp",
						gson.toJsonTree("No Record Found for the " + jobId));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while ResetPeriodicJobById  ", e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@GetMapping(value = "/resetPeriodicJobs", produces = "application/json")
	public ResponseEntity<String> resetPeriodicJobs() {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			int updatedOrNot = periodicJobRepo.resetPeriodicJobs();
			JsonObject resp = new JsonObject();
			if (updatedOrNot > 0) {
				resp.add("hdr", gson.toJsonTree("S"));
				resp.add("resp",
						gson.toJsonTree(
								"Job Reset has been Successful,No of Jobs Reseted is "
										+ updatedOrNot));
			} else {
				resp.add("hdr", gson.toJsonTree("E"));
				resp.add("resp", gson.toJsonTree("No Records Found"));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while ResetPeriodicJobById  ", e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/resetallPeriodicJobs", produces = "application/json")
	public ResponseEntity<String> resetallPeriodicJobs() {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		try {
			int updatedOrNot = periodicJobRepo.resetallPeriodicJobs();
			JsonObject resp = new JsonObject();
			if (updatedOrNot > 0) {
				resp.add("hdr", gson.toJsonTree("S"));
				resp.add("resp",
						gson.toJsonTree(
								"Job Reset has been Successful,No of Jobs Reseted is "
										+ updatedOrNot));
			} else {
				resp.add("hdr", gson.toJsonTree("E"));
				resp.add("resp", gson.toJsonTree("No Records Found"));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while ResetPeriodicJobById  ", e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/replaceJobBundle", produces = "application/json")
	public ResponseEntity<String> replaceJobBundle(
			@RequestParam("jobId") String jobName,
			@RequestParam("jobBundle") String jobBundleName) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			String preJobBundle = jobUrlResolver.replaceTaskUrl(jobName,
					jobBundleName);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("S"));
			resp.add("resp",
					gson.toJsonTree(
							String.format("Job %s has mapped to %s from %s",
									jobName, jobBundleName, preJobBundle)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while replaceJobBundle ", e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/resetJobtoSub", produces = "application/json")
	public ResponseEntity<String> resetJobtoSub(
			@RequestParam("jobId") Long jobId,@RequestParam("status") String jobStatus) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			int updatedOrNot = asyncJobRepo.updateJobDetails(jobId,jobStatus);
			JsonObject resp = new JsonObject();
			if (updatedOrNot > 0) {
				resp.add("hdr", gson.toJsonTree("S"));
				resp.add("resp",
						gson.toJsonTree("Job Reset has been Successful"));
			} else {
				resp.add("hdr", gson.toJsonTree("E"));
				resp.add("resp",
						gson.toJsonTree("No Record Found for the " + jobId));
			}
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while ResetPeriodicJobById  ", e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@GetMapping(value = "/insertGlSftpCredentials", produces = "application/json")
	public ResponseEntity<String> insertSftpCredentials(
			@RequestParam("userName") String userName,
			@RequestParam("passWord") String passWord,
			@RequestParam("groupCode") String groupCode) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();
		try {
			TenantContext.setTenantId(groupCode);

			GlReconSFTPConfigEntity entity = new GlReconSFTPConfigEntity();

			entity.setSftpUsername(getEncryption(
					env.getProperty("gl.recon.sftp.key"), userName));
			entity.setSftpPaswrd(getEncryption(
					env.getProperty("gl.recon.sftp.key"), passWord));

			List<GlReconSFTPConfigEntity> sftpConfigs = sftpConfig.findAll();
			JsonObject resp = new JsonObject();

			if (sftpConfigs.isEmpty()) {
				sftpConfig.save(entity);
				resp.add("hdr", gson.toJsonTree("S"));
				resp.add("resp", gson.toJsonTree("Updated successfully"));

			} else {
				resp.add("hdr", gson.toJsonTree("S"));
				resp.add("resp", gson.toJsonTree(
						"Please update the entry. One entry already exists. "));
			}

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while inserting sftp config  ", e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<String>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static String getEncryption(String key, String message) {

		String encrypt = encrypt(Base64.encodeBase64String(
				message.getBytes(StandardCharsets.UTF_8)), key);
		return encrypt;

	}

	public static String encrypt(String text, String encKey) {
		try {
			Cipher cipher = createCipher();
			byte[] dataToEncrypt = Base64.decodeBase64(text);
			byte[] keyBytes = Base64.decodeBase64(encKey);

			SecretKeySpec sk = new SecretKeySpec(keyBytes, AES_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, sk);
			return Base64.encodeBase64String((cipher.doFinal(dataToEncrypt)));
		} catch (Exception ex) {
			String msg = "Exception while encrypting data";
			throw new APICryptoException(msg, ex);
		}
	}

	private static Cipher createCipher() {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			return Cipher.getInstance(AES_TRANSFORMATION, "BC");
		} catch (Exception ex) {
			String msg = "Exception while instantiating "
					+ "Encrypt/Decrypt Ciphers";
			LOGGER.error(msg, ex);
			throw new APICryptoException(msg, ex);
		}
	}
	@PostMapping(value = "/insertGspOnboardingDetails", produces = "application/json")
	public ResponseEntity<String> insertGspOnboardingDetails(
			@RequestBody String jsonString) {
		Gson gson = GsonUtil.gsonInstanceWithEWBDateFormat();

		JsonObject reqObject = JsonParser.parseString(jsonString)
				.getAsJsonObject();

		JsonObject reqJson = reqObject.get("req").getAsJsonObject();
		AsyncExecAppDetailsDto reqDto = gson.fromJson(reqJson,
				AsyncExecAppDetailsDto.class);

		try {
			Group entityDetails = groupRepository
					.findByGroupCodeAndIsActiveTrue(reqDto.getGroupCode());

			GstnAPIGroupConfig gstnAPIGroupEntityPresent = gstnAPIGroupConfigRepo
					.findByGroupCode(reqDto.getGroupCode());

			if (gstnAPIGroupEntityPresent != null) {

				if (entityDetails != null) {
					gstnAPIGroupEntityPresent
							.setGroupId(entityDetails.getGroupId());
				}
				gstnAPIGroupEntityPresent.setGroupCode(reqDto.getGroupCode());
				gstnAPIGroupEntityPresent
						.setDigiGstUserName(reqDto.getDigiGstUserName());
				gstnAPIGroupEntityPresent.setApiKey(reqDto.getApiKey());
				gstnAPIGroupEntityPresent.setApiSecret(reqDto.getApiSecret());
				gstnAPIGroupEntityPresent.setGspToken(reqDto.getGspToken());
				gstnAPIGroupEntityPresent
						.setCreatedDate(EYDateUtil.toDate(LocalDateTime.now()));

				gstnAPIGroupConfigRepo.save(gstnAPIGroupEntityPresent);

			} else {

				GstnAPIGroupConfig entity = new GstnAPIGroupConfig();

				if (entityDetails != null) {
					entity.setGroupId(entityDetails.getGroupId());
				}
				entity.setGroupCode(reqDto.getGroupCode());
				entity.setDigiGstUserName(reqDto.getDigiGstUserName());
				entity.setApiKey(reqDto.getApiKey());
				entity.setApiSecret(reqDto.getApiSecret());
				entity.setGspToken(reqDto.getGspToken());
				entity.setCreatedDate(EYDateUtil.toDate(LocalDateTime.now()));

				gstnAPIGroupConfigRepo.save(entity);
			}

			JsonObject resp = new JsonObject();

			resp.add("hdr", gson.toJsonTree("S"));
			resp.add("resp", gson.toJsonTree(
					"GSP Onboarding details are Successfully inserted"));

			return new ResponseEntity<String>(resp.toString(), HttpStatus.OK);
		} catch (Exception e) {
			LOGGER.error("Exception while InsertGspOnboardingDetails  ", e);
			JsonObject resp = new JsonObject();
			resp.add("hdr", gson.toJsonTree("E"));
			resp.add("resp", gson.toJsonTree(e.getMessage()));
			return new ResponseEntity<String>(resp.toString(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
