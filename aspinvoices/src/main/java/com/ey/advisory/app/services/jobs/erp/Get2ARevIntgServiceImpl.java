package com.ey.advisory.app.services.jobs.erp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.erp.Get2ARevIntgItemDto;
import com.ey.advisory.app.services.savetogstn.jobs.anx1.ChunkSizeFetcher;

@Service("Get2ARevIntgServiceImpl")
public class Get2ARevIntgServiceImpl implements Get2ARevIntgService {

	@Autowired
	@Qualifier("Get2ARevIntgDaoImpl")
	private Get2ARevIntgDaoImpl get2ARevIntgDaoImpl;

	@Autowired
	@Qualifier("ChunkSizeFetcherImpl")
	private ChunkSizeFetcher chunkSizeFetcher;

	@Override
	public List<Get2ARevIntgItemDto> get2ARevIntg(final String gstin,
			int chunkId) {

		List<Get2ARevIntgItemDto> itemDtoChunck = new ArrayList<>();
		List<Object[]> objs = get2ARevIntgDaoImpl.get2ARevIntgDao(gstin,
				chunkId);
		for (Object[] obj : objs) {
			Get2ARevIntgItemDto itemDto = new Get2ARevIntgItemDto();
			itemDto.setSgstin(obj[0] != null ? String.valueOf(obj[0]) : null);
			itemDto.setCgstin(obj[1] != null ? String.valueOf(obj[1]) : null);
			itemDto.setTaxPeriod(
					obj[2] != null ? String.valueOf(obj[2]) : null);
			itemDto.setCfs(obj[3] != null ? String.valueOf(obj[3]) : null);
			itemDto.setSupplierName(
					obj[4] != null ? String.valueOf(obj[4]) : null);
			itemDto.setStateName(
					obj[5] != null ? String.valueOf(obj[5]) : null);
			itemDto.setDocNum(obj[6] != null ? String.valueOf(obj[6]) : null);
			itemDto.setDocDate(obj[7] != null ? String.valueOf(obj[7]) : null);
			itemDto.setInvNum(obj[8] != null ? String.valueOf(obj[8]) : null);
			itemDto.setInvDate(obj[9] != null ? String.valueOf(obj[9]) : null);
			itemDto.setPos(obj[10] != null ? String.valueOf(obj[10]) : null);
			itemDto.setRchrg(obj[11] != null ? String.valueOf(obj[11]) : null);
			itemDto.setInvType(
					obj[12] != null ? String.valueOf(obj[12]) : null);
			itemDto.setDiffPercent(
					obj[13] != null ? new BigDecimal(String.valueOf(obj[13]))
							: BigDecimal.ZERO);
			itemDto.setOrgInvNum(
					obj[14] != null ? String.valueOf(obj[14]) : null);
			itemDto.setOrgInvDate(
					obj[15] != null ? String.valueOf(obj[15]) : null);
			itemDto.setItemNumber(
					obj[16] != null ? String.valueOf(obj[16]) : null);
			itemDto.setCrdrPreGst(
					obj[17] != null ? String.valueOf(obj[17]) : null);
			itemDto.setItcEntitlement(
					obj[18] != null ? String.valueOf(obj[18]) : null);

			itemDto.setTaxableValue(
					obj[19] != null ? new BigDecimal(String.valueOf(obj[19]))
							: BigDecimal.ZERO);
			itemDto.setTaxRate(
					obj[20] != null ? new BigDecimal(String.valueOf(obj[20]))
							: BigDecimal.ZERO);
			itemDto.setIgstAmt(
					obj[21] != null ? new BigDecimal(String.valueOf(obj[21]))
							: BigDecimal.ZERO);
			itemDto.setCgstAmt(
					obj[22] != null ? new BigDecimal(String.valueOf(obj[22]))
							: BigDecimal.ZERO);

			itemDto.setSgstAmt(
					obj[23] != null ? new BigDecimal(String.valueOf(obj[23]))
							: BigDecimal.ZERO);

			itemDto.setCessAmt(
					obj[24] != null ? new BigDecimal(String.valueOf(obj[24]))
							: BigDecimal.ZERO);
			itemDto.setInvVal(
					obj[25] != null ? new BigDecimal(String.valueOf(obj[25]))
							: BigDecimal.ZERO);
			itemDto.setDocKey(obj[26] != null ? String.valueOf(obj[26]) : null);
			itemDto.setSection(
					obj[27] != null ? String.valueOf(obj[27]) : null);
			itemDtoChunck.add(itemDto);
		}
		return itemDtoChunck;
	}
}
