package com.ey.advisory.app.services.savetogstn.jobs.gstr1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.B2CSInvoices;
import com.ey.advisory.app.docs.dto.Ecom;
import com.ey.advisory.app.docs.dto.SaveBatchProcessDto;
import com.ey.advisory.app.docs.dto.SaveGstr1;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.api.APIConstants;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Siva.Reddy
 *
 */
@Slf4j
@Service("RateDataToEcomSupSummConverter")
public class RateDataToEcomSupSummConverter
		implements RateDataToGstr1Converter {

	@Override
	public SaveBatchProcessDto convertToGstr1Object(List<Object[]> objects,
			String section, String groupCode, String taxDocType, int chunkSize) {
		SaveBatchProcessDto batchDto = new SaveBatchProcessDto();
		List<SaveGstr1> batchesList = new ArrayList<>();
		List<List<Long>> batchIdsList = new ArrayList<>();
		Ecom supEcomDto = new Ecom();
		List<B2CSInvoices> b2cList = new ArrayList<>();
		List<B2CSInvoices> urp2cList = new ArrayList<>();
		try {
			if (objects != null && !objects.isEmpty()) {
				int totSize = objects.size();
				List<Long> idsList = new ArrayList<>();
				SaveGstr1 gstr1 = null;
				for (int counter = 0; counter < totSize; counter++) {
					List<B2CSInvoices> b2cInvoiceList = new ArrayList<>();
					B2CSInvoices b2cInvoices = new B2CSInvoices();
					Object[] resultSet = objects.get(counter);
					// first object[]
					Long id = resultSet[0] != null ? (Long) resultSet[0] : null;
					String sGstin = resultSet[1] != null
							? String.valueOf(resultSet[1]) : null;
					String txPriod = resultSet[2] != null
							? String.valueOf(resultSet[2]) : null;
					String tableSection = resultSet[4] != null
							? String.valueOf(resultSet[4]) : null;
					String ecomGstin = resultSet[5] != null
							? String.valueOf(resultSet[5]) : null;

					BigDecimal taxable = resultSet[6] != null
							? new BigDecimal(String.valueOf(resultSet[6]))
							: BigDecimal.ZERO;
					BigDecimal igst = resultSet[7] != null
							? new BigDecimal(String.valueOf(resultSet[7]))
							: BigDecimal.ZERO;
					BigDecimal cgst = resultSet[8] != null
							? new BigDecimal(String.valueOf(resultSet[8]))
							: BigDecimal.ZERO;
					BigDecimal sgst = resultSet[9] != null
							? new BigDecimal(String.valueOf(resultSet[9]))
							: BigDecimal.ZERO;
					BigDecimal cess = resultSet[10] != null
							? new BigDecimal(String.valueOf(resultSet[10]))
							: BigDecimal.ZERO;

					String pos = resultSet[11] != null
							? String.valueOf(resultSet[11]) : null;

					BigDecimal taxRate = resultSet[12] != null
							? new BigDecimal(String.valueOf(resultSet[12]))
							: BigDecimal.ZERO;

					b2cInvoices.setStin(ecomGstin);
					b2cInvoices.setTaxableValue(taxable);
					b2cInvoices.setIgstAmount(igst);
					b2cInvoices.setSgstAmount(sgst);
					b2cInvoices.setCgstAmount(cgst);
					b2cInvoices.setCessAmount(cess);
					b2cInvoices.setInvoiceStatus(APIConstants.N);

					b2cInvoices.setRate(taxRate);
					b2cInvoices.setPointOfSupply(pos);

					if (sGstin != null
							&& !sGstin.substring(0, 2).equalsIgnoreCase(pos)) {
						b2cInvoices.setSupplyType(APIConstants.SUP_TYPE_INTER);
					} else {
						b2cInvoices.setSupplyType(APIConstants.SUP_TYPE_INTRA);
					}

					idsList.add(id);
					b2cInvoiceList.add(b2cInvoices);
					if (GSTConstants.GSTR1_15II
							.equalsIgnoreCase(tableSection)) {
						b2cList.addAll(b2cInvoiceList);
					} else if (GSTConstants.GSTR1_15IV
							.equalsIgnoreCase(tableSection)) {
						urp2cList.addAll(b2cInvoiceList);
					}

					if (gstr1 == null) {
						gstr1 = new SaveGstr1();
						gstr1.setSgstin(sGstin);
						gstr1.setTaxperiod(txPriod);
					}
				}
				if (!b2cList.isEmpty()) {
					supEcomDto.setB2c(b2cList);
				}
				if (!urp2cList.isEmpty()) {
					supEcomDto.setUrp2c(urp2cList);
				}
				gstr1.setEcom(supEcomDto);
				batchesList.add(gstr1);
				batchIdsList.add(idsList);
				idsList = new ArrayList<>();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("SaveGstr1 Dto {} ", gstr1);
				}
			} else {
				String msg = "Zero eligible documents found to do Save to Gstn";
				LOGGER.warn(msg, objects);
			}
		} catch (Exception ex) {
			String msg = "Unexpected error while saving documents to GSTN";
			LOGGER.error(msg, ex);
			throw new AppException(msg, ex);
		}
		batchDto.setGstr1(batchesList);
		batchDto.setIdsList(batchIdsList);
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("batch Dto {} ", batchDto);
		}
		return batchDto;
	}
}
