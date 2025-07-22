/**
 * 
 */
package com.ey.advisory.services.gstr3b.itc.reclaim;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.data.repositories.client.Gstr3BGstinAspUserInputRepository;
import com.ey.advisory.app.data.repositories.client.Gstr3BGstinItcReclaimRepository;
import com.ey.advisory.app.gstr3b.Gstr3BGstinAspUserInputEntity;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.EYDateUtil;

import lombok.extern.slf4j.Slf4j;

/**
 * @author vishal.verma
 *
 */

@Slf4j
@Component("Gstr3BValidateITCReclaimServiceImpl")
public class Gstr3BValidateITCReclaimServiceImpl
		implements Gstr3BValidateITCReclaimService {

	@Autowired
	@Qualifier("Gstr3BGstinAspUserInputRepository")
	private Gstr3BGstinAspUserInputRepository userRepo;

	@Autowired
	@Qualifier("Gstr3BGstinItcReclaimRepository")
	private Gstr3BGstinItcReclaimRepository itcReclaimRepo;

	private static final List<String> sections = new ArrayList<>(
			Arrays.asList("4(b)(2)", "4(d)(1)"));

	@Override
	public List<Gstr3BValidateItcReclaimDto> validate3BReclaimAmount(
			String gstin, String taxPeriod) {

		List<Gstr3BValidateItcReclaimDto> respList = new ArrayList<>();

		try {

			Gstr3BGstinItcReclaimEntity itcEntity = itcReclaimRepo
					.findByGstinAndTaxPeriodAndIsActive(gstin, taxPeriod, true);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr3BValidateITCReclaimServiceImpl invoked "
								+ "from Gstr3BGstinItcReclaimEntity with gstin %s, "
								+ "taxPeriod %s, itcEntity %s : ",
						gstin, taxPeriod, itcEntity);
				LOGGER.debug(msg);
			}

			Gstr3BValidateItcReclaimDto dto1 = new Gstr3BValidateItcReclaimDto();

			dto1.setCess(
					itcEntity != null ? itcEntity.getCess() : BigDecimal.ZERO);
			dto1.setCgst(
					itcEntity != null ? itcEntity.getCgst() : BigDecimal.ZERO);
			dto1.setIgst(
					itcEntity != null ? itcEntity.getIgst() : BigDecimal.ZERO);
			dto1.setSectionName(itcEntity != null ? itcEntity.getSectionName()
					: "GET_4D1(A)");
			dto1.setSgst(
					itcEntity != null ? itcEntity.getSgst() : BigDecimal.ZERO);

			String timeStamp = null;
			LocalDateTime createdOn = null;

			if (itcEntity != null) {

				createdOn = itcEntity.getCreateDate() != null
						? EYDateUtil
								.toISTDateTimeFromUTC(itcEntity.getCreateDate())
						: null;

				if (createdOn != null) {
					String dateTime = createdOn.toString();
					String date = dateTime.substring(0, 10);
					String time = dateTime.substring(11, 19);
					timeStamp = (date + " " + time);
				}
			}
			dto1.setTimeStamp(timeStamp);
			
			dto1.setOrder(1);

			respList.add(dto1);

			List<Gstr3BGstinAspUserInputEntity> userEntity = userRepo
					.getITC10PercSectionData(taxPeriod, gstin, sections);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Gstr3BValidateITCReclaimServiceImpl invoked "
								+ "from Gstr3BGstinAspUserInputEntity with gstin %s, "
								+ "taxPeriod %s, userEntity %s : ",
						gstin, taxPeriod, userEntity);
				LOGGER.debug(msg);
			}
			if (userEntity != null && !userEntity.isEmpty()) {
				for (Gstr3BGstinAspUserInputEntity entity : userEntity) {

					Gstr3BValidateItcReclaimDto dto = new Gstr3BValidateItcReclaimDto();

					dto.setCess(entity.getCess());
					dto.setCgst(entity.getCgst());
					dto.setIgst(entity.getIgst());
					dto.setSectionName(entity.getSectionName());
					dto.setSgst(entity.getSgst());
					dto.setTimeStamp(timeStamp);
					if (dto.getSectionName().equalsIgnoreCase("4(b)(2)")) {
						dto.setOrder(2);
					} else {
						dto.setOrder(3);
					}

					respList.add(dto);

				}
			} else {
				BigDecimal zero = BigDecimal.ZERO;
				for (String section : sections) {

					Gstr3BValidateItcReclaimDto dto = new Gstr3BValidateItcReclaimDto();
					dto.setCess(zero);
					dto.setCgst(zero);
					dto.setIgst(zero);
					dto.setSectionName(section);
					dto.setSgst(zero);
					dto.setTimeStamp(timeStamp);

					if(section.equalsIgnoreCase("4(b)(2)"))
					{
						dto.setOrder(2);
					}else {
						dto.setOrder(3);
					}

					respList.add(dto);
				}
			}
		} catch (Exception ex) {

			String msg = String.format("Error occured in "
					+ "Gstr3BValidateITCReclaimServiceImpl {} :", ex);
			LOGGER.error(msg);

			throw new AppException(msg, ex);
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Gstr3BValidateITCReclaimServiceImpl before returing "
							+ "gstin %s, taxPeriod %s, respList %s : ",
					gstin, taxPeriod, respList);
			LOGGER.debug(msg);
		}
		// sorting list
		respList.sort(
				Comparator.comparing(Gstr3BValidateItcReclaimDto::getOrder));
		return respList;
	}

}
