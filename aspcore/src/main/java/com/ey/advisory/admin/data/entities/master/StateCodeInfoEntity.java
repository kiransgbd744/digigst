package com.ey.advisory.admin.data.entities.master;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 
 * @author Siva.Nandam
 *
 * This class is works as stateCode master Table
 */
@Entity
@Table(name = "MASTER_STATE")
public class StateCodeInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "State_ID", nullable = false)
	private Long id;
	
	@Expose
	@Column(name = "STATE_CODE")
	private String stateCode;
	
	@Expose
	@Column(name = "STATE_NAME")
	private String stateName;
	
	@Expose
	@Column(name = "STATE_IDENTIFIER")
	private String stateIdentefier;

	@Expose
	@Column(name = "IS_UT")
	private boolean isUt;

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}

	
	/**
	 * @return the stateCode
	 */
	public String getStateCode() {
		return stateCode;
	}

	/**
	 * @param stateCode the stateCode to set
	 */
	public void setStateCode(String stateCode) {
		this.stateCode = stateCode;
	}

	/**
	 * @return the stateName
	 */
	public String getStateName() {
		return stateName;
	}

	/**
	 * @param stateName the stateName to set
	 */
	public void setStateName(String stateName) {
		this.stateName = stateName;
	}

	/**
	 * @return the stateIdentefier
	 */
	public String getStateIdentefier() {
		return stateIdentefier;
	}

	/**
	 * @param stateIdentefier the stateIdentefier to set
	 */
	public void setStateIdentefier(String stateIdentefier) {
		this.stateIdentefier = stateIdentefier;
	}

	/**
	 * @return the isUt
	 */
	public boolean isUt() {
		return isUt;
	}

	/**
	 * @param isUt the isUt to set
	 */
	public void setUt(boolean isUt) {
		this.isUt = isUt;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StateCodeInfoEntity [id=" + id + ", stateCode=" + stateCode
				+ ", stateName=" + stateName + ", stateIdentefier="
				+ stateIdentefier + ", isUt=" + isUt + "]";
	}

	
	
}
