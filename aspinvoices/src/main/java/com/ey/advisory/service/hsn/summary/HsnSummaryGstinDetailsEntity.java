package com.ey.advisory.service.hsn.summary;

import java.io.Serializable;

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
 * @author Shashikant.Shukla
 *
 */
@Entity
@Table(name = "TBL_HSN_SMRY_GSTIN_DETAILS")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class HsnSummaryGstinDetailsEntity implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "RECON_CONFIG_ID")
	private Long configId;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;

	@Column(name = "STATUS")
	private String status;
}
