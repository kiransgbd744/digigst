package com.ey.advisory.core.async.domain.master;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Siva.Reddy
 *
 */
@Entity
@Table(name = "FD_CONFG_QUESTION")
@Setter
@Getter
@ToString
public class FeedbackConfigQuestionEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "QUESTION_CATEGORY")
	private String questionCategory;
	
	@Column(name = "QUESTION_SUB_CATEGORY")
	private String questionSubCategory;
	
	@Column(name = "QUESTION_CODE")
	private String questionCode;

	@Column(name = "QUESTION_DESCRIPTION")
	private String questionDescription;

	@Column(name = "QUESTION_TYPE")
	private String questionType;
	
	@Column(name = "IS_ACTIVE")
	private boolean isActive;
	
	
}