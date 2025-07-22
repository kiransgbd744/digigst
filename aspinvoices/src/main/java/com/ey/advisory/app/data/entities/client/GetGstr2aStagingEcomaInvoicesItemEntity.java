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
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author Ravindra V S
 *
 */
@Entity
@Table(name = "GETGSTR2A_STAGING_ECOMA_ITEM")
@Data
public class GetGstr2aStagingEcomaInvoicesItemEntity 
		extends GetGstr2aEcomaInvoicesItem {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_ECOMA_STAGING_ITEM_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	protected GetGstr2aStagingEcomaInvoicesHeaderEntity header;

}
