/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */
@Data
@Entity
@Table(name = "GETGSTR2A_ERP_B2BA_HEADER")
public class GetGstr2aErpB2baInvoicesHeaderEntity extends GetGstr2aB2baInvoicesHeader{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@OneToMany(mappedBy = "header", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	protected List<GetGstr2aErpB2baInvoicesItemEntity> lineItems = new ArrayList<>();
	
	@Column(name = "ERP_BATCH_ID")
	protected Long erpBatchId;
	
	@Column(name = "GET_BATCH_ID")
	protected Long getBatchId;
}
