/**
 * 
 */
package com.ey.advisory.app.search.reports;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.repositories.client.GSTNDetailRepository;
import com.ey.advisory.app.util.DataSecurityAttributeUtil;
import com.ey.advisory.app.util.OnboardingConstant;
import com.ey.advisory.core.dto.Gstr1ReviwSummReportsReqDto;

/**
 * @author Laxmi.Salukuti
 *
 */

@Component("BasicCommonSecParamRSReports")
public class BasicCommonSecParamRSReports {
	
	private static final String REGULAR = "REGULAR";
	private static final String SEZ = "SEZ";
	private static final String TDS = "TDS";
	private static final String ISD = "ISD";
	private static final String SEZU = "SEZU";
	private static final String SEZD = "SEZD";

	@Autowired
	@Qualifier("GSTNDetailRepository")
	private GSTNDetailRepository gstNDetailRepository;

	public Gstr1ReviwSummReportsReqDto setDataSecuritySearchParams(
			Gstr1ReviwSummReportsReqDto searchParams) {

		List<Long> entityIds = searchParams.getEntityId();
		String dataType = searchParams.getDataType();
		Map<String, List<String>> dataSecurityAttrMap = new HashMap<>();

		Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
				.getOutwardSecurityAttributeMap();
		dataSecurityAttrMap = DataSecurityAttributeUtil
				.dataSecurityAttrMapForQuery(entityIds,
						outwardSecurityAttributeMap);
		if (searchParams.getDataSecAttrs() == null
				|| searchParams.getDataSecAttrs().isEmpty()) {
			searchParams.setDataSecAttrs(dataSecurityAttrMap);
		} else {
			Map<String, List<String>> dataSecReqMap = searchParams
					.getDataSecAttrs();
			List<String> gstinList = dataSecReqMap
					.get(OnboardingConstant.GSTIN);
			List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
			List<String> plantList = dataSecReqMap
					.get(OnboardingConstant.PLANT);
			List<String> divList = dataSecReqMap
					.get(OnboardingConstant.DIVISION);
			List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
			List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
			List<String> locList = dataSecReqMap
					.get(OnboardingConstant.LOCATION);
			List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
			List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
			List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
			List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
			List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
			List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
			if ((gstinList == null || gstinList.isEmpty())
					&& (pcList == null || pcList.isEmpty())
					&& (plantList == null || plantList.isEmpty())
					&& (divList == null || divList.isEmpty())
					&& (soList == null || soList.isEmpty())
					&& (dcList == null || dcList.isEmpty())
					&& (locList == null || locList.isEmpty())
					&& (ud1List == null || ud1List.isEmpty())
					&& (ud2List == null || ud2List.isEmpty())
					&& (ud3List == null || ud3List.isEmpty())
					&& (ud4List == null || ud4List.isEmpty())
					&& (ud5List == null || ud5List.isEmpty())
					&& (ud6List == null || ud6List.isEmpty())) {
				searchParams.setDataSecAttrs(dataSecurityAttrMap);
			} else {
				if ((gstinList != null && !gstinList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.GSTIN,
							gstinList);
				}

				if ((pcList != null && !pcList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PC, pcList);
				}

				if ((plantList != null && !plantList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.PLANT,
							plantList);
				}

				if ((divList != null && !divList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.DIVISION,
							divList);
				}

				if ((soList != null && !soList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.SO, soList);
				}

				if ((dcList != null && !dcList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.DC, dcList);
				}

				if ((locList != null && !locList.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.LOCATION,
							locList);
				}

				if ((ud1List != null && !ud1List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD1, ud1List);
				}

				if ((ud2List != null && !ud2List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD2, ud2List);
				}

				if ((ud3List != null && !ud3List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD3, ud3List);
				}

				if ((ud4List != null && !ud4List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD4, ud4List);
				}

				if ((ud5List != null && !ud5List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD5, ud5List);
				}

				if ((ud6List != null && !ud6List.isEmpty())) {
					dataSecurityAttrMap.put(OnboardingConstant.UD6, ud6List);
				}

				searchParams.setDataSecAttrs(dataSecurityAttrMap);
			}
		}
		return searchParams;
	
	
	}
	
