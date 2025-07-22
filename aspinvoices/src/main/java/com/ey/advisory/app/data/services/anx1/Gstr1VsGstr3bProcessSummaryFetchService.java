package com.ey.advisory.app.data.services.anx1;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.beust.jcommander.internal.Maps;
import com.ey.advisory.app.data.daos.client.Gstr1VsGstr3bProcessSummaryFetchDaoImpl;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessStatusRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessSummaryFinalRespDto;
import com.ey.advisory.app.docs.dto.Gstr1VsGstr3bProcessSummaryRespDto;
import com.ey.advisory.app.services.daos.get2a.Gstr1vsGstr3bGetGstr1StatusFetchDaoImpl;
import com.ey.advisory.app.services.daos.get2a.Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl;
import com.ey.advisory.common.ProcessedRecordsCommonSecParam;
import com.ey.advisory.core.dto.Gstr1VsGstr3bProcessSummaryReqDto;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

@Component("Gstr1VsGstr3bProcessSummaryFetchService")
public class Gstr1VsGstr3bProcessSummaryFetchService {

    private static final String NOT_INITIATED = "NOT_INITIATED";
    private static final String SUCCESS_WITH_NO_DATA = "SUCCESS_WITH_NO_DATA";
    private static final String INPROGRESS = "INPROGRESS";
    private static final String SUCCESS = "SUCCESS";
    private static final String FAILED = "FAILED";
    private static final String INITIATED = "INITIATED";
    private static final String PARTIALLY_SUCCESS = "PARTIALLY_SUCCESS";

    @Autowired
    @Qualifier("ProcessedRecordsCommonSecParam")
    private ProcessedRecordsCommonSecParam processedRecordsCommonSecParam;

    @Autowired
    @Qualifier("Gstr1VsGstr3bProcessSummaryFetchDaoImpl")
    private Gstr1VsGstr3bProcessSummaryFetchDaoImpl gstr1VsGstr3bProcessSummaryFetchDaoImpl;

    @Autowired
    @Qualifier("Gstr1vsGstr3bGetGstr1StatusFetchDaoImpl")
    private Gstr1vsGstr3bGetGstr1StatusFetchDaoImpl getGstr1vs3bDetailStatusFetchDao;

    @Autowired
    @Qualifier("Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl")
    private Gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl;

    public List<Gstr1VsGstr3bProcessSummaryFinalRespDto> response(
            Gstr1VsGstr3bProcessSummaryReqDto req) throws Exception {
        List<Gstr1VsGstr3bProcessSummaryFinalRespDto> summaryResponses = Lists
                .newArrayList();
        Gstr1VsGstr3bProcessSummaryReqDto reqDto = processedRecordsCommonSecParam
                .setGstr1VsGstr3bDataSecuritySearchParams(req);

        List<Gstr1VsGstr3bProcessStatusRespDto> itemsList = getGstr1vs3bDetailStatusFetchDao
                .getDataUploadedStatusDetails(reqDto);
        Map<String, String> statusMap = calculateStatusByGstin(itemsList);

        
        List<Gstr1VsGstr3bProcessStatusRespDto> gstr1aStatusList = getGstr1vs3bDetailStatusFetchDao
                .getGstr1aStatusDetails(reqDto);
        Map<String, String> gstr1aStatusMap = calculateStatusByGstin(gstr1aStatusList);
        
        List<Gstr1VsGstr3bProcessStatusRespDto> gstr3bstatusList = gstr2avsGstr3bGetGstr3bStatusFetchDaoImpl
                .getDataUploadedStatusDetails(reqDto);
        Map<String, String> gstr3bStatusMap = calculateGstr3bStatusByGstin(
                gstr3bstatusList);

        List<Gstr1VsGstr3bProcessSummaryRespDto> processedRespDtos = gstr1VsGstr3bProcessSummaryFetchDaoImpl
                .fetchGstr1VsGstr3bRecords(reqDto, statusMap, gstr3bStatusMap,gstr1aStatusMap);
        if (CollectionUtils.isNotEmpty(processedRespDtos)) {

            Map<String, List<Gstr1VsGstr3bProcessSummaryRespDto>> gstinMap = processedRespDtos
                    .stream().collect(Collectors.groupingBy(
                            Gstr1VsGstr3bProcessSummaryRespDto::getGstin));
            summaryResponses.addAll(
                    addGstinsToList(gstinMap, statusMap, gstr3bStatusMap,gstr1aStatusMap));

        }

        return summaryResponses;
    }

