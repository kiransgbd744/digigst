package com.ey.advisory.app.data.entities.client.asprecon;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "TBL_RECON_2BPR_PATTERN")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Gstr2Recon2BPRPatternEntity {
	
	@Id
	@Column(name = "PATTERN_ID")
	private Long patternId;
	
	@Column(name = "PATTERN")
	private String pattern;
	
	@Column(name = "PATTERN_COUNT")
	private Long patternCount;
	
	@Column(name = "USER_TYPE")
	private String userType;
	
	@Column(name = "FILTER_TYPE")
	private String filterType;	
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
}
