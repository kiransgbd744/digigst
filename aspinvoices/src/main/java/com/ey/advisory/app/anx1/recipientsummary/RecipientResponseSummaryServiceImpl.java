package com.ey.advisory.app.anx1.recipientsummary;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.common.AppException;
import com.google.common.collect.ImmutableList;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Arun KA
 *
 **/


@Slf4j
@Component("RecipientResponseSummaryServiceImpl")
public class RecipientResponseSummaryServiceImpl
		implements RecipientResponseSummaryService {

	@Autowired
	@Qualifier("RecipientResponseSummaryDetailDaoImpl")
	RecipientResponseSummaryDetailDao recipientResponseSummaryDetailDao;
	
	@Autowired
	@Qualifier("RecipientServiceImpl")
	RecipientService recipientService;

	public List<RecipientResponseSummaryDto> getRecipientResponseSummary(
			String condition, RecipientSummaryRequestDto request) {
		
		
		
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin getRecipientResponseSummary service. "
					+ "creating response with 3 levels of data.";
			LOGGER.debug(msg);
		}
		
		

			Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			Map<String, String> outwardSecurityAttributeMap 
			= DataSecurityAttributeUtil
					.getOutwardSecurityAttributeMap();
			dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(
							Arrays.asList(request.getEntityId()),
							outwardSecurityAttributeMap);
			setDataSecurityAttributes(request, dataSecurityAttrMap);
			condition = queryCondition(request);
		
		if(CollectionUtils.isEmpty(request.getGstins()))
			throw new AppException("User Does not have any gstin");
		//Getting 3rd level of response from DB with provided input parameters
		List<RecipientDBResponseDto> rdbList = recipientResponseSummaryDetailDao
				.getAllResponseSummaryDetails(condition, request);

		
		/*Streaming the db response data and grouping by CPan,Cgstin,TableSection
		 * doctype to generate 3rd level of data
		 */
		Collection<RecipientResponseSummaryDto> l3List = rdbList.stream()
				.collect(
						Collectors.groupingBy(
							o -> o.getCPan() + o.getCgstin()
							+ o.getTableSection() + o.getDocType(),
							getCollectorForLevel("L3", rdb -> convertDto(rdb))))
				.values();
		
		/*The above l3List will not contain the cName so first we 
         * are collecting all the cPan from l3List and then we are 
         * calling another service to get all the cName for 
         * corresponding cNames .
         * */
       
        
        List<String> cPanList = l3List.stream()
                     .map(o -> o.getCPan())
                     .collect(Collectors.toList());
       
        Map<String, String> cPanNameMap = recipientService
                     .getCNamesForCPans(cPanList);
       
       l3List.forEach(o -> 
              o.setCName(!cPanNameMap.containsKey(o.getCPan())
                                  ? "NA" : cPanNameMap.get(o.getCPan())));

		
		/*Streaming the 3rd level of data and grouping by Cgstin
		 * to generate 2nd level of data
		 */
		Collection<RecipientResponseSummaryDto> l2List = l3List.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getCgstin(),
						getCollectorForLevel("L2", Function.identity())))
				.values();
		
		/*Streaming the 2rd level of data and grouping by CPan
		 * to generate 1st level of data
		 */
		Collection<RecipientResponseSummaryDto> l1List = l2List.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getCPan(),
						getCollectorForLevel("L1", Function.identity())))
				.values();
		
		//Adding all levels of data response into single Arraylist
		List<RecipientResponseSummaryDto> list = 
				new ImmutableList.Builder<RecipientResponseSummaryDto>()
				.addAll(l1List).addAll(l2List).addAll(l3List).build();
		
		return list.stream().sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());

	}

	/*This method accepts level as a parameter and returns collector for 
	 * that particular level.
	 */
	private <T> Collector<T, ?,
			RecipientResponseSummaryDto> getCollectorForLevel(
			String level,
			Function<? super T,
					? extends RecipientResponseSummaryDto> converter) {
		return Collectors.reducing(
				new RecipientResponseSummaryDto(level),
				converter,
				(o1, o2) -> addtoResponse(o1, o2));
	}

	private RecipientResponseSummaryDto addtoResponse(
			RecipientResponseSummaryDto o1, RecipientResponseSummaryDto o2) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin addtoResponse in service "
					+ ".create RecipientResponseSummaryDto obj";
			LOGGER.debug(msg);
		}
		
		String cgstin = "L1".equals(o1.getLevel()) ? null : o2.getCgstin();
		String tableType = "L3".equals(o1.getLevel()) ? o2.getTableType()
				: null;
		String docType = "L3".equals(o1.getLevel()) ? o2.getDocType() : null;
		return new RecipientResponseSummaryDto(o1.getLevel(), o2.getCPan(),
				o2.getCName(), tableType, cgstin, docType,
				o2.getNotSavedCount() + o1.getNotSavedCount(),
				o2.getSavedCount() + o1.getSavedCount(),
				o2.getAcceptedCount() + o1.getAcceptedCount(),
				o2.getRejectedCount() + o1.getRejectedCount(),
				o2.getPendingCount() + o1.getPendingCount(),
				o2.getNoActionCount() + o1.getNoActionCount());
		
	}

	private RecipientResponseSummaryDto convertDto(
			RecipientDBResponseDto rrdb) {
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "Begin convertDto in service "
					+ ".converting the db response obj to "
					+ "RecipientResponseSummaryDto obj";
			LOGGER.debug(msg);
		}

		RecipientResponseSummaryDto rrdDto = new RecipientResponseSummaryDto();
		rrdDto.setCgstin(rrdb.getCgstin());
		rrdDto.setTableType(rrdb.getTableSection());
		rrdDto.setCPan(rrdb.getCPan());
		rrdDto.setDocType(rrdb.getDocType());
		if ("S".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setSavedCount(rrdb.getCount());
		} else if ("NS".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setNotSavedCount(rrdb.getCount());

		} else if ("A".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setAcceptedCount(rrdb.getCount());

		} else if ("R".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setRejectedCount(rrdb.getCount());

		} else if ("P".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setPendingCount(rrdb.getCount());

		} else if ("N".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setNoActionCount(rrdb.getCount());
		}
		
		if (LOGGER.isDebugEnabled()) {
			String msg = "End convertDto in service. Converted to  "
					+ "RecipientResponseSummaryDto obj";
					
			LOGGER.debug(msg);
		}
		
		return rrdDto;
	}
	
	private RecipientSummaryRequestDto 
	setDataSecurityAttributes(RecipientSummaryRequestDto
			requestRecipientSummary ,Map<String, List<String>>
	dataSecurityAttrMap){
		if (CollectionUtils.isEmpty(requestRecipientSummary.getGstins())) {
		requestRecipientSummary
		.setGstins(dataSecurityAttrMap.get(OnboardingConstant.GSTIN));
		}


		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getProfitCentres())) {
			requestRecipientSummary
			.setProfitCentres(dataSecurityAttrMap.get(OnboardingConstant.PC));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getPlants())) {
			requestRecipientSummary
			.setPlants(dataSecurityAttrMap.get(OnboardingConstant.PLANT));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getDivisions())) {
			requestRecipientSummary
			.setDivisions(dataSecurityAttrMap.get(OnboardingConstant.DIVISION));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getLocations())) {
			requestRecipientSummary
			.setLocations(dataSecurityAttrMap.get(OnboardingConstant.LOCATION));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary.getSalesOrgs())) {
			requestRecipientSummary
			.setSalesOrgs(dataSecurityAttrMap.get(OnboardingConstant.SO));
		}
		
		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getPurchaseOrgs())) {
			requestRecipientSummary
			.setPurchaseOrgs(dataSecurityAttrMap.get(OnboardingConstant.PO));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getDistributionChannels())) {
			requestRecipientSummary
			.setDistributionChannels(dataSecurityAttrMap.get(OnboardingConstant.DC));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess1())) {
			requestRecipientSummary
			.setUserAccess1(dataSecurityAttrMap.get(OnboardingConstant.UD1));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess2())) {
			requestRecipientSummary
			.setUserAccess2(dataSecurityAttrMap.get(OnboardingConstant.UD2));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess3())) {
			requestRecipientSummary
			.setUserAccess3(dataSecurityAttrMap.get(OnboardingConstant.UD3));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess4())) {
			requestRecipientSummary
			.setUserAccess4(dataSecurityAttrMap.get(OnboardingConstant.UD4));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess5())) {
			requestRecipientSummary
			.setUserAccess5(dataSecurityAttrMap.get(OnboardingConstant.UD5));
		}

		if (CollectionUtils.isEmpty(requestRecipientSummary
				.getUserAccess6())) {
			requestRecipientSummary
			.setUserAccess6(dataSecurityAttrMap.get(OnboardingConstant.UD6));
		}
		return requestRecipientSummary;
		
	}
	
	//refactoring required put this method in generic class it is used in 
		//many services
		public static String queryCondition(RecipientSummaryRequestDto req) {

			if (LOGGER.isDebugEnabled()) {
				String msg = " Begin Anx1RecipientSummaryValidation"
						+ ".queryCondition ";
				LOGGER.debug(msg);
			}

			StringBuilder condition1 = new StringBuilder();

			if (!(req.getStartDocDate() == null && req.getEndDocDate() == null)) {
				condition1.append(" AND \"DOC_DATE\" BETWEEN :startDocDate AND "
						+ ":docEndDate");
			}
			if (!CollectionUtils.isEmpty(req.getProfitCentres())) {
				condition1.append(" AND \"PROFIT_CENTRE\" in (:profitCenters)");
			}

			if (!CollectionUtils.isEmpty(req.getPlants())) {
				condition1.append(" AND \"PLANT_CODE\" in (:plants)");
			}

			if (!CollectionUtils.isEmpty(req.getDivisions())) {
				condition1.append(" AND \"DIVISION\" in (:divisions)");
			}

			if (!CollectionUtils.isEmpty(req.getLocations())) {
				condition1.append(" AND \"LOCATION\" in (:locations)");
			}

			if (!CollectionUtils.isEmpty(req.getSalesOrgs())) {
				condition1.append(
						" AND \"SALES_ORGANIZATION\" in " + "(:salesOrganization)");
			}

			if (!CollectionUtils.isEmpty(req.getDistributionChannels())) {
				condition1.append(" AND \"DISTRIBUTION_CHANNEL\" in "
						+ "(:distributionChannels)");

			}
			if (!CollectionUtils.isEmpty(req.getPurchaseOrgs())) {
				condition1.append(" AND \"PURCHASE_ORGANIZATION\" in "
						+ "(:purchaseOrgs)");

			}

			if (!CollectionUtils.isEmpty(req.getUserAccess1())) {
				condition1.append(" AND \"USERACCESS1\" in " + "(:userAccess1)");
			}

			if (!CollectionUtils.isEmpty(req.getUserAccess2())) {
				condition1.append(" AND \"USERACCESS2\" in " + "(:userAccess2)");
			}

			if (!CollectionUtils.isEmpty(req.getUserAccess3())) {
				condition1.append(" AND \"USERACCESS3\" in " + "(:userAccess3)");
			}

			if (!CollectionUtils.isEmpty(req.getUserAccess4())) {
				condition1.append(" AND \"USERACCESS4\" in " + "(:userAccess4)");
			}

			if (!CollectionUtils.isEmpty(req.getUserAccess5())) {
				condition1.append(" AND \"USERACCESS5\" in " + "(:userAccess5)");
			}

			if (!CollectionUtils.isEmpty(req.getUserAccess6())) {
				condition1.append(" AND \"USERACCESS6\" in " + "(:userAccess6)");
			}

			return condition1.toString();
		}
}
