package com.ey.advisory.app.services.search.simplified.docsummary;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.RefundEntity;
import com.ey.advisory.app.data.repositories.client.RefundRepository;
import com.ey.advisory.app.docs.dto.simplified.Ret1RefundDetailSummaryDto;
import com.ey.advisory.app.services.docs.SRFileToRefundExcelConvertion;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1RefundUpdateService")
public class Ret1RefundUpdateService {

	@Autowired
	@Qualifier("RefundRepository")
	RefundRepository refundRepo;
	
	@Autowired
	@Qualifier("SRFileToRefundExcelConvertion")
	SRFileToRefundExcelConvertion key;
	
	@Transactional(value = "clientTransactionManager")
	public RefundEntity updateRefundData(List<Ret1RefundDetailSummaryDto> list) {

		RefundEntity save = null;
		for (Ret1RefundDetailSummaryDto dto : list) {

			RefundEntity entity = new RefundEntity();
			
			
			entity.setSgstin(dto.getGstin());
			entity.setRetPeriod(dto.getTaxPeriod());
			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getTaxPeriod());
			entity.setDerivedRetPeriod(derivedRetPeriod);
			
			entity.setId(dto.getId());
			entity.setDesc(dto.getDesc());
			entity.setTax(dto.getTax());
			entity.setInterest(dto.getInterest());
			entity.setPenalty(dto.getPenalty());
			entity.setFee(dto.getFee());
			entity.setOther(dto.getOther());
			entity.setTotal(dto.getTotal());
			entity.setUserDefined1(dto.getUserDefined1());
			entity.setUserDefined2(dto.getUserDefined2());
			entity.setUserDefined3(dto.getUserDefined3());
			entity.setSNo(dto.getsNo());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCreatedBy("SYSTEM");
			// generating key
			
			Object[] arrInvKey = {dto.getsNo(),dto.getGstin(),dto.getTaxPeriod(),dto.getDesc()};
			Object[] arrGstnKey = {dto.getsNo(),dto.getGstin(),dto.getTaxPeriod(),dto.getDesc()};

			
			String refundGstnKey = key.getRefundGstnKey(arrGstnKey);
			String refundInvKey = key.getRefundInvKey(arrInvKey);
		
			entity.setRefundInvkey(refundInvKey);
			entity.setRefundGstnkey(refundGstnKey);
	
			RefundEntity findId = refundRepo.findId(entity.getId());
			if (findId != null) {
				if (findId.getId() != null) {
					// entity.setDelete(true);
					entity.setDerivedRetPeriod(findId.getDerivedRetPeriod());
					entity.setRetPeriod(findId.getRetPeriod());
					entity.setAsEnterTableId(findId.getAsEnterTableId());
					entity.setSgstin(findId.getSgstin());
					entity.setDesc(findId.getDesc());
					entity.setTax(findId.getTax());
					entity.setInterest(findId.getInterest());
					entity.setPenalty(findId.getPenalty());
					entity.setFee(findId.getFee());
					entity.setOther(findId.getOther());
					entity.setTotal(findId.getTotal());
					entity.setUserDefined1(findId.getUserDefined1());
					entity.setUserDefined2(findId.getUserDefined2());
					entity.setUserDefined3(findId.getUserDefined3());
					entity.setRefundInvkey(findId.getRefundInvkey());
					entity.setRefundGstnkey(findId.getRefundGstnkey());
					entity.setInfo(findId.isInfo());
					
					entity.setCreatedBy(findId.getCreatedBy());
					entity.setCreatedOn(findId.getCreatedOn());
					entity.setFileId(findId.getFileId());
					
					entity.setModifiedBy("SYSTEM");
					entity.setModifiedOn(LocalDateTime.now());
					entity.setSNo(findId.getSNo());
					
					
				} 
				if (findId.getId() != null) {
				if (refundInvKey != null) {
					refundRepo.UpdateSameInvKey(refundInvKey);
					}

				}
			}

			save = refundRepo.save(entity);
		}

		return save;
	}

	
}
