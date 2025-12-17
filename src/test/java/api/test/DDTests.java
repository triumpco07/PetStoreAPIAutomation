package api.test;

import org.testng.Assert;
import org.testng.annotations.Test;

import api.endpoints.userEndPoints;
import api.endpoints.userEndPoints2;
import api.payload.User_Pojo;
import api.utilities.DataProviders;
import io.restassured.response.Response;

public class DDTests {
	
	
	@Test(priority=1, dataProvider="Data", dataProviderClass=DataProviders.class)
	public void testPostUsers(String userID, String userName,String fName,  String lName, String userEmail, String Pwd, String Phone) {
		
		User_Pojo userPayload = new User_Pojo();
		
		userPayload.setId(Integer.parseInt(userID));
		userPayload.setUsername(userName);
		userPayload.setFirstName(fName);
		userPayload.setLastName(lName);
		userPayload.setEmail(userEmail);
		userPayload.setPassword(Pwd);
		userPayload.setPhone(Phone);
		
		
		Response response = userEndPoints.createUser(userPayload);
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	@Test(priority=2, dataProvider="Data", dataProviderClass=DataProviders.class)
	public void testUserLoginDD(String userID, String userName,String fName,  String lName, String userEmail, String Pwd, String Phone) {
		Response response = userEndPoints2.userLogin(userName, Pwd);
		Assert.assertEquals(response.getStatusCode(), 200);
	}
	
	
	@Test(priority=3, dataProvider="UserNames", dataProviderClass = DataProviders.class)
	public void testDeleteUSerByName(String userName) {
		Response response = userEndPoints.deleteUser(userName);
		Assert.assertEquals(response.getStatusCode(), 200);
		
	}
	
	
	
}
