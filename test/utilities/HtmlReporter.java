package utilities;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

/**
 * Html Reporting Utility
 *
 * @author Sizwe I. Mkhonza
 * @since 13 November 2020
 */
public class HtmlReporter {

    private ExtentTest logger;

    private final ExtentReports extent = new ExtentReports();

    //region getter and setter

    /**
     * Get an instance for logging to the Html Report.
     *
     * @return ExtentTest logger, instance for logging to the Html Report.
     * @throws NullPointerException when {@link #getLogger()};
     *                              is invoked, before {@link #createTestCaseReport(String, String)}
     */
    public ExtentTest getLogger() throws NullPointerException {
        if (logger == null)
            throw new NullPointerException(" getLogger method was not invoked before createTestCaseReport method! ");
        else
            return logger;
    }

    private void setLogger(ExtentTest logger) {
        this.logger = logger;
    }

    //endregion

    public HtmlReporter(String operating_system, String tester, String report_doc_title) {

        // region environment Setup

        extent.setSystemInfo("operating system", operating_system);
        extent.setSystemInfo("tester", tester);

        //endregion

        //region initialize report

        String DEFAULT_REPORT_FOLDER_PATH = "./test_reports/";
        String report_file_path = DEFAULT_REPORT_FOLDER_PATH + report_doc_title + "_report.html";

        ExtentHtmlReporter reporter = new ExtentHtmlReporter(report_file_path);
        reporter.config().setDocumentTitle("Class: " + report_doc_title);
        reporter.config().setReportName(report_doc_title);
        reporter.config().setTheme(Theme.DARK);

        extent.attachReporter(reporter);

        //endregion
    }

    //region helper sub-class report method

    /**
     * Must be, invoked once per test case, before an invoke of getLogger method
     *
     * @param test_name,        descriptive test case; name.
     * @param test_description, descriptive test case; description.
     */
    public void createTestCaseReport(String test_name, String test_description) {
        setLogger(extent.createTest("TestCase: " + test_name, "Description: " + test_description));
    }


    /**
     * will dump all the logger's logs, on to extent reports
     */
    public void publishTestReport() {
        extent.flush();
    }

    //endregion

}