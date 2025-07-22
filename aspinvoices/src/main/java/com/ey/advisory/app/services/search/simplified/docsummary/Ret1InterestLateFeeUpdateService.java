package com.ey.advisory.app.services.search.simplified.docsummary;
/**
 * 
 * @author Balakrishna.S
 *
 */

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.InterestAndLateFeeEntity;
import com.ey.advisory.app.data.repositories.client.InterestAndLateFeeRepository;
import com.ey.advisory.app.docs.dto.simplified.Ret1LateFeeDetailSummaryDto;
import com.ey.advisory.app.services.docs.SRFileToIntersetExcelConvertion;
import com.ey.advisory.common.GenUtil;

@Service("Ret1InterestLateFeeUpdateService")
public class Ret1InterestLateFeeUpdateService {

	@Autowired
	@Qualifier("InterestAndLateFeeRepository")
	InterestAndLateFeeRepository repoLateFeeRepo;

	@Autowired
	@Qualifier("SRFileToIntersetExcelConvertion")
	SRFileToIntersetExcelConvertion interestKey;

	@Transactional(value = "clientTransactionManager")
	public InterestAndLateFeeEntity updateVerticalData(
			List<Ret1LateFeeDetailSummaryDto> list) {

		InterestAndLateFeeEntity save = null;
		for (Ret1LateFeeDetailSummaryDto dto : list) {

			InterestAndLateFeeEntity entity = new InterestAndLateFeeEntity();
			entity.setSNo(dto.getsNo());
			entity.setReturnType(dto.getReturnType());
			entity.setRetPeriod(dto.getTaxPeriod());

			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getTaxPeriod());
			entity.setDerivedRetPeriod(derivedRetPeriod);
			entity.setSgstin(dto.getGstin());
			entity.setId(dto.getId());
			entity.setReturnTable(dto.getReturnTable());
			entity.setInterestSgstAmt(dto.getSgstInterest());
			entity.setInterestIgstAmt(dto.getIgstInterest());
			entity.setInterestCgstAmt(dto.getCgstInterest());
			entity.setInterestCessAmt(dto.getCessInterest());
			entity.setLateSgstAmt(dto.getSgstLateFee());
			entity.setLateCgstAmt(dto.getCgstLateFee());

			entity.setUserDef1(dto.getUserDefined1());
			entity.setUserDef2(dto.getUserDefined2());
			entity.setUserDef3(dto.getUserDefined3());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCreatedBy("SYSTEM");

			// generating inv & Gstn Key For Late Fee
			
			Object[] arrInv = { dto.getsNo(), dto.getReturnType(),
					dto.getGstin(), dto.getTaxPeriod(), dto.getReturnTable() };

			Object[] arrGstn = { dto.getsNo(), dto.getReturnType(),
					dto.getGstin(), dto.getTaxPeriod(), dto.getReturnTable() };

			String interestGstnKey = interestKey.getInterestGstnKey(arrGstn);
			String interestInvKey = interestKey.getInterestInvKey(arrInv);

			entity.setInterestGstnKey(interestGstnKey);
			entity.setInterestInvKey(interestInvKey);

			InterestAndLateFeeEntity findId = repoLateFeeRepo
					.findId(entity.getId());
			if (findId != null) {
				if (findId.getId() != null) {
					// entity.setDelete(true);

					entity.setSNo(findId.getSNo());
					entity.setReturnType(findId.getReturnType());
					entity.setRetPeriod(findId.getRetPeriod());
					entity.setDerivedRetPeriod(findId.getDerivedRetPeriod());
					entity.setSgstin(findId.getSgstin());
					entity.setId(findId.getId());
					entity.setFileId(findId.getFileId());
					entity.setReturnTable(findId.getReturnTable());
					entity.setInterestSgstAmt(findId.getInterestSgstAmt());
					entity.setInterestIgstAmt(findId.getInterestIgstAmt());
					entity.setInterestCgstAmt(findId.getInterestCgstAmt());
					entity.setInterestCessAmt(findId.getInterestCessAmt());
					entity.setLateSgstAmt(findId.getLateSgstAmt());
					entity.setLateCgstAmt(findId.getLateCgstAmt());
					entity.setAsEnterTableId(findId.getAsEnterTableId());

					entity.setUserDef1(findId.getUserDef1());
					entity.setUserDef2(findId.getUserDef2());
					entity.setUserDef3(findId.getUserDef3());

					entity.setInterestGstnKey(findId.getInterestGstnKey());
					entity.setInterestInvKey(findId.getInterestInvKey());
					entity.setModifiedBy("SYSTEM");
					entity.setModifiedOn(LocalDateTime.now());
					entity.setCreatedBy(findId.getCreatedBy());
					entity.setCreatedOn(findId.getCreatedOn());

				}
				if (findId.getId() != null) {
					if (interestInvKey != null) {
						repoLateFeeRepo.UpdateSameInvKey(interestInvKey);
					}

				}
			}

			save = repoLateFeeRepo.save(entity);
		}

		return save;
	}

}
