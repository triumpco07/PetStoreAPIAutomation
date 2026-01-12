package api.endpoints;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import java.util.ResourceBundle;

public class StoreEndPoints {
	
	static ResourceBundle getURL() {
		ResourceBundle routes = ResourceBundle.getBundle("routes"); 
		return routes;
	}
	
	//Get pet inventory by status
	public static Response getPetInventoryByStatus() {
		
		String get_inventory_by_status_url = getURL().getString("get_inventory_by_status_url");
		
		Response response = 
			given()
				.accept(ContentType.JSON)
				.log().all()
			.when()
				.get(get_inventory_by_status_url);
		
		return response;
	}
	
	public static Response placeOrder(Object payload) {
		String place_order_url = getURL().getString("place_order_url");

		
	            Response response = given()
	                .contentType(io.restassured.http.ContentType.JSON)
	                .accept(io.restassured.http.ContentType.JSON)
	                .body(payload)
	            .when()
	                .post(place_order_url);
	            
	    	    return response;

	}
	// FIND PURCHASE ORDER BY ID - Int
	public static Response getOrderById(int orderId) {
		
		String get_order_by_id_url = getURL().getString("get_order_by_id_url");


	    return io.restassured.RestAssured
	            .given()
	                .accept(io.restassured.http.ContentType.JSON)
	                .pathParam("orderId", orderId)
	            .when()
	                .get(get_order_by_id_url)
	            .then()
	                .extract()
	                .response();
	}
	// FIND PURCHASE ORDER BY ID - String

	public static Response getOrderById(String orderId) {
		
		String get_order_by_id_url = getURL().getString("get_order_by_id_url");


	    return io.restassured.RestAssured
	            .given()
	                .accept(io.restassured.http.ContentType.JSON)
	                .pathParam("orderId", orderId)
	            .when()
	                .get(get_order_by_id_url)
	            .then()
	                .extract()
	                .response();
	}
	
	// ================= DELETE ORDER BY ID (VALID NUMERIC) =================
	public static Response deleteOrderById(int orderId) {

		String get_order_by_id_url = getURL().getString("get_order_by_id_url");

	    return given()
	            .pathParam("orderId", orderId)
	        .when()
	            .delete(get_order_by_id_url);
	}
	// ================= DELETE ORDER BY ID (INVALID / NON-NUMERIC) =================
	public static Response deleteOrderById(String orderId) {
		String get_order_by_id_url = getURL().getString("get_order_by_id_url");

	    return given()
	        .when()
	            .delete(get_order_by_id_url + "/" + orderId);
	}




}
