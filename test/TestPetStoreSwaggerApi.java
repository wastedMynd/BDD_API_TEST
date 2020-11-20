import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.*;

import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

import org.json.JSONObject;
import org.junit.*;
import utilities.HtmlReporter;

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

    //region  helper variables

    private static HtmlReporter reporter;
    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    //endregion

    //region helper methods

    public String[] getRandomNames(final int generateSize) {

        HashSet<String> list = new HashSet<>();

        Random ran = new Random(26);

        for (int i = 0; i < generateSize; ++i) {

            String name;

            do {

                name = RandomStringUtils.randomAlphanumeric(ran.nextInt(generateSize) + 1);

            } while (list.contains(name));

            list.add(name);

        }

        return list.toArray(new String[]{});
    }

    private String getRandomPetName() {

        String[] pet_names = getRandomNames(10);

        Random ran = new Random(pet_names.length);

        int random_name_index = ran.nextInt(pet_names.length - 1);

        String random_name = pet_names[random_name_index];

        reporter.getLogger().info("Random name chosen for your pet: " + random_name);

        return random_name;
    }

    //endregion


    @BeforeClass
    public static void TestPetS() {
        reporter = new HtmlReporter(
                "ubuntu",
                "sizwe",
                TestPetStoreSwaggerApi.class.getSimpleName()
        );
    }

    //region Test Cases

    @Test
    public void retrieve_all_available_pets() {

        //region reporting initialization
        reporter.createTestCaseReport(
                "retrieve_all_available_pets",
                "1. Retrieve all available pets and confirm that the name 'doggie'" +
                        " with category id '12' is on the list\n"
        );
        //endregion

        //region testing

        String pet_status = "available";

        // compose a pet status url, with this pet status name
        String pet_status_url = String.format("%s/pet/findByStatus?status=%s", BASE_URL, pet_status);

        // send a get request to the PetStoreApi, and get the response back...
        Response response = RestAssured.get(pet_status_url);


        try {
            // then after; let's check if the response header is ok...
            // remember status code 200 is ok, and 400 is an invalid status value.
            response.then().statusCode(200).statusLine("HTTP/1.1 200 OK");

            reporter.getLogger().pass("Good response...");

        } catch (AssertionError e) {

            reporter.getLogger().fail("Bad response...");

            throw e;
        }

        try {
            // and also check that the content type on the response header is of type 'application/json';
            // this tells us that the content of the response is in JSON format...
            response.then().contentType("application/json");

            reporter.getLogger().pass("Good content...");

        } catch (AssertionError e) {

            reporter.getLogger().fail("Bad content...");

            throw e;
        }

        // let also confirm that the name doggie is on the list of available pets...
        // firstly lets setup a few variables to store state
        // below will store the search parameters:
        final String PET_NAME = "doggie";
        final Number PET_CATEGORY_ID = 12;

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
            reporter.getLogger().pass(passMessage);
        else
            reporter.getLogger().fail(failureMessage);

        //... and then afterwards;
        Assert.assertTrue(failureMessage, is_pet_found_on_the_list_of_available_pets);
        // if the above assertion is false
        // (being that is_pet_found_on_the_list_of_available_pets is equal to false);
        // then the test failed!

        //endregion

    }

    @Test
    public void add_a_new_pet() {

        //region reporting initialization
        reporter.createTestCaseReport(
                "add_a_new_pet",
                "2. Add a new pet with an auto generated name" +
                        " and status available " +
                        "- Confirm the new pet has been added\n"
        );
        //endregion

        //region testing  Add a new pet with an auto generated name
        //and status available
        //Confirm the new pet has been added

        final String PET_NAME = getRandomPetName();

        // compose add a new pet to store url, with this pet name
        RestAssured.baseURI = String.format("%s/pet", BASE_URL);
        RequestSpecification httpRequest = RestAssured.given();

        //region request body
        JSONObject request_params = new JSONObject();

        request_params.put("id", 0);

        JSONObject request_category_params = new JSONObject();
        request_category_params.put("id", 1989);
        request_category_params.put("name", "hunting-wild-hound");
        request_params.put("category", request_category_params);

        request_params.put("name", PET_NAME);

        String[] photos = {"https://images.dog.ceo/breeds/greyhound-italian/n02091032_10352.jpg"};
        request_params.put("photoUrls", photos);


        JSONObject request_tags_params = new JSONObject();
        request_tags_params.put("id", 19890616);
        request_tags_params.put("name", "k9");
        JSONObject[] tags = {request_tags_params};
        request_params.put("tags", tags);

        request_params.put("status", "available");

        //endregion

        //region headers
        httpRequest.header("Content-Type", "application/json");
        httpRequest.body(request_params.toString());
        //endregion

        Response response = httpRequest.request(Method.POST);

        //region testing

        try {
            // then after; let's check if the response header is ok...
            // remember status code 200 is ok, and 400 is an invalid status value.
            response.then().statusCode(200).statusLine("HTTP/1.1 200 OK");

            reporter.getLogger().pass("Pet Added.");

        } catch (AssertionError e) {

            reporter.getLogger().fail("Pet Not Added");

            throw e;
        }

        try {
            // and also check that the content type on the response header is of type 'application/json';
            // this tells us that the content of the response is in JSON format...
            response.then().contentType("application/json");

            reporter.getLogger().pass("Good content...");

        } catch (AssertionError e) {

            reporter.getLogger().fail("Bad content...");

            throw e;
        }


        Long pet_id = response.jsonPath().get("id");
        if (pet_id > 0) {
            reporter.getLogger().pass(String.format("Pet has ID %d, and was created", pet_id));
        } else {
            reporter.getLogger().fail("Pet has No ID!");
        }
        //endregion

        //endregion


        //region reporting initialization
        reporter.createTestCaseReport(
                "retrieve_the_created_pet",
                "3. From point 2 above, retrieve the created pet using the ID\n"
        );
        //endregion

        //region testing retrieve the created pet using the ID given on the test above

        String find_pet_by_id_pet_url = String.format("%s/pet/%d", BASE_URL, pet_id);
        response = RestAssured.get(find_pet_by_id_pet_url);

        //region testing
        try {
            // then after; let's check if the response header is ok...
            // remember status code 200 is ok, and 400 is an invalid status value.
            response.then().statusCode(200).statusLine("HTTP/1.1 200 OK");

            reporter.getLogger().pass(String.format("Pet with the Name %s with id %d; was found.", PET_NAME, pet_id));

        } catch (AssertionError e) {

            reporter.getLogger().fail(String.format("Pet with Name %s with id %d; was not found!", PET_NAME, pet_id));

            throw e;

        }
        //endregion

        // endregion

    }

    //endregion

    @AfterClass
    public static void tearDown() {
        reporter.publishTestReport();
    }

}