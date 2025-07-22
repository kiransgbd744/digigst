package com.ey.advisory.app.services.gstr1fileupload;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Gstr1AsEnteredAREntity;
import com.ey.advisory.app.docs.dto.Gstr1VerticalAtRespDto;
import com.google.common.base.Strings;
import com.google.gson.JsonObject;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Service("Gstr1AtTxpdUpdateService")
public class Gstr1AtTxpdUpdateService {

	@Autowired
	@Qualifier("Gstr1AtUpdateService")
	private Gstr1AtUpdateService gstr1AtUpdateService;

	@Autowired
	@Qualifier("Gstr1TxpdUpdateService")
	private Gstr1TxpdUpdateService gstr1TxpdUpdateService;

	@Transactional(value = "clientTransactionManager")
	public JsonObject updateVerticalData(List<Gstr1VerticalAtRespDto> list) {
		JsonObject resp = new JsonObject();
		for (Gstr1VerticalAtRespDto dto : list) {
			String section = dto.getSection();
			String returnType = dto.getReturnType();
			Gstr1AsEnteredAREntity entity = new Gstr1AsEnteredAREntity();
			entity.setSgstin(dto.getGstin());
			entity.setReturnPeriod(dto.getTaxPeriod());
			entity.setTransType(dto.getTransType());
			//gstr1a code
			if (!Strings.isNullOrEmpty(returnType) && returnType.equalsIgnoreCase("GSTR1A")) {
				if ("ATA".equalsIgnoreCase(section)
						|| "AT".equalsIgnoreCase(section)) {
					return gstr1AtUpdateService.updateGstr1aVerticalData(list);
				} else {
					return gstr1TxpdUpdateService.updateGstr1aVerticalData(list);
				}
			} else {
				if ("ATA".equalsIgnoreCase(section)
						|| "AT".equalsIgnoreCase(section)) {
					return gstr1AtUpdateService.updateVerticalData(list);
				} else {
					return gstr1TxpdUpdateService.updateVerticalData(list);
				}
			}
		}
		return resp;
	}
}
