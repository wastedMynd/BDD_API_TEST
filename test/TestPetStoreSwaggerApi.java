import io.restassured.RestAssured;
import io.restassured.response.Response;

import java.util.*;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

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

    private static final String BASE_URL = "https://petstore.swagger.io/v2";

    private static enum PetStatus {available, pending, sold}

    @Test
    public void retrieve_all_available_pats_and_confirm_that_the_name_doggie_is_on_the_list() {

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

        String failureMessage = String.format("Pet %s with category id %s was not found!", PET_NAME,PET_CATEGORY_ID);
        String passMessage = String.format("Pet %s with category id %s found!", PET_NAME,PET_CATEGORY_ID);

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

        //... and then afterwards;
        Assert.assertTrue(failureMessage, is_pet_found_on_the_list_of_available_pets);
        // if the above assertion is false
        // (being that is_pet_found_on_the_list_of_available_pets is equal to false);
        // then the test failed!

        System.out.println(passMessage);
    }

    @Ignore
    public void add_a_new_pet_with_an_auto_generated_name_and_status_available_confirm_the_new_pet_has_been_added() {

    }

    @Ignore
    public void from_point_2_above_retrieve_the_created_pet_using_the_id() {

    }

}