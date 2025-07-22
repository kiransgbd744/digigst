package com.ey.advisory.app.asprecon.gstr2.initiaterecon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.ReconEntity;

/**
 * 
 * @author vishal.verma
 *
 */

@Service("Gstr2InitiateReconService")
public class Gstr2InitiateReconService {

	@Autowired
	@Qualifier("Gstr2InitiateReconDaoImpl")
	Gstr2InitiateReconDao initiateReconDao;

	public List<Gstr2InitiateReconLineItemDto> find(Gstr2InitiateReconDto dto) {

		Gstr2InitiateReconDto req = (Gstr2InitiateReconDto) dto;

		List<ReconEntity> entityResponse = initiateReconDao
				.gstr2InitiateRecon(req);

		List<Gstr2InitiateReconLineItemDto> sectionFileList = new ArrayList<>();

		Gstr2InitiateReconLineItemDto total = new Gstr2InitiateReconLineItemDto();

		Gstr2InitiateReconLineItemDto critem = new Gstr2InitiateReconLineItemDto();
		Gstr2InitiateReconLineItemDto dritem = new Gstr2InitiateReconLineItemDto();
		Gstr2InitiateReconLineItemDto inv = new Gstr2InitiateReconLineItemDto();

		Gstr2InitiateReconLineItemDto crAItem = new Gstr2InitiateReconLineItemDto();
		Gstr2InitiateReconLineItemDto drAItem = new Gstr2InitiateReconLineItemDto();
		Gstr2InitiateReconLineItemDto invA = new Gstr2InitiateReconLineItemDto();
		
		Gstr2InitiateReconLineItemDto impg = new Gstr2InitiateReconLineItemDto();
		Gstr2InitiateReconLineItemDto impgSez = new Gstr2InitiateReconLineItemDto();
		
		Gstr2InitiateReconLineItemDto isd = new Gstr2InitiateReconLineItemDto();
		Gstr2InitiateReconLineItemDto isdA = new Gstr2InitiateReconLineItemDto();

		Gstr2InitiateReconLineItemDto fmResp = new Gstr2InitiateReconLineItemDto();
		Gstr2InitiateReconLineItemDto grandTotal = new Gstr2InitiateReconLineItemDto();
		
		

		Integer a2totalRec_Count = 0;
		BigDecimal a2taxablevalue = BigDecimal.ZERO;
		BigDecimal a2totigst = BigDecimal.ZERO;
		BigDecimal a2totcgst = BigDecimal.ZERO;
		BigDecimal a2totsgst = BigDecimal.ZERO;
		BigDecimal a2totcess = BigDecimal.ZERO;
		Integer prtotalRec_Count = 0;
		BigDecimal prtaxablevalue = BigDecimal.ZERO;
		BigDecimal prtotigst = BigDecimal.ZERO;
		BigDecimal prtotcgst = BigDecimal.ZERO;
		BigDecimal prtotsgst = BigDecimal.ZERO;
		BigDecimal prtotcess = BigDecimal.ZERO;

		for (ReconEntity apires : entityResponse) {
			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.DR))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.DR))) {
				dritem.setSection("DR Note");
				dritem.setGstr2Count(apires.getAid2());
				dritem.setGstr2TaxableValue(apires.getA2TaxableValue());
				dritem.setGstr2IGST(apires.getA2Igst());
				dritem.setGstr2CGST(apires.getA2Cgst());
				dritem.setGstr2SGST(apires.getA2Sgst());
				dritem.setGstr2Cess(apires.getA2Cess());
				dritem.setPrCount(apires.getPrid());
				dritem.setPrTaxableValue(apires.getPrTaxableValue());
				dritem.setPrIGST(apires.getPrIgst());
				dritem.setPrCGST(apires.getPrCgst());
				dritem.setPrSGST(apires.getPrSgst());
				dritem.setPrCess(apires.getPrCess());
				sectionFileList.add(dritem);

			}

			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.DRA))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.DRA))) {
				drAItem.setSection("DR Note Amendment");
				drAItem.setGstr2Count(apires.getAid2());
				drAItem.setGstr2TaxableValue(apires.getA2TaxableValue());
				drAItem.setGstr2IGST(apires.getA2Igst());
				drAItem.setGstr2CGST(apires.getA2Cgst());
				drAItem.setGstr2SGST(apires.getA2Sgst());
				drAItem.setGstr2Cess(apires.getA2Cess());
				drAItem.setPrCount(apires.getPrid());
				drAItem.setPrTaxableValue(apires.getPrTaxableValue());
				drAItem.setPrIGST(apires.getPrIgst());
				drAItem.setPrCGST(apires.getPrCgst());
				drAItem.setPrSGST(apires.getPrSgst());
				drAItem.setPrCess(apires.getPrCess());
				sectionFileList.add(drAItem);

			}

			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.CR))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.CR))) {
				critem.setSection("CR Note");
				critem.setGstr2Count(apires.getAid2());
				critem.setGstr2TaxableValue(
						CheckForNegativeValue(apires.getA2TaxableValue()));
				critem.setGstr2IGST(CheckForNegativeValue(apires.getA2Igst()));
				critem.setGstr2CGST(CheckForNegativeValue(apires.getA2Cgst()));
				critem.setGstr2SGST(CheckForNegativeValue(apires.getA2Sgst()));
				critem.setGstr2Cess(CheckForNegativeValue(apires.getA2Cess()));
				critem.setPrCount(apires.getPrid());
				critem.setPrTaxableValue(
						CheckForNegativeValue(apires.getPrTaxableValue()));
				critem.setPrIGST(CheckForNegativeValue(apires.getPrIgst()));
				critem.setPrCGST(CheckForNegativeValue(apires.getPrCgst()));
				critem.setPrSGST(CheckForNegativeValue(apires.getPrSgst()));
				critem.setPrCess(CheckForNegativeValue(apires.getPrCess()));
				sectionFileList.add(critem);

			}

			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.CRA))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.CRA))) {
				crAItem.setSection("CR Note Amendment");
				crAItem.setGstr2Count(apires.getAid2());
				crAItem.setGstr2TaxableValue(
						CheckForNegativeValue(apires.getA2TaxableValue()));
				crAItem.setGstr2IGST(CheckForNegativeValue(apires.getA2Igst()));
				crAItem.setGstr2CGST(CheckForNegativeValue(apires.getA2Cgst()));
				crAItem.setGstr2SGST(CheckForNegativeValue(apires.getA2Sgst()));
				crAItem.setGstr2Cess(CheckForNegativeValue(apires.getA2Cess()));
				crAItem.setPrCount(apires.getPrid());
				crAItem.setPrTaxableValue(
						CheckForNegativeValue(apires.getPrTaxableValue()));
				crAItem.setPrIGST(CheckForNegativeValue(apires.getPrIgst()));
				crAItem.setPrCGST(CheckForNegativeValue(apires.getPrCgst()));
				crAItem.setPrSGST(CheckForNegativeValue(apires.getPrSgst()));
				crAItem.setPrCess(CheckForNegativeValue(apires.getPrCess()));
				sectionFileList.add(crAItem);

			}

			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.INV))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.INV))) {
				inv.setSection("B2B");
				inv.setGstr2Count(apires.getAid2());
				inv.setGstr2TaxableValue(apires.getA2TaxableValue());
				inv.setGstr2IGST(apires.getA2Igst());
				inv.setGstr2CGST(apires.getA2Cgst());
				inv.setGstr2SGST(apires.getA2Sgst());
				inv.setGstr2Cess(apires.getA2Cess());
				inv.setPrCount(apires.getPrid());
				inv.setPrTaxableValue(apires.getPrTaxableValue());
				inv.setPrIGST(apires.getPrIgst());
				inv.setPrCGST(apires.getPrCgst());
				inv.setPrSGST(apires.getPrSgst());
				inv.setPrCess(apires.getPrCess());
				sectionFileList.add(inv);

			}

			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType()
							.equalsIgnoreCase(GSTConstants.INVA))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.INVA))) {
				invA.setSection("B2B Amendment");
				invA.setGstr2Count(apires.getAid2());
				invA.setGstr2TaxableValue(apires.getA2TaxableValue());
				invA.setGstr2IGST(apires.getA2Igst());
				invA.setGstr2CGST(apires.getA2Cgst());
				invA.setGstr2SGST(apires.getA2Sgst());
				invA.setGstr2Cess(apires.getA2Cess());
				invA.setPrCount(apires.getPrid());
				invA.setPrTaxableValue(apires.getPrTaxableValue());
				invA.setPrIGST(apires.getPrIgst());
				invA.setPrCGST(apires.getPrCgst());
				invA.setPrSGST(apires.getPrSgst());
				invA.setPrCess(apires.getPrCess());
				sectionFileList.add(invA);

			}
			
			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType()
							.equalsIgnoreCase(GSTConstants.IMPG))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.IMPG))) {
				impg.setSection("IMPG");
				impg.setGstr2Count(apires.getAid2());
				impg.setGstr2TaxableValue(apires.getA2TaxableValue());
				impg.setGstr2IGST(apires.getA2Igst());
				impg.setGstr2CGST(apires.getA2Cgst());
				impg.setGstr2SGST(apires.getA2Sgst());
				impg.setGstr2Cess(apires.getA2Cess());
				impg.setPrCount(apires.getPrid());
				impg.setPrTaxableValue(apires.getPrTaxableValue());
				impg.setPrIGST(apires.getPrIgst());
				impg.setPrCGST(apires.getPrCgst());
				impg.setPrSGST(apires.getPrSgst());
				impg.setPrCess(apires.getPrCess());
				sectionFileList.add(impg);

			}
			
			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType()
							.equalsIgnoreCase(GSTConstants.IMPGS))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.IMPGS))) {
				impgSez.setSection("IMPGSEZ");
				impgSez.setGstr2Count(apires.getAid2());
				impgSez.setGstr2TaxableValue(apires.getA2TaxableValue());
				impgSez.setGstr2IGST(apires.getA2Igst());
				impgSez.setGstr2CGST(apires.getA2Cgst());
				impgSez.setGstr2SGST(apires.getA2Sgst());
				impgSez.setGstr2Cess(apires.getA2Cess());
				impgSez.setPrCount(apires.getPrid());
				impgSez.setPrTaxableValue(apires.getPrTaxableValue());
				impgSez.setPrIGST(apires.getPrIgst());
				impgSez.setPrCGST(apires.getPrCgst());
				impgSez.setPrSGST(apires.getPrSgst());
				impgSez.setPrCess(apires.getPrCess());
				sectionFileList.add(impgSez);

			}
			

			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase("FMR"))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType().equalsIgnoreCase("FMR"))) {
				fmResp.setSection("Force Match");
				fmResp.setGstr2Count(apires.getAid2());
				fmResp.setGstr2TaxableValue(apires.getA2TaxableValue());
				fmResp.setGstr2IGST(apires.getA2Igst());
				fmResp.setGstr2CGST(apires.getA2Cgst());
				fmResp.setGstr2SGST(apires.getA2Sgst());
				fmResp.setGstr2Cess(apires.getA2Cess());
				fmResp.setPrCount(apires.getPrid());
				fmResp.setPrTaxableValue(apires.getPrTaxableValue());
				fmResp.setPrIGST(apires.getPrIgst());
				fmResp.setPrCGST(apires.getPrCgst());
				fmResp.setPrSGST(apires.getPrSgst());
				fmResp.setPrCess(apires.getPrCess());
				sectionFileList.add(fmResp);

			}
			
			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType()
							.equalsIgnoreCase(GSTConstants.ISD))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.ISD))) {
				isd.setSection("ISD");
				isd.setGstr2Count(apires.getAid2());
				isd.setGstr2TaxableValue(apires.getA2TaxableValue());
				isd.setGstr2IGST(apires.getA2Igst());
				isd.setGstr2CGST(apires.getA2Cgst());
				isd.setGstr2SGST(apires.getA2Sgst());
				isd.setGstr2Cess(apires.getA2Cess());
				isd.setPrCount(apires.getPrid());
				isd.setPrTaxableValue(apires.getPrTaxableValue());
				isd.setPrIGST(apires.getPrIgst());
				isd.setPrCGST(apires.getPrCgst());
				isd.setPrSGST(apires.getPrSgst());
				isd.setPrCess(apires.getPrCess());
				sectionFileList.add(isd);

			}
			

			
			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType()
							.equalsIgnoreCase(GSTConstants.ISDA))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.ISDA))) {
				isdA.setSection("ISD Amendment");
				isdA.setGstr2Count(apires.getAid2());
				isdA.setGstr2TaxableValue(apires.getA2TaxableValue());
				isdA.setGstr2IGST(apires.getA2Igst());
				isdA.setGstr2CGST(apires.getA2Cgst());
				isdA.setGstr2SGST(apires.getA2Sgst());
				isdA.setGstr2Cess(apires.getA2Cess());
				isdA.setPrCount(apires.getPrid());
				isdA.setPrTaxableValue(apires.getPrTaxableValue());
				isdA.setPrIGST(apires.getPrIgst());
				isdA.setPrCGST(apires.getPrCgst());
				isdA.setPrSGST(apires.getPrSgst());
				isdA.setPrCess(apires.getPrCess());
				sectionFileList.add(isdA);

			}

		}
		a2totalRec_Count = critem.getGstr2Count() + dritem.getGstr2Count()
				+ inv.getGstr2Count() + crAItem.getGstr2Count()
				+ drAItem.getGstr2Count() + invA.getGstr2Count() 
				+ impg.getGstr2Count() + impgSez.getGstr2Count()
				+ isd.getGstr2Count() + isdA.getGstr2Count();
		
		

		a2taxablevalue = dritem.getGstr2TaxableValue()
				.add(inv.getGstr2TaxableValue())
				.add(drAItem.getGstr2TaxableValue())
				.add(invA.getGstr2TaxableValue())
				.add(critem.getGstr2TaxableValue())
				.add(crAItem.getGstr2TaxableValue())
				.add(impg.getGstr2TaxableValue())
				.add(impgSez.getGstr2TaxableValue())
				.add(isd.getGstr2TaxableValue())
				.add(isdA.getGstr2TaxableValue());

		a2totigst = dritem.getGstr2IGST().add(inv.getGstr2IGST())
				.add(critem.getGstr2IGST()).add(drAItem.getGstr2IGST())
				.add(invA.getGstr2IGST()).add(crAItem.getGstr2IGST())
				.add(impg.getGstr2IGST()).add(impgSez.getGstr2IGST())
				.add(isd.getGstr2IGST()).add(isdA.getGstr2IGST());
		a2totcgst = dritem.getGstr2CGST().add(inv.getGstr2CGST())
				.add(critem.getGstr2CGST()).add(drAItem.getGstr2CGST())
				.add(invA.getGstr2CGST()).add(crAItem.getGstr2CGST())
				.add(impg.getGstr2CGST()).add(impgSez.getGstr2CGST())
				.add(isd.getGstr2CGST()).add(isdA.getGstr2CGST());
		a2totsgst = dritem.getGstr2SGST().add(inv.getGstr2SGST())
				.add(critem.getGstr2SGST()).add(drAItem.getGstr2SGST())
				.add(invA.getGstr2SGST()).add(crAItem.getGstr2SGST())
				.add(impg.getGstr2SGST()).add(impgSez.getGstr2SGST())
				.add(isd.getGstr2SGST()).add(isdA.getGstr2SGST());
		a2totcess = dritem.getGstr2Cess().add(inv.getGstr2Cess())
				.add(critem.getGstr2Cess()).add(drAItem.getGstr2Cess())
				.add(invA.getGstr2Cess()).add(crAItem.getGstr2Cess())
				.add(impg.getGstr2Cess()).add(impgSez.getGstr2Cess())
				.add(isd.getGstr2Cess()).add(isdA.getGstr2Cess());
		prtotalRec_Count = critem.getPrCount() + dritem.getPrCount()
				+ inv.getPrCount() + crAItem.getPrCount() + drAItem.getPrCount()
				+ invA.getPrCount() + impg.getPrCount() + impgSez.getPrCount()
				+ isd.getPrCount() + isdA.getPrCount();
		prtaxablevalue = dritem.getPrTaxableValue().add(inv.getPrTaxableValue())
				.add(critem.getPrTaxableValue())
				.add(drAItem.getPrTaxableValue()).add(invA.getPrTaxableValue())
				.add(crAItem.getPrTaxableValue()).add(impg.getPrTaxableValue())
				.add(impgSez.getPrTaxableValue())
				.add(isd.getPrTaxableValue())
				.add(isdA.getPrTaxableValue());
		prtotigst = dritem.getPrIGST().add(inv.getPrIGST())
				.add(critem.getPrIGST()).add(drAItem.getPrIGST())
				.add(invA.getPrIGST()).add(crAItem.getPrIGST())
				.add(impg.getPrIGST()).add(impgSez.getPrIGST())
				.add(isd.getPrIGST()).add(isdA.getPrIGST());
		prtotcgst = dritem.getPrCGST().add(inv.getPrCGST())
				.add(critem.getPrCGST()).add(drAItem.getPrCGST())
				.add(invA.getPrCGST()).add(crAItem.getPrCGST())
				.add(impg.getPrCGST()).add(impgSez.getPrCGST())
				.add(isd.getPrCGST()).add(isdA.getPrCGST());
		prtotsgst = dritem.getPrSGST().add(inv.getPrSGST())
				.add(critem.getPrSGST()).add(drAItem.getPrSGST())
				.add(invA.getPrSGST()).add(crAItem.getPrSGST())
				.add(impg.getPrSGST()).add(impgSez.getPrSGST())
				.add(isd.getPrSGST()).add(isdA.getPrSGST());
		prtotcess = dritem.getPrCess().add(inv.getPrCess())
				.add(critem.getPrCess()).add(drAItem.getPrCess())
				.add(invA.getPrCess()).add(crAItem.getPrCess())
				.add(impg.getPrCess()).add(impgSez.getPrCess())
				.add(isd.getPrCess()).add(isdA.getPrCess());

		// grand Total

		grandTotal.setSection("Grand Total");
		grandTotal.setGstr2Count(fmResp.getGstr2Count() + a2totalRec_Count);
		grandTotal.setGstr2TaxableValue(
				fmResp.getGstr2TaxableValue().add(a2taxablevalue));
		grandTotal.setGstr2IGST(fmResp.getGstr2IGST().add(a2totigst));
		grandTotal.setGstr2CGST(fmResp.getGstr2CGST().add(a2totcgst));
		grandTotal.setGstr2SGST(fmResp.getGstr2SGST().add(a2totsgst));
		grandTotal.setGstr2Cess(fmResp.getGstr2Cess().add(a2totcess));
		grandTotal.setPrCount(fmResp.getPrCount() + prtotalRec_Count);
		grandTotal.setPrTaxableValue(
				fmResp.getPrTaxableValue().add(prtaxablevalue));
		grandTotal.setPrIGST(fmResp.getPrIGST().add(prtotigst));
		grandTotal.setPrCGST(fmResp.getPrCGST().add(prtotcgst));
		grandTotal.setPrSGST(fmResp.getPrSGST().add(prtotsgst));
		grandTotal.setPrCess(fmResp.getPrCess().add(prtotcess));
		sectionFileList.add(grandTotal);

		total.setSection("Total");
		total.setGstr2Count(a2totalRec_Count);
		total.setGstr2TaxableValue(a2taxablevalue);
		total.setGstr2IGST(a2totigst);
		total.setGstr2CGST(a2totcgst);
		total.setGstr2SGST(a2totsgst);
		total.setGstr2Cess(a2totcess);
		total.setPrCount(prtotalRec_Count);
		total.setPrTaxableValue(prtaxablevalue);
		total.setPrIGST(prtotigst);
		total.setPrCGST(prtotcgst);
		total.setPrSGST(prtotsgst);
		total.setPrCess(prtotcess);
		sectionFileList.add(total);

		Map<String, Gstr2InitiateReconLineItemDto> respMap = sectionFileList
				.stream()
				.collect(Collectors.toMap(o -> o.getSection(), o -> o));

		List<String> desirList = Arrays.asList("B2B", "B2B Amendment",
				"DR Note", "CR Note", "DR Note Amendment", "CR Note Amendment",
				"ISD", "ISD Amendment",
				"IMPG", "IMPGSEZ", "Total", "Force Match", "Grand Total" );

		List<Gstr2InitiateReconLineItemDto> newRespList = new ArrayList<>();

		for (String repType : desirList) {
			if (respMap.containsKey(repType)) {
				newRespList.add(respMap.get(repType));
			}
		}
		return newRespList;

	}

	private BigDecimal CheckForNegativeValue(Object value) {

		if (value != null) {
			if (value instanceof BigDecimal) {
				return new BigDecimal((value != null
						? ((((BigDecimal) value).compareTo(BigDecimal.ZERO) > 0)
								? "-" + value.toString() : value.toString())
						: null));
			}
		}
		return null;
	}
}
