package com.ey.advisory.app.service.ims.supplier;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import com.ey.advisory.app.data.entities.client.GetAnx1BatchEntity;
import com.ey.advisory.common.EYDateUtil;
import com.google.common.base.Strings;

import lombok.extern.slf4j.Slf4j;

@Service("SupplierImsApiCallServiceImpl")
@Slf4j
public class SupplierImsApiCallServiceImpl implements SupplierImsApiCallService {
	
	private static final String SUCCESS = "SUCCESS";
	private static final String FAILED = "FAILED";
	private static final String INITIATED = "INITIATED";
	private static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";
	private static final String INPROGRESS = "INPROGRESS";
	
	@Override
	public List<SupplierImsGstinDetailsDto> getTaxPeriodDetailsIms(
			List<GetAnx1BatchEntity> getBatchEntityDetails) {
		
		Map<String, LocalDateTime> maxCreatedOnByTaxPeriod = getBatchEntityDetails.stream()
			    .collect(Collectors.toMap(
			        e -> (String)(e.getSgstin() + "|" + e.getTaxPeriod()),
			        GetAnx1BatchEntity::getCreatedOn,
			        BinaryOperator.maxBy(Comparator.naturalOrder())
			    ));

	
		Collector<GetAnx1BatchEntity, ?, SupplierImsTaxPeriodDetailsDto> collector3 = Collectors
				.reducing(new SupplierImsTaxPeriodDetailsDto(), cpi -> convertDto(cpi,maxCreatedOnByTaxPeriod),
						(cpt1, cpt2) -> merge(cpt1, cpt2));

		Collector<GetAnx1BatchEntity, ?, Map<String, SupplierImsTaxPeriodDetailsDto>> collector2 = Collectors
				.groupingBy(obj -> obj.getTaxPeriod(), collector3);

		Collector<GetAnx1BatchEntity, ?, Map<String, Map<String, SupplierImsTaxPeriodDetailsDto>>> collector1 = Collectors
				.groupingBy(o -> o.getSgstin(), collector2);

		Map<String, Map<String, SupplierImsTaxPeriodDetailsDto>> map1 = getBatchEntityDetails
				.stream().collect(collector1);

		LOGGER.info("Group by Gstin and Tax period has been completed");

		List<SupplierImsGstinDetailsDto> getTaxperiodDetails = map1.entrySet().stream()
				.map(es -> createGstinDetails(es.getKey(), es.getValue()))
				.collect(Collectors.toList());
		

		
		return getTaxperiodDetails;
	
	}
//GstnGetStatusEntity
	private SupplierImsTaxPeriodDetailsDto convertDto(GetAnx1BatchEntity cpi, 
			Map<String, LocalDateTime> maxCreatedOnByTaxPeriod) {
		SupplierImsTaxPeriodDetailsDto txpd = new SupplierImsTaxPeriodDetailsDto();
		txpd.setTaxPeriod(cpi.getTaxPeriod());
		
		String key = cpi.getSgstin() + "|" + cpi.getTaxPeriod();
	    LocalDateTime maxCreatedOn = maxCreatedOnByTaxPeriod.get(key);

	    if (maxCreatedOn != null) {
	        txpd.setInitiatedOn(EYDateUtil.toLocalDateTimeFromUTC(maxCreatedOn));
	    }
		
		//txpd.setInitiatedOn(cpi.getCreatedOn());
	    if("GSTR1".equalsIgnoreCase(cpi.getImsReturnType())){

		if (SUCCESS.equals(cpi.getStatus())) {
			txpd.setSuccessSections("1" + "-" + cpi.getType());
		}
		if (SUCCESS_WITH_NO_DATA.equals(cpi.getStatus())) {
			txpd.setSuccessWithNoDataSections("1" + "-" + cpi.getType());
		}
		if (FAILED.equals(cpi.getStatus())) {
			txpd.setFailedSections("1" + "-" + cpi.getType());
		}
		if (INITIATED.equals(cpi.getStatus())
				|| INPROGRESS.equalsIgnoreCase(cpi.getStatus())) {
			txpd.setInProgressSections(Strings.isNullOrEmpty(cpi.getType())
					? "NA" : ("1" + "-" + cpi.getType().toUpperCase()));
		}
	    }else{


			if (SUCCESS.equals(cpi.getStatus())) {
				txpd.setSuccessSections("1A" + "-" + cpi.getType());
			}
			if (SUCCESS_WITH_NO_DATA.equals(cpi.getStatus())) {
				txpd.setSuccessWithNoDataSections("1A" + "-" + cpi.getType());
			}
			if (FAILED.equals(cpi.getStatus())) {
				txpd.setFailedSections("1A" + "-" + cpi.getType());
			}
			if (INITIATED.equals(cpi.getStatus())
					|| INPROGRESS.equalsIgnoreCase(cpi.getStatus())) {
				txpd.setInProgressSections(Strings.isNullOrEmpty(cpi.getType())
						? "NA" : ("1A" + "-" + cpi.getType().toUpperCase()));
			}
		    
	    }
		return txpd;
	}

