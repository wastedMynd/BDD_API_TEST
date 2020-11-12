import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import java.util.List;

import static io.restassured.RestAssured.get;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasKey;

/**
 * <p>
 * The following test's on the DogAPI; and will demonstrates the BDD ( Behavior Driven Development )
 * test approach, whereby an API (Application Program Interface ) endpoint(s)
 * responses' { state or behavior } is tested using REST ( Representation State Transfer )
 * to Assured that the { state or behavior } of the APIs' is responding in a proper manner,
 * against; what is expected.
 * </p>
 * <br/>
 * <p>
 * BDD, test approach utilises;
 * test cases that are written in a natural language that even non-programmers can comprehend.
 * </p>
 * <br/>
 * <p>
 * Thereby Using a BDD Framework and REST Assured; I'll create an automated test script
 * using the API endpoint https://dog.ceo/dog-api/
 * </p>
 * <br/>
 * <p>
 * Test case(s) are as follows:
 * <ol>
 *     <li>
 *              Verify that a successful message is returned when a user searches for random breeds
 *     </li>
 *     <li>
 *              Verify that bulldog is on the list of all breeds
 *      </li>
 *      <li>
 *              Retrieve all sub-breeds for bulldogs and their respective images
 *      </li>
 * </ol>
 * </p>
 * <br/>
 * <p>
 *     <b><u>Helpful resource(s)</u>:</b><br/>
 *     <ul>
 *         <li>
 *              <b>https://www.appsdeveloperblog.com/rest-assured-evaluate-json-response-body/</b>
 *         </li>
 *         <li>
 *              <b>https://dog.ceo/dog-api/</b>
 *         </li>
 *     </ul>
 * </p>
 * <br/>
 *
 * @author Sizwe I. Mkhonza
 * @since 10 Nov 2020
 */
public class TestDogApi {

    private static final String BASE_URL = "https://dog.ceo/api/";
    private static final String EXPECTED_STATUS = "success";
    private static final String EXPECTED_STATUS_KEY = "status";
    private static final String EXPECTED_MESSAGE_KEY = "message";

    /**
     * Helper Method / Function, that...
     * <p>
     * validate or asserts that the API GET response's status_code,
     * status_line, content_type, body status, body_status_key and body_message_key;
     * are what is expected:
     * </p>
     * <p>
     * <code>
     * status_code = 200,
     * status_line = "HTTP/1.1 200 OK",
     * content_type = "application/json",
     * body_status = "success",
     * body_status_key = "status"
     * body_message_key = "message"
     * </code>
     * <p/>
     * <p>
     * whenever what's expected above is not meet...
     * <b>an unhandled; {@link AssertionError} will be thrown</b>.
     * Thereby failing the test!
     * <p/>
     *
     * @param response is {@link Response} from a GET request
     **/
    private void then_validate_response(Response response) {
        response.then().statusCode(200).statusLine("HTTP/1.1 200 OK")
                .contentType(equalTo("application/json"))
                .body("$", hasKey(EXPECTED_STATUS_KEY)) //body has key
                .body("$", hasKey(EXPECTED_MESSAGE_KEY)) //body has key
                .assertThat().body(EXPECTED_STATUS_KEY, equalTo(EXPECTED_STATUS));
    }

    /**
     * 1. Verify that a successful message is returned when a user searches for random breeds
     */
    @Test
    public void random_breed_search_is_successful() {

        String random_breed = "hound";

        String SEARCH_BREED_URL = String.format("%sbreed/%s/images/random", BASE_URL, random_breed);

        Response response = get(SEARCH_BREED_URL);

        then_validate_response(response);

    }

    /**
     * 2. Verify that bulldog is on the list of all breeds
     */
    @Test
    public void bulldog_is_on_the_list_of_breeds() {

        final String BREED = "bulldog";

        String LIST_OF_ALL_BREEDS_URL = String.format("%sbreeds/list/all", BASE_URL);

        Response response = get(LIST_OF_ALL_BREEDS_URL);

        then_validate_response(response);

        response.then().assertThat().body(EXPECTED_MESSAGE_KEY, hasKey(BREED));

    }

    /**
     * 3. Retrieve all sub-breeds for bulldogs and their respective images
     */
    @Test
    public void retrieve_all_sub_breeds_for_bulldog_and_their_respective_images() {

        final String BREED = "bulldog";

        String LIST_OF_BREED_SUB_BREED_URL = String.format("%sbreed/%s/list", BASE_URL, BREED);

        Response response = get(LIST_OF_BREED_SUB_BREED_URL);

        then_validate_response(response);

        JsonPath json_response = response.jsonPath();

        List<String> sub_breeds = json_response.getList(EXPECTED_MESSAGE_KEY);

        for (String sub_breed : sub_breeds) {

            String sub_breed_url = String.format("%sbreed/%s/%s/images", BASE_URL, BREED, sub_breed);

            Response sub_breed_response = get(sub_breed_url);

            then_validate_response(sub_breed_response);

            JsonPath json_sub_breed_response = sub_breed_response.jsonPath();

            List<String> actual_sub_breed_images = json_sub_breed_response.getList(EXPECTED_MESSAGE_KEY);

            System.out.printf("sub-breed %s of breed %s images: \n", sub_breed, BREED);

            for (String actual_sub_breed_image : actual_sub_breed_images)
                System.out.printf("image src = %s \n", actual_sub_breed_image);

            System.out.println();
        }
    }
}