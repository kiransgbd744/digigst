package com.ey.advisory.app.data.entities.client;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * @author Hemasundar.J
 *
 */

@Entity
@Table(name = "GETGSTR2A_STAGING_B2B_HEADER")
@Data
public class GetGstr2aStagingB2bInvoicesHeaderEntity 
		extends GetGstr2aB2bInvoicesHeader {

	@Id
	//@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_STAGING_B2B_HEADER_SEQ", allocationSize = 100)
	@SequenceGenerator(name = "sequence", sequenceName = "GETGSTR2A_B2B_HEADER_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;

	@OneToMany(mappedBy = "header")
	@OrderColumn(name = "ITEM_INDEX")
	@LazyCollection(LazyCollectionOption.FALSE)
	@Cascade({ org.hibernate.annotations.CascadeType.ALL })
	protected List<GetGstr2aStagingB2bInvoicesItemEntity> lineItems = new ArrayList<>();

}
