package com.ey.advisory.app.gstr1.einv;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva Reddy
 *
 */

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Gstr1GSTINDeleteDataRespDto {


	private String supplierGstin;
	private String returnPeriod;
	private String documentType;
	private String documentNumber;
	private String documentDate;
	private String customerGstin;
	private boolean isDelete;
	private boolean isProcessed;
	private String docKey;
	private Integer fy;
	private Integer fileId;
	private LocalDate createdOn;
	private String createdUser;
	private String errorCode;
	private String errorMsg;
	private String pos;
	private String tableType;


}
