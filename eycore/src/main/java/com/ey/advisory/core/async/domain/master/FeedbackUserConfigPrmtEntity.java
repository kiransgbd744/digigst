package com.ey.advisory.core.async.domain.master;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @POST For USERNAME_CONFIG_PARAMTR
 * 
 * @author Siva.Reddy
 *
 */
@Entity
@Table(name = "FD_USERNAME_CONFG_PRMTR")
@Setter
@Getter
@ToString
@EqualsAndHashCode
public class FeedbackUserConfigPrmtEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "GROUP_CODE")
	private String groupCode;

	@Column(name = "USERNAME")
	private String userName;

	@Column(name = "FD_CONFG_QUESTION_ID")
	private Long confgPrmtId;
	
	@Column(name = "QUESTION_CODE")
	private String questionCode;

	@Column(name = "ANSWER")
	private String answer;
	
	@Column(name = "IS_DELETE")
	private boolean isDelete;
	
	@Column(name = "DERIVED_ANSWER")
	private Integer derivedAnswer;
	
	@Column(name = "FILE_PATH")
	private String filePath;
	
	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "CREATED_BY")
	private String createdBy;
	
	@Column(name = "CREATED_ON")
	private LocalDateTime createdOn;
	
//	@Column(name = "MODIFIED_BY")
//	private String modifiedBy;
//	
//	@Column(name = "MODIFIED_ON")
//	private LocalDateTime modifiedOn;
	
}