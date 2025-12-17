package api.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.github.javafaker.Faker;

import api.endpoints.userEndPoints;
import api.payload.User_Pojo;
import io.restassured.response.Response;

public class userTests {
	
	Faker faker; //used to create the data and then pass the data into getter and setter
	User_Pojo userPayload;
	
	public Logger logger;
	
	@BeforeClass
	public void setup() {
		faker = new Faker();
		userPayload = new User_Pojo();
		
		userPayload.setId(faker.idNumber().hashCode());
		userPayload.setUsername(faker.name().username());
		userPayload.setFirstName(faker.name().firstName());
		userPayload.setLastName(faker.name().lastName());
		userPayload.setEmail(faker.internet().safeEmailAddress());
		userPayload.setPassword(faker.internet().password(5, 10));
		userPayload.setPhone(faker.phoneNumber().cellPhone());
		userPayload.setId(faker.idNumber().hashCode());
		
		
		
		//logs
		logger = LogManager.getLogger(this.getClass());
	}
	
	@Test(priority = 1)
	public void testPostUser() {
		
		logger.info("********************Creating User***********************");
		Response response = userEndPoints.createUser(userPayload);
		response.then().log().all();
		
		Assert.assertEquals(response.getStatusCode(), 200);
		
		logger.info("********************User Created***********************");
	}
	
	
	@Test(priority=2)
	public void testGetUserByName() {
		
		logger.info("********************Reading User Info***********************");

		Response response = userEndPoints.readUser(this.userPayload.getUsername());
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		
		logger.info("********************User Info is displayed***********************");

	}
	
	@Test(priority=3)
	public void testUpdateUserName() {
		
		logger.info("********************Updating User***********************");

		
		//update data using payload
		userPayload.setFirstName(faker.name().firstName());
		userPayload.setLastName(faker.name().lastName());
		userPayload.setEmail(faker.internet().safeEmailAddress());
		
		Response response = userEndPoints.updateUser(this.userPayload.getUsername(), userPayload);
		response.then().log().all();
		// response.then().log().body().statusCode(200); --> this is Chai assertion
		Assert.assertEquals(response.getStatusCode(), 200);
		
		logger.info("********************User Updated***********************");

		
		//Checking data after update
		Response updatedResponse = userEndPoints.readUser(this.userPayload.getUsername());
		response.then().log().all();
		Assert.assertEquals(updatedResponse.getStatusCode(), 200);
	}
	
	@Test(priority=4)
	public void testDeleteUserByName() {
		
		logger.info("********************Deleting User***********************");

		Response response = userEndPoints.deleteUser(this.userPayload.getUsername());
		Assert.assertEquals(response.getStatusCode(), 200);
		
		logger.info("********************User Deleted***********************");

	}
	
}
