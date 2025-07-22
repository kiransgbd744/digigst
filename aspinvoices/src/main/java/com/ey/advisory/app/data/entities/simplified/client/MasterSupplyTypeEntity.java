package com.ey.advisory.app.data.entities.simplified.client;

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
 * 
 * @author Anand3.M
 *
 */
@Entity
@Table(name = "MASTER_SUPPLYTYPE")
@Setter
@Getter
@ToString
public class MasterSupplyTypeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "REPORTS_KEY")
	private String repKey;

	@Column(name = "SUPPLYTYPE_KEY")
	private String suppTypeKey;

	@Column(name = "SUPPLYTYPE_NAME")
	private String suppTypeName;

}