    private Map<String, String> calculateStatusByGstin(
            List<Gstr1VsGstr3bProcessStatusRespDto> itemsList) {
        Map<String, String> gstinStatusMap = Maps.newHashMap();

        Map<String, List<Gstr1VsGstr3bProcessStatusRespDto>> statusMap = itemsList
                .stream().collect(Collectors.groupingBy(
                        Gstr1VsGstr3bProcessStatusRespDto::getGstin));

        Map<String, List<String>> statusesMap = Maps.newHashMap();
        Map<String, List<String>> timeStampMap = Maps.newHashMap();
        statusMap.keySet().forEach(gstin -> {
            List<Gstr1VsGstr3bProcessStatusRespDto> list = statusMap.get(gstin);
            statusesMap.put(gstin,
                    list.stream()
                            .map(Gstr1VsGstr3bProcessStatusRespDto::getStatus)
                            .collect(Collectors.toList()));
            timeStampMap.put(gstin,
                    list.stream()
                            .map(Gstr1VsGstr3bProcessStatusRespDto::getLastUpdatedTime)
                            .collect(Collectors.toList()));
        });

        statusesMap.keySet().forEach(gstin -> {
            String finalStatus = NOT_INITIATED;
            List<String> statusList = statusesMap.get(gstin);
            Set<String> uniqueStatuses = Sets.newHashSet(statusList);
            if (CollectionUtils.isNotEmpty(uniqueStatuses)) {
                if (uniqueStatuses.size() > 0) {
                	if (uniqueStatuses.contains(INITIATED)) {
                        finalStatus = INITIATED;
                    } else if (uniqueStatuses.contains(INPROGRESS)) {
                        finalStatus = INPROGRESS;
                    } else if (uniqueStatuses.contains(FAILED)
                            && (uniqueStatuses.contains(SUCCESS)
                                    || uniqueStatuses
                                            .contains(SUCCESS_WITH_NO_DATA))) {
                        finalStatus = PARTIALLY_SUCCESS;
                    } else if (uniqueStatuses.contains(SUCCESS)
                            || uniqueStatuses.contains(SUCCESS_WITH_NO_DATA)) {
                        finalStatus = SUCCESS;
                    } else if (uniqueStatuses.contains(FAILED)) {
                        finalStatus = FAILED;
                    }
                } /*else {
                    finalStatus = PARTIALLY_SUCCESS;
                }*/
            }
            gstinStatusMap.put(gstin, finalStatus);
        });

        return updateTimeStampOnExisitngGstin(timeStampMap, gstinStatusMap);
    }

    private Map<String, String> updateTimeStampOnExisitngGstin(
            Map<String, List<String>> statusMap,
            Map<String, String> gstinStatusMap) {
        Map<String, String> finalMap = Maps.newHashMap();
        gstinStatusMap.keySet().forEach(gstin -> {
            List<String> timestampList = statusMap.get(gstin);
            
            finalMap.put(gstin, gstinStatusMap.get(gstin) + "__"
                    + getTimestamp(timestampList));
            
        });

        return finalMap;
    }