	private SupplierImsTaxPeriodDetailsDto merge(SupplierImsTaxPeriodDetailsDto txpd1,
			SupplierImsTaxPeriodDetailsDto txpd2) {
		String succSection = "";
		String failedSection = "";
		String inprogressSection = "";
		String successWithNoDataSection = "";
		LocalDateTime initiatedOn = null;
		if (txpd1.getSuccessSections() != null
				&& !txpd1.getSuccessSections().isEmpty()) {
			succSection = txpd1.getSuccessSections();
			if (txpd2.getSuccessSections() != null
					&& !txpd2.getSuccessSections().isEmpty()) {
				succSection = succSection + " " + txpd2.getSuccessSections();
			}
		} else {
			succSection = txpd2.getSuccessSections();
		}
		if (txpd1.getFailedSections() != null
				&& !txpd1.getFailedSections().isEmpty()) {
			failedSection = txpd1.getFailedSections();
			if (txpd2.getFailedSections() != null
					&& !txpd2.getFailedSections().isEmpty()) {
				failedSection = failedSection + " " + txpd2.getFailedSections();
			}
		} else {
			failedSection = txpd2.getFailedSections();
		}

		if (txpd1.getInProgressSections() != null
				&& !txpd1.getInProgressSections().isEmpty()) {
			inprogressSection = txpd1.getInProgressSections();
			if (txpd2.getInProgressSections() != null
					&& !txpd2.getInProgressSections().isEmpty()) {
				inprogressSection = inprogressSection + " "
						+ txpd2.getInProgressSections();
			}
		} else {
			inprogressSection = txpd2.getInProgressSections();
		}

		if (txpd1.getSuccessWithNoDataSections() != null
				&& !txpd1.getSuccessWithNoDataSections().isEmpty()) {
			successWithNoDataSection = txpd1.getSuccessWithNoDataSections();
			if (txpd2.getSuccessWithNoDataSections() != null
					&& !txpd2.getSuccessWithNoDataSections().isEmpty()) {
				successWithNoDataSection = successWithNoDataSection + " "
						+ txpd2.getSuccessWithNoDataSections();
			}
		} else {
			successWithNoDataSection = txpd2.getSuccessWithNoDataSections();
		}

		if (txpd2.getInitiatedOn() != null) {
			initiatedOn = EYDateUtil
					.toISTDateTimeFromUTC(txpd2.getInitiatedOn());
		} else if (txpd1.getInitiatedOn() != null) {
			initiatedOn = EYDateUtil
					.toISTDateTimeFromUTC(txpd1.getInitiatedOn());
		}

		return new SupplierImsTaxPeriodDetailsDto(txpd2.getTaxPeriod(), initiatedOn,
				succSection, failedSection, inprogressSection,
				successWithNoDataSection);
	}

