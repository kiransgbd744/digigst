package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * 
 * @author Mahesh.Golla
 *
 */

@Entity
@Table(name = "GETGSTR2A_TDS_ITEM")
@Data
public class GetGstr2aTdsInvoicesItemEntity extends GetGstr2aTdsInvoicesItem {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_TDS_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	protected GetGstr2aTDSDetailsEntity header;

}