    private String getTimestamp(List<String> timestampList) {
    	  SimpleDateFormat out = new SimpleDateFormat("yyyy-MM-dd : HH:mm:ss");
        String returnStamp = null;
        try {
            if (CollectionUtils.isNotEmpty(timestampList)) {
            	returnStamp = timestampList.get(0);
    			
                Date startValue = out.parse(timestampList.get(0));
                
                for (int i = 1; i < timestampList.size(); i++) {
                    Date nextValue = out.parse(timestampList.get(i));
                    if (nextValue.after(startValue)) {
                        //startValue = nextValue;
                        returnStamp=timestampList.get(i);
                    }
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return returnStamp;
    }

    private List<Gstr1VsGstr3bProcessSummaryFinalRespDto> addGstinsToList(
            Map<String, List<Gstr1VsGstr3bProcessSummaryRespDto>> gstinMap,
            Map<String, String> statusMap,
            Map<String, String> gstr3bStatusMap,Map<String,String> gstr1aStatusMap ) {
        List<Gstr1VsGstr3bProcessSummaryFinalRespDto> respDtos = Lists
                .newArrayList();
        gstinMap.keySet().forEach(gstinKey -> {
            List<Gstr1VsGstr3bProcessSummaryRespDto> dtos = gstinMap
                    .get(gstinKey);
            Gstr1VsGstr3bProcessSummaryFinalRespDto finalRespDto = new Gstr1VsGstr3bProcessSummaryFinalRespDto();

            String gstin = "";
            String authToken = "";
            String state = "";
            String reconStatus = "";
            String reconDateTime = "";
            BigDecimal gstr3bTaxableValue = BigDecimal.ZERO;
            BigDecimal gstr3bTotalTax = BigDecimal.ZERO;
            BigDecimal gstr1TaxableValue = BigDecimal.ZERO;
            BigDecimal gstr1TotalTax = BigDecimal.ZERO;
            BigDecimal diffTaxableValue = BigDecimal.ZERO;
            BigDecimal diffTotalTax = BigDecimal.ZERO;

            for (Gstr1VsGstr3bProcessSummaryRespDto dto : dtos) {
                gstin = dto.getGstin();
                authToken = dto.getAuthToken();
                state = dto.getState();
                reconStatus = dto.getReconStatus();
                reconDateTime = dto.getReconDateTime();
                gstr3bTaxableValue = gstr3bTaxableValue
                        .add(dto.getGstr3bTaxableValue());
                gstr3bTotalTax = gstr3bTotalTax.add(dto.getGstr3bTotalTax());
                gstr1TaxableValue = gstr1TaxableValue
                        .add(dto.getGstr1TaxableValue());
                gstr1TotalTax = gstr1TotalTax.add(dto.getGstr1TotalTax());
                diffTaxableValue = dto.getDiffTaxableValue();
                diffTotalTax = dto.getDiffTotalTax();
            }
           /* diffTaxableValue = gstr3bTaxableValue.subtract(gstr1TaxableValue);
            diffTotalTax = gstr3bTotalTax.subtract(gstr1TotalTax);*/
            finalRespDto.setGstin(gstin);
          //gstr1a
            if (gstr1aStatusMap.containsKey(gstin)) {
                String value[] = gstr1aStatusMap.get(gstin).split("__");
                if (value[0] != null && !value[0].contains("null")) {
                    finalRespDto.setGstr1aStatus(value[0]);
                }
                String s = value[1];
                if (value[1] != null && !s.contains("null")) {
                    finalRespDto.setGstr1aTime(value[1]);
                }
            } else {
                finalRespDto.setGstr1aStatus(NOT_INITIATED);
            }
            //gstr1a
            if (statusMap.containsKey(gstin)) {
                String value[] = statusMap.get(gstin).split("__");
                if (value[0] != null && !value[0].contains("null")) {
                    finalRespDto.setGstr1Status(value[0]);
                }
                String s = value[1];
                if (value[1] != null && !s.contains("null")) {
                    finalRespDto.setGstr1Time(value[1]);
                }
            } else {
                finalRespDto.setGstr1Status(NOT_INITIATED);
            }
            if (gstr3bStatusMap.containsKey(gstin)) {
                String value[] = gstr3bStatusMap.get(gstin).split("__");
                if (value[0] != null && !value[0].contains("null")) {
                    finalRespDto.setGstr3bStatus(value[0]);
                }
                String s = value[1];
                if (value[1] != null && !s.contains("null")) {
                    finalRespDto.setGstr3bTime(value[1]);
                }
            } else {
                finalRespDto.setGstr3bStatus(NOT_INITIATED);
            }
            finalRespDto.setAuthToken(authToken);
            finalRespDto.setState(state);
            finalRespDto.setReconStatus(reconStatus);
            finalRespDto.setReconDateTime(reconDateTime);
            finalRespDto.setGstr1TaxableValue(gstr1TaxableValue);
            finalRespDto.setGstr1TotalTax(gstr1TotalTax);
            finalRespDto.setGstr3bTaxableValue(gstr3bTaxableValue);
            finalRespDto.setGstr3bTotalTax(gstr3bTotalTax);
            finalRespDto.setDiffTaxableValue(diffTaxableValue);
            finalRespDto.setDiffTotalTax(diffTotalTax);
            respDtos.add(finalRespDto);
        });
        return respDtos;
    }

    private Map<String, String> calculateGstr3bStatusByGstin(
            List<Gstr1VsGstr3bProcessStatusRespDto> gstr3bstatusList) {
        Map<String, String> gstinStatusMap = Maps.newHashMap();

        Map<String, List<Gstr1VsGstr3bProcessStatusRespDto>> statusMap = gstr3bstatusList
                .stream().collect(Collectors.groupingBy(
                        Gstr1VsGstr3bProcessStatusRespDto::getGstin));

        Map<String, List<String>> statusesMap = Maps.newHashMap();
        Map<String, List<String>> timeStampMap = Maps.newHashMap();
        statusMap.keySet().forEach(gstin -> {
            List<Gstr1VsGstr3bProcessStatusRespDto> list = statusMap.get(gstin);
            statusesMap.put(gstin,
                    list.stream()
                            .map(Gstr1VsGstr3bProcessStatusRespDto::getStatus)
                            .collect(Collectors.toList()));
            timeStampMap.put(gstin,
                    list.stream()
                            .map(Gstr1VsGstr3bProcessStatusRespDto::getLastUpdatedTime)
                            .collect(Collectors.toList()));
        });

        statusesMap.keySet().forEach(gstin -> {
            String finalStatus = NOT_INITIATED;
            List<String> statusList = statusesMap.get(gstin);
            Set<String> uniqueStatuses = Sets.newHashSet(statusList);
            if (CollectionUtils.isNotEmpty(uniqueStatuses)) {
            	finalStatus=deriveStatus(uniqueStatuses);
            }
            gstinStatusMap.put(gstin, finalStatus);
        });

        return updateTimeStampOnExisitngGstin(timeStampMap, gstinStatusMap);
    }

	private static String deriveStatus(Set<String> uniqueStatuses) {

		if (uniqueStatuses.size() > 0) {
			//if (uniqueStatuses.size() > 1) {
				//Old logic 
				/*if (uniqueStatuses.contains(FAILED) && uniqueStatuses.contains(SUCCESS)) {
					return PARTIALLY_SUCCESS;
				}
				if (uniqueStatuses.contains(SUCCESS))
					return SUCCESS;

				if (uniqueStatuses.contains(FAILED))
					return FAILED;*/
				// new logic to correct the status 
				if (uniqueStatuses.contains(INITIATED)) {
					return INITIATED;
				} else if (uniqueStatuses.contains(INPROGRESS)) {
					return INPROGRESS;
				} else if ((uniqueStatuses.contains(SUCCESS) || 
						uniqueStatuses.contains(SUCCESS_WITH_NO_DATA))
						&& uniqueStatuses.contains(FAILED)) {
					return PARTIALLY_SUCCESS;
				} else if (uniqueStatuses.contains(SUCCESS) || 
						uniqueStatuses.contains(SUCCESS_WITH_NO_DATA)) {
					return SUCCESS;
				} else if (uniqueStatuses.contains(FAILED)) {
					return FAILED;
				}
		//	}

		}

		return uniqueStatuses.stream().findFirst().get();

	}
}