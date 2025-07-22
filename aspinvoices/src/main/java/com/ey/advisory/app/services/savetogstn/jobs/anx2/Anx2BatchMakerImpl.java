/**
 * 
 */
package com.ey.advisory.app.services.savetogstn.jobs.anx2;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.core.api.SaveToGstnOprtnType;
import com.ey.advisory.core.dto.SaveToGstnBatchRefIds;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Hemasundar.J
 *
 */
@Service("Anx2BatchMakerImpl")
@Slf4j
public class Anx2BatchMakerImpl implements Anx2BatchMaker {

	@Autowired
	@Qualifier("RateDataToAnx2ConverterImpl")
	private RateDataToAnx2Converter anx2Converter;

	@Autowired
	@Qualifier("Anx2SaveBatchProcessImpl")
	private Anx2SectionWiseSaveBatchProcess saveBatchProcess;

	@Override
	public List<SaveToGstnBatchRefIds> saveAnx2Data(String groupCode, String section,
			List<Object[]> docs, SaveToGstnOprtnType operationType) {

		String taxDocType = null;
		if (operationType != null) {
			taxDocType = operationType.toString();
		}
		List<SaveToGstnBatchRefIds> respList = new ArrayList<>();
		try {
			if (docs != null && !docs.isEmpty()) {
			//	for (List<Object[]> doc : docs) {
				//	if (doc != null && !doc.isEmpty()) {
						List<SaveToGstnBatchRefIds> list = new ArrayList<>();
						/*if (APIConstants.B2B.equals(section)
								|| APIConstants.B2BA.equals(section)) {*/
							SaveBatchProcessDto batchDto = anx2Converter
									.convertToAnx2Object(docs, section,
											groupCode, taxDocType);
							if (batchDto != null) {
								list = saveBatchProcess.execute(batchDto,
										groupCode, section);
							}
						/*} else if (APIConstants.DE.equals(section)
								|| APIConstants.DEA.equals(section)) {
							SaveBatchProcessDto batchDto = anx2Converter
									.convertToAnx2Object(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
								list = saveBatchProcess.execute(batchDto,
										groupCode, section);
							}
						} else if (APIConstants.SEZWP.equals(section)
								|| APIConstants.SEZWPA.equals(section)) {
							SaveBatchProcessDto batchDto = anx2Converter
									.convertToAnx2Object(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
								list = saveBatchProcess.execute(batchDto,
										groupCode, section);
							}
						} else if (APIConstants.SEZWOP.equals(section)
								|| APIConstants.SEZWOPA.equals(section)) {
							SaveBatchProcessDto batchDto = anx2Converter
									.convertToAnx2Object(doc, section,
											groupCode, taxDocType);
							if (batchDto != null) {
								list = saveBatchProcess.execute(batchDto,
										groupCode, section);
							}
						}*/
						if (!list.isEmpty()) {
							respList.addAll(list);
						}
				//	}
				//}
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving {} docs to Gstn {}";
			LOGGER.error(msg, section, ex);
		}
		return respList;
	}

}
