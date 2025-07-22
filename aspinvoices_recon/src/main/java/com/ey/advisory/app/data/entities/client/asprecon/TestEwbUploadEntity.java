/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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

@Entity
@Table(name = "TEST_EWB_UPLOAD")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TestEwbUploadEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ID")
	protected Long id;

	@Expose
	@Column(name = "EWBNo")
	protected Long ewbNo;

	@Expose
	@Column(name = "Supply_Type")
	protected String supplyType;

	@Expose
	@Column(name = "EWB_Date")
	protected LocalDateTime ewbDate;

}
