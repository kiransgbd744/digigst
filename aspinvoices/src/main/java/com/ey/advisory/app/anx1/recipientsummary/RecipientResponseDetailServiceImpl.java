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
 * @author vishal.verma
 *
 */

@Slf4j
@Component("RecipientResponseDetailServiceImpl")
public class RecipientResponseDetailServiceImpl
		implements RecipientResponseDetailService {

	@Autowired
	@Qualifier("RecipientResponseSummaryDetailDaoImpl")
	RecipientResponseSummaryDetailDao detailsDao;
	
	@Autowired
	@Qualifier("RecipientServiceImpl")
	RecipientService recipientService;

	@Override
	public List<RecipientResponseDetailsDto> getRecipientResponseDetail
	(String reqData, RecipientSummaryRequestDto req) {
		
			Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();
			Map<String, String> outwardSecurityAttributeMap 
			= DataSecurityAttributeUtil
					.getOutwardSecurityAttributeMap();
			dataSecurityAttrMap = DataSecurityAttributeUtil
					.dataSecurityAttrMapForQuery(
							Arrays.asList(req.getEntityId()),
							outwardSecurityAttributeMap);
			setDataSecurityAttributes(req, dataSecurityAttrMap);
			reqData = queryCondition(req);
		
		if(CollectionUtils.isEmpty(req.getGstins()))
			throw new AppException("User Does not have any gstin");
		
		if(LOGGER.isDebugEnabled()){
			String msg = "Begin RecipientResponseDetailServiceImpl"
					+ ".getRecipientResponseDetail, calling"
					+ " RecipientResponseSummaryDetailDao"
					+ "to get List<RecipientResponseDetailsDto>";
			LOGGER.debug(msg);
		}

		/*
		 * Calling DAO and getting the response as List of
		 * RecipientDBResponseDto
		 */
		List<RecipientDBResponseDto> rdbList = detailsDao
				.getAllResponseSummaryDetails(reqData, req);
		
		if(LOGGER.isDebugEnabled()){
			String msg = "Begin RecipientResponseSummaryDetailDao"
					+ ".getAllResponseSummaryDetails, called"
					+ " RecipientResponseSummaryDetailDao"
					+ "to get List<RecipientResponseDetailsDto>";
			LOGGER.debug(msg);
		}

		
		/* Streaming the coming Dao response and groupingBy on the basis of
		 * Cgstin, TableSection and Action then reducing as l3Reducer.
		 * Getting the the value as the List of RecipientResponseDetailsDto.
		 */

		 Collection<RecipientResponseDetailsDto>
		l3List = rdbList.stream().collect(Collectors
				.groupingBy(
					o -> o.getCgstin() + o.getTableSection()  
					+ o.getAction(),
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



		/*groupingBy as Cgstin for forming level 2 data, and 
		 * calling CollectorForLevel() as  L2 collector.  
		 */
		 
		Collection<RecipientResponseDetailsDto> l2List = l3List.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getCgstin(),
						getCollectorForLevel("L2", Function.identity())))
				.values();
		
		/*groupingBy as getCPan for forming level 1 data, and 
		 * calling getCollectorForLevel() as  L1 collector.  
		 */
		Collection<RecipientResponseDetailsDto> l1List = l2List.stream()
				.collect(Collectors.groupingBy(o1 -> o1.getCPan(),
						getCollectorForLevel("L1", Function.identity())))
				.values();
		

		/*Building ImmutableList of RecipientResponseDetailsDto
		 * as final Response by adding all L1, L2 and L3 data into it.
		 */

		List<RecipientResponseDetailsDto> list = 
				new ImmutableList.Builder<RecipientResponseDetailsDto>()
				.addAll(l1List)
				.addAll(l2List)
				.addAll(l3List)
				.build();
		
		if(LOGGER.isDebugEnabled()){
			String msg = "End RecipientResponseDetailServiceImpl"
					+ ".getRecipientResponseDetail, calling"
					+ " RecipientResponseSummaryDetailDao"
					+ "to get List<RecipientResponseDetailsDto>";
			LOGGER.debug(msg);
		}

		return list.stream().sorted(Comparator.comparing(o -> o.getKey()))
				.collect(Collectors.toList());

	}


	/*This method accepts level as a parameter and returns collector for 
	 * that particular level.
	 */
	private <T> Collector<T, ?,
			RecipientResponseDetailsDto> getCollectorForLevel(
			String level,
			Function<? super T,
					? extends RecipientResponseDetailsDto> converter) {
		return Collectors.reducing(
				new RecipientResponseDetailsDto(level),
				converter,
				(o1, o2) -> addtoResponse(o1, o2));
	}


	/*In this method we grouping the RecipientResponseDetailsDto based on level.
	 */
	private RecipientResponseDetailsDto addtoResponse(
			RecipientResponseDetailsDto o1, RecipientResponseDetailsDto o2) {
		String cgstin = "L1".equals(o1.getLevel()) ? null : o2.getCgstin();
		String tableType = "L3".equals(o1.getLevel()) ? o2.getTableType()
				: null;
		
		if ("CR".equalsIgnoreCase(o2.getDocType()) || 
						"RCR".equalsIgnoreCase(o2.getDocType())) {
			
			return new RecipientResponseDetailsDto(o1.getLevel(), o2.getCPan(),
					o2.getCName(),
					cgstin, tableType,
					o1.getNotSavedcount() + o2.getNotSavedcount(),
					o1.getNotSavedTaxableVal().subtract(o2.getNotSavedTaxableVal()),
					o1.getNotSavedTaxAmt().subtract(o2.getNotSavedTaxAmt()),
					o1.getSavedCount() + o2.getSavedCount(),
					o1.getSavedTaxableVal().subtract(o2.getSavedTaxableVal()),
					o1.getSavedTaxAmt().subtract(o2.getSavedTaxAmt()),
					o1.getAcceptedCount() + o2.getAcceptedCount(),
					o1.getAcceptedTaxableVal().subtract(o2.getAcceptedTaxableVal()),
					o1.getAcceptedTaxAmt().subtract(o2.getAcceptedTaxAmt()),
					o1.getRejectedCount() + o2.getRejectedCount(),
					o1.getRejectedTaxableVal().subtract(o2.getRejectedTaxableVal()),
					o1.getRejectedTaxAmt().subtract(o2.getRejectedTaxAmt()),
					o1.getPendingCount() + o2.getPendingCount(),
					o1.getPendingTaxableVal().subtract(o2.getPendingTaxableVal()),
					o1.getPendingTaxAmt().subtract(o2.getPendingTaxAmt()),
					o1.getNoActionCount() + o2.getNoActionCount(),
					o1.getNoActionTaxableVal().subtract(o2.getNoActionTaxableVal()),
					o1.getNoActionTaxAmt().subtract(o2.getNoActionTaxAmt()));
			
			
		}
		
		return new RecipientResponseDetailsDto(o1.getLevel(), o2.getCPan(),
				o2.getCName(),
				cgstin, tableType,
				o2.getNotSavedcount() + o1.getNotSavedcount(),
				o2.getNotSavedTaxableVal().add(o1.getNotSavedTaxableVal()),
				o2.getNotSavedTaxAmt().add(o1.getNotSavedTaxAmt()),
				o2.getSavedCount() + o1.getSavedCount(),
				o2.getSavedTaxableVal().add(o1.getSavedTaxableVal()),
				o2.getSavedTaxAmt().add(o1.getSavedTaxAmt()),
				o2.getAcceptedCount() + o1.getAcceptedCount(),
				o2.getAcceptedTaxableVal().add(o1.getAcceptedTaxableVal()),
				o2.getAcceptedTaxAmt().add(o1.getAcceptedTaxAmt()),
				o2.getRejectedCount() + o1.getRejectedCount(),
				o2.getRejectedTaxableVal().add(o1.getRejectedTaxableVal()),
				o2.getRejectedTaxAmt().add(o1.getRejectedTaxAmt()),
				o2.getPendingCount() + o1.getPendingCount(),
				o2.getPendingTaxableVal().add(o1.getPendingTaxableVal()),
				o2.getPendingTaxAmt().add(o1.getPendingTaxAmt()),
				o2.getNoActionCount() + o1.getNoActionCount(),
				o2.getNoActionTaxableVal().add(o1.getNoActionTaxableVal()),
				o2.getNoActionTaxAmt().add(o1.getNoActionTaxAmt()));

	}

	/*In this method we are converting RecipientResponseDetailsDto to
	 * RecipientDBResponseDto based on Action.
	 */

	private RecipientResponseDetailsDto convertDto(
			RecipientDBResponseDto rrdb) {

		RecipientResponseDetailsDto rrdDto = new RecipientResponseDetailsDto();
		rrdDto.setCgstin(rrdb.getCgstin());
		rrdDto.setTableType(rrdb.getTableSection());
		rrdDto.setCPan(rrdb.getCPan());
		rrdDto.setDocType(rrdb.getDocType());
		if ("S".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setSavedCount(rrdb.getCount());
			rrdDto.setSavedTaxableVal(rrdb.getTaxableVal());
			rrdDto.setSavedTaxAmt(rrdb.getTaxAmt());
		} else if ("NS".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setNotSavedcount(rrdb.getCount());
			rrdDto.setNotSavedTaxableVal(rrdb.getTaxableVal());
			rrdDto.setNotSavedTaxAmt(rrdb.getTaxAmt());

		} else if ("A".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setAcceptedCount(rrdb.getCount());
			rrdDto.setAcceptedTaxableVal(rrdb.getTaxableVal());
			rrdDto.setAcceptedTaxAmt(rrdb.getTaxAmt());

		} else if ("R".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setRejectedCount(rrdb.getCount());
			rrdDto.setRejectedTaxableVal(rrdb.getTaxableVal());
			rrdDto.setRejectedTaxAmt(rrdb.getTaxAmt());

		} else if ("P".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setPendingCount(rrdb.getCount());
			rrdDto.setPendingTaxableVal(rrdb.getTaxableVal());
			rrdDto.setPendingTaxAmt(rrdb.getTaxAmt());

		} else if ("N".equalsIgnoreCase(rrdb.getAction())) {
			rrdDto.setNoActionCount(rrdb.getCount());
			rrdDto.setNoActionTaxableVal(rrdb.getTaxableVal());
			rrdDto.setNoActionTaxAmt(rrdb.getTaxAmt());
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
