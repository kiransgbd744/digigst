package com.ey.advisory.app.docs.dto.gstr9;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 
 * @author Siva.Reddy
 *
 */

@Setter
@Getter
@ToString
public class Gstr9OutwardDashboardDTO {

	private List<Gstr9GstinInOutwardDashBoardDTO> gstr9OutwardDashboard;
}
