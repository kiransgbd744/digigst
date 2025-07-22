package com.ey.advisory.app.data.entities.client;

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
 * @author Hemasundar.J
 *
 */
@Data
@Entity
@Table(name = "GETGSTR2A_STAGING_TDS_HEADER")
public class GetGstr2aStagingTDSDetailsEntity  extends GetGstr2aTDSDetailsHeader {

	@Id
	//@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_STAGING_TDS_HEADER_SEQ", allocationSize = 100)
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_TDS_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	protected Long id;

	@OneToMany(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr2aStagingTdsInvoicesItemEntity> lineItems = new ArrayList<>();
	

}
