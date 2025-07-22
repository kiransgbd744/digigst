package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "SAVE_RCM_OPENING_BAL")
@Data
public class SaveRcmOpeningBalEntity {

	@Id
    @Column(name = "ID", nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "SAVE_STATUS")
	private String saveStatus;
	
	@Column(name = "IGST")
	private BigDecimal igst;
	
	@Column(name = "CGST")
	private BigDecimal cgst;
	
	@Column(name = "SGST")
	private BigDecimal sgst;
	
	@Column(name = "CESS")
	private BigDecimal cess;
	
	@Column(name = "INITIATED_ON")
	private LocalDateTime initiatedOn;
	
	@Column(name = "COMPLETED_ON")
	private LocalDateTime completedOn;
	
	@Column(name = "ERR_MSG")
	private String errMsg;
	
	@Column(name = "STATUS")
	private String status;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
}
