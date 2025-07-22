package com.ey.advisory.app.gstr3b;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * @author vishal.verma
 *
 */
@Table(name = "GSTR3B_PDITC")
@Entity
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Gstr3BSaveChangesLiabilitySetOffEntity {

	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "GSTR3B_PDITC_SEQ", allocationSize = 1)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "ID")
	private Long id;
	
	@Column(name = "GSTIN")
	private String gstin;
	
	@Column(name = "TAX_PERIOD")
	private String taxPeriod;
	
	@Column(name = "LIAB_LGD_ID")
	private Long liabLgdId;
	
	@Column(name = "TRANS_TYPE")
	private Long transType;
	
	@Column(name = "I_PDI")
	private BigDecimal iPDIgst;
	
	@Column(name = "I_PDC")
	private BigDecimal iPDCgst;
	
	@Column(name = "I_PDS")
	private BigDecimal iPDSgst;
	
	@Column(name = "C_PDI")
	private BigDecimal cPDIgst;
	
	@Column(name = "C_PDC")
	private BigDecimal cPDCgst;
	
	@Column(name = "S_PDI")
	private BigDecimal sPDIgst;
	
	@Column(name = "S_PDS")
	private BigDecimal sPDSgst;
	
	@Column(name = "CS_PDCS")
	private BigDecimal csPdCess;
	
	@Column(name = "CREATEDTM")
	private LocalDateTime createdOn = LocalDateTime.now();
	
	@Column(name = "UPDATEDTM")
	private LocalDateTime updatedOn = LocalDateTime.now();
	
	@Column(name = "IS_ACTIVE")
	private Boolean isActive;
	
	//81, 2ii
	
	@Column(name = "ADJ2I_IGST")
	private BigDecimal i_adjNegative2i;
	
	
	@Column(name = "ADJ2I_CGST")
	private BigDecimal c_adjNegative2i;
	
	
	@Column(name = "ADJ2I_SGST")
	private BigDecimal s_adjNegative2i;
	
	
	@Column(name = "ADJ2I_CESS")
	private BigDecimal cs_adjNegative2i;
	 
	
	@Column(name = "ADJ8A_IGST")
	private BigDecimal i_adjNegative8A;
	
	
	@Column(name = "ADJ8A_CGST")
	private BigDecimal c_adjNegative8A;
	
	
	@Column(name = "ADJ8A_SGST")
	private BigDecimal s_adjNegative8A;
	
	
	@Column(name = "ADJ8A_CESS")
	private BigDecimal cs_adjNegative8A;

}
