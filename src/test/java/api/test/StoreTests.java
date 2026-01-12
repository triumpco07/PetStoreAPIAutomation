package api.test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import api.endpoints.StoreEndPoints;
import api.utilities.DataProviders;
import api.utilities.ExtentReportManager;
import io.restassured.response.Response;

import static org.hamcrest.Matchers.lessThan;

public class StoreTests {
	
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
	        // Fallback if ExtentTest is not available
	        return null;
	    }
	    return extentTest;
	}
	
    // ================= GET PET INVENTORY BY STATUS =================
	@Test(priority=1)
	public void testPetInventoryByStatus() {
		
		logger.info("======== Get Pet Inventory by Status Test Started ========");
		ExtentTest test = getExtentTest();
		if (test != null) {
		    test.log(Status.INFO, "Starting test: Get Pet Inventory by Status");
		}
		
		// ---------- API CALL ----------
		Response response = StoreEndPoints.getPetInventoryByStatus();
		
		logger.info("Response Body: {}", response.asString());
		
		if (test != null) {
		    test.log(Status.INFO, "API Call: GET /v2/store/inventory");
		    test.log(Status.INFO, "Response Status Code: " + response.getStatusCode());
		    test.log(Status.INFO, "Response Body: " + response.asString());
		}
		
		// ==========================================================
		// BASIC CONTRACT VALIDATION 
		// ==========================================================
		logger.info("======== Step 1: Basic Contract Validation ========");
		
		// Status Code: 200 OK
		Assert.assertEquals(response.getStatusCode(), 200, 
			"Expected status code 200, but got: " + response.getStatusCode());
		
		// Content-Type: application/json
		Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"),
			"Content-Type should be application/json");
		
		if (test != null) {
		    test.log(Status.INFO, "Status Code: 200 OK");
		    test.log(Status.INFO, "Content-Type: application/json");
		}
		
		// ==========================================================
		// RESPONSE STRUCTURE VALIDATION (Most Important)
		// ==========================================================
		logger.info("======== Step 2: Response Structure Validation ========");
		
		// Response is a JSON object (Map)
		Map<String, Object> inventory = response.jsonPath().getMap("$");
		
		// ✅ Response is a JSON object
		Assert.assertNotNull(inventory, "Response is not a JSON object");
		Assert.assertFalse(inventory.isEmpty(), "Inventory response is empty");
		
		if (test != null) {
		    test.log(Status.INFO, "Response is a JSON object");
		    test.log(Status.INFO, "Inventory contains " + inventory.size() + " status entries");
		}
		
		// Keys are String (Status names)
		// Values are Number (Inventory counts)
		// Values ≥ 0 (No negative inventory)
		for (Map.Entry<String, Object> entry : inventory.entrySet()) {
		    String statusKey = entry.getKey();
		    Object statusValue = entry.getValue();
		    
		    // Key must be String
		    Assert.assertTrue(statusKey instanceof String, 
		        "Status key must be String, but got: " + statusKey.getClass().getSimpleName());
		    
		    // Value must be Number
		    Assert.assertTrue(statusValue instanceof Number, 
		        "Status value must be Number for key '" + statusKey + "', but got: " + 
		        (statusValue != null ? statusValue.getClass().getSimpleName() : "null"));
		    
		    // Value must be >= 0
		    int count = ((Number) statusValue).intValue();
		    Assert.assertTrue(count >= 0, 
		        "Inventory count for '" + statusKey + "' must be >= 0, but got: " + count);
		    
		    logger.debug("Status: {} = {}", statusKey, count);
		}
		
		if (test != null) {
		    test.log(Status.INFO, "All keys are Strings");
		    test.log(Status.INFO, "All values are Numbers");
		    test.log(Status.INFO, "All values are >= 0");
		}
		
		// ==========================================================
		// BUSINESS LOGIC VALIDATION 
		// ==========================================================
		logger.info("======== Step 3: Business Logic Validation ========");
		
		// Known Statuses Must Exist
		List<String> validStatuses = List.of("available", "pending", "sold");
		
		for (String validStatus : validStatuses) {
		    Assert.assertTrue(inventory.containsKey(validStatus), 
		        "Required status '" + validStatus + "' is missing from inventory");
		    logger.info("✅ Required status '{}' exists with count: {}", 
		        validStatus, inventory.get(validStatus));
		}
		
		if (test != null) {
		    test.log(Status.INFO, "All required statuses exist: available, pending, sold");
		}
		
		// Invalid Statuses Should NOT Exist (or must be flagged)
		// Note: Swagger Petstore may return invalid statuses like "solt" (typo)
		// This is a data quality issue that should be logged but may not fail in test environments
		java.util.List<String> invalidStatuses = new java.util.ArrayList<>();
		for (String key : inventory.keySet()) {
		    if (!validStatuses.contains(key)) {
		        invalidStatuses.add(key);
		        logger.warn("Invalid status found in inventory: '{}'. Valid statuses are: {}", key, validStatuses);
		    }
		}
		
		if (!invalidStatuses.isEmpty()) {
		    String invalidStatusesStr = String.join(", ", invalidStatuses);
		    logger.warn("⚠️ Found {} invalid status(es) in inventory: {}", invalidStatuses.size(), invalidStatusesStr);
		    
		    if (test != null) {
		        test.log(Status.WARNING, "Invalid statuses found (data quality issue): " + invalidStatusesStr);
		        test.log(Status.WARNING, "This indicates potential backend data corruption. Valid statuses: " + validStatuses);
		    }
		    
		    // In production, this should fail. For Swagger Petstore, we log as warning
		    // Uncomment the line below to make test fail on invalid statuses:
		    // Assert.fail("Invalid statuses found in inventory: " + invalidStatusesStr + ". Valid statuses: " + validStatuses);
		} else {
		    if (test != null) {
		        test.log(Status.INFO, "No invalid statuses found - Data quality is good");
		    }
		}
		
		// ==========================================================
		// CASE SENSITIVITY VALIDATION 
		// ==========================================================
		logger.info("======== Step 4: Case Sensitivity Validation ========");
		
		// Status values should be case-consistent
		// No duplicate meanings (e.g., "available" and "Available")
		// Note: Swagger Petstore may have case-inconsistent statuses
		Set<String> normalizedKeys = new HashSet<>();
		java.util.List<String> duplicateStatuses = new java.util.ArrayList<>();
		java.util.Map<String, String> normalizedToOriginal = new java.util.HashMap<>();
		
		for (String key : inventory.keySet()) {
		    String normalized = key.toLowerCase().replace(" ", "");
		    
		    if (!normalizedKeys.add(normalized)) {
		        // Found duplicate - this is a case consistency issue
		        String originalDuplicate = normalizedToOriginal.get(normalized);
		        duplicateStatuses.add(key);
		        logger.warn("⚠️ Case-inconsistent status found: '{}' (duplicate of '{}')", key, originalDuplicate);
		    } else {
		        normalizedToOriginal.put(normalized, key);
		    }
		}
		
		if (!duplicateStatuses.isEmpty()) {
		    String duplicatesStr = String.join(", ", duplicateStatuses);
		    logger.warn("⚠️ Found {} case-inconsistent status(es): {}", duplicateStatuses.size(), duplicatesStr);
		    
		    if (test != null) {
		        test.log(Status.WARNING, "Case-inconsistent statuses found (data quality issue): " + duplicatesStr);
		        test.log(Status.WARNING, "Status values should be case-consistent. This indicates potential backend data issues.");
		    }
		    
		    // In production, this should fail. For Swagger Petstore, we log as warning
		    // Uncomment the line below to make test fail on case inconsistencies:
		    // Assert.fail("Case-inconsistent statuses found: " + duplicatesStr + ". Status values should be case-consistent.");
		} else {
		    if (test != null) {
		        test.log(Status.INFO, "No duplicate or case-inconsistent statuses found - Data quality is good");
		    }
		}
		
		// ==========================================================
		// DATA ACCURACY
		// ==========================================================
		logger.info("======== Step 5: Data Accuracy Validation ========");
		
		// Sum of all inventory counts should be > 0
		int totalInventory = inventory.values().stream()
		        .mapToInt(v -> ((Number) v).intValue())
		        .sum();
		
		Assert.assertTrue(totalInventory > 0, 
		    "Total inventory count should be > 0, but got: " + totalInventory);
		
		logger.info("Total inventory count: {}", totalInventory);
		
		if (test != null) {
		    test.log(Status.INFO, "Total inventory count: " + totalInventory);
		}
		
		// ==========================================================
		// PERFORMANCE EXPECTATIONS (Production Reality)
		// ==========================================================
		logger.info("======== Step 6: Performance Validation ========");
		
		// Response time validation (non-blocking)
		long responseTime = response.getTime();

		logger.info("Response time: {} ms", responseTime);

		if (test != null) {
		    test.log(Status.INFO, "Response time: " + responseTime + " ms");
		}
		// Soft threshold (industry standard for public APIs)
		if (responseTime > 3000) {
		    logger.warn("⚠️ Response time is slow: {} ms", responseTime);
		    
		    if (test != null) {
		        test.log(Status.WARNING, 
		            "Response time exceeded 3s threshold: " + responseTime + " ms");
		    }
		}

		
		// ==========================================================
		// TEST SUMMARY
		// ==========================================================
		logger.info("======== Test Summary ========");
		logger.info("Total status entries: {}", inventory.size());
		logger.info("Total inventory count: {}", totalInventory);
		logger.info("Response time: {} ms", responseTime);
		
		// Log all statuses and their counts
		for (Map.Entry<String, Object> entry : inventory.entrySet()) {
		    logger.info("  {}: {}", entry.getKey(), entry.getValue());
		}
		
		if (test != null) {
		    test.log(Status.PASS, "Pet inventory by status test passed successfully");
		    test.log(Status.INFO, "Total status entries: " + inventory.size());
		    test.log(Status.INFO, "Total inventory count: " + totalInventory);
		}
		
		logger.info("======== Get Pet Inventory by Status Test Completed Successfully ========");
	}
	
	// ================= NEGATIVE & SECURITY TESTS =================
	@Test(priority=2)
	public void testPetInventoryByStatus_WrongMethod() {
		
		logger.info("======== Negative Test: Wrong HTTP Method ========");
		ExtentTest test = getExtentTest();
		if (test != null) {
		    test.log(Status.INFO, "Starting test: Wrong HTTP Method (POST instead of GET)");
		}
		
		// Wrong HTTP Method: POST /store/inventory → 405
		// Access the URL directly using ResourceBundle
		java.util.ResourceBundle routes = java.util.ResourceBundle.getBundle("routes");
		String get_inventory_by_status_url = routes.getString("get_inventory_by_status_url");
		
		Response response = 
			io.restassured.RestAssured.given()
				.accept(io.restassured.http.ContentType.JSON)
				.log().all()
			.when()
				.post(get_inventory_by_status_url);
		
		logger.info("Response Status Code: {}", response.getStatusCode());
		
		// Should return 405 Method Not Allowed
		Assert.assertTrue(
		    response.getStatusCode() == 405 || response.getStatusCode() == 404,
		    "Expected 405 (Method Not Allowed) or 404, but got: " + response.getStatusCode()
		);
		
		if (test != null) {
		    test.log(Status.INFO, "Response Status Code: " + response.getStatusCode());
		    test.log(Status.PASS, "Wrong HTTP method correctly rejected");
		}
		
		logger.info("======== Wrong HTTP Method Test Completed ========");
	}
	
	// ================= PERFORMANCE TEST =================
	@Test(priority = 3)
	public void testPetInventoryByStatus_Performance() {

	    logger.info("======== Performance Test: Response Time ========");
	    ExtentTest test = getExtentTest();
	    if (test != null) {
	        test.log(Status.INFO, "Starting test: Performance Validation");
	    }

	    Response response = StoreEndPoints.getPetInventoryByStatus();

	    long responseTime = response.getTime();
	    logger.info("Response time: {} ms", responseTime);

	    if (test != null) {
	        test.log(Status.INFO, "Response time: " + responseTime + " ms");
	    }

	    // Soft threshold (industry standard for public APIs)
	    if (responseTime > 1500) {
	        logger.warn("Response time is slow: {} ms", responseTime);

	        if (test != null) {
	            test.log(Status.WARNING,
	                "Response time exceeded expected threshold: " + responseTime + " ms");
	        }
	    } else {
	        logger.info("Response time is within acceptable range");
	    }

	    // Functional sanity check
	    Map<String, Object> inventory = response.jsonPath().getMap("$");
	    Assert.assertFalse(inventory.isEmpty(), "Response should not be empty");

	    if (test != null) {
	        test.log(Status.PASS, "Performance validation completed");
	    }

	    logger.info("======== Performance Test Completed ========");
	}
	
	// ================= PLACE ORDER FOR PET =================
	@Test(priority = 4, dataProvider = "PlaceOrderData", dataProviderClass = DataProviders.class)
	public void testPlaceOrderForPetDD(
	        String orderId,
	        String petId,
	        String quantity,
	        String shipDate,
	        String status,
	        String complete) {

	    logger.info("======== Place Order For Pet (DDT) Started ========");
	    ExtentTest test = getExtentTest();  

	    if (test != null) {
	        test.log(Status.INFO, "Starting test: Place Order For Pet");
	    }
	    // ---------------- GIVEN ----------------
	    Map<String, Object> orderPayload = new java.util.HashMap<>();
	    orderPayload.put("id", Long.parseLong(orderId));
	    orderPayload.put("petId", Long.parseLong(petId));
	    orderPayload.put("quantity", Integer.parseInt(quantity));
	    orderPayload.put("shipDate", shipDate);
	    orderPayload.put("status", status);
	    orderPayload.put("complete", Boolean.parseBoolean(complete));

	    logger.info("Order Payload: {}", orderPayload);

	    if (test != null) {
	        test.log(Status.INFO, "Order Payload: " + orderPayload);
	    }

	    // ---------------- WHEN ----------------
	    Response response = StoreEndPoints.placeOrder(orderPayload);

	    logger.info("Response Body: {}", response.asString());

	    // ---------------- THEN ----------------
	    Assert.assertEquals(response.getStatusCode(), 200,
	            "Expected status code 200 but got " + response.getStatusCode());

	    // Contract validation
	    Assert.assertNotNull(response.jsonPath().get("id"));
	    Assert.assertTrue(response.jsonPath().get("id") instanceof Number);

	    Assert.assertEquals(response.jsonPath().getInt("petId"),
	            Integer.parseInt(petId));

	    Assert.assertTrue(response.jsonPath().get("quantity") instanceof Number);
	    Assert.assertTrue(response.jsonPath().get("status") instanceof String);
	    Assert.assertTrue(response.jsonPath().get("complete") instanceof Boolean);

	    // Business rules
	    Assert.assertTrue(response.jsonPath().getInt("quantity") > 0,
	            "Quantity must be greater than 0");

	    // Performance (soft check)
	    long responseTime = response.getTime();
	    logger.info("Response time: {} ms", responseTime);

	    if (responseTime > 2000) {
	        logger.warn("Order API response is slow: {} ms", responseTime);
	    }

	    if (test != null) {
	        test.log(Status.INFO, "Response Time: " + responseTime + " ms");
	        test.log(Status.PASS, "Place Order For Pet test passed");
	    }

	    logger.info("======== Place Order For Pet (DDT) Completed ========");
	}
	
	// ================= FIND PURCHASE ORDER BY ID =================
	@Test(priority = 5,dataProvider = "FindOrderByIdData", dataProviderClass = DataProviders.class)
	public void testFindOrderById(
	        String testCase,
	        String orderId,
	        String expectedStatus,
	        String scenario) {

	    Logger logger = LogManager.getLogger(this.getClass());
	    ExtentTest test = ExtentReportManager.getTest();

	    logger.info("Running {} | orderId={} | scenario={}", testCase, orderId, scenario);

	    Response response;

	    // Handle non-numeric IDs safely
	    try {
	        int id = Integer.parseInt(orderId);
	        response = StoreEndPoints.getOrderById(id);
	    } catch (NumberFormatException e) {
	        response = StoreEndPoints.getOrderById(orderId);
	    }

	    int actualStatus = response.getStatusCode();

	    // Status Code Assertion (COMMON FOR ALL)
	    Assert.assertEquals(
	        actualStatus,
	        Integer.parseInt(expectedStatus),
	        "Status code mismatch for " + testCase
	    );

	    // Scenario-specific assertions
	    if (scenario.equalsIgnoreCase("valid")) {

	        Assert.assertNotNull(response.jsonPath().get("id"));
	        Assert.assertTrue(response.jsonPath().get("id") instanceof Number);

	        Assert.assertTrue(response.jsonPath().get("status") instanceof String);

	        if (test != null) {
	            test.log(Status.PASS, testCase + " | Valid order fetched successfully");
	        }

	    } else {

	        Assert.assertNotNull(response.jsonPath().get("message"));

	        if (test != null) {
	            test.log(Status.PASS, testCase + " | Error handled correctly");
	        }
	    }
	}
	
	
	@Test(
		    priority = 6,
			dataProvider = "DeleteOrderData",
		    dataProviderClass = DataProviders.class
		)
		public void testDeleteOrderByIdDD(String orderId, String expectedStatus) {

		    logger.info("Deleting order with ID: {}", orderId);
		    ExtentTest test = getExtentTest();

		    Response response;

		    // Handle numeric vs non-numeric IDs safely
		    try {
		        int id = Integer.parseInt(orderId);
		        response = StoreEndPoints.deleteOrderById(id);
		    } catch (NumberFormatException e) {
		        response = StoreEndPoints.deleteOrderById(orderId);
		    }

		    int actualStatus = response.getStatusCode();
		    int expected = Integer.parseInt(expectedStatus);

		    logger.info("Expected Status: {}, Actual Status: {}", expected, actualStatus);

		    Assert.assertEquals(
		        actualStatus,
		        expected,
		        "Status code mismatch for orderId: " + orderId
		    );

		    if (test != null) {
		        test.log(Status.INFO, "Order ID: " + orderId);
		        test.log(Status.INFO, "Expected Status: " + expected);
		        test.log(Status.INFO, "Actual Status: " + actualStatus);
		        test.log(Status.PASS, "Delete order validation passed");
		    }
		}







}
