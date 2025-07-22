/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.io.Serializable;
import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Data;

/**
 * @author Khalid1.Khan
 *
 */
@Data
public class Gstr3BEntityDashboardDto implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	private String gstin;

	@Expose
	private String stateName;

	@Expose
	private String lastModifiedOn;

	@Expose
	private String auth;

	@Expose
	private String savedStatus;

	@Expose
	private String lastUpdatedOn;

	@Expose
	private BigDecimal totalLiability = BigDecimal.ZERO;

	@Expose
	private BigDecimal totalItc = BigDecimal.ZERO;

	@Expose
	private String taxPeriod;

	@Expose
	protected String filedDate;

	@Expose
	private boolean interestFalg = false;

	@Expose
	private String optionSelected;
	
	@Expose
	private String digiStatus;
	
	@Expose
	private String digiUpdateOn;

	/**
	 * @param gstin
	 * @param stateName
	 * @param lastModifiedOn
	 * @param auth
	 * @param savedStatus
	 * @param totalLiability
	 * @param totalItc
	 * @param taxPeriod
	 */

	public Gstr3BEntityDashboardDto(String gstin, String stateName,
			String lastModifiedOn, String auth, String savedStatus,
			BigDecimal totalLiability, BigDecimal totalItc, String taxPeriod,String digiStatus,String digiUpdateOn) {
		super();
		this.gstin = gstin;
		this.stateName = stateName;
		this.lastModifiedOn = lastModifiedOn;
		this.auth = auth;
		this.savedStatus = savedStatus;
		this.totalLiability = totalLiability;
		this.totalItc = totalItc;
		this.taxPeriod = taxPeriod;
		this.digiStatus = digiStatus;
		this.digiUpdateOn = digiUpdateOn;
	}

	public Gstr3BEntityDashboardDto() {
		// TODO Auto-generated constructor stub
	}

}
