package com.ey.advisory.app.glrecon;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.ey.advisory.common.CommonCryptoUtils;
import com.ey.advisory.common.CommonUtility;
import com.ey.advisory.common.DocumentUtility;
import com.ey.advisory.common.multitenancy.TenantContext;
import com.ey.advisory.core.config.Config;
import com.ey.advisory.core.config.ConfigManager;
import com.ey.advisory.domain.client.GlReconSFTPConfigEntity;
import com.ey.advisory.repositories.client.GLReconSFTPConfigRepository;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.ChannelSftp.LsEntrySelector;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;

/* Used for GL Recon
 * This class implements all the SFTP operations like upload a file,
 * download the file/files and Move Files from source to destination.
 * Further to this connects to SFTP channel and performs the file operations
 * with in a session and Closes the session post file operations
 * 
 * @author Sai.Pakanati
 */
@Slf4j
@Component("GLReconSFTPFileTransferServiceImpl")
public class GLReconSFTPFileTransferServiceImpl
		implements GLReconSFTPFileTransferService {

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
	CommonUtility commonUtility;

	@Autowired
	private GLReconSFTPConfigRepository sftpConfig;

	@PostConstruct
	public void initProperties() {

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

	}

	@Override
	public boolean uploadFile(String localFilePath, String remoteFilePath) {
		ChannelSftp channelSftp = createChannelSftp();
		try {
			channelSftp.put(localFilePath, remoteFilePath);
			return true;
		} catch (SftpException ex) {
			LOGGER.error("Error upload file", ex);
		} finally {
			disconnectChannelSftp(channelSftp);
		}

		return false;
	}

	@Override
	public boolean uploadFiles(List<String> localFilePath,
			String remoteFilePath) {
		ChannelSftp channelSftp = createChannelSftp();
		try {
			for (String loaclFile : localFilePath) {
				channelSftp.put(loaclFile, remoteFilePath);
			}
			return true;
		} catch (SftpException ex) {
			LOGGER.error("Error while uploading files", ex);
		} finally {
			disconnectChannelSftp(channelSftp);
		}
		return false;
	}

	@Override
	public boolean downloadFile(String localFilePath, String remoteFilePath) {
		ChannelSftp channelSftp = createChannelSftp();
		try (OutputStream outputStream = new FileOutputStream(
				new File(localFilePath))) {
			channelSftp.get(remoteFilePath, outputStream);
			return true;
		} catch (SftpException | IOException ex) {
			LOGGER.error("Error download file", ex);
			return false;
		} finally {
			disconnectChannelSftp(channelSftp);
		}

	}

	@Override
	public boolean downloadFiles(String localDir, String sftpSource,
			List<String> fileExtns) {
		ChannelSftp channelSftp = createChannelSftp();
		List<LsEntry> filelist = new ArrayList<>();
		try {
			channelSftp.ls(sftpSource, entry -> {
				if (entry.getAttrs().isDir()) {
					return LsEntrySelector.CONTINUE;
				}
				String fileNm = entry.getFilename();
				String fileExt = fileNm.substring(fileNm.lastIndexOf(".") + 1);
				if (fileExtns.contains(fileExt)) {
					filelist.add(entry);
				}
				return LsEntrySelector.CONTINUE;
			});

			if (filelist.isEmpty()) {
				LOGGER.error(
						"Remote File Path {} is empty,"
								+ " Hence cannot download the files",
						sftpSource);
				return false;
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" no of files{} in the directory",
						filelist.size());
			}
			filelist.forEach(entry -> {
				String fileNm = entry.getFilename();
				String srcPath = String.format("%s%s", sftpSource, fileNm);
				String destPath = String.format("%s%s%s", localDir,
						File.separator, fileNm);
				try {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"files downloaded started "
										+ "from source path{} and endPath{}",
								srcPath, destPath);
					}
					channelSftp.get(srcPath, destPath);

					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug(
								"files downloaded end"
										+ "from source path{} and endPath{}",
								srcPath, destPath);
					}
				} catch (SftpException e) {
					LOGGER.error("Filed to download the file", e);
				}
			});
			return true;
		} catch (Exception ex) {
			LOGGER.error("Failed to download the file", ex);
			return false;
		} finally {
			disconnectChannelSftp(channelSftp);
		}

	}

	private ChannelSftp createChannelSftp() {
		try {

			String groupCode = TenantContext.getTenantId();
			List<GlReconSFTPConfigEntity> configs = (List<GlReconSFTPConfigEntity>) sftpConfig
					.findAll();
			JSch jSch = new JSch();
			String userName = 
					CommonCryptoUtils.decrypt(configs.get(0).getSftpUsername(),
							Base64.decodeBase64(commonUtility
									.getProp("gl.recon.sftp.key")));
			String paswrd = CommonCryptoUtils.decrypt(configs.get(0).getSftpPaswrd(),
					Base64.decodeBase64(commonUtility
							.getProp("gl.recon.sftp.key")));
			
			Session session = jSch.getSession(userName,
					host, port);
			
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(paswrd);
			session.connect(sessionTimeout);
			session.setTimeout(channelTimeout);
			Channel channel = session.openChannel("sftp");
			channel.connect(channelTimeout);
			return (ChannelSftp) channel;
		} catch (JSchException ex) {
			LOGGER.error("Create ChannelSftp error", ex);
			throw new AppException("Exception while connecting to SFTP Server");
		}
	}

	private void disconnectChannelSftp(ChannelSftp channelSftp) {
		try {
			if (channelSftp == null)
				return;

			if (channelSftp.isConnected())
				channelSftp.exit();

			if (channelSftp.getSession() != null)
				channelSftp.getSession().disconnect();

		} catch (Exception ex) {
			LOGGER.error("Exception while disconnecting SFTP Channel", ex);
		}
	}

	@Override
	public boolean moveFiles(List<String> fileNamesToBeMoved, String sftpSource,
			String sftpDestination) {

		ChannelSftp channelSftp = createChannelSftp();
		try {
			for (String fileNme : fileNamesToBeMoved) {
				String srcPath = String.format("%s%s", sftpSource, fileNme);
				String destPath = String.format("%s%s", sftpDestination,
						fileNme);
				channelSftp.rename(srcPath,
						DocumentUtility.getUniqueFileName(destPath));
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(
							"file-{} successfully moved from src - {} to destination -{}",
							fileNme, srcPath, destPath);
				}
			}
			return true;
		} catch (Exception ex) {
			LOGGER.error(
					"Error while archiving files {} from "
							+ "REMOTE_PATH-{} to SFTPTAGETDIR -{} --{}",
					fileNamesToBeMoved, sftpSource, sftpDestination, ex);
			return false;
		} finally {
			disconnectChannelSftp(channelSftp);
		}

	}

	@Override
	public boolean moveFile(String source, String destination,
			String fileNameToBeMoved) {
		ChannelSftp channelSftp = createChannelSftp();
		try {

			channelSftp.rename(source + fileNameToBeMoved, destination
					+ DocumentUtility.getUniqueFileName(fileNameToBeMoved));
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(
						"fileNme{}  file moved to destination{} Archeived folder",
						fileNameToBeMoved, destination);
			}
			return true;
		} catch (Exception ex) {
			LOGGER.error("Exception while moving the file", ex);
			return false;
		} finally {
			disconnectChannelSftp(channelSftp);
		}

	}

	@Override
	public boolean createFolder(String destinationPath, String folderName) {
		ChannelSftp channelSftp = createChannelSftp();
		try {
			channelSftp.mkdir(destinationPath + "/" + folderName);
			
			return true;
			
/*			try{
				
				
			SftpATTRS attrs = channelSftp
					.lstat(destinationPath + "/" + folderName);
			if (attrs.isDir()) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" folder already exists {} ",
							destinationPath + "/" + folderName);
				}
				
			}
			return true;
			
			}catch(Exception ex)
			{
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug(" Creating folder ",
							destinationPath + "/" + folderName);
				}
				channelSftp.mkdir(destinationPath + "/" + folderName);
				
				return true;
			}
	*/			
	
	
		} catch (SftpException ex) {
			LOGGER.error("Error upload file", ex);
			throw new AppException(ex);
		} finally {
			disconnectChannelSftp(channelSftp);
		}

	}

}
