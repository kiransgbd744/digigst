package com.ey.advisory.app.docs.dto.gstr2;

import java.time.LocalDateTime;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TaxCreditMatchingStatusDto {

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
	@SerializedName("MayStatus")
	private String MayStatus;
	@Expose
	@SerializedName("maytimeStamp")
	private LocalDateTime maytimeStamp;
	
	@Expose
	@SerializedName("JuneStatus")
	private String JuneStatus;
	@Expose
	@SerializedName("JunetimeStamp")
	private LocalDateTime JunetimeStamp;
	
	@Expose
	@SerializedName("JulyStatus")
	private String JulyStatus;
	@Expose
	@SerializedName("JulytimeStamp")
	private LocalDateTime JulytimeStamp;
	
	@Expose
	@SerializedName("AugestStatus")
	private String AugestStatus;
	@Expose
	@SerializedName("AugesttimeStamp")
	private LocalDateTime AugesttimeStamp;
	
	@Expose
	@SerializedName("SeptemberStatus")
	private String SeptemberStatus;
	@Expose
	@SerializedName("SeptembertimeStamp")
	private LocalDateTime SeptembertimeStamp;
	
	@Expose
	@SerializedName("OctoberStatus")
	private String OctoberStatus;
	@Expose
	@SerializedName("OctobertimeStamp")
	private LocalDateTime OctobertimeStamp;
	
	@Expose
	@SerializedName("NovemberStatus")
	private String NovemberStatus;
	@Expose
	@SerializedName("NovembertimeStamp")
	private LocalDateTime NovembertimeStamp;
	
	@Expose
	@SerializedName("DecemberStatus")
	private String DecemberStatus;
	@Expose
	@SerializedName("DecembertimeStamp")
	private LocalDateTime DecembertimeStamp;
	
	@Expose
	@SerializedName("JanStatus")
	private String JanStatus;
	@Expose
	@SerializedName("JantimeStamp")
	private LocalDateTime JantimeStamp;
	
	@Expose
	@SerializedName("FebStatus")
	private String FebStatus;
	@Expose
	@SerializedName("FebtimeStamp")
	private LocalDateTime FebtimeStamp;
	
	@Expose
	@SerializedName("MarchStatus")
	private String MarchStatus;
	@Expose
	@SerializedName("MarchtimeStamp")
	private LocalDateTime MarchtimeStamp;
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
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "TaxCreditMatchingStatusDto [id=" + id + ", gstin=" + gstin
				+ ", AprilStatus=" + AprilStatus + ", ApriltimeStamp="
				+ ApriltimeStamp + ", MayStatus=" + MayStatus
				+ ", maytimeStamp=" + maytimeStamp + ", JuneStatus="
				+ JuneStatus + ", JunetimeStamp=" + JunetimeStamp
				+ ", JulyStatus=" + JulyStatus + ", JulytimeStamp="
				+ JulytimeStamp + ", AugestStatus=" + AugestStatus
				+ ", AugesttimeStamp=" + AugesttimeStamp + ", SeptemberStatus="
				+ SeptemberStatus + ", SeptembertimeStamp=" + SeptembertimeStamp
				+ ", OctoberStatus=" + OctoberStatus + ", OctobertimeStamp="
				+ OctobertimeStamp + ", NovemberStatus=" + NovemberStatus
				+ ", NovembertimeStamp=" + NovembertimeStamp
				+ ", DecemberStatus=" + DecemberStatus + ", DecembertimeStamp="
				+ DecembertimeStamp + ", JanStatus=" + JanStatus
				+ ", JantimeStamp=" + JantimeStamp + ", FebStatus=" + FebStatus
				+ ", FebtimeStamp=" + FebtimeStamp + ", MarchStatus="
				+ MarchStatus + ", MarchtimeStamp=" + MarchtimeStamp + "]";
	}
	
	
}
