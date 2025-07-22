/**
 * 
 */
package com.ey.advisory.app.docs.dto;

import java.util.List;

import lombok.Data;

/**
 * @author Laxmi.Salukuti
 *
 */
@Data
public class Itc04DocDetailsSaveRespDto {

	private Long id;

	private Long oldId;

	private String supplierGstin;

	private String tableNumber;

	private String invNumber;

	private String fyInvDate;

	private String deliveryChallanaNumber;

	private String jwDeliveryChallanaNumber;

	private String fyDcDate;

	private String fyjwDcDate;

	private String retPeriod;

	private String jobWorkerGstin;

	private String jobWorkerStateCode;

	private String accountingVoucherNumber;

	private String companyCode;

	private String payloadId;

	private List<Itc04DocErrorDetailsDto> errors;

}
