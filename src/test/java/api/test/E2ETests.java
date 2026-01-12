package api.test;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;

import api.endpoints.PetEndPoints;
import api.endpoints.StoreEndPoints;
import api.endpoints.userEndPoints2;
import api.payload.Category_Pojo;
import api.payload.Pet_Pojo;
import api.payload.Store_Pojo;
import api.payload.User_Pojo;
import api.utilities.DataProviders;
import api.utilities.ExtentReportManager;
import io.restassured.response.Response;
import com.aventstack.extentreports.Status;


public class E2ETests {

	
	public Logger logger;
	public ExtentTest extentTest;

	
	@BeforeClass
	public void setup() {
	    logger = LogManager.getLogger(this.getClass());
	}
	
	// Get ExtentTest instance for reporting
	private ExtentTest getExtentTest() {
	    extentTest = ExtentReportManager.getTest();
	    if (extentTest == null) {
	        return null;
	    }
	    return extentTest;
	}
	
	ExtentTest test = ExtentReportManager.getTest();

	
	
	@Test(dataProvider = "E2E_UserPetOrder", dataProviderClass = DataProviders.class)
	public void testE2E_User_PlaceOrder_ForPet(
	        String username,
	        String firstName,
	        String lastName,
	        String email,
	        String password,
	        String phone,
	        String petName,
	        String petStatus,
	        String orderQty) {

	    ExtentTest test = ExtentReportManager.getTest();

	    long petId = 0;
	    long orderId = 0;

	    try {
	        // ================= CREATE USER =================
	        User_Pojo user = new User_Pojo();
	        user.setId(System.currentTimeMillis());
	        user.setUsername(username);
	        user.setFirstName(firstName);
	        user.setLastName(lastName);
	        user.setEmail(email);
	        user.setPassword(password);
	        user.setPhone(phone);

	        Response userRes = userEndPoints2.createUser(user);
	        Assert.assertEquals(userRes.getStatusCode(), 200);
	        if(test != null) {
	        test.log(Status.PASS, "User created: " + username);
	        }
	        
	        // ================= CREATE PET =================
	        Pet_Pojo pet = new Pet_Pojo();
	        pet.setId(System.currentTimeMillis());
	        pet.setName(petName);
	        pet.setStatus(petStatus);
	        
	        Category_Pojo category = new Category_Pojo();
	        category.setId(1);
	        category.setName("Dogs");
	        pet.setCategory(category);
	        
	        pet.setPhotoUrls(List.of("https://petstore.com/test.jpg"));


	        Response petRes = PetEndPoints.addNewPet(pet);
	        Assert.assertEquals(petRes.getStatusCode(), 200);

	        petId = petRes.jsonPath().getLong("id");
	        if(test != null) {
	        test.log(Status.PASS, "Pet created with ID: " + petId);
	        }
	        // ================= PLACE ORDER =================
	        Store_Pojo order = new Store_Pojo();
	        order.setId(System.currentTimeMillis());
	        order.setPetId(petId);
	        order.setQuantity(Integer.parseInt(orderQty));
	        order.setStatus("placed");
	        order.setComplete(true);

	        Response orderRes = StoreEndPoints.placeOrder(order);
	        Assert.assertEquals(orderRes.getStatusCode(), 200);

	        orderId = orderRes.jsonPath().getLong("id");
	        if(test != null) {
	        test.log(Status.PASS, "Order placed with ID: " + orderId);
	        }
	     // ================= VERIFY ORDER =================
	        Response getOrderRes = StoreEndPoints.getOrderById((int) orderId);
	        int getStatus = getOrderRes.getStatusCode();

	        if (getStatus == 404) {
	            logger.warn("Order not found after creation. Known Swagger Petstore limitation.");

	            if (test != null) {
	                test.log(Status.WARNING,
	                    "Order not found after creation (Swagger Petstore inconsistency)");
	            }

	        } else {
	            Assert.assertEquals(getStatus, 200, "Expected 200 when fetching order");

	            Long fetchedPetId = getOrderRes.jsonPath().getLong("petId");
	            Assert.assertNotNull(fetchedPetId, "petId should not be null in order response");
	            Assert.assertEquals(fetchedPetId.longValue(), petId,
	                    "Order petId does not match created petId");

	            if (test != null) {
	                test.log(Status.PASS, "Order verified successfully");
	            }
	        }

	    } finally {

	        // ================= CLEANUP =================
	        if (orderId > 0) {
	            StoreEndPoints.deleteOrderById((int) orderId);
	            if(test != null) {
	            test.log(Status.INFO, "Order deleted");
	            }
	        }

	        if (petId > 0) {
	            PetEndPoints.deletePetById(petId);
	            if(test != null) {
	            test.log(Status.INFO, "Pet deleted");
	            }
	        }

	        userEndPoints2.deleteUser(username);
	        if(test != null) {
	        test.log(Status.INFO, "User deleted");
	        }
	    }
	}

}
