/**
 * 
 */
package com.ey.advisory.app.dto.compute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * @author Khalid1.Khan
 *
 */
public class Annexure2ComputeDto {
	
	@Expose
	@SerializedName("a2InvoiceKey")
	private String a2InvoiceKey;
	
	@Expose
	@SerializedName("prInvoiceKey")
	private String prInvoiceKey;
	
	@Expose
	@SerializedName("matchingType")
	private String matchingType;
	
	@Expose
	@SerializedName("action")
	private String action;

	/**
	 * @return the a2InvoiceKey
	 */
	public String getA2InvoiceKey() {
		return a2InvoiceKey;
	}

	/**
	 * @param a2InvoiceKey the a2InvoiceKey to set
	 */
	public void setA2InvoiceKey(String a2InvoiceKey) {
		this.a2InvoiceKey = a2InvoiceKey;
	}

	/**
	 * @return the prInvoiceKey
	 */
	public String getPrInvoiceKey() {
		return prInvoiceKey;
	}

	/**
	 * @param prInvoiceKey the prInvoiceKey to set
	 */
	public void setPrInvoiceKey(String prInvoiceKey) {
		this.prInvoiceKey = prInvoiceKey;
	}

	/**
	 * @return the matchingType
	 */
	public String getMatchingType() {
		return matchingType;
	}

	/**
	 * @param matchingType the matchingType to set
	 */
	public void setMatchingType(String matchingType) {
		this.matchingType = matchingType;
	}

	/**
	 * @return the action
	 */
	public String getAction() {
		return action;
	}

	/**
	 * @param action the action to set
	 */
	public void setAction(String action) {
		this.action = action;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((a2InvoiceKey == null) ? 0 : a2InvoiceKey.hashCode());
		result = prime * result + ((action == null) ? 0 : action.hashCode());
		result = prime * result + ((matchingType == null) ? 0 : matchingType.hashCode());
		result = prime * result + ((prInvoiceKey == null) ? 0 : prInvoiceKey.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Annexure2ComputeDto other = (Annexure2ComputeDto) obj;
		if (a2InvoiceKey == null) {
			if (other.a2InvoiceKey != null)
				return false;
		} else if (!a2InvoiceKey.equals(other.a2InvoiceKey))
			return false;
		if (action == null) {
			if (other.action != null)
				return false;
		} else if (!action.equals(other.action))
			return false;
		if (matchingType == null) {
			if (other.matchingType != null)
				return false;
		} else if (!matchingType.equals(other.matchingType))
			return false;
		if (prInvoiceKey == null) {
			if (other.prInvoiceKey != null)
				return false;
		} else if (!prInvoiceKey.equals(other.prInvoiceKey))
			return false;
		return true;
	}
	
	

}
