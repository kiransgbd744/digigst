package com.ey.advisory.app.services.daos.initiaterecon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.app.docs.dto.EntityIRDto;
import com.ey.advisory.common.GSTConstants;
import com.ey.advisory.core.dto.ReconEntity;

@Component("Anx2InitiateReconService")
public class Anx2InitiateReconService {

	@Autowired
	@Qualifier("Anx2InitiateReconDaoImpl")
	Anx2InitiateReconDao anx2InitiateReconDao;

	@SuppressWarnings("unchecked")
	public List<InitiateReconHeaderDto> find(EntityIRDto dto) {
		EntityIRDto req = (EntityIRDto) dto;

		List<ReconEntity> entityResponse = anx2InitiateReconDao
				.anx2InitiateRecon(req);

		List<InitiateReconHeaderDto> headerList = new ArrayList<>();
		List<Object> sectionFileList = new ArrayList<>();
		InitiateReconHeaderDto reconfile = new InitiateReconHeaderDto();

		InitiateReconLineItemDto critem = new InitiateReconLineItemDto();
		InitiateReconLineItemDto dritem = new InitiateReconLineItemDto();
		InitiateReconLineItemDto inv = new InitiateReconLineItemDto();

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
		BigDecimal totavigst = BigDecimal.ZERO;
		BigDecimal totavcgst = BigDecimal.ZERO;
		BigDecimal totavsgst = BigDecimal.ZERO;
		BigDecimal totavcess = BigDecimal.ZERO;

		for (ReconEntity apires : entityResponse) {
			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.DR))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.DR))) {
				dritem.setSection("DR Note");
				dritem.setAnx2Count(apires.getAid2());
				dritem.setAnx2TaxableValue(apires.getA2TaxableValue());
				dritem.setAnx2IGST(apires.getA2Igst());
				dritem.setAnx2CGST(apires.getA2Cgst());
				dritem.setAnx2SGST(apires.getA2Sgst());
				dritem.setAnx2Cess(apires.getA2Cess());
				dritem.setPrCount(apires.getPrid());
				dritem.setPrTaxableValue(apires.getPrTaxableValue());
				dritem.setPrIGST(apires.getPrIgst());
				dritem.setPrCGST(apires.getPrCgst());
				dritem.setPrSGST(apires.getPrSgst());
				dritem.setPrCess(apires.getPrCess());
				dritem.setAvIGST(apires.getAvilableIgst());
				dritem.setAvCGST(apires.getAvilableCgst());
				dritem.setAvSGST(apires.getAvilableSgst());
				dritem.setAvCess(apires.getAvilableCess());
				sectionFileList.add(dritem);

			}
			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.CR))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.CR))) {
				critem.setSection("CR Note");
				critem.setAnx2Count(apires.getAid2());
				critem.setAnx2TaxableValue(apires.getA2TaxableValue());
				critem.setAnx2IGST(apires.getA2Igst());
				critem.setAnx2CGST(apires.getA2Cgst());
				critem.setAnx2SGST(apires.getA2Sgst());
				critem.setAnx2Cess(apires.getA2Cess());
				critem.setPrCount(apires.getPrid());
				critem.setPrTaxableValue(apires.getPrTaxableValue());
				critem.setPrIGST(apires.getPrIgst());
				critem.setPrCGST(apires.getPrCgst());
				critem.setPrSGST(apires.getPrSgst());
				critem.setPrCess(apires.getPrCess());
				critem.setAvIGST(apires.getAvilableIgst());
				critem.setAvCGST(apires.getAvilableCgst());
				critem.setAvSGST(apires.getAvilableSgst());
				critem.setAvCess(apires.getAvilableCess());
				sectionFileList.add(critem);

			}

			if ((apires.getPrInvType() != null
					&& !apires.getPrInvType().isEmpty()
					&& apires.getPrInvType().equalsIgnoreCase(GSTConstants.INV))
					|| (apires.getA2InvType() != null
							&& !apires.getA2InvType().isEmpty()
							&& apires.getA2InvType()
									.equalsIgnoreCase(GSTConstants.INV))) {
				inv.setSection("Inv");
				inv.setAnx2Count(apires.getAid2());
				inv.setAnx2TaxableValue(apires.getA2TaxableValue());
				inv.setAnx2IGST(apires.getA2Igst());
				inv.setAnx2CGST(apires.getA2Cgst());
				inv.setAnx2SGST(apires.getA2Sgst());
				inv.setAnx2Cess(apires.getA2Cess());
				inv.setPrCount(apires.getPrid());
				inv.setPrTaxableValue(apires.getPrTaxableValue());
				inv.setPrIGST(apires.getPrIgst());
				inv.setPrCGST(apires.getPrCgst());
				inv.setPrSGST(apires.getPrSgst());
				inv.setPrCess(apires.getPrCess());
				inv.setAvIGST(apires.getAvilableIgst());
				inv.setAvCGST(apires.getAvilableCgst());
				inv.setAvSGST(apires.getAvilableSgst());
				inv.setAvCess(apires.getAvilableCess());
				sectionFileList.add(inv);

			}

		}
		a2totalRec_Count = critem.getAnx2Count() + dritem.getAnx2Count()
				+ inv.getAnx2Count();
		a2taxablevalue = dritem.getAnx2TaxableValue()
				.add(inv.getAnx2TaxableValue())
				.subtract(critem.getAnx2TaxableValue());
		a2totigst = dritem.getAnx2IGST().add(inv.getAnx2IGST())
				.subtract(critem.getAnx2IGST());
		a2totcgst = dritem.getAnx2CGST().add(inv.getAnx2CGST())
				.subtract(critem.getAnx2CGST());
		a2totsgst = dritem.getAnx2SGST().add(inv.getAnx2SGST())
				.subtract(critem.getAnx2SGST());
		a2totcess = dritem.getAnx2Cess().add(inv.getAnx2Cess())
				.subtract(critem.getAnx2Cess());
		prtotalRec_Count = critem.getPrCount() + dritem.getPrCount()
				+ inv.getPrCount();
		prtaxablevalue = dritem.getPrTaxableValue().add(inv.getPrTaxableValue())
				.subtract(critem.getPrTaxableValue());
		prtotigst = dritem.getPrIGST().add(inv.getPrIGST())
				.subtract(critem.getPrIGST());
		prtotcgst = dritem.getPrCGST().add(inv.getPrCGST())
				.subtract(critem.getPrCGST());
		prtotsgst = dritem.getPrSGST().add(inv.getPrSGST())
				.subtract(critem.getPrSGST());
		prtotcess = dritem.getPrCess().add(inv.getPrCess())
				.subtract(critem.getPrCess());
		totavigst = dritem.getAvIGST().add(inv.getAvIGST())
				.subtract(critem.getAvIGST());
		totavcgst = dritem.getAvCGST().add(inv.getAvCGST())
				.subtract(critem.getAvCGST());
		totavsgst = dritem.getAvSGST().add(inv.getAvSGST())
				.subtract(critem.getAvSGST());
		totavcess = dritem.getAvCess().add(inv.getAvCess())
				.subtract(critem.getAvCess());

		reconfile.setShowing("Current Period");
		reconfile.setSection("Total");
		reconfile.setAnx2Count(a2totalRec_Count);
		reconfile.setAnx2TaxableValue(a2taxablevalue);
		reconfile.setAnx2IGST(a2totigst);
		reconfile.setAnx2CGST(a2totcgst);
		reconfile.setAnx2SGST(a2totsgst);
		reconfile.setAnx2Cess(a2totcess);
		reconfile.setPrCount(prtotalRec_Count);
		reconfile.setPrTaxableValue(prtaxablevalue);
		reconfile.setPrIGST(prtotigst);
		reconfile.setPrCGST(prtotcgst);
		reconfile.setPrSGST(prtotsgst);
		reconfile.setPrCess(prtotcess);
		reconfile.setAvIgst(totavigst);
		reconfile.setAvCgst(totavcgst);
		reconfile.setAvSgst(totavsgst);
		reconfile.setAvCess(totavcess);
		reconfile.setLineItems(sectionFileList);
		headerList.add(reconfile);

		return headerList;

	}

}
