package com.ey.advisory.admin.data.entities.client;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

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
 */
@Entity
@Table(name = "GROUP_INFO")
@Component
public class GroupInfoEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;
	
	@Expose
	@Column(name="GROUP_CODE", length = 10)
	private String groupcode;
	
	@Expose
	@Column(name="GROUP_NAME", length = 60)
	private String groupname;
	
	@Expose
	@Column(name = "PAN_COUNT", nullable = false)
	private Long pancount;
	
	@Expose
	@Column(name = "GSTIN_COUNT", nullable = false)
	private Long gstincount;
	
	@Expose
	@Column(name = "IS_DELETE", nullable = false)
	private boolean is_active;
	
	@Expose
	@Column(name="ACCOUNT_NO", length = 100)
	private String accountno;
	
	@Expose
	@Column(name = "CREATED_DATE")
	private LocalDateTime created_date;
	
	@Expose
	@Column(name = "MODIFIED_DATE")
	private LocalDateTime update_date;

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
	 * @return the groupcode
	 */
	public String getGroupcode() {
		return groupcode;
	}

	/**
	 * @param groupcode the groupcode to set
	 */
	public void setGroupcode(String groupcode) {
		this.groupcode = groupcode;
	}

	/**
	 * @return the groupname
	 */
	public String getGroupname() {
		return groupname;
	}

	/**
	 * @param groupname the groupname to set
	 */
	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	/**
	 * @return the pancount
	 */
	public Long getPancount() {
		return pancount;
	}

	/**
	 * @param pancount the pancount to set
	 */
	public void setPancount(Long pancount) {
		this.pancount = pancount;
	}

	/**
	 * @return the gstincount
	 */
	public Long getGstincount() {
		return gstincount;
	}

	/**
	 * @param gstincount the gstincount to set
	 */
	public void setGstincount(Long gstincount) {
		this.gstincount = gstincount;
	}

	/**
	 * @return the is_active
	 */
	public boolean isIs_active() {
		return is_active;
	}

	/**
	 * @param is_active the is_active to set
	 */
	public void setIs_active(boolean is_active) {
		this.is_active = is_active;
	}

	/**
	 * @return the accountno
	 */
	public String getAccountno() {
		return accountno;
	}

	/**
	 * @param accountno the accountno to set
	 */
	public void setAccountno(String accountno) {
		this.accountno = accountno;
	}

	/**
	 * @return the created_date
	 */
	public LocalDateTime getCreated_date() {
		return created_date;
	}

	/**
	 * @param created_date the created_date to set
	 */
	public void setCreated_date(LocalDateTime created_date) {
		this.created_date = created_date;
	}

	/**
	 * @return the update_date
	 */
	public LocalDateTime getUpdate_date() {
		return update_date;
	}

	/**
	 * @param update_date the update_date to set
	 */
	public void setUpdate_date(LocalDateTime update_date) {
		this.update_date = update_date;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "GroupInfoEntity [id=" + id + ", groupcode=" + groupcode
				+ ", groupname=" + groupname + ", pancount=" + pancount
				+ ", gstincount=" + gstincount + ", is_active=" + is_active
				+ ", accountno=" + accountno + ", created_date=" + created_date
				+ ", update_date=" + update_date + "]";
	}
	
	
}
