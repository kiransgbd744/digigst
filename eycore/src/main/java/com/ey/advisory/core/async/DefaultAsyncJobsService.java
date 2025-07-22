package com.ey.advisory.core.async;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.core.async.domain.master.AsyncExecJob;
import com.ey.advisory.core.async.domain.master.PeriodicExecJob;
import com.ey.advisory.core.async.repositories.master.AsyncExecJobRepository;
import com.ey.advisory.core.async.repositories.master.PeriodicExecJobRepository;

@Component
@Transactional(value = "masterTransactionManager", propagation = Propagation.REQUIRED, readOnly = false)
public class DefaultAsyncJobsService implements AsyncJobsService {

	private static final int NO_OF_JOBS_PER_BATCH = 50;

	@Autowired
	private AsyncExecJobRepository jobRepository;

	@Autowired
	private PeriodicExecJobRepository periodicJobRepo;

	@Override
	public AsyncExecJob createJob(AsyncExecJob job) {
		Date createDate = (job.getCreatedDate() != null) ? job.getCreatedDate()
				: new Date();
		job.setCreatedDate(createDate);
		job.setUpdatedDate(createDate);
		return jobRepository.save(job);
	}

	@Override
	public List<AsyncExecJob> createJobs(List<AsyncExecJob> jobs) {

		int noOfJobs = jobs.size();

		// If the total number of jobs is less than the batch size, then
		// save everything in one stretch. The repository will be flushed
		if (noOfJobs <= NO_OF_JOBS_PER_BATCH) {
			return jobRepository.saveAll(jobs);
		}

		int noOfBatches = (noOfJobs / NO_OF_JOBS_PER_BATCH)
				+ ((noOfJobs % NO_OF_JOBS_PER_BATCH) > 0 ? 1 : 0);

		List<AsyncExecJob> jobsBatch = new ArrayList<>();
		List<AsyncExecJob> retList = new ArrayList<>();

		for (int i = 0; i < noOfBatches; i++) {

			int startIdx = i * NO_OF_JOBS_PER_BATCH;
			int endIdx = (i == noOfBatches - 1) ? jobs.size()
					: startIdx + NO_OF_JOBS_PER_BATCH;

			// Add the list of jobs to be saved, to a new batch.
			jobsBatch.addAll(jobs.subList(startIdx, endIdx));

			// After saving one batch of jobs, add the saved list of jobs
			// to the return list. Finally, flush the objects.
			retList.addAll(jobRepository.saveAll(jobsBatch));
			jobRepository.flush();
			jobsBatch.clear();
		}

		// Return the list of jobs saved to the DB. All objects saved within
		// this method will be atomically committed to the DB, within the
		// transaction.
		return retList;
	}

	public void createAsyncJobsAndUpdatePeriodicJobs(List<AsyncExecJob> jobs,
			List<PeriodicExecJob> periodicJobs) {
		List<AsyncExecJob> retJobs = createJobs(jobs);
		Date date = new Date();

		List<Long> ids = periodicJobs.stream().map(job -> job.getId())
				.collect(Collectors.toList());

		// Reload all the detached objects in one shot.
		List<PeriodicExecJob> reloadedJobs = periodicJobRepo.findAllById(ids);

		IntStream.range(0, retJobs.size()).forEach(i -> {
			reloadedJobs.get(i).setLastPostedJobId(retJobs.get(i).getJobId());
			reloadedJobs.get(i).setUpdatedDate(date);
			reloadedJobs.get(i).setLastPostedDate(date);
		});

		// Save all the periodic jobs with the updated ids.
		periodicJobRepo.saveAll(reloadedJobs);

	}

	@Override
	public AsyncExecJob createJob(String groupCode, String jobCategory,
			String jsonParam, String userName, Long priority, Long parentJobId,
			Long scheduleAfterInMins) {

		AsyncExecJob job = createAndGetJob(groupCode, jobCategory, jsonParam,
				userName, priority, parentJobId, scheduleAfterInMins);
		return jobRepository.save(job);
	}

	private AsyncExecJob createAndGetJob(String groupCode, String jobCategory,
			String jsonParam, String userName, Long priority, Long parentJobId,
			Long scheduleAfterInMins) {
		Date curTime = new Date();
		AsyncExecJob job = new AsyncExecJob();
		job.setGroupCode(groupCode);
		job.setJobCategory(jobCategory);
		job.setStatus(JobStatusConstants.SUBMITTED);
		job.setMessage(jsonParam);
		job.setJobPriority(priority);
		job.setUserName(userName);
		job.setParentId(parentJobId);
		job.setCreatedDate(curTime);
		job.setUpdatedDate(curTime);
		if (scheduleAfterInMins != null && scheduleAfterInMins > 0) {
			job.setScheduled(true);
			LocalDateTime schedTime = LocalDateTime.now()
					.plusMinutes(scheduleAfterInMins);
			Date asyncTime = java.sql.Timestamp.valueOf(schedTime);
			job.setScheduledTime(asyncTime);
		}
		return job;
	}

	@Override
	public AsyncExecJob createJobAndReturn(String groupCode, String jobCategory,
			String jsonParam, String userName, Long priority, Long parentJobId,
			Long scheduleAfterInMins) {

		return createAndGetJob(groupCode, jobCategory, jsonParam, userName,
				priority, parentJobId, scheduleAfterInMins);

	}

}
