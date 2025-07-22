package com.ey.advisory.app.services.savetogstn.jobs.gstr7;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.services.savetogstn.jobs.gstr6.Gstr6SectionWiseSaveBatchProcess;
import com.ey.advisory.common.ProcessingContext;
import com.ey.advisory.core.api.APIConstants;
import com.ey.advisory.core.api.APIException;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Sri Bhavya
 *
 */

@Service("Gstr7BatchMakerImpl")
@Slf4j
public class Gstr7BatchMakerImpl implements Gstr7BatchMaker {

	@Autowired
	@Qualifier("Gstr7SaveBatchProcessImpl")
	private Gstr6SectionWiseSaveBatchProcess saveBatchProcess;

	@Autowired
	@Qualifier("Gstr7RateDataToTdsConverter")
	private RateDataToGstr7Converter tdsGstr7Converter;

	@Autowired
	@Qualifier("Gstr7RateDataToTdsaConverter")
	private RateDataToGstr7Converter tdsaGstr7Converter;

	@Autowired
	@Qualifier("Gstr7TransRateDataToTdsConverter")
	private RateDataToGstr7Converter transTdsGstr7Converter;

	@Autowired
	@Qualifier("Gstr7TransRateDataToTdsaConverter")
	private RateDataToGstr7Converter transTdsaGstr7Converter;

	@Override
	public List<SaveToGstnBatchRefIds> saveGstr7Data(String groupCode,
			String section, List<Object[]> docs,
			SaveToGstnOprtnType operationType, Long userRequestId,
			ProcessingContext gstr7context) {
		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			if (docs != null && !docs.isEmpty()) {
				List<SaveToGstnBatchRefIds> list = new ArrayList<>();

				boolean isTransactional = (boolean) gstr7context
						.getAttribute(APIConstants.TRANSACTIONAL);

				if (APIConstants.TDS.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
					if (isTransactional) {

						batchDto = transTdsGstr7Converter.convertToGstr7Object(
								docs, section, groupCode, taxDocType);

					} else {
						batchDto = tdsGstr7Converter.convertToGstr7Object(docs,
								section, groupCode, taxDocType);

					}
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section, userRequestId, taxDocType,
								gstr7context);
				} else if (APIConstants.TDSA.equalsIgnoreCase(section)) {
					SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
					if (isTransactional) {
						batchDto = transTdsaGstr7Converter.convertToGstr7Object(
								docs, section, groupCode, taxDocType);

					} else {
						batchDto = tdsaGstr7Converter.convertToGstr7Object(docs,
								section, groupCode, taxDocType);

					}
					if (batchDto != null)
						list = saveBatchProcess.execute(batchDto, groupCode,
								section, userRequestId, taxDocType,
								gstr7context);
				}
				if (!list.isEmpty()) {
					respList.addAll(list);
				}

			}

		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
			throw new APIException(msg, ex);
		}
		return respList;
	}

}
