package api.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import api.endpoints.userEndPoints;
import api.endpoints.userEndPoints2;
import api.payload.User_Pojo;
import api.utilities.DataProviders;
import io.restassured.response.Response;

public class DDUserTests {
	
	public Logger logger;
	
	@BeforeClass
	public void setup() {
	    logger = LogManager.getLogger(this.getClass());

	}


    // ================= CREATE USER =================
    @Test(priority = 1, dataProvider = "Data", dataProviderClass = DataProviders.class)
    public void testCreateUser(String userID, String userName, String fName,
                               String lName, String userEmail, String pwd, String phone) {
    	
    	logger.info("********************Creating User***********************");
    	
        if (userName == null || userName.isBlank()) {
            throw new SkipException("Skipping create: invalid username");
        }

        User_Pojo payload = new User_Pojo();
        payload.setId(Integer.parseInt(userID));
        payload.setUsername(userName);
        payload.setFirstName(fName);
        payload.setLastName(lName);
        payload.setEmail(userEmail);
        payload.setPassword(pwd);
        payload.setPhone(phone);

        Response response = userEndPoints.createUser(payload);
        Assert.assertEquals(response.getStatusCode(), 200);
        
		logger.info("********************User Created***********************");

    }
    //=========================== GET USER ====================================
    @Test(priority = 2, dataProvider = "Data", dataProviderClass = DataProviders.class)
    public void testGetUserByName(String userID, String userName, String fName,
            String lName, String email, String pwd, String phone) {
    	logger.info("********************Reading User Info***********************");
    	
    	if (userName == null || userName.isBlank()) {
            throw new SkipException("Skipping create: invalid username");
        }

    	
		Response response = userEndPoints2.readUser(userName);
		response.then().log().all();
		Assert.assertEquals(response.getStatusCode(), 200);
		
		logger.info("********************User Info is displayed***********************");

    }
    

    // ================= UPDATE USER =================
    @Test(priority = 3, dataProvider = "Data", dataProviderClass = DataProviders.class)
    public void testUpdateUser(String userID, String userName, String fName,
                               String lName, String userEmail, String pwd, String phone) {

		logger.info("********************Updating User***********************");

		if (userName == null || userName.isBlank()) {
            throw new SkipException("Skipping create: invalid username");
        }

    	

        User_Pojo payload = new User_Pojo();
        payload.setId(Integer.parseInt(userID));
        payload.setUsername(userName);
        payload.setFirstName(fName + "_Updated");
        payload.setLastName(lName + "_Updated");
        payload.setEmail("updated_" + userEmail);
        payload.setPassword(pwd);
        payload.setPhone(phone);

        Response response = userEndPoints2.updateUser(userName, payload);
        Assert.assertEquals(response.getStatusCode(), 200);
        
		logger.info("********************User Updated***********************");

    }

    // ================= DELETE USER =================
    @Test(priority = 4, dataProvider = "UserNames", dataProviderClass = DataProviders.class)
    public void testDeleteUser(String userName) {

		logger.info("********************Deleting User***********************");

		if (userName == null || userName.isBlank()) {
            throw new SkipException("Skipping create: invalid username");
        }


        Response response = userEndPoints.deleteUser(userName);
        Assert.assertEquals(response.getStatusCode(), 200);
        
		logger.info("********************User Deleted***********************");

    }
}
