package api.endpoints;

import static io.restassured.RestAssured.*;

import api.payload.User_Pojo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


//Create for perform CRUD requests to the user API


public class userEndPoints{
	
	//Create user
	public static Response createUser(User_Pojo payload) {
		Response response = given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload).log().all()
		.when()
			.post(routes.post_url);
		 
		return response;
	}
	
	//Read user
	public static Response readUser(String userName ) {
		Response response = given()
				.pathParam("username", userName).log().all()
		.when()
			.get(routes.get_url);
		 
		return response;
	}
	
	//Update user
	public static Response updateUser(String userName, User_Pojo payload) {
		Response response = given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.pathParam("username", userName)
			.body(payload).log().all()
		.when()
			.put(routes.update_url);
		 
		return response;
	}
	
	//Delete user
	public static Response deleteUser(String userName) {
		Response response = given()
			.pathParam("username", userName).log().all()
		.when()
			.delete(routes.delete_url);
		 
		return response;
	}
	
	
}
