/*package com.ey.advisory.app.service.ims;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.ey.advisory.app.data.entities.simplified.client.GetAnx1BatchEntity;

public class Testclass {
	private static final String IMS_STS_SUCCESS = "SUCCESS";
	private static final String IMS_STS_SUCCESSNODATA = "SUCCESS_WITH_NO_DATA";
	private static final String IMS_STS_PARTIALSUCCESS = "PARTIAL SUCCESS";
	private static final String IMS_STS_FAILED = "FAILED";
	private static final String IMS_STS_INPROGRESS = "INPROGRESS";
	private static final String IMS_STS_INITIATED = "INITIATED";
	private static final String IMS_STS_NOTINITIATED = "NOT INITIATED";
	private static final String IMS_TAXPERIOD = "000000";

	public static void main(String[] args) {
		  ImsEntitySummaryServiceImpl test = new ImsEntitySummaryServiceImpl();

	        // Create test data with LocalDateTime
	        List<GetAnx1BatchEntity> statuses1 = new ArrayList<>();
	        statuses1.add(new GetAnx1BatchEntity(IMS_STS_SUCCESS, LocalDateTime.of(2023, 9, 15, 9, 30)));
	        statuses1.add(new GetAnx1BatchEntity(IMS_STS_FAILED, LocalDateTime.of(2023, 9, 15, 9, 30)));
	        statuses1.add(new GetAnx1BatchEntity(IMS_STS_SUCCESSNODATA, LocalDateTime.of(2023, 9, 15, 9, 30)));
	        statuses1.add(new GetAnx1BatchEntity(IMS_STS_PARTIALSUCCESS, LocalDateTime.of(2023, 9, 15, 9, 31)));

	        List<GetAnx1BatchEntity> statuses2 = new ArrayList<>();
	        statuses2.add(new GetAnx1BatchEntity(IMS_STS_INPROGRESS, LocalDateTime.of(2023, 9, 12, 13, 30)));
	        statuses2.add(new GetAnx1BatchEntity(IMS_STS_NOTINITIATED, LocalDateTime.of(2023, 9, 12, 13, 30)));
	        statuses2.add(new GetAnx1BatchEntity(IMS_STS_INITIATED, LocalDateTime.of(2023, 9, 12, 13, 31)));
	        statuses2.add(new GetAnx1BatchEntity(IMS_STS_SUCCESSNODATA, LocalDateTime.of(2023, 9, 12, 13, 30)));

	        List<GetAnx1BatchEntity> statuses3 = new ArrayList<>();
	        statuses3.add(new GetAnx1BatchEntity(IMS_STS_FAILED, LocalDateTime.of(2023, 8, 15, 10, 30)));
	        statuses3.add(new GetAnx1BatchEntity(IMS_STS_SUCCESS, LocalDateTime.of(2023, 9, 10, 15, 30)));

	        // Test determineOverallStatus method
	        System.out.println("Test case 1 (All Success): " + test.determineOverallStatus(statuses1));  
	        System.out.println("Test case 2 (In Progress): " + test.determineOverallStatus(statuses2));  
	        System.out.println("Test case 3 (Partial Success): " + test.determineOverallStatus(statuses3)); 

	        // Test timestamp logic for finding the latest timestamp
	        LocalDateTime detailTimestamp1 = statuses1.stream()
	                .map(GetAnx1BatchEntity::getCreatedOn)
	                .filter(Objects::nonNull)
	                .max(LocalDateTime::compareTo)
	                .orElse(null);
	        System.out.println("Latest timestamp in statuses1: " + detailTimestamp1);

	        LocalDateTime detailTimestamp2 = statuses2.stream()
	                .map(GetAnx1BatchEntity::getCreatedOn)
	                .filter(Objects::nonNull)
	                .max(LocalDateTime::compareTo)
	                .orElse(null);
	        System.out.println("Latest timestamp in statuses2: " + detailTimestamp2);

	        LocalDateTime detailTimestamp3 = statuses3.stream()
	                .map(GetAnx1BatchEntity::getCreatedOn)
	                .filter(Objects::nonNull)
	                .max(LocalDateTime::compareTo)
	                .orElse(null);
	        System.out.println("Latest timestamp in statuses3: " + detailTimestamp3);
	    }

}
*/