package com.ey.advisory.app.services.credit.reversal;

import java.math.BigDecimal;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class CreditReversalForFinancialYearDto {

	@Expose
	@SerializedName("particulars")
	private String particulars;

	@Expose
	@SerializedName("totalIgst")
	private BigDecimal totalIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalCgst")
	private BigDecimal totalCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalSgst")
	private BigDecimal totalSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalCess")
	private BigDecimal totalCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("totalTotalTax")
	private BigDecimal totalTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("aprilIgst")
	private BigDecimal aprilIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("aprilCgst")
	private BigDecimal aprilCgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("aprilSgst")
	private BigDecimal aprilSgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("aprilCess")
	private BigDecimal aprilCess = BigDecimal.ZERO;
	@Expose
	@SerializedName("aprilTotalTax")
	private BigDecimal aprilTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("mayIgst")
	private BigDecimal mayIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("mayCgst")
	private BigDecimal mayCgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("maySgst")
	private BigDecimal maySgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("mayCess")
	private BigDecimal mayCess = BigDecimal.ZERO;
	@Expose
	@SerializedName("mayTotalTax")
	private BigDecimal mayTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("juneIgst")
	private BigDecimal juneIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("juneCgst")
	private BigDecimal juneCgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("juneSgst")
	private BigDecimal juneSgst = BigDecimal.ZERO;
	@Expose
	@SerializedName("juneCess")
	private BigDecimal juneCess = BigDecimal.ZERO;
	@Expose
	@SerializedName("juneTotalTax")
	private BigDecimal juneTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("julyIgst")
	private BigDecimal julyIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("julyCgst")
	private BigDecimal julyCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("julySgst")
	private BigDecimal julySgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("julyCess")
	private BigDecimal julyCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("julyTotalTax")
	private BigDecimal julyTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("augIgst")
	private BigDecimal augIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("augCgst")
	private BigDecimal augCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("augSgst")
	private BigDecimal augSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("augCess")
	private BigDecimal augCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("augTotalTax")
	private BigDecimal augTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("sepIgst")
	private BigDecimal sepIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("sepCgst")
	private BigDecimal sepCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("sepSgst")
	private BigDecimal sepSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("sepCess")
	private BigDecimal sepCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("sepTotalTax")
	private BigDecimal sepTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("octIgst")
	private BigDecimal octIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("octCgst")
	private BigDecimal octCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("octSgst")
	private BigDecimal octSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("octCess")
	private BigDecimal octCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("octTotalTax")
	private BigDecimal octTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("novIgst")
	private BigDecimal novIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("novCgst")
	private BigDecimal novCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("novSgst")
	private BigDecimal novSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("novCess")
	private BigDecimal novCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("novTotalTax")
	private BigDecimal novTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("decIgst")
	private BigDecimal decIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("decCgst")
	private BigDecimal decCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("decSgst")
	private BigDecimal decSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("decCess")
	private BigDecimal decCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("decTotalTax")
	private BigDecimal decTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("janIgst")
	private BigDecimal janIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("janCgst")
	private BigDecimal janCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("janSgst")
	private BigDecimal janSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("janCess")
	private BigDecimal janCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("janTotalTax")
	private BigDecimal janTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("febIgst")
	private BigDecimal febIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("febCgst")
	private BigDecimal febCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("febSgst")
	private BigDecimal febSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("febCess")
	private BigDecimal febCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("febTotalTax")
	private BigDecimal febTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("marchIgst")
	private BigDecimal marchIgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("marchCgst")
	private BigDecimal marchCgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("marchSgst")
	private BigDecimal marchSgst = BigDecimal.ZERO;

	@Expose
	@SerializedName("marchCess")
	private BigDecimal marchCess = BigDecimal.ZERO;

	@Expose
	@SerializedName("marchTotalTax")
	private BigDecimal marchTotalTax = BigDecimal.ZERO;

	@Expose
	@SerializedName("credRevFinaYearDto")
	private List<CreditReversalForFinancialYearDto> credRevFinaYearDtos;

}
