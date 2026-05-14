package billing.acceptancetests.api;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;
import org.billing.mail.MailAttachment;
import org.billing.mail.SendEmail;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.DataProvider;

import javax.mail.MessagingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

import static org.billing.utils.pageConstants.BaseUtilConstants.*;

@CucumberOptions(
        plugin = {
                "com.aventstack.chaintest.plugins.ChainTestCucumberListener:"},
        features = "src/test/resources/features/api/homepage.feature",
        tags = "not @ignore",
        glue = {"billing.stepDefinitions.api","APIhooks"})
public class ApiTestRunner extends AbstractTestNGCucumberTests {

    @Override
    @DataProvider(parallel = true)
    public Object[][] scenarios() {

        return super.scenarios();
    }
//
//    @AfterSuite
//    public void emailReport() throws IOException, MessagingException {
//        byte[] fileContent = Files.readAllBytes(Path.of(
//                System.getProperty("user.dir") + "/target/chaintest/Index.html"));
//        MailAttachment attachment = new MailAttachment(fileContent,
//                "text/html; charset=utf-8",
//                "Index.html");
//        SendEmail.sendEmail(EMAIL_HOST_NAME,
//                EMAIL_PORT,
//                FROM_EMAIL,x
//                TO_EMAIL,
//                "",
//                EMAIL_SUBJECT + LocalDate.now(),
//                EMAIL_BODY,
//                List.of(attachment));
//
//    }
}
