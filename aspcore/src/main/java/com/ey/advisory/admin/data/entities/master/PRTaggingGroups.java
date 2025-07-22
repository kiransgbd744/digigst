package com.ey.advisory.admin.data.entities.master;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 
 * @author Sakshi.jain
 *
 */

@Entity
@Table(name = "TBL_PR_TAGGING_GROUPS")
@Data
public class PRTaggingGroups {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "GROUP_CODE")
	private String groupCode;
	
	@Expose
	@Column(name = "IS_PR_REQUIRED")
	private boolean isPrRequired;
}