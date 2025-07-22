package com.ey.advisory.app.docs.dto.gstr9;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Jithendra.B
 *
 */
@Setter
@Getter
@ToString
public class Gstr9PdfDTO {

	private String subSection;
	private String label;
	private String taxableValue;
	private String igst;
	private String cgst;
	private String sgst;
	private String cess;
	private String taxPayable;
	private String paid;
	private String interest;
	private String penalty;
	private String lateFee;
	private String paidCash;
	private String count;
	private String qty;


	public Gstr9PdfDTO(String subSection, String label) {
		super();
		this.subSection = subSection;
		this.label = label;
	}

	public Gstr9PdfDTO() {
		super();
	}

}
