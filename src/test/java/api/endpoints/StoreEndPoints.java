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

}
