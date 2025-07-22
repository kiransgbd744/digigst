package com.ey.advisory.app.data.entities.client;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import lombok.Data;
/**
 * 
 * @author Mahesh.Golla
 *
 */
@Data
@Entity
@Table(name = "GETGSTR2A_CDNA_HEADER")
public class GetGstr2aCdnaInvoicesHeaderEntity extends GetGstr2aCdnaInvoicesHeader {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_CDNA_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@OneToMany(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr2aCdnaInvoicesItemEntity> lineItems = new ArrayList<>();
	
	@Column(name = "SENT_TO_ERP_DATE")
	protected LocalDate sentToErpDate;
	
	@Column(name = "IS_SENT_TO_ERP")
	protected boolean isSentToErp;
	
	@Column(name = "DELTA_INV_STATUS")
	protected String deltaInStatus = "N";
	
	@Column(name = "ERP_BATCH_ID")
	protected Long erpBatchId;

}
