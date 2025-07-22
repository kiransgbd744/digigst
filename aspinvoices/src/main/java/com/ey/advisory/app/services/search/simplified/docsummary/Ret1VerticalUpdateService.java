package com.ey.advisory.app.services.search.simplified.docsummary;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.app.data.entities.client.Ret1And1AEntity;
import com.ey.advisory.app.data.repositories.client.Ret1And1ARepository;
import com.ey.advisory.app.docs.dto.simplified.Ret1AspDetailRespDto;
import com.ey.advisory.app.services.docs.SRFileToRet1And1AExcelConvertion;
import com.ey.advisory.common.GenUtil;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Ret1VerticalUpdateService")
public class Ret1VerticalUpdateService {

	@Autowired
	@Qualifier("Ret1And1ARepository")
	Ret1And1ARepository repository;

	@Autowired
	@Qualifier("SRFileToRet1And1AExcelConvertion")
	SRFileToRet1And1AExcelConvertion validationKey;

	@Transactional(value = "clientTransactionManager")
	public Ret1And1AEntity updateVerticalData(List<Ret1AspDetailRespDto> list) {

		Ret1And1AEntity save = null;
		for (Ret1AspDetailRespDto dto : list) {

			Ret1And1AEntity entity = new Ret1And1AEntity();
			entity.setSgstin(dto.getGstin());
			entity.setRetPeriod(dto.getTaxPeriod());

			int derivedRetPeriod = GenUtil
					.convertTaxPeriodToInt(dto.getTaxPeriod());
			entity.setDerivedRetPeriod(derivedRetPeriod);
			entity.setId(dto.getId());
			entity.setReturnTable(dto.getReturnTable());
			entity.setRetType(dto.getReturnType());
			entity.setTaxableValue(dto.getValue());
			entity.setIgstAmt(dto.getIgstAmt());
			entity.setSgstAmt(dto.getSgstAmt());
			entity.setCgstAmt(dto.getCgstAmt());
			entity.setCessAmt(dto.getCessAmt());
			entity.setProfitCentre(dto.getProfitCenter());
			entity.setPlant(dto.getPlant());
			entity.setDivision(dto.getDivision());
			entity.setLocation(dto.getLocation());
			entity.setSalesOrganisation(dto.getSalesOrg());
			entity.setDistributionChannel(dto.getDistrChannel());
			entity.setUserAccess1(dto.getUserAccess1());
			entity.setUserAccess2(dto.getUserAccess2());
			entity.setUserAccess3(dto.getUserAccess3());
			entity.setUserAccess4(dto.getUserAccess4());
			entity.setUserAccess5(dto.getUserAccess5());
			entity.setUserAccess6(dto.getUserAccess6());
			entity.setUserDef1(dto.getUserDefined1());
			entity.setUserDef2(dto.getUserDefined2());
			entity.setUserDef3(dto.getUserDefined3());
			entity.setCreatedOn(LocalDateTime.now());
			entity.setCreatedBy("SYSTEM");
			Object[] arrGstnKey = { dto.getReturnType(), dto.getGstin(),
					dto.getTaxPeriod(), dto.getReturnTable() };

			Object[] arrInvKey = { dto.getReturnType(), dto.getGstin(),
					dto.getTaxPeriod(), dto.getReturnTable(), null, null, null,
					null, null, dto.getProfitCenter(), dto.getPlant(),
					dto.getDivision(), dto.getSalesOrg(), dto.getDistrChannel(),
					dto.getUserAccess1(), dto.getUserAccess2(),
					dto.getUserAccess3(), dto.getUserAccess4(),
					dto.getUserAccess5(), dto.getUserAccess6(),
					dto.getUserDefined1(), dto.getUserDefined2(),
					dto.getUserDefined3() };

			String ret1GstnKey = validationKey.getRet1And1AGstnKey(arrGstnKey);
			String ret1InvKey = validationKey.getRet1And1AInvKey(arrInvKey);
			entity.setRetInvKey(ret1InvKey);
			entity.setRetGstnKey(ret1GstnKey);

			Ret1And1AEntity findId = repository.findId(entity.getId());
			if (findId != null) {
				if (findId.getId() != null) {
					// entity.setDelete(true);
					entity.setDerivedRetPeriod(findId.getDerivedRetPeriod());
					entity.setRetPeriod(findId.getRetPeriod());
					entity.setRetGstnKey(findId.getRetGstnKey());
					entity.setAsEnterTableId(findId.getAsEnterTableId());
					entity.setCreatedBy(findId.getCreatedBy());
					entity.setCreatedOn(findId.getCreatedOn());
					entity.setFileId(findId.getFileId());
					entity.setInfo(findId.isInfo());
					entity.setModifiedBy("SYSTEM");
					entity.setModifiedOn(LocalDateTime.now());
					entity.setRetType(findId.getRetType());
					entity.setRetInvKey(findId.getRetInvKey());
					entity.setReturnTable(findId.getReturnTable());
					entity.setAsEnterTableId(findId.getAsEnterTableId());
					entity.setSgstin(findId.getSgstin());
				} 
				if (findId.getId() != null) {
				if (ret1InvKey != null) {
						repository.UpdateSameInvKey(ret1InvKey);
					}

				}
			}

			save = repository.save(entity);
		}

		return save;
	}
}
