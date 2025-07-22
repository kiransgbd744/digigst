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
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "MASTER_GSTR2X_CATEGORY")
@Data
public class CategoryMasterEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "CATEGORY")
	private String category;
	
	@Expose
	@Column(name = "CATEGORY_DESCRIPTION")
	private String description;
}