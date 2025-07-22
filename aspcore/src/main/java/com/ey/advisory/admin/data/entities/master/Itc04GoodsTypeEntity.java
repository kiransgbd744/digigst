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
@Table(name = "MASTER_ITC04_GOODS_TYPE")
@Data
public class Itc04GoodsTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID", nullable = false)
	private Long id;

	@Expose
	@Column(name = "GOODS_TYPE")
	private String goodsType;

	@Expose
	@Column(name = "DESCRIPTION")
	private String description;

}