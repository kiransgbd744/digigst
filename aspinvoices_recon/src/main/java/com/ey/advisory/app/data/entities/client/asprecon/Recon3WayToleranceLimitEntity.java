package com.ey.advisory.app.data.entities.client.asprecon;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Sakshi.jain
 *
 */
@Entity
@Table(name = "TBL_3WAY_TOLERANCE_LIMIT")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Recon3WayToleranceLimitEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "T_ID")
	private Long toleranceId;

	@Column(name = "RECON_CONFIG_ID")
	private Long configId;

	@Column(name = "TAXABLE_VALUE")
	private BigDecimal taxableValue;
	
	@Column(name = "CGST")
	private BigDecimal cGST;
	
	@Column(name = "SGST")
	private BigDecimal sGST;
	
	@Column(name = "IGST")
	private BigDecimal iGST;
	
	@Column(name = "G_CESS")
	private BigDecimal gCess;

	@Column(name = "CREATED_DATE")
	private LocalDate createdDate;

}
