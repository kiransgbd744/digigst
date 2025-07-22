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
@Table(name = "FD_CONFG_ANSWER")
@Setter
@Getter
@ToString
public class FeedbackConfigAnswerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "FD_CONFG_QUESTION_ID")
	private String fdConfgQuestionId;

	@Column(name = "ANSWER_OPTIONS")
	private String answerOptions;

	@Column(name = "ANSWER_OPTIONS_DESCRIPTION")
	private String answerOptionsDesc;

	@Column(name = "QUESTION_TYPE")
	private String questionType;
	
}