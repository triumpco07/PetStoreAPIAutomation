package api.endpoints;

import static io.restassured.RestAssured.*;

import java.util.ResourceBundle;

import api.payload.User_Pojo;
import io.restassured.http.ContentType;
import io.restassured.response.Response;


//Create for perform CRUD requests to the user API


public class userEndPoints2{
	
	static ResourceBundle getURL() {
		ResourceBundle routes = ResourceBundle.getBundle("routes"); 
		return routes;
	}
	
	
	
	//Create user
	public static Response createUser(User_Pojo payload) {
		
		String post_url = getURL().getString("post_url");
		

		Response response = given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(payload).log().all()
		.when()
			.post(post_url);
		 
		return response;
	}
	
	//Read user
	public static Response readUser(String userName ) {
		
		
		String getURL = getURL().getString("get_url");
		Response response = given()
				.pathParam("username", userName).log().all()
		.when()
			.get(getURL);
		 
		return response;
	}
	
	//Update user
	public static Response updateUser(String userName, User_Pojo payload) {
		
		String update_url = getURL().getString("update_url");
		
		Response response = given()
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.pathParam("username", userName)
			.body(payload).log().all()
		.when()
			.put(update_url);
		 
		return response;
	}
	
	//Delete user
	public static Response deleteUser(String userName) {
		
		String delete_url = getURL().getString("delete_url");
		Response response = given()
			.pathParam("username", userName).log().all()
		.when()
			.delete(delete_url);
		 
		return response;
	}
	
	//User login
	public static Response userLogin(String username, String password) {
		String login_url = getURL().getString("login_url");
		
		Response response = given()
			.queryParam("username", username)
			.queryParam("username", username)
			.log().all()
		.when()
			.get(login_url);
		
		
		return response;
		
	}
	
	
	
	
}
