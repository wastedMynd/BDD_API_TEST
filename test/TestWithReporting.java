import com.aventstack.extentreports.ExtentReporter;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.junit.After;

abstract class TestWithReporting {
    protected static ExtentTest logger;
    private static ExtentReports extent = new ExtentReports();
    private String report_file_path = "./test_reports/";
    private final String os_name;
    private final String os_username;
    private final String report_doc_title;
    private static boolean was_report_initialized = false;

    private String getReport_file_path() {
        return this.report_file_path;
    }

    private String getOs_name() {
        return this.os_name;
    }

    protected String getOs_username() {
        return this.os_username;
    }

    public String getReport_doc_title() {
        return this.report_doc_title;
    }

    private static ExtentReports getExtent() {
        return extent;
    }

    public TestWithReporting(String os_name, String os_username, String report_doc_title) {
        this.os_name = os_name;
        this.os_username = os_username;
        this.report_doc_title = "Class: " + report_doc_title;
        this.report_file_path = this.report_file_path + String.format("%s_Report.html", report_doc_title);
        if (!was_report_initialized) {
            this.reportsInitialization();
            was_report_initialized = true;
        }

    }

    @After
    public void after_a_test() {
        generateReport();
    }

    private void reportsInitialization() {
        ExtentHtmlReporter reporter = new ExtentHtmlReporter(this.getReport_file_path());
        extent.setSystemInfo("OS Name", this.getOs_name());
        extent.setSystemInfo("Username", this.getOs_username());
        reporter.config().setDocumentTitle(this.getReport_doc_title());
        reporter.config().setReportName(this.getReport_doc_title());
        reporter.config().setTheme(Theme.DARK);
        extent.attachReporter(new ExtentReporter[]{reporter});
    }

    protected static void reportTestNameAndDescription(String testName, String testDescription) {
        logger = extent.createTest("TestCase: " + testName, "Description: " + testDescription);
    }

    private static void generateReport() {
        extent.flush();
    }
}