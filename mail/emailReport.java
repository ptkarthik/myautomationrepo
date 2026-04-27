package org.billing.mail;

import static org.billing.utils.pageConstants.BaseUtilConstants.EMAIL_BODY;
import static org.billing.utils.pageConstants.BaseUtilConstants.EMAIL_HOST_NAME;
import static org.billing.utils.pageConstants.BaseUtilConstants.EMAIL_PORT;
import static org.billing.utils.pageConstants.BaseUtilConstants.EMAIL_SUBJECT;
import static org.billing.utils.pageConstants.BaseUtilConstants.FROM_EMAIL;
import static org.billing.utils.pageConstants.BaseUtilConstants.TO_EMAIL;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Arrays;
import javax.mail.MessagingException;

public class emailReport {

    public static void main(String[] args) throws IOException, MessagingException {
        SendEmail email = new SendEmail();
        byte[] fileContent = Files.readAllBytes(Path.of(
            System.getProperty("user.dir") + "/target/cucumber-html-report.html"));
        MailAttachment attachment = new MailAttachment(fileContent,
                                                       "text/html; charset=utf-8",
                                                       "cucumber-html-report.html");
        SendEmail.sendEmail(EMAIL_HOST_NAME,
                            EMAIL_PORT,
                            FROM_EMAIL,
                            TO_EMAIL,
                            "",
                            EMAIL_SUBJECT + LocalDate.now(),
                            EMAIL_BODY,
                            Arrays.asList(attachment));

    }
}