	private SupplierImsGstinDetailsDto createGstinDetails(String sGstin,
			Map<String, SupplierImsTaxPeriodDetailsDto> map) {

		SupplierImsGstinDetailsDto apiGstindetails = new SupplierImsGstinDetailsDto();
		apiGstindetails.setGstin(sGstin);

		List<SupplierImsTaxPeriodDetailsDto> taxPeriodDetailsDto = map.entrySet().stream()
				.map(es -> es.getValue()).collect(Collectors.toList());
		taxPeriodDetailsDto.forEach(x -> {
			boolean isSuccess = !Strings.isNullOrEmpty(x.getSuccessSections());
			boolean isFailed = !Strings.isNullOrEmpty(x.getFailedSections());
			boolean isInProgress = !Strings
					.isNullOrEmpty(x.getInProgressSections());
			boolean isSuccessWithNoData = !Strings
					.isNullOrEmpty(x.getSuccessWithNoDataSections());

			if (isInProgress) {
				x.setApiStatus("INPROGRESS");
			} else if (isSuccess && isFailed && !isInProgress) {
				x.setApiStatus("PARTIAL_SUCCESS");
			} else if (isFailed || (isFailed && isSuccessWithNoData)) {
				x.setApiStatus("FAILED");
			} else if (isSuccess || (isSuccess && isSuccessWithNoData)) {
				x.setApiStatus("SUCCESS");
			} else {
				x.setApiStatus("SUCCESS_WITH_NO_DATA");
			}
		});

		apiGstindetails.setTaxPeriodDetails(taxPeriodDetailsDto);
		return apiGstindetails;
	}

	private String getConverterTime(LocalDateTime extractTime) {

		Date date = EYDateUtil.toDate(extractTime);
		String strDateFormat = "hh:mm:ss a";
		DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
		String formattedDate = dateFormat.format(date);
		return formattedDate;
	}
	/*// Step 1: Calculate max(createdOn) for each taxPeriod
			Map<String, LocalDateTime> maxCreatedOnByTaxPeriod = getBatchEntityDetails.stream()
			    .collect(Collectors.groupingBy(
			        GetAnx1BatchEntity::getTaxPeriod,
			        Collectors.mapping(GetAnx1BatchEntity::getCreatedOn, Collectors.maxBy(Comparator.naturalOrder()))
			    ))
			    .entrySet().stream()
			    .collect(Collectors.toMap(
			        Map.Entry::getKey,
			        entry -> entry.getValue().orElse(null)  // Handle empty Optional if no values
			    ));*/
	
	/*public static void main(String[] args) {
		
		String returnType = "GSTR1";
		
		 if("GSTR1".equalsIgnoreCase(returnType)){

				if (SUCCESS.equals(returnType)) {
					("1" + "-" + returnType);
				}
				if (SUCCESS_WITH_NO_DATA.equals(cpi.getStatus())) {
					txpd.setSuccessWithNoDataSections("1" + "-" + cpi.getType());
				}
				if (FAILED.equals(cpi.getStatus())) {
					txpd.setFailedSections("1" + "-" + cpi.getType());
				}
				if (INITIATED.equals(cpi.getStatus())
						|| INPROGRESS.equalsIgnoreCase(cpi.getStatus())) {
					txpd.setInProgressSections(Strings.isNullOrEmpty(cpi.getType())
							? "NA" : ("1" + "-" + cpi.getType().toUpperCase()));
				}
			    }else{


					if (SUCCESS.equals(cpi.getStatus())) {
						txpd.setSuccessSections("1A" + "-" + cpi.getType());
					}
					if (SUCCESS_WITH_NO_DATA.equals(cpi.getStatus())) {
						txpd.setSuccessWithNoDataSections("1A" + "-" + cpi.getType());
					}
					if (FAILED.equals(cpi.getStatus())) {
						txpd.setFailedSections("1A" + "-" + cpi.getType());
					}
					if (INITIATED.equals(cpi.getStatus())
							|| INPROGRESS.equalsIgnoreCase(cpi.getStatus())) {
						txpd.setInProgressSections(Strings.isNullOrEmpty(cpi.getType())
								? "NA" : ("1A" + "-" + cpi.getType().toUpperCase()));
					}
				    
			    }
	}*/
}


