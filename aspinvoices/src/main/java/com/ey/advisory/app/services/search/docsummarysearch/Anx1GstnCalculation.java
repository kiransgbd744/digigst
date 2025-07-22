package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicEcomSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1BasicSummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummaryDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;
import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionEcomDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Anx1GstnCalculation")
public class Anx1GstnCalculation {

	public Annexure1SummarySectionDto addGstnB2cData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto b2c : gstnResult) {
			Annexure1BasicSummaryDto b2cGstn = b2c.getB2c();

			gstnSummary = b2cGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnB2BData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto b2b : gstnResult) {
			Annexure1BasicSummaryDto b2bGstn = b2b.getB2b();

			gstnSummary = b2bGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);

		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnExpData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto exp : gstnResult) {
			Annexure1BasicSummaryDto exptGstn = exp.getExpt();

			gstnSummary = exptGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnExpwtData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto expwt : gstnResult) {
			Annexure1BasicSummaryDto expwtGstn = expwt.getExpwt();

			gstnSummary = expwtGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnSeztData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto sezt : gstnResult) {
			Annexure1BasicSummaryDto seztGstn = sezt.getSezt();

			gstnSummary = seztGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnSezwtData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto sezt : gstnResult) {
			Annexure1BasicSummaryDto sezwtGstn = sezt.getSezwt();

			gstnSummary = sezwtGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnDEData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto de : gstnResult) {
			Annexure1BasicSummaryDto deGstn = de.getDeemedExp();

			gstnSummary = deGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnImpgData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto impg : gstnResult) {
			Annexure1BasicSummaryDto impgGstn = impg.getImpg();

			gstnSummary = impgGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnImpsData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto impgSez : gstnResult) {
			Annexure1BasicSummaryDto impgSezGstn = impgSez.getImps();

			gstnSummary = impgSezGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnImpgSezData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto impgSez : gstnResult) {
			Annexure1BasicSummaryDto impgSezGstn = impgSez.getImpgSez();

			gstnSummary = impgSezGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionDto addGstnRevData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionDto addGstnData = new Annexure1SummarySectionDto();
		List<Annexure1SummarySectionDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto rev : gstnResult) {
			Annexure1BasicSummaryDto revGstn = rev.getRev();

			gstnSummary = revGstn.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1GstnData(gstnSummary);
		}
		return addGstnData;

	}

	public Annexure1SummarySectionEcomDto addGstnEcomData(
			List<? extends Annexure1SummaryDto> gstnResult) {

		Annexure1SummarySectionEcomDto addGstnData = new Annexure1SummarySectionEcomDto();
		List<Annexure1SummarySectionEcomDto> gstnSummary = new ArrayList<>();
		for (Annexure1SummaryDto ecom : gstnResult) {
			Annexure1BasicEcomSummaryDto table4 = ecom.getTable4();

			gstnSummary = table4.getGstnSummary();

		}
		if (gstnSummary != null && gstnSummary.size() > 0) {
			addGstnData = addAnx1EcomGstnData(gstnSummary);
		}
		return addGstnData;

	}

	private Annexure1SummarySectionDto addAnx1GstnData(
			List<Annexure1SummarySectionDto> gstnSummary) {

		Annexure1SummarySectionDto sumDto = new Annexure1SummarySectionDto();
		BigDecimal taxableValue = BigDecimal.ZERO;
		BigDecimal invValue = BigDecimal.ZERO;
		BigDecimal taxPayable = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal cess = BigDecimal.ZERO;
		Integer records = 0;
		if (gstnSummary != null) {
			for (Annexure1SummarySectionDto dto : gstnSummary) {

				taxableValue = taxableValue.add((dto.getTaxableValue() == null)
						? BigDecimal.ZERO : dto.getTaxableValue());
				invValue = invValue.add((dto.getInvValue() == null)
						? BigDecimal.ZERO : dto.getInvValue());
				taxPayable = taxPayable.add((dto.getTaxPayble() == null)
						? BigDecimal.ZERO : dto.getTaxPayble());
				igst = igst.add((dto.getIgst() == null) ? BigDecimal.ZERO
						: dto.getIgst());
				sgst = sgst.add((dto.getSgst() == null) ? BigDecimal.ZERO
						: dto.getSgst());
				cgst = cgst.add((dto.getCgst() == null) ? BigDecimal.ZERO
						: dto.getCgst());
				cess = cess.add((dto.getCess() == null) ? BigDecimal.ZERO
						: dto.getCess());
				records = records
						+ (dto.getRecords() == null ? 0 : dto.getRecords());
				sumDto.setRecords(records);
				sumDto.setInvValue(invValue);
				sumDto.setTaxableValue(taxableValue);
				sumDto.setTaxPayble(taxPayable);
				sumDto.setIgst(igst);
				sumDto.setSgst(sgst);
				sumDto.setCgst(cgst);
				sumDto.setCess(cess);

			}
		}
		return sumDto;

	}

	private Annexure1SummarySectionEcomDto addAnx1EcomGstnData(
			List<Annexure1SummarySectionEcomDto> gstnSummary) {

		Annexure1SummarySectionEcomDto sumDto = new Annexure1SummarySectionEcomDto();
		BigDecimal supplyMade = BigDecimal.ZERO;
		BigDecimal supplyReturn = BigDecimal.ZERO;
		BigDecimal taxPayable = BigDecimal.ZERO;
		BigDecimal igst = BigDecimal.ZERO;
		BigDecimal sgst = BigDecimal.ZERO;
		BigDecimal cgst = BigDecimal.ZERO;
		BigDecimal cess = BigDecimal.ZERO;
		Integer records = 0;
		if (gstnSummary != null) {
			for (Annexure1SummarySectionEcomDto dto : gstnSummary) {

				supplyMade = supplyMade.add((dto.getSupplyMade() == null)
						? BigDecimal.ZERO : dto.getSupplyMade());
				supplyReturn = supplyReturn.add((dto.getSupplyReturn() == null)
						? BigDecimal.ZERO : dto.getSupplyReturn());
				taxPayable = taxPayable.add((dto.getTaxPayble() == null)
						? BigDecimal.ZERO : dto.getTaxPayble());
				igst = igst.add((dto.getIgst() == null) ? BigDecimal.ZERO
						: dto.getIgst());
				sgst = sgst.add((dto.getSgst() == null) ? BigDecimal.ZERO
						: dto.getSgst());
				cgst = cgst.add((dto.getCgst() == null) ? BigDecimal.ZERO
						: dto.getCgst());
				cess = cess.add((dto.getCess() == null) ? BigDecimal.ZERO
						: dto.getCess());
				records = records
						+ (dto.getRecords() == null ? 0 : dto.getRecords());
				sumDto.setRecords(records);

				sumDto.setTaxPayble(taxPayable);
				sumDto.setIgst(igst);
				sumDto.setSgst(sgst);
				sumDto.setCgst(cgst);
				sumDto.setCess(cess);
				sumDto.setSupplyMade(supplyMade);
				sumDto.setSupplyReturn(supplyReturn);

			}
		}
		return sumDto;

	}

}
