/**
 * 
 */
package com.ey.advisory.app.data.entities.client.asprecon;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

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
@Table(name = "TBL_EWB_FU_VEHICLE")
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class EwbUploadProcessedVehicleEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Expose
	@SerializedName("id")
	@Id
	@SequenceGenerator(name = "sequence", sequenceName = "TBL_EWB_FU_VEHICLE_SEQ", allocationSize = 100)
	@GeneratedValue(generator = "sequence", strategy = GenerationType.SEQUENCE)
	@Column(name = "EWB_VEHICLE_ID")
	protected Long id;

	@ManyToOne
	@JoinColumn(name = "EWB_HEADER_ID", referencedColumnName = "EWB_HEADER_ID")
	protected EwbUploadProcessedHeaderEntity ewbHeaderId;
	
    @SerializedName("updMode")
    @Expose
	@Column(name = "UPD_MODE")
	protected String updMode;

    @SerializedName("vehicleNo")
    @Expose
	@Column(name = "VEHICLE_NO")
	protected String vehicleNo;
	
    @SerializedName("fromPlace")
    @Expose
	@Column(name = "FROM_PLACE")
	protected String fromePlace;

    @SerializedName("fromState")
    @Expose
	@Column(name = "FROM_STATE")
	protected String fromState;

    @SerializedName("tripshtNo")
    @Expose
	@Column(name = "TRIPSHT_NO")
	protected String tripshtNo;

    @SerializedName("userGSTINTransin")
    @Expose
	@Column(name = "USER_GSTIN_TRANSIN")
	protected String userGstinTransin;

    @SerializedName("enteredDate")
    @Expose
	@Column(name = "ENTERED_DATE")
	protected LocalDateTime enteredDate;

    @SerializedName("transMode")
    @Expose
	@Column(name = "TRANS_MODE")
	protected String transMode;

    @SerializedName("transDocNo")
    @Expose
	@Column(name = "TRANS_DOC_NO")
	protected String transportDocNo;

    @SerializedName("transDocDate")
    @Expose
	@Column(name = "TRANS_DOC_DATE")
	protected LocalDate transportDocDate;
	
    @SerializedName("vehicleType")
    @Expose
	@Column(name = "VEHICLE_TYPE")
	protected String vehicleType;
	
    @SerializedName("groupNo")
    @Expose
	@Column(name = "GROUP_NO")
	protected String groupNo;

	@Expose
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@Expose
	@Column(name = "CREATED_BY")
	protected String createdBy;


}
