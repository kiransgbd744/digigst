package com.ey.advisory.ewb.data.entities.clientBusiness;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Jithendra Kumar B
 *
 */

@Entity
@Table(name = "EINV_EWB_STATISTICS")
@Getter
@Setter
@ToString
public class EinvEwbStatisticsEntity {

	@Id
	@Column(name = "ID")
	@SequenceGenerator(name = "sequence", sequenceName = "EINV_EWB_STATISTICS_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	private Long id;

	@Column(name = "ACK_NUM")
	private String ackNum;

	@Column(name = "ACK_DT")
	private LocalDateTime ackDate;

	@Column(name = "IRN")
	private String irn;

	@Column(name = "DOC_TYPE")
	private String docType;

	@Column(name = "DOC_NUM")
	private String docNum;

	@Column(name = "DOC_DATE")
	private LocalDate docDate;

	@Column(name = "SELLER_GSTIN")
	private String sellerGstin;

	@Column(name = "BUYER_GSTIN")
	private String buyerGstin;

	@Column(name = "EWB_NUM")
	private String ewbNum;

	@Column(name = "EWB_DATE")
	private LocalDateTime ewbDate;

	@Column(name = "EWB_VALID_UPTO")
	private LocalDateTime ewbValidUpto;

	@Column(name = "CANCELLATION_DATE")
	private LocalDateTime cancellationDate;

	@Column(name = "NIC_DISTANCE")
	private Integer nicDistance;

	@Column(name = "IS_CANCELLED")
	private boolean isCancelled;

	@Column(name = "API_IDENTIFIER")
	private String apiIdentifier;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "IS_DUPLICATE")
	protected boolean isDuplicate;

	@Column(name = "IDENTIFIER")
	protected String identifer;
	
	@Column(name = "CREATED_ON_IST")
	private LocalDateTime createdOnIst;

}
