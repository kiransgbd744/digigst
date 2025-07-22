/**
 * 
 */
package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;

import com.google.gson.annotations.Expose;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Khalid1.Khan
 *
 */

@Setter
@Getter
public class Gstr3BGstinDashboardDto {
	
	@Expose
	private String level;
	
	@Expose
	private Boolean lastLevel;
	
	@Expose
	private Boolean gstnSuccess = true;
	
	@Expose
	private String table;
	
	@Expose
	private String supplyType;
	
	@Expose
	private BigDecimal computedTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computedIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computedCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computedSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal computdCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal userInputCess = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal gstinCess = BigDecimal.ZERO;
	
	
	@Expose
	private BigDecimal autoCalTaxableVal = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCalIgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCalCgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCalSgst = BigDecimal.ZERO;
	
	@Expose
	private BigDecimal autoCalCess = BigDecimal.ZERO;
	
	
	

	/**
	 * @param level
	 * @param table
	 * @param supplyType
	 * @param computedTaxableVal
	 * @param computedIgst
	 * @param computedCgst
	 * @param computedSgst
	 * @param computdCess
	 * @param userInputTaxableVal
	 * @param userInputIgst
	 * @param userInputCgst
	 * @param userInputSgst
	 * @param userInputCess
	 * @param gstinTaxableVal
	 * @param gstinIgst
	 * @param gstinCgst
	 * @param gstinSgst
	 * @param gstinCess
	 */
	public Gstr3BGstinDashboardDto(String level, String table,
			String supplyType, BigDecimal computedTaxableVal,
			BigDecimal computedIgst, BigDecimal computedCgst,
			BigDecimal computedSgst, BigDecimal computdCess,
			BigDecimal userInputTaxableVal, BigDecimal userInputIgst,
			BigDecimal userInputCgst, BigDecimal userInputSgst,
			BigDecimal userInputCess, BigDecimal gstinTaxableVal,
			BigDecimal gstinIgst, BigDecimal gstinCgst, BigDecimal gstinSgst,
			BigDecimal gstinCess) {
		super();
		this.level = level;
		this.table = table;
		this.supplyType = supplyType;
		this.computedTaxableVal = computedTaxableVal;
		this.computedIgst = computedIgst;
		this.computedCgst = computedCgst;
		this.computedSgst = computedSgst;
		this.computdCess = computdCess;
		this.userInputTaxableVal = userInputTaxableVal;
		this.userInputIgst = userInputIgst;
		this.userInputCgst = userInputCgst;
		this.userInputSgst = userInputSgst;
		this.userInputCess = userInputCess;
		this.gstinTaxableVal = gstinTaxableVal;
		this.gstinIgst = gstinIgst;
		this.gstinCgst = gstinCgst;
		this.gstinSgst = gstinSgst;
		this.gstinCess = gstinCess;
	}

