import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

//git# removed unused imports
import org.junit.*;

/**
 * <p>
 * Using a BDD Framework and REST Assured create an automated script using the below API
 *
 * <p>
 * B.<b><u>https://petstore.swagger.io</u></b>
 *      <ol>
 *              <li>
 *                  Retrieve all available pets and confirm that the name “doggie” with category id "12" is on the list
 *              </li>
 *              <li>
 *                  Add a new pet with an auto generated name and status available - Confirm the new pet has been added
 *              </li>
 *              <li>
 *                  From point 2 above retrieve the created pet using the ID
 *              </li>
 *      </ol>
 *  </p>
 *
 * </p>
 *
 * @author Sizwe I. Mkhonza
 * @since 13 November 2020
 */
abstract class TestWithReporting {

    // helper report variables
    protected static ExtentTest logger;
    private static ExtentReports extent = new ExtentReports();
    private String report_file_path = "./test_reports/";
    private final String os_name;
    private final String os_username;
    private final String report_doc_title;
    private static boolean was_report_initialized = false;


    //region api end points

    private String getReport_file_path() {
        return report_file_path;
    }

    private String getOs_name() {
        return os_name;
    }

    protected String getOs_username() {
        return os_username;
    }

    public String getReport_doc_title() {
        return report_doc_title;
    }

    //endregion

    private static ExtentReports getExtent() {
        return extent;
    }



    public TestWithReporting(String os_name, String os_username, String report_doc_title) {
        this.os_name = os_name;
        this.os_username =os_username;

        this.report_doc_title = "Class: " + report_doc_title;

        this.report_file_path += String.format("%s_Report.html", report_doc_title);

        if(!was_report_initialized) {
            reportsInitialization();
            was_report_initialized = true;
        }
    }



    @After
    public void after_a_test() {
        generateReport();
    }

    //region helper report methods

    private void reportsInitialization() {

        ExtentHtmlReporter reporter = new ExtentHtmlReporter(getReport_file_path());

        // Environment Setup
        extent.setSystemInfo("OS Name", this.getOs_name());
        extent.setSystemInfo("Username", this.getOs_username());

        reporter.config().setDocumentTitle(this.getReport_doc_title());
        reporter.config().setReportName(this.getReport_doc_title());
        reporter.config().setTheme(Theme.DARK);

        extent.attachReporter(reporter);

    }

    protected static void reportTestNameAndDescription(String testName, String testDescription) {
        logger = extent.createTest(
                "TestCase: " + testName,
                "Description: "+ testDescription);
    }

    private static void generateReport() {
        extent.flush();
    }

    //endregion

}