package com.ey.advisory.ewb.client.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.ey.advisory.common.EWBLocalDateTimeAdapter;
import com.ey.advisory.common.LocalDateAdapter;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


/**
 * 
 * @author Arun.KA
 *
 */
@Entity
@Table(name="EWB_VEHICLE")
@Getter
@Setter
@ToString
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class EWBVehicle {

	@Id
	@Column(name = "ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	protected Long id;
	
	@Column(name = "UPD_MODE")
	@Expose @SerializedName("updMode")
	@XmlElement(name = "UPD_MODE")
	protected String updMode;
	
	@Column(name = "VEHICLE_NO")
	@Expose 
	@SerializedName("VEHICLE_NO")
	@XmlElement(name = "VEHICLE_NO")
	protected String vehicleNo;
	
	@Column(name = "FROM_PLACE")
	@Expose
	@SerializedName("fromPlace")
	@XmlElement(name = "FROM_PLACE")
	protected String fromePlace;
		
	@Column(name = "FROM_STATE")
	@Expose 
	@SerializedName("fromState")
	@XmlElement(name = "FROM_STATE")
	protected String fromState;
	
	@Column(name = "TRIPSHT_NO")
	@Expose 
	@SerializedName("tripshtNo")
	@XmlElement(name = "TRIPSHT_NO")
	protected String tripshtNo;
	
	@Column(name = "USER_GSTIN_TRANSIN")
	@Expose 
	@SerializedName("userGstinTransin")
	@XmlElement(name = "USERGSTINTRANSIN")
	protected String userGstinTransin;	
	
	@Column(name = "ENTERED_DATE")	
	@XmlJavaTypeAdapter(value = EWBLocalDateTimeAdapter.class)
	@XmlElement(name = "ENTERED_DATE")
	protected LocalDateTime enteredDate;	
	
	@Column(name = "TRANS_MODE")
	@Expose
	@SerializedName("transMode")
	@XmlElement(name = "TRANS_MODE")
	protected String transMode;
	
	@Column(name = "TRANS_DOC_NO")
	@Expose 
	@SerializedName("transDocNo")
	@XmlElement(name = "TRANS_DOC_NO")
	protected String transportDocNo;
	
	@Column(name = "TRANS_DOC_DATE")
	@Expose 
	@SerializedName("transDocDate")
	@XmlJavaTypeAdapter(value = LocalDateAdapter.class)
	@XmlElement(name = "TRANS_DOC_DATE")
	protected LocalDate transportDocDate;
	
	@Column(name = "VEHICLE_TYPE")
	@Expose
	@SerializedName("vehicleType")
	@XmlElement(name = "VEHICLE_TYPE")
	protected String vehicleType;

	@Column(name = "GROUP_NO")
	@Expose @SerializedName("groupNo")
	@XmlElement(name = "GROUP_NO")
	protected String groupNo;
	
	@Column(name = "CREATED_BY")
	@Expose 
	@SerializedName("createdBy")
	@XmlElement(name = "CREATED_BY")
	protected String createdBy;
	
	@Column(name = "CREATED_ON")
	@Expose @SerializedName("createdOn")
	@XmlElement(name = "CREATED_ON")
	protected Date createdOn;
	
	@Column(name = "MODIFIED_BY")
	@Expose
	@SerializedName("modifiedBy")
	@XmlElement(name = "MODIFIED_BY")
	protected String modifiedBy;
	
	@Column(name = "MODIFIED_ON")
	@Expose @SerializedName("modifiedOn")
	@XmlElement(name = "MODIFIED_ON")
	protected LocalDateTime modifiedOn;
	
	@Column(name = "IS_DELETE")
	@Expose 
	@SerializedName("isDelete")
	@XmlElement(name = "IS_DELETE")
	protected Boolean isDelete;
	
	@ManyToOne
	@JoinColumn(name="EWB_GET_ID", referencedColumnName="ID")
	@XmlTransient
	protected EWBHeader eWayBill;
		
	
	
}
