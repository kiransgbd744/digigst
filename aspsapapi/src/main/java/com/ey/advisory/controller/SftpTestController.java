package com.ey.advisory.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ey.advisory.app.sftp.service.SFTPFileTransferService;
import com.ey.advisory.app.util.Anx1CsvDownloadUtil;
import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CombinAndZipReportFiles;
import com.ey.advisory.common.CommonCryptoUtils;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.GenUtil;
import com.ey.advisory.common.GsonUtil;
import com.ey.advisory.common.multitenancy.GroupService;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.async.domain.master.Group;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.core.dto.APIRespDto;
import com.ey.advisory.domain.client.GlReconSFTPConfigEntity;
import com.ey.advisory.repositories.client.GLReconSFTPConfigRepository;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class SftpTestController {

	@Autowired
	@Qualifier("SFTPFileTransferServiceImplTest")
	private SFTPFileTransferServiceTest sftpService;
	
	
	@Autowired
	@Qualifier("SFTPFileTransferServiceImpl")
	private SFTPFileTransferService sftpServicetest;
	
	
	@Autowired
	@Qualifier("Anx1CsvDownloadUtil")
	private Anx1CsvDownloadUtil anx1CsvDownloadUtil;

	@Autowired
	CombinAndZipReportFiles combinAndZipReportFiles;
	
	@Autowired
	private GLReconSFTPConfigRepository sftpConfig;


	@Value("${sftp.remoteFilePath}")
	private String remoteFilePath;
	
	@Autowired
	CommonUtility commonUtility;
	
	private static final String SFTP_HOST = "ey.internal.sftp.host";

	private static final String SFTP_PORT = "ey.internal.sftp.port";

	private static final String SFTP_SESSION_TIMEOUT = "ey.internal.sftp.session.timeout";

	private static final String SFTP_CHANNEL_TIMEOUT = "ey.internal.sftp.channel.timeout";

	private String host;

	private Integer port;

	private Integer sessionTimeout;

	private Integer channelTimeout;


	@Autowired
	@Qualifier("ConfigManagerImpl")
	private ConfigManager configManager;

	@Autowired
	private GroupService groupService;

	@PostMapping("/api/downlaodSftpFiles")
	public ResponseEntity<String> sftpFilesDownload(
			HttpServletResponse response) {
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		File tempDir = null;
		String zipFileName = "";
		FileInputStream is = null;
		try {
			tempDir = createTempDir();
			if (LOGGER.isDebugEnabled()) {
				String msg = String.format("temp directory name : %s",
						tempDir);
				LOGGER.debug(msg);
			}
			String fullPath = tempDir.getAbsolutePath();
			boolean downloadFiles = sftpService.downloadFiles(fullPath,
					remoteFilePath);
			if (!downloadFiles) {
				String errMsg = String
						.format("Remote File Path %s is empty,Hence "
								+ "cannot download the files", remoteFilePath);
				LOGGER.error(errMsg);
				throw new AppException(errMsg);
			}

			if (tempDir.list().length > 0) {

				if (LOGGER.isDebugEnabled()) {
					String msg = "Creting Zip folder";
					LOGGER.debug(msg);
				}
				zipFileName = combinAndZipReportFiles.zipfolder(tempDir,
						"data", 100L);
				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("ZipFile name : %s",
							zipFileName);
					LOGGER.debug(msg);
				}
				if (LOGGER.isDebugEnabled()) {
					String[] list = tempDir.list();
					for (String file : list) {
						LOGGER.debug("files{} in temp directory", file);
					}

				}
				
				File zipFile = new File(tempDir,zipFileName);

				if (LOGGER.isDebugEnabled()) {
					String msg = String.format("final file name : %s",
							zipFile);
					LOGGER.debug(msg);
				}
				 is = new FileInputStream(zipFile);
				int read = 0;
				byte[] bytes = new byte[1024];
				String fileName = "sftpFile.zip";
				response.setHeader("Content-Disposition",
						String.format("attachment; filename = %s", fileName));
				OutputStream outputStream = response.getOutputStream();
				while ((read = is.read(bytes)) != -1) {
					outputStream.write(bytes, 0, read);

				}
			} else {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("files are not found in directory{}", tempDir);
				}
			}

			jobParams.addProperty("status", "reports Uploaded sucessfully");
			Gson gson = GsonUtil.newSAPGsonInstance();
			JsonElement respBody = gson.toJsonTree(jobParams);
			resp.add("hdr", gson.toJsonTree(APIRespDto.createSuccessResp()));
			resp.add("resp", respBody);
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} catch (Exception ex) {
			String msg = " Unexpected error occured ";
			LOGGER.error(msg, ex);
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", msg)));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
		} finally {
			if (tempDir != null) {
				GenUtil.deleteTempDir(tempDir);
			}
			 if (is != null) {
			        try {
			            is.close();
			        } catch (IOException e) {
			     LOGGER.debug("An error occurred while closing is", e);

			        }
			    }
		}
	}

	@PostMapping("/api/sftpChannel")
	public ResponseEntity<String>  sftpFilesDownload1(HttpServletResponse response) {
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.sftp");
			host = configMap.containsKey(SFTP_HOST)
					? configMap.get(SFTP_HOST).getValue() : "";
			port = configMap.containsKey(SFTP_PORT)
					? Integer.valueOf(configMap.get(SFTP_PORT).getValue()) : 8443;
			sessionTimeout = configMap.containsKey(SFTP_SESSION_TIMEOUT) ? Integer
					.valueOf(configMap.get(SFTP_SESSION_TIMEOUT).getValue())
					: 10000;
			channelTimeout = configMap.containsKey(SFTP_CHANNEL_TIMEOUT) ? Integer
					.valueOf(configMap.get(SFTP_CHANNEL_TIMEOUT).getValue())
					: 50000;
							
			createChannelSftp();
			return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
		} catch (Exception ex) {
			System.out.println(ex);
			
			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ex.toString())));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	
		}

	}
	
	private ChannelSftp createChannelSftp() {
		try {
			Map<String, Group> groupMap = groupService.getGroupMap();
			Group group = groupMap.get(TenantContext.getTenantId());
			JSch jSch = new JSch();
			Session session = jSch.getSession(group.getSftpUserName(), host,
					port);
			
				LOGGER.debug(" session connected {} ",session.isConnected());
			
			if(LOGGER.isDebugEnabled())

				{
				LOGGER.debug(" session details {} ",session.toString());
				}

			
			LOGGER.debug(" userName {} ",group.getSftpUserName());
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(group.getSftpPassword());
			

			LOGGER.debug(" password {} ",group.getSftpPassword());
			
			session.connect(sessionTimeout);
			Channel channel = session.openChannel("sftp");
			channel.connect(channelTimeout);
			return (ChannelSftp) channel;
		} catch (JSchException ex) {
			LOGGER.error("Create ChannelSftp error", ex);
			throw new AppException("Exception while connecting to SFTP Server");
		}
	}
	
	
	@PostMapping("/api/glReconSftpChannel")
	public ResponseEntity<String>  glReconFilesDownload(HttpServletResponse response) {
	
		JsonObject jobParams = new JsonObject();
		JsonObject resp = new JsonObject();
	
		try {
			Map<String, Config> configMap = configManager.getConfigs("SFTP",
					"ey.internal.sftp");
			host = configMap.containsKey(SFTP_HOST)
					? configMap.get(SFTP_HOST).getValue() : "";
			port = configMap.containsKey(SFTP_PORT)
					? Integer.valueOf(configMap.get(SFTP_PORT).getValue()) : 8443;
			sessionTimeout = configMap.containsKey(SFTP_SESSION_TIMEOUT) ? Integer
					.valueOf(configMap.get(SFTP_SESSION_TIMEOUT).getValue())
					: 10000;
			channelTimeout = configMap.containsKey(SFTP_CHANNEL_TIMEOUT) ? Integer
					.valueOf(configMap.get(SFTP_CHANNEL_TIMEOUT).getValue())
					: 50000;
							
			createChannelGLReconSftp();
			

			return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
			
		} catch (Exception ex) {
			System.out.println(ex);
			

			resp.add("hdr", new Gson().toJsonTree(new APIRespDto("E", ex.toString())));
			return new ResponseEntity<>(resp.toString(), HttpStatus.OK);
	
		}

	}
	
	
	
	
	private ChannelSftp createChannelGLReconSftp() {
		try {
			
			List<GlReconSFTPConfigEntity> configs = (List<GlReconSFTPConfigEntity>) sftpConfig
					.findAll();
			JSch jSch = new JSch();
			String userName = 
					CommonCryptoUtils.decrypt(configs.get(0).getSftpUsername(),
							Base64.decodeBase64(commonUtility
									.getProp("gl.recon.sftp.key")));
			
			LOGGER.debug(" userName {} ",userName);
			
			String paswrd = CommonCryptoUtils.decrypt(configs.get(0).getSftpPaswrd(),
					Base64.decodeBase64(commonUtility
							.getProp("gl.recon.sftp.key")));
			
			LOGGER.debug(" paswrd {} ",paswrd);
			
			Session session = jSch.getSession(userName,
					host, port);
			
			LOGGER.debug(" session connected {} ",session.isConnected());
			
			if(LOGGER.isDebugEnabled())

				{
				LOGGER.debug(" session details {} ",session.toString());
				}
			
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(paswrd);
			session.connect(sessionTimeout);
			session.setTimeout(channelTimeout);
			Channel channel = session.openChannel("sftp");
			
			LOGGER.debug(" channel {} ",channel.isConnected());
			
			channel.connect(channelTimeout);
			
			
			return (ChannelSftp) channel;
		} catch (JSchException ex) {
			LOGGER.error("Create ChannelSftp error", ex);
			throw new AppException("Exception while connecting to SFTP Server");
		}
	}

	private static File createTempDir() throws IOException {
		return Files.createTempDirectory("SftpFiles").toFile();
	}

}
