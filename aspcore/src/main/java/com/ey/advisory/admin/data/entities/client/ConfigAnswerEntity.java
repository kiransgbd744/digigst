package com.ey.advisory.admin.data.entities.client;

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
 * @author Umesha.M
 *
 */
@Entity
@Table(name = "CONFG_ANSWER")
@Setter
@Getter
@ToString
public class ConfigAnswerEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Column(name = "CONFG_QUESTION_ID")
	private String confgQuestionId;

	@Column(name = "ANSWER_OPTIONS")
	private String answerOptions;

	@Column(name = "ANSWER_OPTIONS_DESCRIPTION")
	private String answerOptionsDesc;

	@Column(name = "QUESTION_TYPE")
	private String questionType;

}