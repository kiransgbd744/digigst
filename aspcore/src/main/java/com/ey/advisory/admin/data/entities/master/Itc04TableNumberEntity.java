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
@Table(name = "MASTER_ITC04_TABLE_NUMBER")
@Data
public class Itc04TableNumberEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "TABLE_NUMBER")
	private String tableNumber;

	@Expose
	@Column(name = "TABLE_DESCRIPTION")
	private String tableDesc;
	
}