import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import java.util.Iterator;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

public class TestDogApi {

    private static final String BASE_URL = "https://dog.ceo/api/";
    private static final String EXPECTED_STATUS = "success";
    private static final String EXPECTED_STATUS_KEY = "status";
    private static final String EXPECTED_MESSAGE_KEY = "message";

    private static void then_validates(Response response) {
        response.then().statusCode(200).statusLine("HTTP/1.1 200 OK")               // server feedback ok ?
                .contentType(Matchers.equalTo("application/json"))                          // content in json ?
                .body("$", Matchers.hasKey(EXPECTED_STATUS_KEY))                            // is this key present
                .body("$", Matchers.hasKey(EXPECTED_MESSAGE_KEY))                           // and also, is this key present ?
                .assertThat().body(EXPECTED_STATUS_KEY, Matchers.equalTo(EXPECTED_STATUS)); // is status ?
    }

    private static Response get_response_and_validate_it(String from_url) {
        Response response = RestAssured.get(from_url);

        this.then_validates(response);

        return response;
    }

    @Test
    public void random_breed_search_is_successful() {
        String random_breed = "hound";

        String SEARCH_BREED_URL = String.format("%sbreed/%s/images/random", BASE_URL, random_breed);

        get_response_and_validate_it(SEARCH_BREED_URL)
    }

    @Test
    public void bulldog_is_on_the_list_of_breeds() {
        String BREED = "bulldog";

        String LIST_OF_ALL_BREEDS_URL = String.format("%sbreeds/list/all", BASE_URL);

        get_response_and_validate_it(LIST_OF_ALL_BREEDS_URL)
                .then().assertThat().body(EXPECTED_MESSAGE_KEY, Matchers.hasKey(BREED));
    }

    @Test
    public void retrieve_all_sub_breeds_for_bulldog_and_their_respective_images() {
        final String BREED = "bulldog";
        final String LIST_OF_BREED_SUB_BREED_URL = String.format("%sbreed/%s/list", BASE_URL, BREED);
        Response response = get_response_and_validate_it(LIST_OF_BREED_SUB_BREED_URL);

        JsonPath json_response = response.jsonPath();
        Iterator sub_breeds_iterator = json_response.getList(EXPECTED_MESSAGE_KEY).iterator();

        while (sub_breeds_iterator.hasNext()) {

            String sub_breed = (String) sub_breeds_iterator.next();
            System.out.printf("sub-breed %s of breed %s images: \n", sub_breed, BREED);

            String sub_breed_url = String.format("%sbreed/%s/%s/images", BASE_URL, BREED, sub_breed);
            Response sub_breed_response = get_response_and_validate_it(sub_breed_url);

            JsonPath json_sub_breed_response = sub_breed_response.jsonPath();

            Iterator actual_sub_breed_images_iterator = json_sub_breed_response
                    .getList(EXPECTED_MESSAGE_KEY).iterator();

            while (actual_sub_breed_images_iterator.hasNext()) {

                String actual_sub_breed_image = (String) actual_sub_breed_images_iterator.next();

                System.out.printf("image src = %s \n", actual_sub_breed_image);

            }

            System.out.println();
        }

    }
}

