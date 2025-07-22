package com.ey.advisory.gstnapi.repositories.master;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.ey.advisory.gstnapi.domain.master.GstnAPIAuthInfo;

@Repository("GstinAPIAuthInfoRepository")
@Transactional(propagation = Propagation.REQUIRED, readOnly = false)
public interface GstinAPIAuthInfoRepository
		extends JpaRepository<GstnAPIAuthInfo, Long>,
		JpaSpecificationExecutor<GstnAPIAuthInfo> {

	/**
	 * Find the Authorization details for the specified GSTIN. This entity will
	 * contain both the GSTN and GSP authorization information.
	 * 
	 * @param gstin
	 * @return
	 */
	public GstnAPIAuthInfo findByGstinAndProviderName(String gstin,
			String providerName);

	@Query("select g from GstnAPIAuthInfo g where g.gstin IN :gstins and g.providerName = :providerName")
	public List<GstnAPIAuthInfo> findByGstins(
			@Param("gstins") List<String> gstins,
			@Param("providerName") String providerName);

	public List<GstnAPIAuthInfo> findAllByProviderNameAndGroupCodeNotInAndGstnTokenExpiryTimeBetween(
			String providerName, List<String> groupCode, Date expiryStTime,
			Date expiryEndTime, Sort arg1);

	public List<GstnAPIAuthInfo> findAllByProviderNameAndGroupCode(
			String providerName, String groupCode);

	@Query("SELECT g FROM GstnAPIAuthInfo g WHERE g.providerName = :providerName"
			+ " AND g.groupCode = :groupCode AND g.gstin IN :gstins AND "
			+ " (g.gstnTokenExpiryTime < :currentTime OR g.gstnTokenExpiryTime "
			+ " BETWEEN :expiryStTime AND :expiryEndTime)")
	public List<GstnAPIAuthInfo> findAllByProviderNameAndGroupCodeAndGstinInAndGstnTokenExpiryTimeLessThanOrGstnTokenExpiryTimeBetween(
			@Param("providerName") String providerName,
			@Param("groupCode") String groupCode,
			@Param("gstins") List<String> gstins,
			@Param("currentTime") Date currentTime,
			@Param("expiryStTime") Date expiryStTime,
			@Param("expiryEndTime") Date expiryEndTime);

	@Modifying
	@Query("UPDATE GstnAPIAuthInfo SET gstnToken =:authToken, sessionKey =:sk,"
			+ " gstnTokenGenTime =:genTime, gstnTokenExpiryTime =:expTime,"
			+ " updatedDate =:updatedOn WHERE gstin =:gstin AND "
			+ " providerName =:providerName")
	void updateAuthTokens(@Param("authToken") String authToken,
			@Param("sk") String sk, @Param("genTime") Date genTime,
			@Param("expTime") Date expTime, @Param("updatedOn") Date updatedOn,
			@Param("gstin") String gstin,
			@Param("providerName") String providerName);
	
	public List<GstnAPIAuthInfo> findAllByProviderNameInAndGstnTokenExpiryTimeBetween(
			List<String>providerName, Date expiryStTime,
			Date expiryEndTime, Sort arg1);
	
	public List<GstnAPIAuthInfo> findAllByProviderNameInAndGstnTokenExpiryTimeLessThanEqual(
			List<String> providerName,
			Date expiryEndTime, Sort arg1);


}
