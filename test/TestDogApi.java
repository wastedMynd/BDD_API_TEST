import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.hamcrest.Matchers;
import org.junit.Test;

public class TestDogApi{

    // region  requirements

    private static final String BASE_URL = "https://dog.ceo/api/";

    // SERVER RESPONSE STATUS
    private static final int STATUS_CODE = 200;
    private static final String STATUS_LINE = "HTTP/1.1 200 OK";
    private static final String CONTENT_TYPE = "application/json";

    // RESPONSE BODY KEYS
    private static final String EXPECTED_STATUS_KEY = "status";
    private static final String EXPECTED_MESSAGE_KEY = "message";

    // RESPONSE BODY  VALUE
    private static final String EXPECTED_STATUS = "success";

    // endregion

    private interface RestAssuredResponseRequirementsValidatorInterface {
        Response validate(Response response);
    }

    private static final RestAssuredResponseRequirementsValidatorInterface REQUIREMENTS = (Response response) -> {
            response.then().statusCode(STATUS_CODE).statusLine(STATUS_LINE)               // server feedback ok ?
                    .contentType(Matchers.equalTo(CONTENT_TYPE))                          // content in json ?
                    .body("$", Matchers.hasKey(EXPECTED_STATUS_KEY))                            // is this key present
                    .body("$", Matchers.hasKey(EXPECTED_MESSAGE_KEY))                           // is this key present ?
                    .assertThat().body(EXPECTED_STATUS_KEY, Matchers.equalTo(EXPECTED_STATUS)); // is status ?
            return response;
        };


    private static Response get_response_and_validate_it(String from_url) {
        return REQUIREMENTS.validate(RestAssured.get(from_url));
    }

    @Test
    public void random_breed_search_is_successful() {
        String random_breed = "pitbull";
        System.out.println(get_response_and_validate_it(
                String.format("%sbreed/%s/images/random", BASE_URL, random_breed)
        ).jsonPath().getString(EXPECTED_MESSAGE_KEY));
    }

    @Test
    public void bulldog_is_on_the_list_of_breeds() {
        String BREED = "bulldog";
        get_response_and_validate_it(String.format("%sbreeds/list/all", BASE_URL))
                .then().assertThat().body(EXPECTED_MESSAGE_KEY, Matchers.hasKey(BREED));
    }

    @Test
    public void retrieve_all_sub_breeds_for_bulldog_and_their_respective_images() {
        final String BREED = "bulldog";
        for (Object sub_breed_object :
                get_response_and_validate_it(String.format("%sbreed/%s/list", BASE_URL, BREED))
                .jsonPath().getList(EXPECTED_MESSAGE_KEY)
        ) {

            String sub_breed = (String) sub_breed_object;

            System.out.printf("sub-breed %s of breed %s images: \n", sub_breed, BREED);

            for (Object actual_sub_breed_image_object :
                    get_response_and_validate_it(String.format("%sbreed/%s/%s/images", BASE_URL, BREED, sub_breed))
                    .jsonPath().getList(EXPECTED_MESSAGE_KEY)
            ) System.out.printf("image src = %s \n", actual_sub_breed_image_object);

            System.out.println();
        }
    }
}