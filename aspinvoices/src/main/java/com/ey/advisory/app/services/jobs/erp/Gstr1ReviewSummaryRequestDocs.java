package com.ey.advisory.app.services.jobs.erp;

import java.util.List;

import com.ey.advisory.app.docs.dto.erp.Advp111aPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp111bPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp211aPopupDto;
import com.ey.advisory.app.docs.dto.erp.Advp211bPopupDto;
import com.ey.advisory.app.docs.dto.erp.B2csPopupDto;
import com.ey.advisory.app.docs.dto.erp.B2csaPopupDto;
import com.ey.advisory.app.docs.dto.erp.DocPopupDto;
import com.ey.advisory.app.docs.dto.erp.Gstr1ReviewSummaryRequestItemDto;
import com.ey.advisory.app.docs.dto.erp.HsnPopupDto;
import com.ey.advisory.app.docs.dto.erp.NilPopupDto;

public interface Gstr1ReviewSummaryRequestDocs {

	public List<Gstr1ReviewSummaryRequestItemDto> processSummary(String gstin,
			int derivedTaxPeriod, String entityPan, String stateName,
			String entityName, String companyCode);

	public List<Gstr1ReviewSummaryRequestItemDto> reviewSummary(String gstin,
			String returnPeriod, String entityName, String entityPan,
			Long entityId, String companyCode);

	public B2csPopupDto getB2csPopupDto(String gstin, String returnPeriod, Long entityId);

	public B2csaPopupDto getB2csaPopupDto(String gstin, String returnPeriod, Long entityId);

	public HsnPopupDto getHsnPopup(String gstin, String returnPeriod, Long entityId);

	public DocPopupDto getDocPopup(String gstin, String returnPeriod, Long entityId);

	public NilPopupDto getNilNonPopup(String gstin, String returnPeriod, Long entityId);

	public Advp111aPopupDto getAdvp111aPopupDto(String gstin,
			String returnPeriod, Long entityId);

	public Advp111bPopupDto getAdvp111bPopupDto(String gstin,
			String returnPeriod, Long entityId);

	public Advp211aPopupDto getAdvp211aPopupDto(String gstin,
			String returnPeriod, Long entityId);

	public Advp211bPopupDto getAdvp211bPopupDto(String gstin,
			String returnPeriod, Long entityId);

}
