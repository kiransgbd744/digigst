package com.ey.advisory.app.services.savetogstn.jobs.gstr2x;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author SriBhavya
 *
 */

@Slf4j
@Service("Gstr2XBatchMakerImpl")
public class Gstr2XBatchMakerImpl implements Gstr2XBatchMaker {

	@Autowired
	@Qualifier("Gstr2XSaveBatchProcessImpl")
	private Gstr2XSectionWiseSaveBatchProcess saveBatchProcess;

	@Autowired
	@Qualifier("Gstr2XRateDataToTdsConverterImpl")
	private RateDataToGstr2XConverter rateDataToTdsConverter;
	
	@Autowired
	@Qualifier("Gstr2XRateDataToTdsaConverterImpl")
	private RateDataToGstr2XConverter rateDataToTdsaConverter;
	
	@Autowired
	@Qualifier("Gstr2XRateDataToTcsConverterImpl")
	private RateDataToGstr2XConverter rateDataToTcsConverter;
	
	@Autowired
	@Qualifier("Gstr2XRateDataToTcsaConverterImpl")
	private RateDataToGstr2XConverter rateDataToTcsaConverter;

	@Override
	public List<SaveToGstnBatchRefIds> saveGstr2XData(String groupCode, String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId) {
		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			if (docs != null && !docs.isEmpty()) {
				List<SaveToGstnBatchRefIds> list = new ArrayList<>();

				if (APIConstants.TCS.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = rateDataToTcsConverter.convertToGstr2XObject(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				} else if (APIConstants.TCSA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = rateDataToTcsaConverter.convertToGstr2XObject(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				} else if (APIConstants.TDS.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = rateDataToTdsConverter.convertToGstr2XObject(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				} else if (APIConstants.TDSA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = rateDataToTdsaConverter.convertToGstr2XObject(docs, section, groupCode,
							taxDocType);
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode, section, userRequestId, taxDocType);
				}

					respList.addAll(list);
				}			

		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
			throw new APIException(msg, ex);
		}
		return respList;
	}

}
