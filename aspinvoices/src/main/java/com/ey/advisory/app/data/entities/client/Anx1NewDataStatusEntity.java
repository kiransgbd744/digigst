package com.ey.advisory.app.data.entities.client;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Balakrishna.S
 *
 */
@Getter
@Setter
@ToString
public class Anx1NewDataStatusEntity {

	private List<String> sgstins;
	private LocalDate docDate;
	private LocalDate receivedDate;
	private Integer derivedRetPeriod;
	private Integer sapTotal = 0;
	private BigInteger totalRecords = BigInteger.ZERO;
	private BigInteger diff = BigInteger.ZERO;

	private BigInteger processedActive = BigInteger.ZERO;
	private BigInteger processedInactive = BigInteger.ZERO;
	private BigInteger errorActive = BigInteger.ZERO;
	private BigInteger errorInactive = BigInteger.ZERO;
	private BigInteger infoActive = BigInteger.ZERO;
	private BigInteger infoInactive = BigInteger.ZERO;

	private BigInteger invNotApplicable = BigInteger.ZERO;
	private BigInteger invAspError = BigInteger.ZERO;
	private BigInteger invAspProcessed = BigInteger.ZERO;
	private BigInteger invIrnInProgress = BigInteger.ZERO;
	private BigInteger invIrnInError = BigInteger.ZERO;
	private BigInteger invIrnProcessed = BigInteger.ZERO;
	private BigInteger invIrnCancelled = BigInteger.ZERO;
	private BigInteger einvVInfoError = BigInteger.ZERO;
	private BigInteger einvNotOpted = BigInteger.ZERO;
	private BigInteger einvId = BigInteger.ZERO;
	
	private BigInteger ewbNotApplicable = BigInteger.ZERO;
	private BigInteger ewbAspError = BigInteger.ZERO;
	private BigInteger ewbAspProcessed = BigInteger.ZERO;
	private BigInteger ewbGenInProgress = BigInteger.ZERO;
	private BigInteger ewbNicError = BigInteger.ZERO;
	private BigInteger ewbPartAGenerated = BigInteger.ZERO;
	private BigInteger ewbCancelled = BigInteger.ZERO;
	private BigInteger ewbGeneratedOnErp = BigInteger.ZERO;
	private BigInteger ewbNotGeneratedOnErp = BigInteger.ZERO;
	private BigInteger ewbNotOpted = BigInteger.ZERO;
	private BigInteger ewbId = BigInteger.ZERO;
	
	private BigInteger aspNA = BigInteger.ZERO;
	private BigInteger aspError = BigInteger.ZERO;
	private BigInteger aspProcess = BigInteger.ZERO;
	private BigInteger aspSaveInitiated = BigInteger.ZERO;
	private BigInteger aspSavedGstin = BigInteger.ZERO;
	private BigInteger aspErrorsGstin = BigInteger.ZERO;
	
	
}
