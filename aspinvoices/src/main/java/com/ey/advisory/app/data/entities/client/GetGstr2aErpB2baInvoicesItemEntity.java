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

/**
 * @author Hemasundar.J
 *
 */

@Entity
@Table(name = "GETGSTR2A_ERP_B2BA_ITEM")
@Data
public class GetGstr2aErpB2baInvoicesItemEntity extends GetGstr2aB2baInvoicesItem {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@ManyToOne
	@JoinColumn(name = "HEADER_ID", referencedColumnName = "ID", nullable = false)
	protected GetGstr2aErpB2baInvoicesHeaderEntity header;


}
