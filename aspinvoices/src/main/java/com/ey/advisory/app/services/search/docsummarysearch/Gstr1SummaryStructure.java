package com.ey.advisory.app.services.search.docsummarysearch;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.ey.advisory.app.docs.dto.Gstr1B2BGstnRespDto;
import com.ey.advisory.app.docs.dto.Gstr1BasicSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1DocIssuedSummarySectionDto;
import com.ey.advisory.app.docs.dto.Gstr1NilRatedSummarySectionDto;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Service("Gstr1SummaryStructure")
public class Gstr1SummaryStructure {

	@Autowired
	@Qualifier("B2BStructure")
	private B2BStructure b2bStructure;
	@Autowired
	@Qualifier("B2CLStructure")
	private B2CLStructure b2clStructure;

	@Autowired
	@Qualifier("B2CSStructure")
	private B2CSStructure b2csStructure;

	@Autowired
	@Qualifier("CDNRStructure")
	private CDNRStructure cdnrStructure;

	@Autowired
	@Qualifier("B2BAStructure")
	private B2BAStructure b2baStructure;

	@Autowired
	@Qualifier("CDNRAStructure")
	private CDNRAStructure cdnraStructure;

	@Autowired
	@Qualifier("HSNStructure")
	private HSNStructure hsnStructure;

	@Autowired
	@Qualifier("CDNURStructure")
	private CDNURStructure cdnurStructure;
	
	@Autowired
	@Qualifier("EXPStructure")
	private EXPStructure expStructure;
	
	@Autowired
	@Qualifier("EXPAMDStructure")
	private EXPAMDStructure expamdStructure;
	
	@Autowired
	@Qualifier("CDNURAStructure")
	private CDNURAStructure crnuraStructure;
	
	@Autowired
	@Qualifier("B2CSAStructure")
	private B2CSAStructure b2csaStructure;
	
	@Autowired
	@Qualifier("B2CLAStructure")
	private B2CLAStructure b2claStructure;

	@Autowired
	@Qualifier("ATStructure")
	private ATStructure atStructure;
	
	@Autowired
	@Qualifier("ATAStructure")
	private ATAStructure ataStructure;

	@Autowired
	@Qualifier("TXPDStructure")
	private TXPDStructure txpdStructure;
	
	@Autowired
	@Qualifier("TXPDAStructure")
	private TXPDAStructure txpdaStructure;
	
	@Autowired
	@Qualifier("DOCStructure")
	private DOCStructure docStructure;
	
	@Autowired
	@Qualifier("NILStructure")
	private NILStructure nilStructure;
	
	@Autowired
	@Qualifier("SEZWStructure")
	private SEZWStructure sezwStructure;
	
	@Autowired
	@Qualifier("SEZWOStructure")
	private SEZWOStructure sezwoStructure;


	// For b2b Structure
	public JsonElement b2bStructure(List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return b2bStructure.b2bResp(eySummary, gstnList);
	}

	// For b2cl Structure
	public JsonElement b2clStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return b2clStructure.b2clResp(eySummary, gstnList);

	}

	// For b2cs Structure
	public JsonElement b2csStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return b2csStructure.b2csResp(eySummary, gstnList);

	}

	// For cdnr Structure
	public JsonElement cdnrStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return cdnrStructure.cdnrResp(eySummary, gstnList);

	}

	// For cdnr Structure
	public JsonElement b2baStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return b2baStructure.b2baResp(eySummary, gstnList);

	}

	// For cdnra Structure
	public JsonElement cdnraStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return cdnraStructure.cdnraResp(eySummary, gstnList);

	}

	// For cdnur Structure
	public JsonElement cdnurStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return cdnurStructure.cdnurResp(eySummary, gstnList);

	}

	// For HSN Structure
	public JsonElement hsnStructure(List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return hsnStructure.hsnResp(eySummary, gstnList);

	}

	// For EXP Structure
	public JsonElement expStructure(List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return expStructure.expResp(eySummary, gstnList);

	}

	// For EXPAMD Structure
	public JsonElement expaStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return expamdStructure.expaResp(eySummary, gstnList);

	}

	// For CDNURA Structure
	public JsonElement cdnuraStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return crnuraStructure.cdnuraResp(eySummary, gstnList);

	}
	// For B2CSA Structure
	public JsonElement b2csaStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return b2csaStructure.b2csaResp(eySummary, gstnList);

	}
	
	// For B2CLA Structure
	public JsonElement b2claStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return b2claStructure.b2claResp(eySummary, gstnList);

	}
	
		// For AT Structure
	public JsonElement atStructure(List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return atStructure.atResp(eySummary, gstnList);

	}

	// For ATA Structure
	public JsonElement ataStructure(List<Gstr1BasicSummarySectionDto> eySummary,
			List<? extends Gstr1B2BGstnRespDto> gstnList) {
		return ataStructure.ataResp(eySummary, gstnList);

	}

	// For TXPD Structure
	public JsonElement txpdStructure(List<Gstr1BasicSummarySectionDto> eySummary,
				List<? extends Gstr1B2BGstnRespDto> gstnList) {
			return txpdStructure.txpdResp(eySummary, gstnList);

		}

	// For TXPDA Structure
	public JsonElement txpdaStructure(
			List<Gstr1BasicSummarySectionDto> eySummary,
					List<? extends Gstr1B2BGstnRespDto> gstnList) {
				return txpdaStructure.txpdaResp(eySummary, gstnList);

			}
		
		// For DOC Issued Structure
	public JsonElement docStructure(
			List<Gstr1DocIssuedSummarySectionDto> eySummary,
							List<Gstr1DocIssuedSummarySectionDto> gstnList) {
						return docStructure.docResp(eySummary, gstnList);

					}
		// For NIL Issued Structure
	public JsonElement nilStructure(
			List<Gstr1NilRatedSummarySectionDto> eySummary,
							List<Gstr1NilRatedSummarySectionDto> gstnList) {
						return nilStructure.nilResp(eySummary, gstnList);

					}
		// For sezw Structure
		public JsonElement sezwStructure(
				List<Gstr1BasicSummarySectionDto> eySummary) {
			return sezwStructure.sezwResp(eySummary);

		}
		
		// For sezwo Structure
				public JsonElement sezwoStructure(
						List<Gstr1BasicSummarySectionDto> eySummary) {
					return sezwoStructure.sezwoResp(eySummary);

				}



}