	@Override
	public String toString() {
		return "Gstr3BGstinDashboardDto [level=" + level + ", table=" + table
				+ ", supplyType=" + supplyType + ", computedTaxableVal="
				+ computedTaxableVal + ", computedIgst=" + computedIgst
				+ ", computedCgst=" + computedCgst + ", computedSgst="
				+ computedSgst + ", computdCess=" + computdCess
				+ ", userInputTaxableVal=" + userInputTaxableVal
				+ ", userInputIgst=" + userInputIgst + ", userInputCgst="
				+ userInputCgst + ", userInputSgst=" + userInputSgst
				+ ", userInputCess=" + userInputCess + ", gstinTaxableVal="
				+ gstinTaxableVal + ", gstinIgst=" + gstinIgst + ", gstinCgst="
				+ gstinCgst + ", gstinSgst=" + gstinSgst + ", gstinCess="
				+ gstinCess + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((computdCess == null) ? 0 : computdCess.hashCode());
		result = prime * result
				+ ((computedCgst == null) ? 0 : computedCgst.hashCode());
		result = prime * result
				+ ((computedIgst == null) ? 0 : computedIgst.hashCode());
		result = prime * result
				+ ((computedSgst == null) ? 0 : computedSgst.hashCode());
		result = prime * result + ((computedTaxableVal == null) ? 0
				: computedTaxableVal.hashCode());
		result = prime * result
				+ ((gstinCess == null) ? 0 : gstinCess.hashCode());
		result = prime * result
				+ ((gstinCgst == null) ? 0 : gstinCgst.hashCode());
		result = prime * result
				+ ((gstinIgst == null) ? 0 : gstinIgst.hashCode());
		result = prime * result
				+ ((gstinSgst == null) ? 0 : gstinSgst.hashCode());
		result = prime * result
				+ ((gstinTaxableVal == null) ? 0 : gstinTaxableVal.hashCode());
		result = prime * result + ((level == null) ? 0 : level.hashCode());
		result = prime * result
				+ ((supplyType == null) ? 0 : supplyType.hashCode());
		result = prime * result + ((table == null) ? 0 : table.hashCode());
		result = prime * result
				+ ((userInputCess == null) ? 0 : userInputCess.hashCode());
		result = prime * result
				+ ((userInputCgst == null) ? 0 : userInputCgst.hashCode());
		result = prime * result
				+ ((userInputIgst == null) ? 0 : userInputIgst.hashCode());
		result = prime * result
				+ ((userInputSgst == null) ? 0 : userInputSgst.hashCode());
		result = prime * result + ((userInputTaxableVal == null) ? 0
				: userInputTaxableVal.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Gstr3BGstinDashboardDto other = (Gstr3BGstinDashboardDto) obj;
		if (computdCess == null) {
			if (other.computdCess != null)
				return false;
		} else if (!computdCess.equals(other.computdCess))
			return false;
		if (computedCgst == null) {
			if (other.computedCgst != null)
				return false;
		} else if (!computedCgst.equals(other.computedCgst))
			return false;
		if (computedIgst == null) {
			if (other.computedIgst != null)
				return false;
		} else if (!computedIgst.equals(other.computedIgst))
			return false;
		if (computedSgst == null) {
			if (other.computedSgst != null)
				return false;
		} else if (!computedSgst.equals(other.computedSgst))
			return false;
		if (computedTaxableVal == null) {
			if (other.computedTaxableVal != null)
				return false;
		} else if (!computedTaxableVal.equals(other.computedTaxableVal))
			return false;
		if (gstinCess == null) {
			if (other.gstinCess != null)
				return false;
		} else if (!gstinCess.equals(other.gstinCess))
			return false;
		if (gstinCgst == null) {
			if (other.gstinCgst != null)
				return false;
		} else if (!gstinCgst.equals(other.gstinCgst))
			return false;
		if (gstinIgst == null) {
			if (other.gstinIgst != null)
				return false;
		} else if (!gstinIgst.equals(other.gstinIgst))
			return false;
		if (gstinSgst == null) {
			if (other.gstinSgst != null)
				return false;
		} else if (!gstinSgst.equals(other.gstinSgst))
			return false;
		if (gstinTaxableVal == null) {
			if (other.gstinTaxableVal != null)
				return false;
		} else if (!gstinTaxableVal.equals(other.gstinTaxableVal))
			return false;
		if (level == null) {
			if (other.level != null)
				return false;
		} else if (!level.equals(other.level))
			return false;
		if (supplyType == null) {
			if (other.supplyType != null)
				return false;
		} else if (!supplyType.equals(other.supplyType))
			return false;
		if (table == null) {
			if (other.table != null)
				return false;
		} else if (!table.equals(other.table))
			return false;
		if (userInputCess == null) {
			if (other.userInputCess != null)
				return false;
		} else if (!userInputCess.equals(other.userInputCess))
			return false;
		if (userInputCgst == null) {
			if (other.userInputCgst != null)
				return false;
		} else if (!userInputCgst.equals(other.userInputCgst))
			return false;
		if (userInputIgst == null) {
			if (other.userInputIgst != null)
				return false;
		} else if (!userInputIgst.equals(other.userInputIgst))
			return false;
		if (userInputSgst == null) {
			if (other.userInputSgst != null)
				return false;
		} else if (!userInputSgst.equals(other.userInputSgst))
			return false;
		if (userInputTaxableVal == null) {
			if (other.userInputTaxableVal != null)
				return false;
		} else if (!userInputTaxableVal.equals(other.userInputTaxableVal))
			return false;
		return true;
	}

	/**
	 * 
	 */
	public Gstr3BGstinDashboardDto() {
		super();
	}

	public Gstr3BGstinDashboardDto(String level) {
		this.level = level;
	}

	public Gstr3BGstinDashboardDto(String level, Boolean lastLevel,
			String table, String supplyType) {
		super();
		this.level = level;
		this.lastLevel = lastLevel;
		this.table = table;
		this.supplyType = supplyType;
	}
	
	
	
	

}
