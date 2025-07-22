/**
 * 
 */
package com.ey.advisory.gstnapi.domain.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * @author Sachindra.S
 *
 */
@Entity
@Table(name="EINV_NIC_ERROR")
public class NICError {

	
	@Id
	@Column(name = "ID") 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	@Column(name="NIC_ERR_CATEG")
	protected String errCategory;
	
	@Column(name="NIC_ERR_CODE")
	protected Integer errCode;
	
	@Column(name="ERR_DESC")
	protected String errDesc;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getErrCategory() {
		return errCategory;
	}

	public void setErrCategory(String errCategory) {
		this.errCategory = errCategory;
	}

	public Integer getErrCode() {
		return errCode;
	}

	public void setErrCode(Integer errCode) {
		this.errCode = errCode;
	}

	public String getErrDesc() {
		return errDesc;
	}

	public void setErrDesc(String errDesc) {
		this.errDesc = errDesc;
	}
	
}
	
