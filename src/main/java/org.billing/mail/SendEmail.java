package org.billing.mail;

import org.billing.utils.billing.StringUtil;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;
import org.apache.commons.lang3.StringUtils;

public class SendEmail {

    public static void sendEmail(String smtpServerHostname,
                                 String smtpServerPort,
                                 String mailSenderAddress,
                                 String mailRecipientAddresses,
                                 String mailCCAddresses,
                                 String subject,
                                 String body,
                                 List<MailAttachment> attachments) throws MessagingException {
        try {
            Message mailMessage = prepareMail(smtpServerHostname,
                                              smtpServerPort,
                                              mailSenderAddress,
                                              mailRecipientAddresses,
                                              mailCCAddresses,
                                              subject);

            Multipart multipart = new MimeMultipart();
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(body);
            multipart.addBodyPart(messageBodyPart);
            attachments.stream()
                       .map(attachment -> {
                           try {
                               MimeBodyPart messageAttachment = new MimeBodyPart();
                               DataSource source = new ByteArrayDataSource(attachment.getAttachmentContent(),
                                                                           attachment.getType());
                               messageAttachment.setDataHandler(new DataHandler(source));
                               messageAttachment.setFileName(attachment.getName());
                               messageAttachment.setDisposition(MimeBodyPart.ATTACHMENT);
                               return messageAttachment;
                           } catch (MessagingException e) {
                               System.err.println(e.getMessage());
                           }

                           return null;
                       })
                       .filter(Objects::nonNull)
                       .forEach(part -> {
                           try {
                               multipart.addBodyPart(part);
                           } catch (MessagingException e) {
                               System.err.println(e.getMessage());
                           }
                       });

            mailMessage.setContent(multipart);
            Transport.send(mailMessage);
        } catch (MessagingException mex) {
            System.err.println(mex.getMessage());
            throw mex;
        }
    }

    private static Message prepareMail(String smtpServerHostname,
                                       String smtpServerPort,
                                       String mailSenderAddress,
                                       String mailRecipientAddresses,
                                       String mailCCAddresses,
                                       String subject) throws MessagingException {
        if (StringUtil.isNullOrEmpty(mailRecipientAddresses)) {
            throw new IllegalArgumentException("No email recipients are set");
        }
        if (!StringUtil.isEmailAddress(mailRecipientAddresses)
            && !StringUtil.isEmailAddressList(mailRecipientAddresses)) {
            throw new IllegalArgumentException("Invalid email / email list");
        } else {
            Properties props = new Properties();
            props.put("mail.smtp.host", smtpServerHostname);
            props.put("mail.smtp.port", smtpServerPort);
            props.put("mail.debug", "false");
            Session session = Session.getInstance(props);

            try {
                Message msg = new MimeMessage(session);
                String fromEmail = mailSenderAddress;
                msg.setFrom(new InternetAddress(fromEmail));

                InternetAddress[] address = InternetAddress.parse(mailRecipientAddresses);
                msg.setRecipients(Message.RecipientType.TO, address);
                msg.setSubject(subject);
                msg.setSentDate(new Date());

                if (StringUtil.isNullOrEmpty(mailCCAddresses)) {
                    mailCCAddresses = fromEmail;
                } else {
                    mailCCAddresses += ", " + fromEmail;
                }
                if (StringUtils.isNotBlank(mailCCAddresses)) {
                    InternetAddress[] ccAddress = InternetAddress.parse(mailCCAddresses);
                    msg.setRecipients(Message.RecipientType.CC, ccAddress);
                }

                return msg;
            } catch (MessagingException mex) {
                System.err.println(mex.getMessage());
                throw mex;
            }
        }
    }
}
