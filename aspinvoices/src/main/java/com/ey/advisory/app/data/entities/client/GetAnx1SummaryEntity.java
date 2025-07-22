package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "GETANX1_SUMMARY_TABLE")
@Setter
@Getter
public class GetAnx1SummaryEntity {

	@Expose
	@SerializedName("id")
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "BATCH_ID")
	private Long batchId;

	@Column(name = "GSTIN")
	private String gstin;

	@Column(name = "RETURN_PERIOD")
	private String retPeriod;

	@Column(name = "CHKSUM")
	private String checksum;

	@Column(name = "SUMMARY_TYPE")
	private String summaryType;

	@Column(name = "LAST_SUMMARY_TS")
	private LocalDateTime lastSummaryTs;

	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "MODIFIED_ON")
	private LocalDateTime modifiedOn;

	@Column(name = "MODIFIED_BY")
	private String modifiedBy;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;

/*	@OneToMany(mappedBy = "secSummaryId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<GetAnx1SectionSummaryEntity> secSummary = new ArrayList<>();

	@OneToMany(mappedBy = "b2cSumId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<GetAnx1B2cSecSummaryEntity> b2cSummary = new ArrayList<>();

	@OneToMany(mappedBy = "ecomSummaryId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<GetAnx1EcomSecSummaryEntity> ecomSummary = new ArrayList<>();

	@OneToMany(mappedBy = "impsSummaryId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<GetAnx1ImpsImpgMisSecSummaryEntity> impsSummary = new ArrayList<>();

	@OneToMany(mappedBy = "impgsezSummaryId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<GetAnx1ImpgsezRevSecSummaryEntity> impgsezSummary = new ArrayList<>();
	
	@OneToMany(mappedBy = "expwpSummaryId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private List<GetAnx1ExpwpExpwopSecSummaryEntity> expwpSummaryId = new ArrayList<>();*/


}
