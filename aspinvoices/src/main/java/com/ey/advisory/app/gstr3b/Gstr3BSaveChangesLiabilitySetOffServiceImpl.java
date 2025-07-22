package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BSaveChangesLiabilitySetOffRepository;
import com.ey.advisory.common.AppException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BSaveChangesLiabilitySetOffServiceImpl")
public class Gstr3BSaveChangesLiabilitySetOffServiceImpl implements
Gstr3BSaveChangesLiabilitySetOffService{
	
	@Autowired
	@Qualifier("Gstr3BSaveChangesLiabilitySetOffRepository")
	private Gstr3BSaveChangesLiabilitySetOffRepository repo;
	
	@Override
	public String saveOffSetchanges(Gstr3BPdItcDto dto) {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug(
					"INSIDE Gstr3BSaveChangesLiabilitySetOffServiceImpl"
					+ ".saveOffSetchanges() method %s Gstr3BPdItcDto {}", dto);
		}
		BigDecimal defaultVal = BigDecimal.ZERO;

		Gstr3BSaveChangesLiabilitySetOffEntity entity = 
				new Gstr3BSaveChangesLiabilitySetOffEntity();
		
		try {
		
		Long liabLgdId = dto.getLedgId();
		Long transType = dto.getTransType();
		String gstin = dto.getGstin();
		String taxPeriod = dto.getTaxPeriod();
		
		BigDecimal ipdI = dto.getIPDIgst() != null ? dto.getIPDIgst()
				: defaultVal;
		BigDecimal ipdC = dto.getIPDCgst() != null ? dto.getIPDCgst()
				: defaultVal;
		BigDecimal ipdS = dto.getIPDSgst() != null ? dto.getIPDSgst()
				: defaultVal;

		BigDecimal cpdI = dto.getCPDIgst() != null ? dto.getCPDIgst()
				: defaultVal;
		BigDecimal cpdC = dto.getCPDCgst() != null ? dto.getCPDCgst()
				: defaultVal;

		BigDecimal spdI = dto.getSPDIgst() != null ? dto.getSPDIgst()
				: defaultVal;
		BigDecimal spdS = dto.getSPDSgst() != null ? dto.getSPDSgst()
				: defaultVal;

		BigDecimal cspdCs = dto.getCsPdCess() != null ? dto.getCsPdCess()
				: defaultVal;
		
		
		BigDecimal i2I = dto.getI_adjNegative2i() != null ? dto.getI_adjNegative2i()
				: defaultVal;
		BigDecimal i2C = dto.getC_adjNegative2i() != null ? dto.getC_adjNegative2i()
				: defaultVal;
		BigDecimal i2S = dto.getS_adjNegative2i() != null ? dto.getS_adjNegative2i()
				: defaultVal;
		BigDecimal i2CS = dto.getCs_adjNegative2i() != null ? dto.getCs_adjNegative2i()
				: defaultVal;
		
		BigDecimal a8I = dto.getI_adjNegative8A() != null ? dto.getI_adjNegative8A()
				: defaultVal;
		BigDecimal a8C = dto.getC_adjNegative8A() != null ? dto.getC_adjNegative8A()
				: defaultVal;
		BigDecimal a8S = dto.getS_adjNegative8A() != null ? dto.getS_adjNegative8A()
				: defaultVal;

		BigDecimal a8CS = dto.getCs_adjNegative8A() != null ? dto.getCs_adjNegative8A()
				: defaultVal;
		
		repo.updateActiveFlag(taxPeriod, gstin);
		
		entity.setGstin(gstin);
		entity.setTaxPeriod(taxPeriod);
		entity.setLiabLgdId(liabLgdId);
		entity.setTransType(transType);
		
		entity.setIPDIgst(ipdI);
		entity.setIPDCgst(ipdC);
		entity.setIPDSgst(ipdS);
		
		entity.setCPDCgst(cpdC);
		entity.setCPDIgst(cpdI);
		
		entity.setSPDSgst(spdS);
		entity.setSPDIgst(spdI);
		
		entity.setCsPdCess(cspdCs);
		
		entity.setI_adjNegative2i(i2I);
		entity.setC_adjNegative2i(i2C);
		entity.setS_adjNegative2i(i2S);
		entity.setCs_adjNegative2i(i2CS);
		
		entity.setI_adjNegative8A(a8I);
		entity.setC_adjNegative8A(a8C);
		entity.setS_adjNegative8A(a8S);
		entity.setCs_adjNegative8A(a8CS);
		
		entity.setIsActive(true);
		repo.save(entity);
		return "Changes saved successfully";
		
		} catch (Exception ex) {
			String msg = "Error while saving Json payload";
			LOGGER.error(msg, ex);
			throw new AppException(ex);
		}
		
	}

}
