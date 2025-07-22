package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Jithendra.B
 *
 */

@Setter
@Getter
@ToString
public class Gstr9InwardDashboardDTO {

	private List<Gstr9GstinInOutwardDashBoardDTO> gstr9InwardDashboard;
}
