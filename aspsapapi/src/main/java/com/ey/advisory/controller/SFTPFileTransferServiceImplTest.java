package com.ey.advisory.controller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.ey.advisory.common.AppException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("SFTPFileTransferServiceImplTest")
public class SFTPFileTransferServiceImplTest implements SFTPFileTransferServiceTest {

	@Value("${sftp.host}")
	private String host;

	@Value("${sftp.port}")
	private Integer port;

	@Value("${sftp.username}")
	private String username;

	@Value("${sftp.password}")
	private String password;

	@Value("${sftp.sessionTimeout}")
	private Integer sessionTimeout;

	@Value("${sftp.channelTimeout}")
	private Integer channelTimeout;

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
	public boolean downloadFile(String localFilePath, String remoteFilePath) {
		ChannelSftp channelSftp = createChannelSftp();
		OutputStream outputStream;
		try {
			File file = new File(localFilePath);
			outputStream = new FileOutputStream(file);
			channelSftp.get(remoteFilePath, outputStream);
			return file.createNewFile();
		} catch (SftpException | IOException ex) {
			LOGGER.error("Error download file", ex);
			return false;
		} finally {
			disconnectChannelSftp(channelSftp);
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean downloadFiles(String localDir, String remoteFilePath) {
		ChannelSftp channelSftp = createChannelSftp();
		try {
			channelSftp.cd(remoteFilePath);
			List<LsEntry> filelist = channelSftp.ls("*.csv");
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug(" no of files{} in the directory",
						filelist.size());
			}
			if (filelist.isEmpty()) {
				LOGGER.error(
						"Remote File Path {} is empty,"
								+ " Hence cannot download the files",
						remoteFilePath);
				return false;
			}
			filelist.forEach(entry -> {
				if (entry.getAttrs().isDir())
					return;
				String fileNm = entry.getFilename();
				String srcPath = String.format("%s%s", remoteFilePath, fileNm);
				String destPath = String.format("%s%s%s", localDir, File.separator,fileNm);
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
			return false;
		} finally {
			disconnectChannelSftp(channelSftp);
		}

	}

	private ChannelSftp createChannelSftp() {
		try {
			JSch jSch = new JSch();
			Session session = jSch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			session.connect(sessionTimeout);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sucessfully connected");
			}
			Channel channel = session.openChannel("sftp");
			channel.connect(channelTimeout);
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("sucessfully connected sftp channel");
			}
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
			LOGGER.error("SFTP disconnect error", ex);
		}
	}

}
