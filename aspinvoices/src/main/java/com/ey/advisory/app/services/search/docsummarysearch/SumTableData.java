package com.ey.advisory.app.services.search.docsummarysearch;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.simplified.Annexure1SummarySectionDto;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Component("sumTableData")
public class SumTableData {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(SumTableData.class);

	@Autowired
	@Qualifier("summaryTotalCalculation")
	private SummaryTotalCalculation summaryTotalCalculation;

	// Summ Two Table Data
	public List<Annexure1SummarySectionDto> totalInImpsCount(
			List<Annexure1SummarySectionDto> iList) {

		LOGGER.debug(" Counting Total for Imps Section");
		List<Annexure1SummarySectionDto> slfList = new ArrayList<>();

		List<Annexure1SummarySectionDto> slf = iList.stream()
				.filter(p -> "SLF".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		if (slf != null && slf.size() > 0) {
			slfList.addAll(slf);
		}

		int slfCount = 0;
		BigDecimal slfInvoive = BigDecimal.ZERO;
		BigDecimal slfTaxable = BigDecimal.ZERO;
		BigDecimal slfTaxPayble = BigDecimal.ZERO;
		BigDecimal slfIgst = BigDecimal.ZERO;
		BigDecimal slfSgst = BigDecimal.ZERO;
		BigDecimal slfCgst = BigDecimal.ZERO;
		BigDecimal slfCess = BigDecimal.ZERO;
		String table = null;
		String slfDocType = null;
		for (Annexure1SummarySectionDto slfAdd : slfList) {

			if (slfAdd.getRecords() != null) {
				slfCount = slfCount + (slfAdd.getRecords());
			}

			if (slfAdd.getTableSection() != null) {
				table = slfAdd.getTableSection();
			}

			if (slfAdd.getInvValue() != null) {
				slfInvoive = slfInvoive.add(slfAdd.getInvValue());
			}
			if (slfAdd.getTaxableValue() != null) {
				slfTaxable = slfTaxable.add(slfAdd.getTaxableValue());
			}
			if (slfAdd.getTaxPayble() != null) {
				slfTaxPayble = slfTaxPayble.add(slfAdd.getTaxPayble());
			}
			if (slfAdd.getIgst() != null) {
				slfIgst = slfIgst.add(slfAdd.getIgst());
			}
			if (slfAdd.getSgst() != null) {
				slfSgst = slfSgst.add(slfAdd.getSgst());
			}
			if (slfAdd.getCgst() != null) {
				slfCgst = slfCgst.add(slfAdd.getCgst());
			}
			if (slfAdd.getCess() != null) {
				slfCess = slfCess.add(slfAdd.getCess());
			}
			if (slfAdd.getDocType() != null) {
				slfDocType = slfAdd.getDocType();
			}
		}
		Annexure1SummarySectionDto finalSlfTotal = new Annexure1SummarySectionDto();
		finalSlfTotal.setRecords(slfCount);
		finalSlfTotal.setInvValue(slfInvoive);
		finalSlfTotal.setTaxableValue(slfTaxable);
		finalSlfTotal.setTaxPayble(slfTaxPayble);
		finalSlfTotal.setIgst(slfIgst);
		finalSlfTotal.setSgst(slfSgst);
		finalSlfTotal.setCgst(slfCgst);
		finalSlfTotal.setCess(slfCess);
		finalSlfTotal.setTableSection(table);
		finalSlfTotal.setDocType(slfDocType);

		List<Annexure1SummarySectionDto> totalList = new ArrayList<>();
		totalList.add(finalSlfTotal);

		return totalList;

	}

	public List<Annexure1SummarySectionDto> totalOutwardList(
			List<Annexure1SummarySectionDto> _3aList) {

		List<Annexure1SummarySectionDto> totalList = new ArrayList<>();
		List<Annexure1SummarySectionDto> invList = new ArrayList<>();

		LOGGER.debug(" Aggregating Raw and Vertical Uploads for b2c ");
		List<Annexure1SummarySectionDto> inv = _3aList.stream()
				.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		if (inv != null && inv.size() > 0) {
			invList.addAll(inv);
		}

		List<Annexure1SummarySectionDto> crList = new ArrayList<>();

		List<Annexure1SummarySectionDto> cr = _3aList.stream()
				.filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());
		if (cr != null) {
			crList.addAll(cr);
		}

		List<Annexure1SummarySectionDto> drList = new ArrayList<>();
		List<Annexure1SummarySectionDto> dr = _3aList.stream()
				.filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		if (dr != null && dr.size() > 0) {
			drList.addAll(dr);
		}
		int invCount = 0;
		BigDecimal invInvoive = BigDecimal.ZERO;
		BigDecimal invTaxable = BigDecimal.ZERO;
		BigDecimal invTaxPayble = BigDecimal.ZERO;
		BigDecimal invIgst = BigDecimal.ZERO;
		BigDecimal invSgst = BigDecimal.ZERO;
		BigDecimal invCgst = BigDecimal.ZERO;
		BigDecimal invCess = BigDecimal.ZERO;
		String table = null;
		String invDocType = null;
		if (invList != null && invList.size() > 0) {
			for (Annexure1SummarySectionDto invAdd : invList) {

				if (invAdd.getRecords() != null) {
					invCount = invCount + (invAdd.getRecords());
				}

				if (invAdd.getTableSection() != null) {
					table = invAdd.getTableSection();
				}

				if (invAdd.getInvValue() != null) {
					invInvoive = invInvoive.add(invAdd.getInvValue());
				}
				if (invAdd.getTaxableValue() != null) {
					invTaxable = invTaxable.add(invAdd.getTaxableValue());
				}
				if (invAdd.getTaxPayble() != null) {
					invTaxPayble = invTaxPayble.add(invAdd.getTaxPayble());
				}
				if (invAdd.getIgst() != null) {
					invIgst = invIgst.add(invAdd.getIgst());
				}
				if (invAdd.getSgst() != null) {
					invSgst = invSgst.add(invAdd.getSgst());
				}
				if (invAdd.getCgst() != null) {
					invCgst = invCgst.add(invAdd.getCgst());
				}
				if (invAdd.getCess() != null) {
					invCess = invCess.add(invAdd.getCess());
				}
				if (invAdd.getDocType() != null) {
					invDocType = invAdd.getDocType();
				}
			}
		}
		Annexure1SummarySectionDto finalInvTotal = new Annexure1SummarySectionDto();
		finalInvTotal.setRecords(invCount);
		finalInvTotal.setInvValue(invInvoive);
		finalInvTotal.setTaxableValue(invTaxable);
		finalInvTotal.setTaxPayble(invTaxPayble);
		finalInvTotal.setIgst(invIgst);
		finalInvTotal.setSgst(invSgst);
		finalInvTotal.setCgst(invCgst);
		finalInvTotal.setCess(invCess);
		finalInvTotal.setTableSection(table);
		finalInvTotal.setDocType(invDocType);

		totalList.add(finalInvTotal);

		int crCount = 0;
		BigDecimal crInvoive = BigDecimal.ZERO;
		BigDecimal crTaxable = BigDecimal.ZERO;
		BigDecimal crTaxPayble = BigDecimal.ZERO;
		BigDecimal crIgst = BigDecimal.ZERO;
		BigDecimal crSgst = BigDecimal.ZERO;
		BigDecimal crCgst = BigDecimal.ZERO;
		BigDecimal crCess = BigDecimal.ZERO;
		String crDocType = null;
		if (crList != null && crList.size() > 0) {
			for (Annexure1SummarySectionDto crAdd : crList) {

				if (crAdd.getRecords() != null) {
					crCount = crCount + (crAdd.getRecords());
				}

				if (crAdd.getTableSection() != null) {
					table = crAdd.getTableSection();
				}
				if (crAdd.getInvValue() != null) {
					crInvoive = crInvoive.add(crAdd.getInvValue());
				}
				if (crAdd.getTaxableValue() != null) {
					crTaxable = crTaxable.add(crAdd.getTaxableValue());
				}
				if (crAdd.getTaxPayble() != null) {
					crTaxPayble = crTaxPayble.add(crAdd.getTaxPayble());
				}
				if (crAdd.getIgst() != null) {
					crIgst = crIgst.add(crAdd.getIgst());
				}
				if (crAdd.getSgst() != null) {
					crSgst = crSgst.add(crAdd.getSgst());
				}
				if (crAdd.getCgst() != null) {
					crCgst = crCgst.add(crAdd.getCgst());
				}
				if (crAdd.getCess() != null) {
					crCess = crCess.add(crAdd.getCess());
				}
				if (crAdd.getDocType() != null) {
					crDocType = crAdd.getDocType();
				}
			}
		}
		Annexure1SummarySectionDto finalCrTotal = new Annexure1SummarySectionDto();
		finalCrTotal.setRecords(crCount);
		finalCrTotal.setInvValue(crInvoive);
		finalCrTotal.setTaxableValue(crTaxable);
		finalCrTotal.setTaxPayble(crTaxPayble);
		finalCrTotal.setIgst(crIgst);
		finalCrTotal.setSgst(crSgst);
		finalCrTotal.setCgst(crCgst);
		finalCrTotal.setCess(crCess);
		finalCrTotal.setTableSection(table);
		finalCrTotal.setDocType(crDocType);
		totalList.add(finalCrTotal);

		int drCount = 0;
		BigDecimal drInvoive = BigDecimal.ZERO;
		BigDecimal drTaxable = BigDecimal.ZERO;
		BigDecimal drTaxPayble = BigDecimal.ZERO;
		BigDecimal drIgst = BigDecimal.ZERO;
		BigDecimal drSgst = BigDecimal.ZERO;
		BigDecimal drCgst = BigDecimal.ZERO;
		BigDecimal drCess = BigDecimal.ZERO;
		String drDocType = null;
		if (drList != null && drList.size() > 0) {
			for (Annexure1SummarySectionDto drAdd : drList) {

				if (drAdd.getRecords() != null) {
					drCount = drCount + (drAdd.getRecords());
				}

				if (drAdd.getTableSection() != null) {
					table = drAdd.getTableSection();
				}

				if (drAdd.getInvValue() != null) {
					drInvoive = drInvoive.add(drAdd.getInvValue());
				}
				if (drAdd.getTaxableValue() != null) {
					drTaxable = drTaxable.add(drAdd.getTaxableValue());
				}
				if (drAdd.getTaxPayble() != null) {
					drTaxPayble = drTaxPayble.add(drAdd.getTaxPayble());
				}
				if (drAdd.getIgst() != null) {
					drIgst = drIgst.add(drAdd.getIgst());
				}
				if (drAdd.getSgst() != null) {
					drSgst = drSgst.add(drAdd.getSgst());
				}
				if (drAdd.getCgst() != null) {
					drCgst = drCgst.add(drAdd.getCgst());
				}
				if (drAdd.getCess() != null) {
					drCess = drCess.add(drAdd.getCess());
				}
				if (drAdd.getDocType() != null) {
					drDocType = drAdd.getDocType();
				}
			}
		}
		Annexure1SummarySectionDto finalDrTotal = new Annexure1SummarySectionDto();
		finalDrTotal.setRecords(drCount);
		finalDrTotal.setInvValue(drInvoive);
		finalDrTotal.setTaxableValue(drTaxable);
		finalDrTotal.setTaxPayble(drTaxPayble);
		finalDrTotal.setIgst(drIgst);
		finalDrTotal.setSgst(drSgst);
		finalDrTotal.setCgst(drCgst);
		finalDrTotal.setCess(drCess);
		finalDrTotal.setTableSection(table);
		finalDrTotal.setDocType(drDocType);
		totalList.add(finalDrTotal);

		return totalList;
	}

