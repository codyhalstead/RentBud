package com.rba18.helpers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Security;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class GMailSender extends javax.mail.Authenticator {
    private String mMailhost = "smtp.gmail.com";
    private String mUser;
    private String mPassword;
    private Session mSession;

    static {
        Security.addProvider(new com.rba18.helpers.JSSEProvider());
    }

    public GMailSender(String user, String password) {
        mUser = user;
        mPassword = password;

        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mMailhost);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");

        mSession = Session.getDefaultInstance(props, this);
    }

    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(mUser, mPassword);
    }

    public synchronized void sendMail(String subject, String body, String sender, String recipients) {
        try{
            MimeMessage message = new MimeMessage(mSession);
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/plain"));
            message.setSender(new InternetAddress(sender));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private class ByteArrayDataSource implements DataSource {
        private byte[] mData;
        private String mType;

        private ByteArrayDataSource(byte[] data, String type) {
            super();
            mData = data;
            mType = type;
        }

        public ByteArrayDataSource(byte[] data) {
            super();
            mData = data;
        }

        public void setType(String type) {
            mType = type;
        }

        public String getContentType() {
            if (mType == null)
                return "application/octet-stream";
            else
                return mType;
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(mData);
        }

        public String getName() {
            return "ByteArrayDataSource";
        }

        public OutputStream getOutputStream() throws IOException {
            throw new IOException("Not Supported");
        }
    }
}