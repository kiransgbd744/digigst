package com.ey.advisory.app.services.strcutvalidation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import com.google.common.collect.ImmutableList;

@Component("DocAndSupplyTypeServiceImpl")
public class DocAndSupplyTypeServiceImpl implements DocAndSupplyTypeService {
	private static final List<String> SUPPLY_TYPE_IMPORTS = 
			ImmutableList.of("TAX", "SEZ", "NON", "EXT", "DXP", "NIL", 
					"EXPT","EXPWT","ISD","ISDIE","ISD8","ISIE8","NSY",
					"SOA","LGAS","CAN","DTA");
	private static final List<String> DOCUMENT_TYPE_IMPORTS = 
	 ImmutableList.of("INV","RNV","CR","RFV","ARFV",
			 "DR","RCR","RDR","DLC","RDLC");
	
	/*@Autowired
	private DocAndSupplyTypeRepository docSupplyTypeRepository;*/

	private List<String> docTypes = new ArrayList<String>();
	private List<String> supplyTypes = new ArrayList<String>();

	@Override
	public boolean isValidDocType(String docType) {
		if (docTypes.isEmpty()) {
			//docTypes = docSupplyTypeRepository.findAllDocTypes();
			docTypes=DOCUMENT_TYPE_IMPORTS;
		}
		return docTypes.stream().anyMatch(docType::equalsIgnoreCase);
	}

	@Override
	public boolean isValidSupplyType(String supplyType) {
		if (supplyTypes.isEmpty()) {
			//supplyTypes = docSupplyTypeRepository.findAllSupplyTypes();
			supplyTypes = SUPPLY_TYPE_IMPORTS;
		}
		return supplyTypes.stream().anyMatch(supplyType::equalsIgnoreCase);
	}

}
