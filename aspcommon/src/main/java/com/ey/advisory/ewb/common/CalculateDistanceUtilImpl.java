/**
 * 
 */
package com.ey.advisory.ewb.common;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.javatuples.Pair;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Khalid1.Khan
 *
 */
@Slf4j
@Component("CalculateDistanceUtilImpl")
public class CalculateDistanceUtilImpl implements CalculateDistanceUtil{

	private static final Map<String, String> pinDictionary = new HashMap<String, String>();

	@PostConstruct
	public void loadDistCalcMap() {
		try {

			Reader reader = new InputStreamReader(getClass().getClassLoader()
					.getResourceAsStream("pins/pins.csv"));
			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1)
					.build();
			List<String[]> allData = csvReader.readAll();

			for (String[] row : allData) {
				pinDictionary.put(row[0], String.join(",",
						Arrays.copyOfRange(row, 1, row.length)));
			}
		} catch (IOException e) {
			// TODO
		}
	}

	private Pair<Double, Double> getGeoLocation(String pincode) {

		try {
			// get from map
			String latLong = pinDictionary.get(pincode);

			if (latLong == null)
				throw new AppException("Provided supplier PIN or Ship to PIN is"
						+ "not found in map file");

			String[] coords = latLong.split(",");
			return new Pair<>(Double.parseDouble(coords[0]),
					Double.parseDouble(coords[1]));
		} catch (Exception ex) {
			throw new AppException(ex.getMessage(), ex);
		}

	}

	public Integer calculateDistance(String srcPinCode, String destPinCode) {

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside calculateDistance() method");
		}

		if (LOGGER.isDebugEnabled()) {
			String msg = String.format(
					"Calculating distance for Pincodes: (%s, %s)...",
					srcPinCode, destPinCode);
			LOGGER.debug(msg);
		}
		Pair<Double, Double> sourceLoc = null;
		Pair<Double, Double> destLoc = null;
		Integer distance = Integer.MIN_VALUE;

		try {
			// Get the source and Destination locations from the
			// source pin code and the destination pin code.
			sourceLoc = getGeoLocation(srcPinCode);
			destLoc = getGeoLocation(destPinCode);

			// if(!sourcePinCode.trim().equalsIgnoreCase(destPinCode.trim())){
			if (sourceLoc != null && destLoc != null) {
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format(
							"Geo Locations obtained. "
									+ "Source Loc is: (%s, %s); "
									+ "Dest Loc is: (%s, %s)",
							sourceLoc.getValue0(), sourceLoc.getValue1(),
							destLoc.getValue0(), destLoc.getValue1());
					LOGGER.debug(msg);
				}
				// Calculate the distance. If there is an issue with
				// the distance calculation, we return Integer.MIN_VALUE.
				distance = getDistance(sourceLoc, destLoc, 'K');
				if (LOGGER.isDebugEnabled()) {
					String msg = "Distance between pincodes (" + srcPinCode
							+ "," + destPinCode + ") obtained. Distance = "
							+ distance;
					LOGGER.debug(msg);
				}
				if (distance != Integer.MIN_VALUE) {
					// distance = distance; Integer Division
					return distance;
				} else {
					// Return null as the distance. Something went wrong
					// while getting the distance using MapMyIndia API
					LOGGER.warn("Unable to calculate distance between "
							+ "pincodes. Returning null as distance");
					return null;
				}
			} else {
				LOGGER.warn("Source or Destination GeoLocation "
						+ "obtained from Map My India is null. "
						+ "Returning null for distance beteween Pincodes. "
						+ "Source is: " + sourceLoc + ";  Dest is: " + destLoc);
				return null;
			}
			/*
			 * }else{
			 * 
			 * if (LOGGER.isDebugEnabled()) { String msg =
			 * "Both Sourse and Destination PINs are same. So we are setting the distance as 1 KM."
			 * ; LOGGER.debug(msg); }
			 * 
			 * distanceMap.put(pair, Double.valueOf("1")); }
			 */

		} catch (AppException ex) {
			LOGGER.error("Error while calculating distance " + ex.getMessage());

			if ("Provided supplier PIN or Ship to PIN isnot found in map file"
					.equalsIgnoreCase(ex.getMessage())) {
				return null;
			} else {
				// LOGGER.error("Error while calculating distance ", ex);
				return null;
			}

		}

	}

	private Integer getDistance(Pair<Double, Double> sourceLoc,
			Pair<Double, Double> destLoc, char unit) {

		double dist = Integer.MIN_VALUE;
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("inside getDistance() method");
		}

		try {
			double lat1 = sourceLoc.getValue0();
			double lon1 = sourceLoc.getValue1();

			double lat2 = destLoc.getValue0();
			double lon2 = destLoc.getValue1();
			double theta = lon1 - lon2;
			dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
					+ Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
							* Math.cos(deg2rad(theta));
			dist = Math.acos(dist);
			dist = rad2deg(dist);
			dist = dist * 60 * 1.1515;
			if (unit == 'K') {
				dist = dist * 1.609344;
			} else if (unit == 'N') {
				dist = dist * 0.8684;
			}

		} catch (AppException ex) {
			throw ex;
		} catch (Exception ex) {
			LOGGER.error("", ex);
			throw new AppException("", ex);
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("End of getDistance() method");
		}

		return (int) dist;
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts decimal degrees to radians : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double deg2rad(double deg) {
		return (deg * Math.PI / 180.0);
	}

	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	/* :: This function converts radians to decimal degrees : */
	/* ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: */
	private double rad2deg(double rad) {
		return (rad * 180.0 / Math.PI);
	}

}
