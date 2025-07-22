package com.ey.advisory.app.services.dashboard.fiori;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Ravindra V S
 *
 */
@Getter
@Setter
public class LastRefreshedOnDto {

	private LocalDateTime lastRefreshedOn2A;
	private LocalDateTime lastRefreshedOn2B;

}
