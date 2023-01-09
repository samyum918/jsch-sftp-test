import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Vector;

@Slf4j
public class Main {
    public static void main(String[] args) throws JSchException, SftpException {
        SftpConfig sftpConfig = SftpConfig.builder()
                .host("localhost")
                .port(2222)
                .username("user")
                .password("user")
                .channelConnectedTimeout(15000)
                .sessionConnectTimeout(15000)
                .build();
        ChannelSftp channelSftp = connectByPassword(sftpConfig);
        Vector<ChannelSftp.LsEntry> lsEntryVector = channelSftp.ls("/share");
        System.out.println(lsEntryVector);
        channelSftp.put("C:\\temp\\test.txt", "/share");
    }



    public static ChannelSftp connectByKey(SftpConfig sftpConfig) throws JSchException {
        JSch jsch = new JSch();
        JSch.setLogger(new JschLogger());
        if(StringUtils.isNotBlank(sftpConfig.getPrivateKey())) {
            if(StringUtils.isNotBlank(sftpConfig.getPassphrase())) {
                jsch.addIdentity(sftpConfig.getPrivateKey(), sftpConfig.getPassphrase());
            }
            else {
                jsch.addIdentity(sftpConfig.getPrivateKey());
            }
        }

        Session session = createSession(jsch, sftpConfig.getHost(), sftpConfig.getUsername(), sftpConfig.getPort());
        session.connect(sftpConfig.getSessionConnectTimeout());
        log.debug("Session connected to " + sftpConfig.getHost() + ".");

        Channel channel = session.openChannel("sftp");
        channel.connect(sftpConfig.getChannelConnectedTimeout());
        log.debug("Channel created to " + sftpConfig.getHost() + ".");

        return (ChannelSftp) channel;
    }

    public static ChannelSftp connectByPassword(SftpConfig sftpConfig) throws JSchException {
        JSch jsch = new JSch();
        JSch.setLogger(new JschLogger());
        Session session = createSession(jsch, sftpConfig.getHost(), sftpConfig.getUsername(), sftpConfig.getPort());
        session.setPassword(sftpConfig.getPassword());
        session.connect(sftpConfig.getSessionConnectTimeout());
        log.debug("Session connected to " + sftpConfig.getHost() + ".");

        Channel channel = session.openChannel("sftp");
        channel.connect(sftpConfig.getChannelConnectedTimeout());
        log.debug("Channel created to " + sftpConfig.getHost() + ".");

        return (ChannelSftp) channel;
    }

    public static Session createSession(JSch jsch, String host, String username, Integer port) throws JSchException {
        Session session;
        if(port <= 0) {
            session = jsch.getSession(username, host);
        }
        else {
            session = jsch.getSession(username, host, port);
        }
        if(session == null) {
            throw new JSchException(host + " session is null");
        }

        session.setConfig("StrictHostKeyChecking", "no");
        return session;
    }

    public static void disconnect(ChannelSftp sftp) {
        try {
            if(sftp != null) {
                if(sftp.isConnected()) {
                    sftp.disconnect();
                }
                else if(sftp.isClosed()) {
                    log.debug("sftp is closed already");
                }
                if(sftp.getSession() != null) {
                    sftp.getSession().disconnect();
                }
            }
        } catch (JSchException ex) {
            log.debug("Sftp exception: ", ex);
        }
    }
}
