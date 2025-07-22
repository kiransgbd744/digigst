/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
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
import lombok.Getter;
import lombok.Setter;

/**
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "GETANX1_SECTION_SUMMARY")
@Setter
@Getter
public class GetAnx1SectionSummaryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "SECNM")
	private String secNum;

	@Column(name = "CHKSUM")
	private String chksum;

	@Column(name = "NET_TAXABLE_VALUE")
	private BigDecimal netTax;

	@Column(name = "TOTAL_IGST")
	private BigDecimal totalIgst;

	@Column(name = "TOTAL_CESS")
	private BigDecimal totalCess;

	@Column(name = "TOTAL_DOC_VALUE")
	private int totalDocVal;

	@Column(name = "TOTAL_DOC_NUM")
	private int totalDocNum;

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

	@Column(name = "SUMMARY_ID")
	private Long secSummaryId;

	@OneToMany(mappedBy = "actionSecSumId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<GetAnx1ActionSummaryEntity> actionsum = new HashSet<>();

	@OneToMany(mappedBy = "counterSecSumId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<GetAnx1CounterPartySummaryEntity> cptysum = new HashSet<>();

	@OneToMany(mappedBy = "docSecSumId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	private Set<GetAnx1DocTypeSummaryEntity> doctypsum = new HashSet<>();

}