	public Gstr1ReviwSummReportsReqDto setGstr1DataSecuritySearchParams(
			Gstr1ReviwSummReportsReqDto gstr1ProcessedRecordsReqDto) {
        List<Long> entityIds = gstr1ProcessedRecordsReqDto.getEntityId();
        Map<String, String> outwardSecurityAttributeMap = DataSecurityAttributeUtil
                .getOutwardSecurityAttributeMap();
        Map<String, List<String>> dataSecAttrs = DataSecurityAttributeUtil
                .dataSecurityAttrMapForQuery(entityIds,
                        outwardSecurityAttributeMap);

 

        List<String> ttlGstinList = dataSecAttrs.get(OnboardingConstant.GSTIN);
        List<String> regTypeList = Arrays.asList(REGULAR, SEZ, SEZU, SEZD);
        ttlGstinList = gstNDetailRepository
                .filterGstinBasedByRegType(ttlGstinList, regTypeList);
        dataSecAttrs.put(OnboardingConstant.GSTIN, ttlGstinList);

 

        if (gstr1ProcessedRecordsReqDto.getDataSecAttrs() == null
                || gstr1ProcessedRecordsReqDto.getDataSecAttrs().isEmpty()) {
            gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
        } else {
            Map<String, List<String>> dataSecReqMap = gstr1ProcessedRecordsReqDto
                    .getDataSecAttrs();
            List<String> gstinList = dataSecReqMap
                    .get(OnboardingConstant.GSTIN);
            List<String> pcList = dataSecReqMap.get(OnboardingConstant.PC);
            List<String> plantList = dataSecReqMap
                    .get(OnboardingConstant.PLANT);
            List<String> divList = dataSecReqMap
                    .get(OnboardingConstant.DIVISION);
            List<String> soList = dataSecReqMap.get(OnboardingConstant.SO);
            List<String> dcList = dataSecReqMap.get(OnboardingConstant.DC);
            List<String> locList = dataSecReqMap
                    .get(OnboardingConstant.LOCATION);
            List<String> ud1List = dataSecReqMap.get(OnboardingConstant.UD1);
            List<String> ud2List = dataSecReqMap.get(OnboardingConstant.UD2);
            List<String> ud3List = dataSecReqMap.get(OnboardingConstant.UD3);
            List<String> ud4List = dataSecReqMap.get(OnboardingConstant.UD4);
            List<String> ud5List = dataSecReqMap.get(OnboardingConstant.UD5);
            List<String> ud6List = dataSecReqMap.get(OnboardingConstant.UD6);
            if ((gstinList == null || gstinList.isEmpty())
                    && (pcList == null || pcList.isEmpty())
                    && (plantList == null || plantList.isEmpty())
                    && (divList == null || divList.isEmpty())
                    && (soList == null || soList.isEmpty())
                    && (dcList == null || dcList.isEmpty())
                    && (locList == null || locList.isEmpty())
                    && (ud1List == null || ud1List.isEmpty())
                    && (ud2List == null || ud2List.isEmpty())
                    && (ud3List == null || ud3List.isEmpty())
                    && (ud4List == null || ud4List.isEmpty())
                    && (ud5List == null || ud5List.isEmpty())
                    && (ud6List == null || ud6List.isEmpty())) {
                gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
            } else {
                if ((gstinList != null && !gstinList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.GSTIN, gstinList);
                }

 

                if ((pcList != null && !pcList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.PC, pcList);
                }

 

                if ((plantList != null && !plantList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.PLANT, plantList);
                }

 

                if ((divList != null && !divList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.DIVISION, divList);
                }

 

                if ((soList != null && !soList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.SO, soList);
                }

 

                if ((dcList != null && !dcList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.DC, dcList);
                }

 

                if ((locList != null && !locList.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.LOCATION, locList);
                }

 

                if ((ud1List != null && !ud1List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD1, ud1List);
                }

 

                if ((ud2List != null && !ud2List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD2, ud2List);
                }

 

                if ((ud3List != null && !ud3List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD3, ud3List);
                }

 

                if ((ud4List != null && !ud4List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD4, ud4List);
                }

 

                if ((ud5List != null && !ud5List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD5, ud5List);
                }

 

                if ((ud6List != null && !ud6List.isEmpty())) {
                    dataSecAttrs.put(OnboardingConstant.UD6, ud6List);
                }

 

                gstr1ProcessedRecordsReqDto.setDataSecAttrs(dataSecAttrs);
            }
        }
        return gstr1ProcessedRecordsReqDto;
    }
}



