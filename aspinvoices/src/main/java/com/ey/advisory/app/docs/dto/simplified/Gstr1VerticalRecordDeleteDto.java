package com.ey.advisory.app.docs.dto.simplified;

import java.util.List;

import lombok.Data;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Data
public class Gstr1VerticalRecordDeleteDto {

		  private List<String> docKey;
		  private String docType;
	      private List<Long> id;
	      private String gstin;
	      private String taxPeriod;	
	      private String returnType;
}
