package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ey.advisory.admin.data.entities.client.Gstr3bRatioUserInputEntity;
import com.ey.advisory.admin.data.repositories.client.Gstr3bRatioUserInputRepository;
import com.ey.advisory.app.gstr3b.Gstr3bRatioUserInputDto;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.SecurityContext;

import lombok.extern.slf4j.Slf4j;

/**
 * @author ashutosh.kar
 *
 */

@Slf4j
@Component("CreditRevForUserInputServiceImpl")
public class CreditRevForUserInputServiceImpl implements CreditRevForUserInputService {

	@Autowired
	private Gstr3bRatioUserInputRepository gstr3bRatioUserInputRepository;

	@PersistenceContext(unitName = "clientDataUnit")
	private EntityManager entityManager;

	@Override
	public void saveCredRevUserInputSummary(Gstr3bRatioUserInputDto reqDto) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("CreditRevForUserInputServiceImpl." + "saveCredRevUserInputSummary begin," + " for gstin : "
					+ reqDto.getGstin() + " and taxPeriod : " + reqDto.getTaxPeriod());
		}

		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			Gstr3bRatioUserInputEntity entity = new Gstr3bRatioUserInputEntity();
			entity.setEntityId(reqDto.getEntityId());
			entity.setGstin(reqDto.getGstin());
			entity.setTaxPeriod(reqDto.getTaxPeriod());
			entity.setUserInputRatio1(parseBigDecimal(reqDto.getUserInputRatio1()));
			entity.setUserInputRatio2(parseBigDecimal(reqDto.getUserInputRatio2()));
			entity.setIsDeleted(false);
			entity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
			entity.setCreatedBy(userName);

			gstr3bRatioUserInputRepository.updateActiveFlag(reqDto.getTaxPeriod(), reqDto.getGstin());
			gstr3bRatioUserInputRepository.save(entity);

		} catch (Exception e) {
			LOGGER.error("Error occurred while saving " + "creditReversalUserInputSummary for gstin: "
					+ reqDto.getGstin() + " and taxPeriod: " + reqDto.getTaxPeriod(), e);
			throw new AppException("Failed to save creditReversalUserInputSummary.", e);
		}
	}

	private BigDecimal parseBigDecimal(String value) {
		try {
			return (value != null && !value.trim().isEmpty()) ? new BigDecimal(value.trim()) : BigDecimal.ZERO;
		} catch (NumberFormatException e) {
			LOGGER.error("Invalid BigDecimal value: {}", value, e);
			return BigDecimal.ZERO;
		}
	}

	@Override
	public void moveToCredRevUserInputSummary(Gstr3bRatioUserInputDto reqDto) {
		String gstin = reqDto.getGstin();
		String taxPeriod = reqDto.getTaxPeriod();

		if (LOGGER.isDebugEnabled()) {
			LOGGER.info("CreditRevForUserInputServiceImpl.moveToCredRevUserInputSummary begin, " + "for gstin: " + gstin
					+ " and taxPeriod: " + taxPeriod);
		}

		try {
			String userName = (SecurityContext.getUser() != null
					&& SecurityContext.getUser().getUserPrincipalName() != null)
							? SecurityContext.getUser().getUserPrincipalName()
							: "SYSTEM";

			String sql = "SELECT RATIO_1, RATIO_2 FROM GSTR3B_RULE42_COMPUTE "
					+ "WHERE GSTIN = :gstin AND TAX_PERIOD = :taxperiod AND IS_ACTIVE = TRUE "
					+ "AND SECTION_NAME = 'ITC Reversal Ratio' AND SUB_SECTION_NAME = 'ITC Reversal Ratio'";

			Query q = entityManager.createNativeQuery(sql);
			q.setParameter("gstin", gstin);
			q.setParameter("taxperiod", taxPeriod);

			@SuppressWarnings("unchecked")
			List<Object[]> resultList = q.getResultList();

			if (!resultList.isEmpty()) {
				Object[] result = resultList.get(0); // Get the first row

				BigDecimal ratio1 = (result[0] != null) ? new BigDecimal(result[0].toString()) : BigDecimal.ZERO;
				BigDecimal ratio2 = (result[1] != null) ? new BigDecimal(result[1].toString()) : BigDecimal.ZERO;

				Gstr3bRatioUserInputEntity entity = new Gstr3bRatioUserInputEntity();
				entity.setEntityId(reqDto.getEntityId());
				entity.setGstin(gstin);
				entity.setTaxPeriod(taxPeriod);
				entity.setUserInputRatio1(ratio1);
				entity.setUserInputRatio2(ratio2);
				entity.setIsDeleted(false);
				entity.setCreatedOn(EYDateUtil.toUTCDateTimeFromLocal(LocalDateTime.now()));
				entity.setCreatedBy(userName);

				gstr3bRatioUserInputRepository.updateActiveFlag(taxPeriod, gstin);
				gstr3bRatioUserInputRepository.save(entity);
			} else {
				LOGGER.warn("No data found for GSTIN: " + gstin + " and Tax Period: " + taxPeriod);
			}

		} catch (Exception e) {
			LOGGER.error("Error in moveToCredRevUserInputSummary", e);
			throw new AppException("Failed to move data creditReversalUserInputSummary.", e);
		}
	}
}
