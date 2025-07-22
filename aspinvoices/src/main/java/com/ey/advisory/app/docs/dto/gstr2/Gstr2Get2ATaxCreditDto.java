package com.ey.advisory.app.docs.dto.gstr2;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Gstr2Get2ATaxCreditDto {


	@Expose
	@SerializedName("id")
	private Long id;
	@Expose
	@SerializedName("gstin")
	private String gstin;
	@Expose
	@SerializedName("AprilStatus")
	private String AprilStatus;
	@Expose
	@SerializedName("ApriltimeStamp")
	private LocalDateTime ApriltimeStamp;
	@Expose
	@SerializedName("AprilCheckSum")
	private Boolean AprilCheckSum;
	@Expose
	@SerializedName("MayStatus")
	private String MayStatus;
	@Expose
	@SerializedName("maytimeStamp")
	private LocalDateTime maytimeStamp;
	@Expose
	@SerializedName("MayCheckSum")
	private Boolean MayCheckSum;
	@Expose
	@SerializedName("JuneStatus")
	private String JuneStatus;
	@Expose
	@SerializedName("JunetimeStamp")
	private LocalDateTime JunetimeStamp;
	@Expose
	@SerializedName("JuneCheckSum")
	private Boolean JuneCheckSum;
	@Expose
	@SerializedName("JulyStatus")
	private String JulyStatus;
	@Expose
	@SerializedName("JulytimeStamp")
	private LocalDateTime JulytimeStamp;
	@Expose
	@SerializedName("JulyCheckSum")
	private Boolean JulyCheckSum;
	@Expose
	@SerializedName("AugestStatus")
	private String AugestStatus;
	@Expose
	@SerializedName("AugesttimeStamp")
	private LocalDateTime AugesttimeStamp;
	@Expose
	@SerializedName("AugestCheckSum")
	private Boolean AugestCheckSum;
	@Expose
	@SerializedName("SeptemberStatus")
	private String SeptemberStatus;
	@Expose
	@SerializedName("SeptembertimeStamp")
	private LocalDateTime SeptembertimeStamp;
	@Expose
	@SerializedName("SeptemberCheckSum")
	private Boolean SeptemberCheckSum;
	@Expose
	@SerializedName("OctoberStatus")
	private String OctoberStatus;
	@Expose
	@SerializedName("OctobertimeStamp")
	private LocalDateTime OctobertimeStamp;
	@Expose
	@SerializedName("OctoberCheckSum")
	private Boolean OctoberCheckSum;
	@Expose
	@SerializedName("NovemberStatus")
	private String NovemberStatus;
	@Expose
	@SerializedName("NovembertimeStamp")
	private LocalDateTime NovembertimeStamp;
	@Expose
	@SerializedName("NovemberCheckSum")
	private Boolean NovemberCheckSum;
	@Expose
	@SerializedName("DecemberStatus")
	private String DecemberStatus;
	@Expose
	@SerializedName("DecembertimeStamp")
	private LocalDateTime DecembertimeStamp;
	@Expose
	@SerializedName("DecemberCheckSum")
	private Boolean DecemberCheckSum;
	@Expose
	@SerializedName("JanStatus")
	private String JanStatus;
	@Expose
	@SerializedName("JantimeStamp")
	private LocalDateTime JantimeStamp;
	@Expose
	@SerializedName("JanCheckSum")
	private Boolean JanCheckSum;
	@Expose
	@SerializedName("FebStatus")
	private String FebStatus;
	@Expose
	@SerializedName("FebtimeStamp")
	private LocalDateTime FebtimeStamp;
	@Expose
	@SerializedName("FebCheckSum")
	private Boolean FebCheckSum;
	@Expose
	@SerializedName("MarchStatus")
	private String MarchStatus;
	@Expose
	@SerializedName("MarchtimeStamp")
	private LocalDateTime MarchtimeStamp;
	@Expose
	@SerializedName("MarchCheckSum")
	private Boolean MarchCheckSum;
	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * @return the gstin
	 */
	public String getGstin() {
		return gstin;
	}
	/**
	 * @param gstin the gstin to set
	 */
	public void setGstin(String gstin) {
		this.gstin = gstin;
	}
	/**
	 * @return the aprilStatus
	 */
	public String getAprilStatus() {
		return AprilStatus;
	}
	/**
	 * @param aprilStatus the aprilStatus to set
	 */
	public void setAprilStatus(String aprilStatus) {
		AprilStatus = aprilStatus;
	}
	/**
	 * @return the apriltimeStamp
	 */
	public LocalDateTime getApriltimeStamp() {
		return ApriltimeStamp;
	}
	/**
	 * @param apriltimeStamp the apriltimeStamp to set
	 */
	public void setApriltimeStamp(LocalDateTime apriltimeStamp) {
		ApriltimeStamp = apriltimeStamp;
	}
	/**
	 * @return the aprilCheckSum
	 */
	public Boolean getAprilCheckSum() {
		return AprilCheckSum;
	}
	/**
	 * @param aprilCheckSum the aprilCheckSum to set
	 */
	public void setAprilCheckSum(Boolean aprilCheckSum) {
		AprilCheckSum = aprilCheckSum;
	}
	/**
	 * @return the mayStatus
	 */
	public String getMayStatus() {
		return MayStatus;
	}
	/**
	 * @param mayStatus the mayStatus to set
	 */
	public void setMayStatus(String mayStatus) {
		MayStatus = mayStatus;
	}
	/**
	 * @return the maytimeStamp
	 */
	public LocalDateTime getMaytimeStamp() {
		return maytimeStamp;
	}
	/**
	 * @param maytimeStamp the maytimeStamp to set
	 */
	public void setMaytimeStamp(LocalDateTime maytimeStamp) {
		this.maytimeStamp = maytimeStamp;
	}
	/**
	 * @return the mayCheckSum
	 */
	public Boolean getMayCheckSum() {
		return MayCheckSum;
	}
	/**
	 * @param mayCheckSum the mayCheckSum to set
	 */
	public void setMayCheckSum(Boolean mayCheckSum) {
		MayCheckSum = mayCheckSum;
	}
	/**
	 * @return the juneStatus
	 */
	public String getJuneStatus() {
		return JuneStatus;
	}
	/**
	 * @param juneStatus the juneStatus to set
	 */
	public void setJuneStatus(String juneStatus) {
		JuneStatus = juneStatus;
	}
	/**
	 * @return the junetimeStamp
	 */
	public LocalDateTime getJunetimeStamp() {
		return JunetimeStamp;
	}
	/**
	 * @param junetimeStamp the junetimeStamp to set
	 */
	public void setJunetimeStamp(LocalDateTime junetimeStamp) {
		JunetimeStamp = junetimeStamp;
	}
	/**
	 * @return the juneCheckSum
	 */
	public Boolean getJuneCheckSum() {
		return JuneCheckSum;
	}
	/**
	 * @param juneCheckSum the juneCheckSum to set
	 */
	public void setJuneCheckSum(Boolean juneCheckSum) {
		JuneCheckSum = juneCheckSum;
	}
	/**
	 * @return the julyStatus
	 */
	public String getJulyStatus() {
		return JulyStatus;
	}
	/**
	 * @param julyStatus the julyStatus to set
	 */
	public void setJulyStatus(String julyStatus) {
		JulyStatus = julyStatus;
	}
	/**
	 * @return the julytimeStamp
	 */
	public LocalDateTime getJulytimeStamp() {
		return JulytimeStamp;
	}
	/**
	 * @param julytimeStamp the julytimeStamp to set
	 */
	public void setJulytimeStamp(LocalDateTime julytimeStamp) {
		JulytimeStamp = julytimeStamp;
	}
	/**
	 * @return the julyCheckSum
	 */
	public Boolean getJulyCheckSum() {
		return JulyCheckSum;
	}
	/**
	 * @param julyCheckSum the julyCheckSum to set
	 */
	public void setJulyCheckSum(Boolean julyCheckSum) {
		JulyCheckSum = julyCheckSum;
	}
	/**
	 * @return the augestStatus
	 */
	public String getAugestStatus() {
		return AugestStatus;
	}
	/**
	 * @param augestStatus the augestStatus to set
	 */
	public void setAugestStatus(String augestStatus) {
		AugestStatus = augestStatus;
	}
	/**
	 * @return the augesttimeStamp
	 */
	public LocalDateTime getAugesttimeStamp() {
		return AugesttimeStamp;
	}
	/**
	 * @param augesttimeStamp the augesttimeStamp to set
	 */
	public void setAugesttimeStamp(LocalDateTime augesttimeStamp) {
		AugesttimeStamp = augesttimeStamp;
	}
	/**
	 * @return the augestCheckSum
	 */
	public Boolean getAugestCheckSum() {
		return AugestCheckSum;
	}
	/**
	 * @param augestCheckSum the augestCheckSum to set
	 */
	public void setAugestCheckSum(Boolean augestCheckSum) {
		AugestCheckSum = augestCheckSum;
	}
	/**
	 * @return the septemberStatus
	 */
	public String getSeptemberStatus() {
		return SeptemberStatus;
	}
	/**
	 * @param septemberStatus the septemberStatus to set
	 */
	public void setSeptemberStatus(String septemberStatus) {
		SeptemberStatus = septemberStatus;
	}
	/**
	 * @return the septembertimeStamp
	 */
	public LocalDateTime getSeptembertimeStamp() {
		return SeptembertimeStamp;
	}
	/**
	 * @param septembertimeStamp the septembertimeStamp to set
	 */
	public void setSeptembertimeStamp(LocalDateTime septembertimeStamp) {
		SeptembertimeStamp = septembertimeStamp;
	}
	/**
	 * @return the septemberCheckSum
	 */
	public Boolean getSeptemberCheckSum() {
		return SeptemberCheckSum;
	}
	/**
	 * @param septemberCheckSum the septemberCheckSum to set
	 */
	public void setSeptemberCheckSum(Boolean septemberCheckSum) {
		SeptemberCheckSum = septemberCheckSum;
	}
	/**
	 * @return the octoberStatus
	 */
	public String getOctoberStatus() {
		return OctoberStatus;
	}
	/**
	 * @param octoberStatus the octoberStatus to set
	 */
	public void setOctoberStatus(String octoberStatus) {
		OctoberStatus = octoberStatus;
	}
	/**
	 * @return the octobertimeStamp
	 */
	public LocalDateTime getOctobertimeStamp() {
		return OctobertimeStamp;
	}
	/**
	 * @param octobertimeStamp the octobertimeStamp to set
	 */
	public void setOctobertimeStamp(LocalDateTime octobertimeStamp) {
		OctobertimeStamp = octobertimeStamp;
	}
	/**
	 * @return the octoberCheckSum
	 */
	public Boolean getOctoberCheckSum() {
		return OctoberCheckSum;
	}
	/**
	 * @param octoberCheckSum the octoberCheckSum to set
	 */
	public void setOctoberCheckSum(Boolean octoberCheckSum) {
		OctoberCheckSum = octoberCheckSum;
	}
	/**
	 * @return the novemberStatus
	 */
	public String getNovemberStatus() {
		return NovemberStatus;
	}
	/**
	 * @param novemberStatus the novemberStatus to set
	 */
	public void setNovemberStatus(String novemberStatus) {
		NovemberStatus = novemberStatus;
	}
	/**
	 * @return the novembertimeStamp
	 */
	public LocalDateTime getNovembertimeStamp() {
		return NovembertimeStamp;
	}
	/**
	 * @param novembertimeStamp the novembertimeStamp to set
	 */
	public void setNovembertimeStamp(LocalDateTime novembertimeStamp) {
		NovembertimeStamp = novembertimeStamp;
	}
	/**
	 * @return the novemberCheckSum
	 */
	public Boolean getNovemberCheckSum() {
		return NovemberCheckSum;
	}
	/**
	 * @param novemberCheckSum the novemberCheckSum to set
	 */
	public void setNovemberCheckSum(Boolean novemberCheckSum) {
		NovemberCheckSum = novemberCheckSum;
	}
	/**
	 * @return the decemberStatus
	 */
	public String getDecemberStatus() {
		return DecemberStatus;
	}
	/**
	 * @param decemberStatus the decemberStatus to set
	 */
	public void setDecemberStatus(String decemberStatus) {
		DecemberStatus = decemberStatus;
	}
	/**
	 * @return the decembertimeStamp
	 */
	public LocalDateTime getDecembertimeStamp() {
		return DecembertimeStamp;
	}
	/**
	 * @param decembertimeStamp the decembertimeStamp to set
	 */
	public void setDecembertimeStamp(LocalDateTime decembertimeStamp) {
		DecembertimeStamp = decembertimeStamp;
	}
	/**
	 * @return the decemberCheckSum
	 */
	public Boolean getDecemberCheckSum() {
		return DecemberCheckSum;
	}
	/**
	 * @param decemberCheckSum the decemberCheckSum to set
	 */
	public void setDecemberCheckSum(Boolean decemberCheckSum) {
		DecemberCheckSum = decemberCheckSum;
	}
	/**
	 * @return the janStatus
	 */
	public String getJanStatus() {
		return JanStatus;
	}
	/**
	 * @param janStatus the janStatus to set
	 */
	public void setJanStatus(String janStatus) {
		JanStatus = janStatus;
	}
	/**
	 * @return the jantimeStamp
	 */
	public LocalDateTime getJantimeStamp() {
		return JantimeStamp;
	}
	/**
	 * @param jantimeStamp the jantimeStamp to set
	 */
	public void setJantimeStamp(LocalDateTime jantimeStamp) {
		JantimeStamp = jantimeStamp;
	}
	/**
	 * @return the janCheckSum
	 */
	public Boolean getJanCheckSum() {
		return JanCheckSum;
	}
	/**
	 * @param janCheckSum the janCheckSum to set
	 */
	public void setJanCheckSum(Boolean janCheckSum) {
		JanCheckSum = janCheckSum;
	}
	/**
	 * @return the febStatus
	 */
	public String getFebStatus() {
		return FebStatus;
	}
	/**
	 * @param febStatus the febStatus to set
	 */
	public void setFebStatus(String febStatus) {
		FebStatus = febStatus;
	}
	/**
	 * @return the febtimeStamp
	 */
	public LocalDateTime getFebtimeStamp() {
		return FebtimeStamp;
	}
	/**
	 * @param febtimeStamp the febtimeStamp to set
	 */
	public void setFebtimeStamp(LocalDateTime febtimeStamp) {
		FebtimeStamp = febtimeStamp;
	}
	/**
	 * @return the febCheckSum
	 */
	public Boolean getFebCheckSum() {
		return FebCheckSum;
	}
	/**
	 * @param febCheckSum the febCheckSum to set
	 */
	public void setFebCheckSum(Boolean febCheckSum) {
		FebCheckSum = febCheckSum;
	}
	/**
	 * @return the marchStatus
	 */
	public String getMarchStatus() {
		return MarchStatus;
	}
	/**
	 * @param marchStatus the marchStatus to set
	 */
	public void setMarchStatus(String marchStatus) {
		MarchStatus = marchStatus;
	}
	/**
	 * @return the marchtimeStamp
	 */
	public LocalDateTime getMarchtimeStamp() {
		return MarchtimeStamp;
	}
	/**
	 * @param marchtimeStamp the marchtimeStamp to set
	 */
	public void setMarchtimeStamp(LocalDateTime marchtimeStamp) {
		MarchtimeStamp = marchtimeStamp;
	}
	/**
	 * @return the marchCheckSum
	 */
	public Boolean getMarchCheckSum() {
		return MarchCheckSum;
	}
	/**
	 * @param marchCheckSum the marchCheckSum to set
	 */
	public void setMarchCheckSum(Boolean marchCheckSum) {
		MarchCheckSum = marchCheckSum;
	}
	
	
	
	
}
