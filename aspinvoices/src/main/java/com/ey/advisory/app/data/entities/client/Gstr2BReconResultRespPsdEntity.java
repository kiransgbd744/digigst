/**
 * 
 */
package com.ey.advisory.app.data.entities.client;

import java.time.LocalDateTime;

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
 * @author Siva Krishna
 *
 */

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TBL_2BPR_RECON_RESP_PSD")
public class Gstr2BReconResultRespPsdEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "INVOICEKEYPR")
	private String invoicekeyPR;
	
	@Column(name = "FMRESPONSE")
	private String fMResponse;

	@Column(name = "RSPTAXPERIOD3B")
	private String rspTaxPeriod3B;
	
	@Column(name = "ENDDTM")
	private LocalDateTime endDtm;
	
	@Column(name = "RGSTINPR")
	private String rGSTINPR;
	
}
