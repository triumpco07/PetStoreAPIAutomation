package api.endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import java.util.ResourceBundle;

import api.payload.Pet_Pojo;

public class PetEndPoints {
	
	static ResourceBundle getURL() {
		ResourceBundle routes = ResourceBundle.getBundle("routes"); 
		return routes;
	}

//	//Upload an a pet image
//	public static Response uploadPetImage() {
//		
//		return response;
//	}
	
	
	//Add a new pet
	public static Response addNewPet(Pet_Pojo petPayload) {
		
		
		String add_pet_url = getURL().getString("add_pet_url");
		
		
		Response response = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(petPayload).log().all()
			.when()
				.post(add_pet_url);
		
		return response;
	}
	
	//Update an existing pet
	public static Response updatePet(Pet_Pojo petPayload) {
		
		String update_pet_url = getURL().getString("add_pet_url"); // Same URL as add, uses PUT method
		
		Response response = 
			given()
				.contentType(ContentType.JSON)
				.accept(ContentType.JSON)
				.body(petPayload).log().all()
			.when()
				.put(update_pet_url);
		
		return response;
	}
	
	//Find pets by status
	public static Response findPetsByStatus(String status) {
		
		String find_pets_by_status_url = getURL().getString("find_pets_by_status_url");
		
		Response response = 
			given()
				.accept(ContentType.JSON)
				.queryParam("status", status)
				.log().all()
			.when()
				.get(find_pets_by_status_url);
		
		return response;
	}
	
	//Find pet by ID
	public static Response getPetById(long petId) {
		
		String get_pet_by_id_url = getURL().getString("get_pet_by_id_url");
		
		Response response = 
			given()
				.accept(ContentType.JSON)
				.pathParam("petId", petId)
				.log().all()
			.when()
				.get(get_pet_by_id_url);
		
		return response;
	}
	
	//Delete pet by ID
	public static Response deletePetById(long petId) {
		
		String delete_pet_url = getURL().getString("delete_pet_url");
		
		Response response = 
			given()
				.accept(ContentType.JSON)
				.pathParam("petId", petId)
				.log().all()
			.when()
				.delete(delete_pet_url);
		
		return response;
	}
}
