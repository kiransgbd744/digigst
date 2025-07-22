package com.ey.advisory.app.docs.dto.gstr3B;


import lombok.Data;

@Data
public class Gstr3bpdfDTO {
	private String label;
	private String totalTaxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String interStateSupplies;
	private String intraStateSupplies;
	
}
