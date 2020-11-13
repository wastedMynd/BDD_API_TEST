import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.*;

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
public class TestPetStoreSwaggerApi {

    // helper report variables
    private static ExtentTest logger;
    private static ExtentReports extent = new ExtentReports();
    private static final String REPORT_FILE_PATH = "./test_reports/TestPetStoreSwaggerApi_Report.html";
    private static final String OS_NAME = "ubuntu";
    private static final String OS_USERNAME = "sizwe";
    private static final String REPORT_DOC_TITLE = "Class: TestPetStoreSwaggerApi";

    // helper test variable
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    private enum PetStatus {available, pending, sold}

    @BeforeClass
    public static void before_any_tests() {
        reportsInitialization();
    }

    @Test
    public void retrieve_all_available_pets() {

        //region reporting initialization
        provideTestNameAndDescription(
                        "retrieve_all_available_pets",
                        "1. Retrieve all available pets and confirm that the name 'doggie'" +
                        " with category id '12' is on the list\n"
        );
        //endregion

        //region testing

        //We'd like to get pets that have a status that is available...
        PetStatus pet_status = PetStatus.available;

        // parse the pet status name from our PetStatus enum
        String pet_status_name = pet_status.name();

        // compose a pet status url, with this pet status name
        String pet_status_url = String.format("%s/pet/findByStatus?status=%s", BASE_URL, pet_status_name);

        // send a get request to the PetStoreApi, and get the response back...
        Response response = RestAssured.get(pet_status_url);

        // then after; let's check if the response header is ok...
        // remember status code 200 is ok, and 400 is an invalid status value.
        response.then().statusCode(200).statusLine("HTTP/1.1 200 OK");

        // and also check that the content type on the response header is of type 'application/json';
        // this tells us that the content of the response is in JSON format...
        response.then().contentType("application/json");

        // let also confirm that the name doggie is on the list of available pets...

        // firstly lets setup a few variables to store state

        // below will store the search parameters:
        final String PET_NAME = "doggie";
        final Number PET_CATEGORY_ID = 120;

        String failureMessage = String.format("Pet %s with category id %s was not found!", PET_NAME, PET_CATEGORY_ID);
        String passMessage = String.format("Pet %s with category id %s found!", PET_NAME, PET_CATEGORY_ID);

        // search on this list...
        ArrayList<LinkedHashMap<String, Object>> list_of_available_pets = response.jsonPath().get();

        // search resultant state; will be kept on this flag...
        boolean is_pet_found_on_the_list_of_available_pets = false;

        //and then, lets do the search...
        for (LinkedHashMap<String, Object> pet_content : list_of_available_pets) {

            String pet_name = pet_content.get("name") != null ? (String) pet_content.get("name") : "";

            LinkedHashMap<String, Object> pet_content_category = (LinkedHashMap<String, Object>) pet_content.get("category");

            Object pet_content_category_id = pet_content_category != null ? pet_content_category.get("id") : -1;

            if (pet_name.equals(PET_NAME) && pet_content_category_id.equals(PET_CATEGORY_ID)) {
                is_pet_found_on_the_list_of_available_pets = true;
                break; //exiting the search...
            }
        }

        if (is_pet_found_on_the_list_of_available_pets)
            logger.pass(passMessage);
        else
            logger.fail(failureMessage);

        //... and then afterwards;
        Assert.assertTrue(failureMessage, is_pet_found_on_the_list_of_available_pets);
        // if the above assertion is false
        // (being that is_pet_found_on_the_list_of_available_pets is equal to false);
        // then the test failed!

        //endregion

    }

    @Test
    public void add_a_new_pet() {
        //todo Add a new pet with an auto generated name
        //todo and status available
        //todo Confirm the new pet has been added

        //region reporting initialization
        provideTestNameAndDescription(
                "add_a_new_pet",
                "2. Add a new pet with an auto generated name" +
                        " and status available " +
                        "- Confirm the new pet has been added\n"
        );
        //endregion

        //region testing
        logger.fail("Test Script Not Implemented...");
        Assert.assertTrue("Test Script Not Implemented...", false);
        //endregion

    }

    @Test
    public void retrieve_the_created_pet() {
        // todo retrieve the created pet using the ID given on the test above

        //region reporting initialization
        provideTestNameAndDescription(
                "retrieve_the_created_pet",
                "3. From point 2 above, retrieve the created pet using the ID\n"
        );
        //endregion

        //region testing
        logger.fail("Test Script Not Implemented...");
        Assert.assertTrue("Test Script Not Implemented...", false);
        //endregion

    }

    @After
    public void after_a_test() {
        generateReport();
    }

    //region helper report methods

    public static void reportsInitialization() {
        ExtentHtmlReporter reporter = new ExtentHtmlReporter(REPORT_FILE_PATH);

        // Environment Setup
        extent.setSystemInfo("OS Name", OS_NAME);
        extent.setSystemInfo("Username", OS_USERNAME);

        reporter.config().setDocumentTitle(REPORT_DOC_TITLE);
        reporter.config().setReportName(REPORT_DOC_TITLE);
        reporter.config().setTheme(Theme.DARK);

        extent.attachReporter(reporter);

    }

    private static void provideTestNameAndDescription(String testName, String testDescription) {
        logger = extent.createTest(
                "TestCase: " + testName,
                "Description: "+ testDescription);
    }

    public void generateReport() {
        extent.flush();
    }

    //endregion

}