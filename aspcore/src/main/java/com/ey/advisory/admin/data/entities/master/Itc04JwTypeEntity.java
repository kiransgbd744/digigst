/**
 * 
 */
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
 * @author Laxmi.Salukuti
 *
 */
@Entity
@Table(name = "MASTER_ITC04_JW_TYPE")
@Data
public class Itc04JwTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "JW_TYPE")
	private String jwType;

	@Expose
	@Column(name = "DESCRIPTION")
	private String description;

}