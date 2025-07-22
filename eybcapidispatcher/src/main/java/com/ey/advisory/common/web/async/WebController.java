package com.ey.advisory.common.web.async;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.JsonUtil;
import com.ey.advisory.common.async.AsyncExecControlParams;
import com.ey.advisory.common.async.TaskFetcherAndDistributor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.async.repositories.master.PeriodicExecJobRepository;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * Dummy Web Controller.
 * Monitoring components will be included as REST controllers in this
 * application, in the future.
 * 
 * @author Sai.Pakanati
 *
 */
@RestController
public class WebController {
	
	@Autowired
	@Qualifier("DBTaskFetcherAndDistributor")
	private TaskFetcherAndDistributor fetcherAndDistributor;

	@Autowired
	@Qualifier("EYAsyncExecPool")
	private ThreadPoolTaskExecutor execPool;
	
	@Autowired
	private AsyncExecControlParams controlParams;

	@Autowired
	private PeriodicExecJobRepository periodicJobRepo;
	
	@Autowired
	private AsyncExecJobRepository asyncJobRepo;

	private static final Logger LOGGER =
			LoggerFactory.getLogger(WebController.class); 
	/**
	 * Use this method to test the embedded tomcat.
	 * @return a dummy string
	 */
    @RequestMapping("/test")
    public String handler() {
        return "{\"msg\": \"App is UP and RUNNING\"}";
    }
    
    /**
	 * Use this method to suspend the processing of further tasks.
	 * @return a dummy string
	 */
    @RequestMapping("/suspend")
    public String suspend() {
    	boolean success = controlParams.setSuspended(true);

    	String statusMsg = success ? "Successfully set the "
    			+ "AsyncExec app to SUSPENDED state" : 
    					"AsyncExec App is already in SUSPENDED state. "
        				+ "Not changing the status.";    	

    	// Create a dto with null as the user message.
    	AsyncExecAppDetailsDto dto = getAsyncDetailsJson(statusMsg);
    	Gson gson = JsonUtil.newGsonInstance();
    	
    	String json = gson.toJson(dto);
        return json;    
    }
    
    /**
	 * Use this method to resume the processing of further tasks.
	 * @return a dummy string
	 */
    @RequestMapping("/resume")
    public String resume() {
    	boolean success = controlParams.setSuspended(false);
    	
    	String statusMsg = success ? "Successfully set the "
    			+ "AsyncExec app to RUNNING state" : 
    					"AsyncExec App is already in RUNNING state. "
        				+ "Not changing the status.";

    	// Create a dto with null as the user message.
    	AsyncExecAppDetailsDto dto = getAsyncDetailsJson(statusMsg);
    	Gson gson = JsonUtil.newGsonInstance();
    	
    	String json = gson.toJson(dto);
        return json;    
    }

    /**
	 * Use this method to get the status of the AsyncExecute application.
	 */
    @RequestMapping("/status")
    public String status() {
    	
    	// Create a dto with null as the user message.
    	AsyncExecAppDetailsDto dto = getAsyncDetailsJson(null);
    	Gson gson = JsonUtil.newGsonInstance();
    	
    	String json = gson.toJson(dto);
        return json;
    }
    
    private AsyncExecAppDetailsDto getAsyncDetailsJson(String msg) {
    	
    	// status can be 'SUSPENDED' or 'RUNNING'
    	String status = controlParams.getStatus();
    	
    	// Frame the comma separated string containing the task types.
    	List<String> existingTasks =  fetcherAndDistributor.getTaskTypeList();
    	String taskTypesStr = existingTasks.size() > 0 ?
    			StringUtils.join(existingTasks, ",") : "";
    	
    	int activeThreadCount = execPool.getActiveCount();
    	int maxPoolSize = execPool.getMaxPoolSize();
    	int corePoolSize = execPool.getCorePoolSize();
    	
    	// Create an async exc dto to later convert to json
    	AsyncExecAppDetailsDto dto = new AsyncExecAppDetailsDto(status, 
    			activeThreadCount, maxPoolSize, corePoolSize, 
    			taskTypesStr, msg);
  
    	return dto;
    }
    
    @RequestMapping("/replaceTasks")
    public String replaceTasks(@RequestParam String taskTypes) {    	
    	try {   	
	    
    		if(taskTypes == null || taskTypes.trim().isEmpty()) {
    	   		String msg = "The taskTypes Request "
    	   				+ "URL parameter cannot be empty. "
    	   				+ "Not switching taskTypes";
	    		LOGGER.error(msg);
	    		
		    	// Create a dto with null as the user message.
		    	AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
		    	Gson gson = JsonUtil.newGsonInstance();
		    	
		    	String json = gson.toJson(dto);
		        return json;    		
    		}
    		
    		// Get the new tasks by parsing the task types list.
	    	List<String> newTasks = splitTaskList(taskTypes);
	    
	    	// Get the existing task type list
	    	CopyOnWriteArrayList<String> existingTasks = 
	    			fetcherAndDistributor.getTaskTypeList();
	    	
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
	    	
	    	String msg = String.format("Replaced existing tasks [%s] "
	    			+ "with new tasks [%s]", existingTasksStr, newTasksStr);
	    	
	    	// Create a dto with null as the user message.
	    	AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
	    	Gson gson = JsonUtil.newGsonInstance();
	    	
	    	String json = gson.toJson(dto);
	        return json;
	        
    	} catch(Exception ex) {
    		String msg = "Errror while replacing the task types. " + 
    					ex.getMessage();
    		LOGGER.error(msg, ex);
    		
	    	// Create a dto with null as the user message.
	    	AsyncExecAppDetailsDto dto = getAsyncDetailsJson(msg);
	    	Gson gson = JsonUtil.newGsonInstance();
	    	
	    	String json = gson.toJson(dto);
	        return json;    		
    	}
    }
   
    private void printListOfTasks(List<String> taskNames) {
		// Printing the list of tasks.
		IntStream.range(0, taskNames.size()).forEach(i -> {
			System.out.println(i);
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

		List<String> tasks = Arrays.stream(taskNames)
				.map(taskName -> taskName.trim())
				.collect(Collectors.toCollection(ArrayList::new));
		return tasks;
    }
    
    private void replaceTaskTypes(List<String> newTasks, 
    			CopyOnWriteArrayList<String> existingTasks) {
    	
    	// Find out all the elements that are present in the new task list,
    	// but are not present in the existing task list.
    	Collection<String> coll = 
    			CollectionUtils.subtract(newTasks, existingTasks);
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
			return new ResponseEntity<String>(str, HttpStatus.OK);
		} catch (Exception e) {
			return new ResponseEntity<String>(e.getMessage(),
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
}
