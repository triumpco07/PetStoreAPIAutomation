package api.test;

import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.Test;

import api.endpoints.userEndPoints2;
import api.utilities.DataProviders;
import io.restassured.response.Response;

public class DDUserAuthenticationTests {

    // ================= POSITIVE LOGIN =================
    @Test(priority = 1, dataProvider = "Data", dataProviderClass = DataProviders.class)
    public void testLoginPositive(String userID, String userName, String fName,
                                  String lName, String email, String pwd, String phone) {

      
     // LOGIN
        Response response = userEndPoints2.userLogin(userName, pwd);
        Assert.assertEquals(response.getStatusCode(), 200);
        
     // LOGOUT
        Response logoutResponse = userEndPoints2.userLogout();
        Assert.assertEquals(logoutResponse.getStatusCode(), 200);
    }
    

}
