package com.ey.advisory.app.data.services.compliancerating;

import java.time.LocalDate;

import lombok.Data;

/**
 * @author Jithendra.B
 *
 */
@Data
public class VendorRegDateDto {

	private LocalDate regDate;
	private LocalDate canDate;
}
