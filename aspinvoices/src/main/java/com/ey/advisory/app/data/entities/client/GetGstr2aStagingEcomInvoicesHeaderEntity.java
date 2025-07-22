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
 * @author Ravindra V S
 *
 */

@Entity
@Table(name = "GETGSTR2A_STAGING_ECOM_HEADER")
@Data
public class GetGstr2aStagingEcomInvoicesHeaderEntity 
		extends GetGstr2aEcomInvoicesHeader {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_ECOM_STAGING_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@OneToMany(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr2aStagingEcomInvoicesItemEntity> lineItems = new ArrayList<>();

}
