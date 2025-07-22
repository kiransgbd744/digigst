package com.ey.advisory.app.services.vendorcomm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.commons.io.FileUtils;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.ey.advisory.app.data.entities.client.asprecon.VendorJsonCommRequestEntity;
import com.ey.advisory.app.data.entities.client.asprecon.VendorReqVendorGstinEntity;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorJsonCommRequestRepository;
import com.ey.advisory.app.data.repositories.client.asprecon.VendorReqVendorGstinRepository;
import com.ey.advisory.app.data.services.qrcodevalidator.QRCommonUtility;
import com.ey.advisory.app.util.MergeFilesUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.EYDateUtil;
import com.ey.advisory.common.GenUtil;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class Vendor2wayCommunicationUtility {

	@Autowired
	@Qualifier("VendorReqVendorGstinRepository")
	VendorReqVendorGstinRepository vendorReqVendorGstinRepo;
	
	@Autowired
	private VendorJsonCommRequestRepository vendorJsonCommRequestRepository;


	public InputStream vendorReqIdZipFile(Long reqId) {

		String fileFolder = "VendorCommJsonReport";
		File tempDir = null;
		File destFile = null;
		File downloadDir = null;
		File zipDir = null;

		try {

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format(
						"Inside vendorReqIdZipFile for reqid: %s", reqId);
				LOGGER.debug(msg);
			}

			tempDir = MergeFilesUtil.createTempDir();
			zipDir = MergeFilesUtil.createTempDir();

			List<VendorReqVendorGstinEntity> vendrGstinEntity = vendorReqVendorGstinRepo
					.findByRequestId(reqId);

			for (VendorReqVendorGstinEntity entity : vendrGstinEntity) {
				if (entity.getJsonCnt() > 0) {

					int jsonCount = entity.getJsonCnt();
					
					List<VendorJsonCommRequestEntity> jsonENtity = vendorJsonCommRequestRepository.findByRequestIdAndVendorGstin(entity.getRequestId(),entity.getVendorGstin());
					
					for(VendorJsonCommRequestEntity jsonentity : jsonENtity){
					/*while (jsonCount > 0) {*/
/*
						String fileName = entity.getVendorGstin() + "_" + reqId
								+ "_" + jsonCount + ".json";
*/						String fileName = jsonentity.getFilePath();

						LOGGER.debug("FileName {}", jsonentity.getFilePath());

						downloadDir = QRCommonUtility
								.createDownloadDir(tempDir);
						destFile = new File(downloadDir.getAbsolutePath()
								+ File.separator + fileName);
						Document document = DocumentUtility
								.downloadDocumentByDocId(jsonentity.getDocId());

						if (LOGGER.isDebugEnabled()) {
							String msg = String.format(
									"Document to download : %s", document);
							LOGGER.debug(msg);
						}
						if (document == null) {
							if (LOGGER.isDebugEnabled()) {
								String msg = String.format(
										"Document downloaded is empty : %s",
										document);
								LOGGER.debug(msg);
							}
							--jsonCount;
							continue;
						}

						FileUtils.copyInputStreamToFile(
								document.getContentStream().getStream(),
								destFile);
						--jsonCount;
					}
				} else
					continue;

			}

			String zipFilePath = zipDir.getAbsolutePath() + "//" + reqId
					+ ".zip";

			LOGGER.debug("zipFilePath {}", zipFilePath);

			pack(downloadDir.getAbsolutePath(), zipFilePath);

			LOGGER.debug("creation of zip completed");

			InputStream inputStream = new FileInputStream(
					new File(zipFilePath));
			return inputStream;

		} catch (Exception e) {
			String msg = "Exception occured while creating input stream";
			LOGGER.error(msg, e);
			throw new AppException(e, msg);
		} finally {
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(zipDir);
		}
	}

	private static void pack(String sourceDirPath, String zipFilePath) {
		try {

			LOGGER.debug("inside pack method");

			Path p = Files.createFile(Paths.get(zipFilePath));
			ZipOutputStream zs = new ZipOutputStream(Files.newOutputStream(p));
			Path pp = Paths.get(sourceDirPath);
			Files.walk(pp).filter(path -> !Files.isDirectory(path))
					.forEach(path -> {
						ZipEntry zipEntry = new ZipEntry(
								pp.relativize(path).toString());
						try {
							zs.putNextEntry(zipEntry);
							Files.copy(path, zs);
							zs.closeEntry();
						} catch (Exception e) {
							String msg = "Error while copying files to zip directory";
							throw new AppException(e, msg);
						}
					});
		} catch (Exception e) {
			String msg = "Exception while creating zip file";
			LOGGER.error(msg);
			throw new AppException(e, msg);
		}
	}

	public void vendorFileUpload(MultipartFile respFile, Long reqId,
			String identifier, String vendrGstin, File tempDir, File destDir) {
		try {
			String tempFileName = tempDir.getAbsolutePath() + File.separator
					+ reqId + "_" + vendrGstin + "_"
					+ respFile.getOriginalFilename();

			File tempFile = new File(tempFileName);
			respFile.transferTo(tempFile);

			Map<String, String> filePaths = unzip(tempFileName, destDir, vendrGstin, reqId);

			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("FilePaths fetched after unzipping : %s", filePaths);
				LOGGER.debug(msg);
			}

			// int fileCount = filePaths.size();

			// while (fileCount > 0) {
			//
			// switch (identifier) {
			//
			// case "Match":
			// vendorReqVendorGstinRepo.updateRespPath(reqId, vendrGstin,
			// filePaths.get(fileCount - 1));
			//
			// case "Empty":
			// vendorReqVendorGstinRepo.updateTotalRecrdsPath(reqId, vendrGstin,
			// filePaths.get(fileCount - 1));
			// break;
			//
			// case "Difference":
			// if (filePaths.get(fileCount - 1).contains(reqId + "_" +
			// vendrGstin + "_All_Response_")) {
			// vendorReqVendorGstinRepo.updateTotalRecrdsPath(reqId, vendrGstin,
			// filePaths.get(fileCount - 1));
			//
			// } else
			// vendorReqVendorGstinRepo.updateRespPath(reqId, vendrGstin,
			// filePaths.get(fileCount - 1));
			//
			// break;
			// }
			//
			// --fileCount;
			// }

			for (Map.Entry<String, String> entry : filePaths.entrySet()) {
				String uploadedDocName = entry.getKey();
				String docId = entry.getValue();

				switch (identifier) {
				case "Match":
					vendorReqVendorGstinRepo.updateRespPath(reqId, vendrGstin, uploadedDocName, docId);
				case "Empty":
					vendorReqVendorGstinRepo.updateTotalRecrdsPath(reqId, vendrGstin, uploadedDocName, docId);
				case "Difference":
					if (uploadedDocName.contains(reqId + "_" + vendrGstin + "_All_Response_")) {
						vendorReqVendorGstinRepo.updateTotalRecrdsPath(reqId, vendrGstin, uploadedDocName, docId);
					} else {
						vendorReqVendorGstinRepo.updateRespPath(reqId, vendrGstin, uploadedDocName, docId);
					}
				}
			}
		} catch (Exception e) {
			String msg = "Exception occured while saving files in doc repo";
			LOGGER.error(msg, e);
			throw new AppException(e, msg);
		}
	}

	private static Map<String, String> unzip(String zipFilePath, File destDir, String vendrGstin, Long reqId) {
		Map<String, String> filePathAndDocIds = new HashMap<>();
		FileInputStream fis;
		byte[] buffer = new byte[1024];
		try {
			fis = new FileInputStream(zipFilePath);
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			while (ze != null) {
				String fileName = ze.getName();
				File newFile = new File(destDir + File.separator + String.valueOf(reqId) + "_" + fileName);

				new File(newFile.getParent()).mkdirs();
				FileOutputStream fos = new FileOutputStream(newFile);
				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}
				fos.close();

				zis.closeEntry();
				ze = zis.getNextEntry();

				LocalDateTime now = LocalDateTime.now();
				String timeMilli = EYDateUtil.toISTDateTimeFromUTC(now).toString();
				timeMilli = timeMilli.replace(".", "");
				timeMilli = timeMilli.replace("-", "");
				timeMilli = timeMilli.replace(":", "");

				String uploadedDocName = null;

				if (newFile.getName().contains("Vendor_GSTIN_All_Response")) {
					String fileNameToBeUploaded = reqId + "_" + vendrGstin + "_All_Response_" + timeMilli + ".csv";
					File newFile1 = new File(fileNameToBeUploaded);
					Files.move(newFile.toPath(), newFile1.toPath(), StandardCopyOption.REPLACE_EXISTING);

					Pair<String, String> uploadedDocNameNew = DocumentUtility.uploadFile(newFile1,
							"VendorCommJsonReport");
					uploadedDocName = uploadedDocNameNew.getValue0();
					String docId = uploadedDocNameNew.getValue1();

					filePathAndDocIds.put(uploadedDocName, docId);
				} else {
					String fileNameToBeUploaded = reqId + "_" + vendrGstin + "_Response_" + timeMilli + ".csv";
					File renamedFile = new File(fileNameToBeUploaded);
					Files.move(newFile.toPath(), renamedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

					Pair<String, String> uploadedDocNameNew = DocumentUtility.uploadFile(renamedFile,
							"VendorCommJsonReport");
					uploadedDocName = uploadedDocNameNew.getValue0();
					String docId = uploadedDocNameNew.getValue1();

					filePathAndDocIds.put(uploadedDocName, docId);
				}
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();

		} catch (Exception e) {
			String msg = "Exception while unzipping";
			LOGGER.error(msg, e);
			throw new AppException(e, msg);
		}
		return filePathAndDocIds;
	}

	public InputStream vendorSummaryFile(Long reqId, String vendrGstin) {
		File newFile = null;
		FileInputStream fis;
		byte[] buffer = new byte[1024];
		File tempDir = null;
		File downloadDir = null;

		try {
//			String zipFilePath = vendorReqVendorGstinRepo.getFilePath(reqId,
//					vendrGstin);
			
			Optional<VendorReqVendorGstinEntity> resultOptional = vendorReqVendorGstinRepo.getFilePathDocId(reqId, vendrGstin);


			String fileFolder = "VendorCommReport";
			String zipFilePath = null;
			String docId = null;
			if (resultOptional.isPresent()) {
			    VendorReqVendorGstinEntity entity = resultOptional.get();
			    zipFilePath = entity.getFilePath();
			    docId = entity.getDocId();
			}
			Document document = null;
			
			downloadDir = MergeFilesUtil.createTempDir();
			File destFile = new File(downloadDir.getAbsolutePath()
					+ File.separator + zipFilePath);

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("zipFilePath {}", zipFilePath);
			}
//			Document document = DocumentUtility.downloadDocument(zipFilePath,
//					"VendorCommReport");
			if (Strings.isNullOrEmpty(docId)) {
				document = DocumentUtility.downloadDocument(zipFilePath,
						fileFolder);
			} else {
				document = DocumentUtility.downloadDocumentByDocId(docId);
			}
			FileUtils.copyInputStreamToFile(
					document.getContentStream().getStream(), destFile);
			if (Strings.isNullOrEmpty(zipFilePath))
				return null;
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("destFile.getAbsolutePath() {}",
						destFile.getAbsolutePath().toString());
			}
			fis = new FileInputStream(destFile.getAbsolutePath());
			ZipInputStream zis = new ZipInputStream(fis);
			ZipEntry ze = zis.getNextEntry();
			tempDir = MergeFilesUtil.createTempDir();
			while (ze != null) {
				String fileName = ze.getName();
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("filename itterating in zipfile {}", fileName);
				}
				if (ze.getName().startsWith(vendrGstin)
						&& ze.getName().contains("Read me_SummaryReport")) {

					newFile = new File(tempDir.getAbsolutePath()
							+ File.separator + fileName);
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("inside creating new file for {}",
								fileName);
					}
					FileOutputStream fos = new FileOutputStream(newFile);
					int len;
					while ((len = zis.read(buffer)) > 0) {
						fos.write(buffer, 0, len);
					}
					fos.close();

					zis.closeEntry();
					break;
				}
				ze = zis.getNextEntry();
			}
			// close last ZipEntry
			zis.closeEntry();
			zis.close();
			fis.close();
			if(Strings.isNullOrEmpty(newFile.getAbsolutePath()))
				return null;
			InputStream inputStream = new FileInputStream(
					newFile.getAbsolutePath());

			return inputStream;

		} catch (Exception e) {
			String msg = "Exception while unzipping";
			LOGGER.error(msg, e);
			throw new AppException(e, msg);
		} finally {
			GenUtil.deleteTempDir(tempDir);
			GenUtil.deleteTempDir(downloadDir);
		}

	}
	
}
