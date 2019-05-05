package com.jyeh.streaming.streaming.file;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.emc.ecs.connector.spring.S3Connector;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
@Service
public class StreamingService {

    private final S3Connector s3Connector;

    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.port}")
    private int port;
    @Value("${sftp.user}")
    private String user;
    @Value("${sftp.password}")
    private String password;

    public StreamingService(S3Connector s3Connector) {
        this.s3Connector = s3Connector;
    }

    public void remoteUnzip(String remoteZipFilePath) {
        ChannelSftp channel = null;
        try {
            channel = getChannel();
            boolean pdfUploaded = streamFile(channel, remoteZipFilePath, FileType.PDF);
            boolean xmlUploaded = streamFile(channel, remoteZipFilePath, FileType.XML);
            boolean dneUploaded = streamFile(channel, remoteZipFilePath, FileType.DNE);
        }
        catch (IOException|SftpException|JSchException e) {
            log.error("Failed to stream zip file", e);
        }
        finally {
            if (channel != null) {
                try { channel.getSession().disconnect(); }
                catch (Exception e) { e.printStackTrace();}
                channel.disconnect();
            }
        }
    }
    private boolean streamFile(ChannelSftp channel, String remoteFilePath, FileType fileType) throws IOException, SftpException {
        try (InputStream remoteInput = channel.get(remoteFilePath)) {
            ZipInputStream zipInput = new ZipInputStream(remoteInput);
            for (ZipEntry zipEntry = zipInput.getNextEntry(); zipEntry != null; zipEntry = zipInput.getNextEntry()) {
                String zipFilename = zipEntry.getName();
                if (FileType.checkFileType(zipFilename) == fileType) {
                    upload(zipFilename, zipInput, zipEntry.getSize());
                    log.debug("Uploaded {} file", fileType.name());
                    return true;
                }
            }
        }
        return false;
    }

    private void upload(String objectName, InputStream sftpInputStream, long length) {
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(length);
        PutObjectRequest request = new PutObjectRequest(s3Connector.getBucket(), objectName, sftpInputStream, metadata);
        request.getRequestClientOptions().setReadLimit(1024);
        s3Connector.getClient().putObject(request);
    }

    private ChannelSftp getChannel() throws JSchException {
        Session session = new JSch().getSession(user, host, port);
        session.setPassword(password);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,keyboard-interactive,password");
        session.connect();
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        return channel;
    }

}
