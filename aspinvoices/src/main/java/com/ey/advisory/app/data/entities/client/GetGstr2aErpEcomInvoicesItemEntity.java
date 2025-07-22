/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Entity
@Table(name = "GETGSTR2A_ERP_ECOM_ITEM")
@Data
public class GetGstr2aErpEcomInvoicesItemEntity
		extends GetGstr2aB2bInvoicesItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	protected GetGstr2aErpEcomInvoicesHeaderEntity header;
}
