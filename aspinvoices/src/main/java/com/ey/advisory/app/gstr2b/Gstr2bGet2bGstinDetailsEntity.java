package com.ey.advisory.app.gstr2b;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Hema G M
 *
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "GET2B_GSTIN_DETAILS")
public class Gstr2bGet2bGstinDetailsEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "GET2B_GSTIN_ID")
	private Long get2bGstinId;

	@Column(name = "REQ_ID")
	private Long reqId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RET_PERIOD")
	private String retPeriod;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "IS_DELETE")
	private Boolean isDelete;
	
	
	public Gstr2bGet2bGstinDetailsEntity(String gstin, String taxPeriod, 
			Long reqId) {
		this.gstin = gstin;
		this.reqId = reqId;
		this.retPeriod = taxPeriod;
	}
	

}
