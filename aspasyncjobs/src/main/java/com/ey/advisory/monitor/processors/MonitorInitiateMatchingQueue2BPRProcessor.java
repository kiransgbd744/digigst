/**
 * 
 */
package com.ey.advisory.monitor.processors;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.data.entities.client.asprecon.Gstr2ReconConfigEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconConfigRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.Gstr2ReconGstinRepository;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.AppExecContext;
import com.ey.advisory.common.JobConstants;
import com.ey.advisory.common.Message;
import com.ey.advisory.common.ReconStatusConstants;
import com.ey.advisory.common.multitenancy.DefaultMultiTenantTaskProcessor;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.AsyncJobsService;
import com.ey.advisory.core.async.domain.master.Group;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonObject;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */
@Slf4j
@Service("MonitorInitiateMatchingQueue2BPRProcessor")
public class MonitorInitiateMatchingQueue2BPRProcessor
		extends DefaultMultiTenantTaskProcessor {

	@Autowired
	AsyncJobsService asyncJobsService;

	@Autowired
	@Qualifier("Gstr2ReconConfigRepository")
	Gstr2ReconConfigRepository reconConfigRepo;
	
	@Autowired
	@Qualifier("Gstr2ReconGstinRepository")
	Gstr2ReconGstinRepository reconGstinRepo;

	private static List<String> status = ImmutableList
			.of(ReconStatusConstants.RECON_IN_QUEUE);
	
	private static List<String> linkingStatus = ImmutableList
			.of("Linking In-Progress", "Linking Initiated");

	private static List<String> ineligibleStatus = ImmutableList.of(
			ReconStatusConstants.RECON_INITIATED,
			ReconStatusConstants.RECON_INPROGRESS);

	@Override
	public void executeForGroup(Group group, Message message,
			AppExecContext ctx) {
		Gstr2ReconConfigEntity entity = null;
		try {
			
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside MonitorInitiateMatchingQueue2BPRProcessor "
								+ "group code {} : ",
						TenantContext.getTenantId());
			}


			if (!isReconEligible()) {

				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"Inside MonitorInitiateMatchingQueue2BPRProcessor "
									+ "group code {} no eligible recon found "
									+ "to proceed, hence terminating the "
									+ "process: ",
							TenantContext.getTenantId());
				}
				return;
			}

			
			List<Gstr2ReconConfigEntity> entityList = reconConfigRepo
					.findByStatusIn(status);
			
			entityList = entityList.stream().filter(o -> o.getType()
					.equalsIgnoreCase("2BPR")).collect(Collectors.toList());

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"Inside MonitorInitiateMatchingQueue2BPRProcessor "
								+ "group code {}, entityList {}: ",
						TenantContext.getTenantId(), entityList);
			}
			if (entityList.isEmpty()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("Inside MonitorInitiateMatchingQueue2BPRProcessor "
							+ "group code {} entityList list is empty "
							+ "no pending recon available to " + "process : ",
							TenantContext.getTenantId());
				}

				return;
			}
			
			entityList.sort(
					Comparator.comparing(Gstr2ReconConfigEntity::getConfigId));

			entity = entityList.get(0);
			
			Boolean apFlag = false;
			String reconType = entity.getType();

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside MonitorInitiateMatchingQueue2BPRProcessor "
						+ "group code {}, entity for submitting recon job {}: ",
						TenantContext.getTenantId(), entity);
			}

			reconConfigRepo.updateReconConfigStatusAndReportName(
					ReconStatusConstants.RECON_INITIATED, null,
					LocalDateTime.now(), entity.getConfigId());

			JsonObject jsonParams = new JsonObject();
			jsonParams.addProperty("configId", entity.getConfigId());

			String groupCode = TenantContext.getTenantId();
			asyncJobsService.createJob(groupCode,
					JobConstants.GSTR2_RECON_INITIATE, jsonParams.toString(),
					"SYSTEM", 1L, null, null);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside MonitorInitiateMatchingQueue2BPRProcessor "
						+ "group code {}, recon job submitted : configId {} : ",
						TenantContext.getTenantId(), entity.getConfigId());
			}

		} catch (Exception e) {
			Long configId = (entity != null) ? entity.getConfigId() : 0L;
			LOGGER.error(
					"Error occured Inside MonitorInitiateMatchingQueue2BPRProcessor "
							+ "group code {}, while recon job submitting : "
							+ "configId {} : ",
					TenantContext.getTenantId(), configId);
			throw new AppException(e);

		}

	}

	private boolean isReconEligible() {

		List<Gstr2ReconConfigEntity> entityList = reconConfigRepo
				.findByStatusIn(ineligibleStatus);
		
		entityList = entityList.stream().filter(o -> o.getType()
				.equalsIgnoreCase("2BPR")).collect(Collectors.toList());

		if (!entityList.isEmpty()) {

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Inside MonitorInitiateMatchingQueue2BPRProcessor - "
						+ "isReconEligible() group code {} previous recon "
						+ "is already in Initiate / Inprogress status hence "
						+ "returing false ", TenantContext.getTenantId());
			}
			return false;
		}

		return true;
	}
	
	
	    public static void main(String[] args) {
	        List<Integer> listA = new ArrayList<>();
	        List<Integer> listB = new ArrayList<>();

	        // Adding elements to list A
	        listA.add(1);
	        listA.add(2);
	        listA.add(3);

	        // Adding elements to list B
	        listB.add(6);
	        listB.add(4);
	        listB.add(5);

	        // Check if any element of list A is in list B
	        List<Integer> tempList = new ArrayList<>(listA);
	        tempList.retainAll(listB);

	        if (!tempList.isEmpty()) {
	            System.out.println("There is at least one common element: " + tempList);
	        } else {
	            System.out.println("No common elements found.");
	        }
	    }
}