	// Rev Charge

	public List<Annexure1SummarySectionDto> totalRevChList(
			List<Annexure1SummarySectionDto> _3aList) {
		List<Annexure1SummarySectionDto> totalList = new ArrayList<>();
		List<Annexure1SummarySectionDto> slfList = new ArrayList<>();

		Annexure1SummarySectionDto slflevel = addingSlfInv(_3aList);
		if (slflevel != null) {
			slfList.add(slflevel);
		}

		List<Annexure1SummarySectionDto> crList = new ArrayList<>();

		List<Annexure1SummarySectionDto> cr = _3aList.stream()
				.filter(p -> "CR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		if (cr != null && cr.size() > 0) {
			crList.addAll(cr);
		}
		List<Annexure1SummarySectionDto> drList = new ArrayList<>();

		List<Annexure1SummarySectionDto> dr = _3aList.stream()
				.filter(p -> "DR".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		if (dr != null && dr.size() > 0) {
			drList.addAll(dr);
		}

		int slfCount = 0;
		BigDecimal slfInvoive = BigDecimal.ZERO;
		BigDecimal slfTaxable = BigDecimal.ZERO;
		BigDecimal slfTaxPayble = BigDecimal.ZERO;
		BigDecimal slfIgst = BigDecimal.ZERO;
		BigDecimal slfSgst = BigDecimal.ZERO;
		BigDecimal slfCgst = BigDecimal.ZERO;
		BigDecimal slfCess = BigDecimal.ZERO;
		String table = null;
		String slfDocType = null;
		if (slfList != null && slfList.size() > 0) {
			for (Annexure1SummarySectionDto slfAdd : slfList) {

				if (slfAdd.getRecords() != null) {
					slfCount = slfCount + (slfAdd.getRecords());
				}
				if (slfAdd.getTableSection() != null) {
					table = slfAdd.getTableSection();
				}

				if (slfAdd.getInvValue() != null) {
					slfInvoive = slfInvoive.add(slfAdd.getInvValue());
				}
				if (slfAdd.getTaxableValue() != null) {
					slfTaxable = slfTaxable.add(slfAdd.getTaxableValue());
				}
				if (slfAdd.getTaxPayble() != null) {
					slfTaxPayble = slfTaxPayble.add(slfAdd.getTaxPayble());
				}
				if (slfAdd.getIgst() != null) {
					slfIgst = slfIgst.add(slfAdd.getIgst());
				}
				if (slfAdd.getSgst() != null) {
					slfSgst = slfSgst.add(slfAdd.getSgst());
				}
				if (slfAdd.getCgst() != null) {
					slfCgst = slfCgst.add(slfAdd.getCgst());
				}
				if (slfAdd.getCess() != null) {
					slfCess = slfCess.add(slfAdd.getCess());
				}
				if (slfAdd.getDocType() != null) {
					slfDocType = slfAdd.getDocType();
				}
			}
		}
		Annexure1SummarySectionDto finalInvTotal = new Annexure1SummarySectionDto();
		finalInvTotal.setRecords(slfCount);
		finalInvTotal.setInvValue(slfInvoive);
		finalInvTotal.setTaxableValue(slfTaxable);
		finalInvTotal.setTaxPayble(slfTaxPayble);
		finalInvTotal.setIgst(slfIgst);
		finalInvTotal.setSgst(slfSgst);
		finalInvTotal.setCgst(slfCgst);
		finalInvTotal.setCess(slfCess);
		finalInvTotal.setTableSection(table);
		finalInvTotal.setDocType(slfDocType);

		totalList.add(finalInvTotal);

		int crCount = 0;
		BigDecimal crInvoive = BigDecimal.ZERO;
		BigDecimal crTaxable = BigDecimal.ZERO;
		BigDecimal crTaxPayble = BigDecimal.ZERO;
		BigDecimal crIgst = BigDecimal.ZERO;
		BigDecimal crSgst = BigDecimal.ZERO;
		BigDecimal crCgst = BigDecimal.ZERO;
		BigDecimal crCess = BigDecimal.ZERO;
		String crDocType = null;
		if (crList != null && crList.size() > 0) {
			for (Annexure1SummarySectionDto crAdd : crList) {

				if (crAdd.getRecords() != null) {
					crCount = crCount + (crAdd.getRecords());
				}

				if (crAdd.getTableSection() != null) {
					table = crAdd.getTableSection();
				}
				if (crAdd.getInvValue() != null) {
					crInvoive = crInvoive.add(crAdd.getInvValue());
				}
				if (crAdd.getTaxableValue() != null) {
					crTaxable = crTaxable.add(crAdd.getTaxableValue());
				}
				if (crAdd.getTaxPayble() != null) {
					crTaxPayble = crTaxPayble.add(crAdd.getTaxPayble());
				}
				if (crAdd.getIgst() != null) {
					crIgst = crIgst.add(crAdd.getIgst());
				}
				if (crAdd.getSgst() != null) {
					crSgst = crSgst.add(crAdd.getSgst());
				}
				if (crAdd.getCgst() != null) {
					crCgst = crCgst.add(crAdd.getCgst());
				}
				if (crAdd.getCess() != null) {
					crCess = crCess.add(crAdd.getCess());
				}
				if (crAdd.getDocType() != null) {
					crDocType = crAdd.getDocType();
				}
			}
		}
		Annexure1SummarySectionDto finalCrTotal = new Annexure1SummarySectionDto();
		finalCrTotal.setRecords(crCount);
		finalCrTotal.setInvValue(crInvoive);
		finalCrTotal.setTaxableValue(crTaxable);
		finalCrTotal.setTaxPayble(crTaxPayble);
		finalCrTotal.setIgst(crIgst);
		finalCrTotal.setSgst(crSgst);
		finalCrTotal.setCgst(crCgst);
		finalCrTotal.setCess(crCess);
		finalCrTotal.setTableSection(table);
		finalCrTotal.setDocType(crDocType);
		totalList.add(finalCrTotal);

		int drCount = 0;
		BigDecimal drInvoive = BigDecimal.ZERO;
		BigDecimal drTaxable = BigDecimal.ZERO;
		BigDecimal drTaxPayble = BigDecimal.ZERO;
		BigDecimal drIgst = BigDecimal.ZERO;
		BigDecimal drSgst = BigDecimal.ZERO;
		BigDecimal drCgst = BigDecimal.ZERO;
		BigDecimal drCess = BigDecimal.ZERO;
		String drDocType = null;
		if (drList != null && drList.size() > 0) {
			for (Annexure1SummarySectionDto drAdd : drList) {

				if (drAdd.getRecords() != null) {
					drCount = drCount + (drAdd.getRecords());
				}

				if (drAdd.getTableSection() != null) {
					table = drAdd.getTableSection();
				}

				if (drAdd.getInvValue() != null) {
					drInvoive = drInvoive.add(drAdd.getInvValue());
				}
				if (drAdd.getTaxableValue() != null) {
					drTaxable = drTaxable.add(drAdd.getTaxableValue());
				}
				if (drAdd.getTaxPayble() != null) {
					drTaxPayble = drTaxPayble.add(drAdd.getTaxPayble());
				}
				if (drAdd.getIgst() != null) {
					drIgst = drIgst.add(drAdd.getIgst());
				}
				if (drAdd.getSgst() != null) {
					drSgst = drSgst.add(drAdd.getSgst());
				}
				if (drAdd.getCgst() != null) {
					drCgst = drCgst.add(drAdd.getCgst());
				}
				if (drAdd.getCess() != null) {
					drCess = drCess.add(drAdd.getCess());
				}
				if (drAdd.getDocType() != null) {
					drDocType = drAdd.getDocType();
				}
			}
		}
		Annexure1SummarySectionDto finalDrTotal = new Annexure1SummarySectionDto();
		finalDrTotal.setRecords(drCount);
		finalDrTotal.setInvValue(drInvoive);
		finalDrTotal.setTaxableValue(drTaxable);
		finalDrTotal.setTaxPayble(drTaxPayble);
		finalDrTotal.setIgst(drIgst);
		finalDrTotal.setSgst(drSgst);
		finalDrTotal.setCgst(drCgst);
		finalDrTotal.setCess(drCess);
		finalDrTotal.setTableSection(table);
		finalDrTotal.setDocType(drDocType);
		totalList.add(finalDrTotal);

		return totalList;
	}

	public Annexure1SummarySectionDto addingSlfInv(
			List<Annexure1SummarySectionDto> _3aList) {

		List<Annexure1SummarySectionDto> slf = _3aList.stream()
				.filter(p -> "SLF".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		List<Annexure1SummarySectionDto> inv = _3aList.stream()
				.filter(p -> "INV".equalsIgnoreCase(p.getDocType()))
				.collect(Collectors.toList());

		List<Annexure1SummarySectionDto> addSlfInv = new ArrayList<>();
		addSlfInv.addAll(slf);
		addSlfInv.addAll(inv);

		int records = 0;
		BigDecimal taxable = new BigDecimal("0");
		BigDecimal taxpay = new BigDecimal("0");
		BigDecimal invValue = new BigDecimal("0");
		BigDecimal igst = new BigDecimal("0");
		BigDecimal sgst = new BigDecimal("0");
		BigDecimal cgst = new BigDecimal("0");
		BigDecimal cess = new BigDecimal("0");
		String table = null;

		if (addSlfInv != null && addSlfInv.size() > 0) {
			for (Annexure1SummarySectionDto sum : addSlfInv) {

				taxable = taxable.add((sum.getTaxableValue()) == null
						? BigDecimal.ZERO : (sum.getTaxableValue()));
				taxpay = taxpay.add((sum.getTaxPayble()) == null
						? BigDecimal.ZERO : (sum.getTaxPayble()));
				records = records
						+ (sum.getRecords() == null ? 0 : sum.getRecords());
				invValue = invValue.add((sum.getInvValue()) == null
						? BigDecimal.ZERO : (sum.getInvValue()));
				igst = igst.add((sum.getIgst()) == null ? BigDecimal.ZERO
						: (sum.getIgst()));
				sgst = sgst.add((sum.getSgst()) == null ? BigDecimal.ZERO
						: (sum.getSgst()));
				cgst = cgst.add((sum.getCgst()) == null ? BigDecimal.ZERO
						: (sum.getCgst()));
				cess = cess.add((sum.getCess()) == null ? BigDecimal.ZERO
						: (sum.getCess()));
				table = sum.getTableSection();

			}
		}

		Annexure1SummarySectionDto summ = new Annexure1SummarySectionDto();
		summ.setTaxableValue(taxable);
		summ.setTaxPayble(taxpay);
		summ.setInvValue(invValue);
		summ.setIgst(igst);
		summ.setSgst(sgst);
		summ.setCgst(cgst);
		summ.setCess(cess);
		summ.setTableSection(table);
		summ.setRecords(records);
		summ.setDocType("SLF");
		return summ;

	}

	private BigDecimal addRev(BigDecimal a, BigDecimal b) {

		BigDecimal a1 = (a == null) ? BigDecimal.ZERO : a;
		BigDecimal b1 = (b == null) ? BigDecimal.ZERO : b;

		return (a1.add(b1));

	}

}
