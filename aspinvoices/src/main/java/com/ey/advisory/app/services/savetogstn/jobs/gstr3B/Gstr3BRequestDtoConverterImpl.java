package com.ey.advisory.app.services.savetogstn.jobs.gstr3B;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BEcoDtlsDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BElgItcDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BIntLateFeeDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BInterStateSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BInwSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BOutInwSuppDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSavetoGstnDTO;
import com.ey.advisory.app.docs.dto.gstr3B.Gstr3BSecDetailsDTO;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.Gstr3BConstants;

@Service("gstr3BRequestDtoConverterImpl")
public class Gstr3BRequestDtoConverterImpl
		implements Gstr3BRequestDtoConverter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(Gstr3BRequestDtoConverterImpl.class);

	@Override
	public Gstr3BSavetoGstnDTO convertToGstr3BObject(List<Object[]> objects,
			String section, String groupCode) {
		Gstr3BSavetoGstnDTO dto = new Gstr3BSavetoGstnDTO();
		Gstr3BOutInwSuppDTO gstr3BOutInwSuppDTO = null;
		Gstr3BInterStateSuppDTO gstr3BInterStateSuppDTO = null;
		Gstr3BElgItcDTO gstr3BElgItcDTO = null;
		Gstr3BInwSuppDTO gstr3BInwSuppDTO = null;
		Gstr3BIntLateFeeDTO gstr3BIntLateFeeDTO = null;
		Gstr3BEcoDtlsDTO gstr3BEcoDtlsDTO = null;
		try {
			if (objects != null && !objects.isEmpty()) {
				for (Object[] obj : objects) {
					dto.setRetPeriod((String) obj[1]);
					dto.setGstin((String) obj[2]);
					String sectionName = (String) obj[3];
					if (sectionName.equals(Gstr3BConstants.Table3_1_A)
							|| sectionName.equals(Gstr3BConstants.Table3_1_B)
							|| sectionName.equals(Gstr3BConstants.Table3_1_C)
							|| sectionName.equals(Gstr3BConstants.Table3_1_D)
							|| sectionName.equals(Gstr3BConstants.Table3_1_E)) {
						gstr3BOutInwSuppDTO = setSupDetails(obj,
								gstr3BOutInwSuppDTO);
					} else if (sectionName.equals(Gstr3BConstants.Table3_2_A)
							|| sectionName.equals(Gstr3BConstants.Table3_2_B)
							|| sectionName.equals(Gstr3BConstants.Table3_2_C)) {
						gstr3BInterStateSuppDTO = setInterSupDetails(obj,
								gstr3BInterStateSuppDTO);
					} // 3.3 A & B
					else if (sectionName.equals(Gstr3BConstants.Table3_1_1_A)
							|| sectionName
									.equals(Gstr3BConstants.Table3_1_1_B)) {
						gstr3BEcoDtlsDTO = setEcoDtls(obj, gstr3BEcoDtlsDTO);

					} else if (sectionName.equals(Gstr3BConstants.Table4A1)
							|| sectionName.equals(Gstr3BConstants.Table4A2)
							|| sectionName.equals(Gstr3BConstants.Table4A3)
							|| sectionName.equals(Gstr3BConstants.Table4A4)
							|| sectionName.equals(Gstr3BConstants.Table4A5)
							|| sectionName.equals(Gstr3BConstants.Table4B1)
							|| sectionName.equals(Gstr3BConstants.Table4B2)
							|| sectionName.equals(Gstr3BConstants.Table4C)
							|| sectionName.equals(Gstr3BConstants.Table4D1)
							|| sectionName.equals(Gstr3BConstants.Table4D2)) {
						gstr3BElgItcDTO = setItcElgDetails(obj,
								gstr3BElgItcDTO);
					} else if (sectionName.equals(Gstr3BConstants.Table5A)
							|| sectionName.equals(Gstr3BConstants.Table5B)) {
						gstr3BInwSuppDTO = setInwardSupDetails(obj,
								gstr3BInwSuppDTO);
					} else if (sectionName.equals(Gstr3BConstants.Table5_1A)
							|| sectionName.equals(Gstr3BConstants.Table5_1B)) {
						gstr3BIntLateFeeDTO = setIntrLtFeeDetails(obj,
								gstr3BIntLateFeeDTO);
					}
				}
				dto.setSupDetails(gstr3BOutInwSuppDTO);
				dto.setInterSup(gstr3BInterStateSuppDTO);
				dto.setEcoDtls(gstr3BEcoDtlsDTO);

				List<Gstr3BSecDetailsDTO> unSortedSec4BList = gstr3BElgItcDTO != null
						? gstr3BElgItcDTO.getItcRev() : null;
				if (gstr3BElgItcDTO != null && unSortedSec4BList != null) {
					Comparator<Gstr3BSecDetailsDTO> compDto = Comparator
							.comparing(Gstr3BSecDetailsDTO::getTy,
									Comparator.reverseOrder());
					Collections.sort(unSortedSec4BList, compDto);
					gstr3BElgItcDTO.setItcRev(unSortedSec4BList);
				} else {
					LOGGER.error(
							"GSTR3B Sec4B details are empty, Hence Sorting cannot be done");
				}
				dto.setItcElg(gstr3BElgItcDTO);

				// sorting dto
				if (gstr3BInwSuppDTO != null) {
					Comparator<Gstr3BSecDetailsDTO> compDto = Comparator
							.comparing(Gstr3BSecDetailsDTO::getTy);

					List<Gstr3BSecDetailsDTO> l1 = gstr3BInwSuppDTO != null
							? gstr3BInwSuppDTO.getIsupDetails() : null;

					Collections.sort(l1, compDto);
					gstr3BInwSuppDTO.setIsupDetails(l1);

				}
				dto.setInwardSup(gstr3BInwSuppDTO);

				dto.setIntrLtfee(gstr3BIntLateFeeDTO);
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}

		} catch (Exception ex) {
			String msg = "Unexpected error while forming the Save 3B Payload";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}

		return dto;
	}

	private Gstr3BOutInwSuppDTO setSupDetails(Object[] obj,
			Gstr3BOutInwSuppDTO dto) {
		if (dto == null)
			dto = new Gstr3BOutInwSuppDTO();
		String subSecName = (String) obj[3];
		if (subSecName.equals(Gstr3BConstants.Table3_1_A)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTxval((BigDecimal) obj[5]);
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.setOsupDet(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table3_1_B)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTxval((BigDecimal) obj[5]);
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.setOsupZero(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table3_1_C)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTxval((BigDecimal) obj[5]);
			dto.setOsupNilExmp(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table3_1_D)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTxval((BigDecimal) obj[5]);
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.setIsupRev(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table3_1_E)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTxval((BigDecimal) obj[5]);
			dto.setOsupNongst(secdto);
		}
		return dto;
	}

	private Gstr3BInterStateSuppDTO setInterSupDetails(Object[] obj,
			Gstr3BInterStateSuppDTO dto) {
		Gstr3BSecDetailsDTO secdto = null;
		if (dto == null) {
			dto = new Gstr3BInterStateSuppDTO();
		}
		String subSecName = (String) obj[3];
		if (subSecName.equals(Gstr3BConstants.Table3_2_A)) {
			secdto = new Gstr3BSecDetailsDTO();
			secdto.setPos((String) obj[10]);
			secdto.setTxval((BigDecimal) obj[5]);
			secdto.setIamt((BigDecimal) obj[6]);
			if (dto.getUnregDetails() == null)
				dto.setUnregDetails(new ArrayList<Gstr3BSecDetailsDTO>());

			dto.getUnregDetails().add(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table3_2_B)) {
			secdto = new Gstr3BSecDetailsDTO();
			secdto.setPos((String) obj[10]);
			secdto.setTxval((BigDecimal) obj[5]);
			secdto.setIamt((BigDecimal) obj[6]);
			if (dto.getCompDetails() == null)
				dto.setCompDetails(new ArrayList<Gstr3BSecDetailsDTO>());

			dto.getCompDetails().add(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table3_2_C)) {
			secdto = new Gstr3BSecDetailsDTO();
			secdto.setPos((String) obj[10]);
			secdto.setTxval((BigDecimal) obj[5]);
			secdto.setIamt((BigDecimal) obj[6]);

			if (dto.getUinDetails() == null)
				dto.setUinDetails(new ArrayList<Gstr3BSecDetailsDTO>());

			dto.getUinDetails().add(secdto);
		}
		return dto;
	}

	private Gstr3BElgItcDTO setItcElgDetails(Object[] obj,
			Gstr3BElgItcDTO dto) {
		if (dto == null) {
			dto = new Gstr3BElgItcDTO();
			dto.setItc_avl(new ArrayList<Gstr3BSecDetailsDTO>());
			dto.setItcInelg(new ArrayList<Gstr3BSecDetailsDTO>());
			dto.setItcRev(new ArrayList<Gstr3BSecDetailsDTO>());
		}
		String subSecName = (String) obj[3];
		if (subSecName.equals(Gstr3BConstants.Table4A1)
				|| subSecName.equals(Gstr3BConstants.Table4A2)
				|| subSecName.equals(Gstr3BConstants.Table4A3)
				|| subSecName.equals(Gstr3BConstants.Table4A4)
				|| subSecName.equals(Gstr3BConstants.Table4A5)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			if (subSecName.equals(Gstr3BConstants.Table4A1))
				secdto.setTy("IMPG");
			else if (subSecName.equals(Gstr3BConstants.Table4A2))
				secdto.setTy("IMPS");
			else if (subSecName.equals(Gstr3BConstants.Table4A3))
				secdto.setTy("ISRC");
			else if (subSecName.equals(Gstr3BConstants.Table4A4))
				secdto.setTy("ISD");
			else if (subSecName.equals(Gstr3BConstants.Table4A5))
				secdto.setTy("OTH");
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.getItc_avl().add(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table4B1)
				|| subSecName.equals(Gstr3BConstants.Table4B2)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			if (subSecName.equals(Gstr3BConstants.Table4B1))
				secdto.setTy("RUL");
			else if (subSecName.equals(Gstr3BConstants.Table4B2))
				secdto.setTy("OTH");
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.getItcRev().add(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table4C)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.setItcNet(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table4D1)
				|| subSecName.equals(Gstr3BConstants.Table4D2)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			if (subSecName.equals(Gstr3BConstants.Table4D1))
				secdto.setTy("RUL");
			else if (subSecName.equals(Gstr3BConstants.Table4D2))
				secdto.setTy("OTH");
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.getItcInelg().add(secdto);
		}
		return dto;
	}

	private Gstr3BInwSuppDTO setInwardSupDetails(Object[] obj,
			Gstr3BInwSuppDTO dto) {
		if (dto == null) {
			dto = new Gstr3BInwSuppDTO();
			dto.setIsupDetails(new ArrayList<Gstr3BSecDetailsDTO>());
		}
		String subSecName = (String) obj[3];
		if (subSecName.equals(Gstr3BConstants.Table5A)
				|| subSecName.equals(Gstr3BConstants.Table5B)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTy(subSecName.equals(Gstr3BConstants.Table5A) ? "GST"
					: "NONGST");
			secdto.setInter((BigDecimal) obj[11]);
			secdto.setIntra((BigDecimal) obj[12]);
			dto.getIsupDetails().add(secdto);
		}

		return dto;
	}

	private Gstr3BIntLateFeeDTO setIntrLtFeeDetails(Object[] obj,
			Gstr3BIntLateFeeDTO dto) {
		if (dto == null) {
			dto = new Gstr3BIntLateFeeDTO();
		}
		String subSecName = (String) obj[3];
		if (subSecName.equals(Gstr3BConstants.Table5_1A)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.setIntrDetails(secdto);

		} else {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			dto.setLateFeeDetails(secdto);
		}

		return dto;
	}

	// 3.3 A&B
	private Gstr3BEcoDtlsDTO setEcoDtls(Object[] obj, Gstr3BEcoDtlsDTO dto) {
		if (dto == null) {
			dto = new Gstr3BEcoDtlsDTO();
		}
		String subSecName = (String) obj[3];
		if (subSecName.equals(Gstr3BConstants.Table3_1_1_A)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTxval((BigDecimal) obj[5]);
			secdto.setIamt((BigDecimal) obj[6]);
			secdto.setCamt((BigDecimal) obj[7]);
			secdto.setSamt((BigDecimal) obj[8]);
			secdto.setCsamt((BigDecimal) obj[9]);
			dto.setEcoSup(secdto);
		} else if (subSecName.equals(Gstr3BConstants.Table3_1_1_B)) {
			Gstr3BSecDetailsDTO secdto = new Gstr3BSecDetailsDTO();
			secdto.setTxval((BigDecimal) obj[5]);
			dto.setEcoRegSup(secdto);
		}
		return dto;
	}

}
