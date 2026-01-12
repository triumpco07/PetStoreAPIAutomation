package api.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import api.endpoints.PetEndPoints;
import api.payload.Category_Pojo;
import api.payload.Pet_Pojo;
import api.payload.Tag_Pojo;
import api.utilities.DataProviders;
import api.utilities.ExtentReportManager;
import io.restassured.response.Response;

public class PetTests {
	
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
	
    // ================= CREATE PET =================
	@Test(priority=1, dataProvider = "PetData", dataProviderClass = DataProviders.class)
	public void testCreatePet(String petId, String name, String status,
	                          String categoryId, String categoryName,
	                          String photoUrls,
	                          String tagIdsStr, String tagNamesStr)
	{
		logger.info("======== Creating Pet Test Started ========");
		ExtentTest test = getExtentTest();
		if (test != null) {
		    test.log(Status.INFO, "Starting test: Create Pet");
		    test.log(Status.INFO, "Pet Name: " + name + ", Status: " + status);
		}

	    // ---------- BASIC INPUT VALIDATION (Excel safety) ----------
	    Assert.assertNotNull(petId, "Pet ID is null in Excel");
	    Assert.assertNotNull(name, "Pet name is null in Excel");
	    Assert.assertNotNull(status, "Pet status is null in Excel");
	    Assert.assertNotNull(categoryId, "Category ID is null in Excel");
	    Assert.assertNotNull(categoryName, "Category Name is null in Excel");
	    Assert.assertNotNull(photoUrls, "Photo URLs are null in Excel");
	    Assert.assertNotNull(tagIdsStr, "Tag IDs are null in Excel");
	    Assert.assertNotNull(tagNamesStr, "Tag Names are null in Excel");

	    // ---------- CATEGORY ----------
	    Category_Pojo category = new Category_Pojo();
	    category.setId(Long.parseLong(categoryId));
	    category.setName(categoryName);

	    // ---------- PHOTO URLS ----------
	    List<String> photoUrls1 = Arrays.asList(photoUrls.split(","));
	    Assert.assertFalse(photoUrls1.isEmpty(), "Photo URLs	 list is empty");

	    // ---------- TAGS ----------
	    String[] tagIds = tagIdsStr.split(",");
	    String[] tagNames = tagNamesStr.split(",");

	    Assert.assertEquals(tagIds.length, tagNames.length,
	            "Tag IDs and Tag Names count mismatch");

	    List<Tag_Pojo> tags = new ArrayList<>();
	    for (int i = 0; i < tagIds.length; i++) {
	        Tag_Pojo tag = new Tag_Pojo();
	        tag.setId(Long.parseLong(tagIds[i]));
	        tag.setName(tagNames[i]);
	        tags.add(tag);
	    }

	    // ---------- PET PAYLOAD ----------
	    Pet_Pojo petPayload = new Pet_Pojo();
	    petPayload.setId(Long.parseLong(petId));
	    petPayload.setName(name);
	    petPayload.setStatus(status);
	    petPayload.setCategory(category);
	    petPayload.setPhotoUrls(photoUrls1);
	    petPayload.setTags(tags);

	    logger.info("Pet Payload Created Successfully");

	    // ---------- API CALL ----------
	    Response response = PetEndPoints.addNewPet(petPayload);

	    logger.info("Response Body: {}", response.asString());
	    
	    if (test != null) {
	        test.log(Status.INFO, "API Call: POST /v2/pet");
	        test.log(Status.INFO, "Response Status Code: " + response.getStatusCode());
	        test.log(Status.INFO, "Response Body: " + response.asString());
	    }

	    // ---------- STATUS + HEADER ----------
	    Assert.assertEquals(response.getStatusCode(), 200);
	    Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"));

	    // ---------- ID VALIDATION ----------
	    Long createdPetId = response.jsonPath().getLong("id");
	    Assert.assertNotNull(createdPetId);
	    Assert.assertTrue(createdPetId > 0);

	    // ---------- CATEGORY VALIDATION ----------
	    Assert.assertNotNull(response.jsonPath().get("category.id"));
	    Assert.assertTrue(response.jsonPath().get("category.id") instanceof Number);
	    Assert.assertTrue(response.jsonPath().get("category.name") instanceof String);

	    // ---------- TAGS VALIDATION ----------
	    List<Object> responseTagIds = response.jsonPath().getList("tags.id");
	    List<Object> responseTagNames = response.jsonPath().getList("tags.name");

	    Assert.assertFalse(responseTagIds.isEmpty());
	    Assert.assertEquals(responseTagIds.size(), tagIds.length);

	    for (Object id : responseTagIds) {
	        Assert.assertNotNull(id);
	        Assert.assertTrue(id instanceof Number);
	    }

	    for (Object tagName : responseTagNames) {
	        Assert.assertTrue(tagName instanceof String);
	    }

	    // ---------- PHOTO URLS VALIDATION ----------
	    List<Object> responsePhotoUrls = response.jsonPath().getList("photoUrls");
	    Assert.assertFalse(responsePhotoUrls.isEmpty());

	    for (Object url : responsePhotoUrls) {
	        Assert.assertTrue(url instanceof String);
	    }

	    // ---------- NAME & STATUS ----------
	    Assert.assertTrue(response.jsonPath().get("name") instanceof String);
	    Assert.assertTrue(response.jsonPath().get("status") instanceof String);

	    logger.info("======== Pet Created Successfully with ID: {} ========", createdPetId);
	    
	    if (test != null) {
	        test.log(Status.PASS, "Pet created successfully with ID: " + createdPetId);
	        test.log(Status.INFO, "Pet Name: " + name + ", Status: " + status);
	    }
		
	}
	
    // ================= UPDATE PET =================
	
	
	@Test(priority=2, dataProvider = "PetData", dataProviderClass = DataProviders.class)
	public void testUpdatePet(String petId, String name, String status,
            String categoryId, String categoryName,
            String photoUrls,
            String tagIdsStr, String tagNamesStr){
		
		logger.info("======== Updating Pet Test Started ========");
		ExtentTest test = getExtentTest();
		if (test != null) {
		    test.log(Status.INFO, "Starting test: Update Pet");
		    test.log(Status.INFO, "Pet ID: " + petId + ", Name: " + name);
		}

	    // ---------- BASIC INPUT VALIDATION (Excel safety) ----------
	    Assert.assertNotNull(petId, "Pet ID is null in Excel");
	    Assert.assertNotNull(name, "Pet name is null in Excel");
	    Assert.assertNotNull(status, "Pet status is null in Excel");
	    Assert.assertNotNull(categoryId, "Category ID is null in Excel");
	    Assert.assertNotNull(categoryName, "Category Name is null in Excel");
	    Assert.assertNotNull(photoUrls, "Photo URLs are null in Excel");
	    Assert.assertNotNull(tagIdsStr, "Tag IDs are null in Excel");
	    Assert.assertNotNull(tagNamesStr, "Tag Names are null in Excel");

	    // ---------- CATEGORY ----------
	    Category_Pojo category = new Category_Pojo();
	    category.setId(Long.parseLong(categoryId));
	    category.setName(categoryName + "_Updated"); // Updated category name

	    // ---------- PHOTO URLS ----------
	    List<String> photoUrls1 = Arrays.asList(photoUrls.split(","));
	    Assert.assertFalse(photoUrls1.isEmpty(), "Photo URLs list is empty");

	    // ---------- TAGS ----------
	    String[] tagIds = tagIdsStr.split(",");
	    String[] tagNames = tagNamesStr.split(",");

	    Assert.assertEquals(tagIds.length, tagNames.length,
	            "Tag IDs and Tag Names count mismatch");

	    List<Tag_Pojo> tags = new ArrayList<>();
	    for (int i = 0; i < tagIds.length; i++) {
	        Tag_Pojo tag = new Tag_Pojo();
	        tag.setId(Long.parseLong(tagIds[i]));
	        tag.setName(tagNames[i] + "_Updated"); // Updated tag names
	        tags.add(tag);
	    }

	    // ---------- PET PAYLOAD WITH UPDATED DATA ----------
	    Pet_Pojo petPayload = new Pet_Pojo();
	    petPayload.setId(Long.parseLong(petId));
	    petPayload.setName(name + "_Updated"); // Updated pet name
	    petPayload.setStatus(status); // Can keep same or update
	    petPayload.setCategory(category);
	    petPayload.setPhotoUrls(photoUrls1);
	    petPayload.setTags(tags);

	    logger.info("Pet Payload for Update Created Successfully");

	    // ---------- API CALL ----------
	    Response response = PetEndPoints.updatePet(petPayload);

	    logger.info("Response Body: {}", response.asString());
	    
	    if (test != null) {
	        test.log(Status.INFO, "API Call: PUT /v2/pet");
	        test.log(Status.INFO, "Response Status Code: " + response.getStatusCode());
	    }

	    // ---------- STATUS + HEADER VALIDATION ----------
	    Assert.assertEquals(response.getStatusCode(), 200, "Update pet request failed");
	    Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"));

	    // ---------- ID VALIDATION ----------
	    Long updatedPetId = response.jsonPath().getLong("id");
	    Assert.assertNotNull(updatedPetId, "Pet ID is null in response");
	    Assert.assertEquals(updatedPetId, Long.parseLong(petId), "Pet ID mismatch after update");

	    // ---------- UPDATED NAME VALIDATION ----------
	    String updatedName = response.jsonPath().getString("name");
	    Assert.assertNotNull(updatedName);
	    Assert.assertTrue(updatedName.contains("_Updated"), "Pet name was not updated");

	    // ---------- CATEGORY VALIDATION ----------
	    Assert.assertNotNull(response.jsonPath().get("category.id"));
	    Assert.assertEquals(response.jsonPath().getLong("category.id"), Long.parseLong(categoryId));
	    String updatedCategoryName = response.jsonPath().getString("category.name");
	    Assert.assertTrue(updatedCategoryName.contains("_Updated"), "Category name was not updated");

	    // ---------- TAGS VALIDATION ----------
	    List<Object> responseTagIds = response.jsonPath().getList("tags.id");
	    List<Object> responseTagNames = response.jsonPath().getList("tags.name");

	    Assert.assertFalse(responseTagIds.isEmpty());
	    Assert.assertEquals(responseTagIds.size(), tagIds.length);

	    // Verify tag names were updated
	    for (Object tagName : responseTagNames) {
	        Assert.assertTrue(tagName instanceof String);
	        Assert.assertTrue(((String) tagName).contains("_Updated"), "Tag name was not updated");
	    }

	    // ---------- STATUS VALIDATION ----------
	    String responseStatus = response.jsonPath().getString("status");
	    Assert.assertNotNull(responseStatus);
	    Assert.assertEquals(responseStatus, status);

	    logger.info("======== Pet Updated Successfully with ID: {} ========", updatedPetId);
	    
	    if (test != null) {
	        test.log(Status.PASS, "Pet updated successfully with ID: " + updatedPetId);
	        test.log(Status.INFO, "Updated Name: " + updatedName);
	    }
		
	}
	
    // ================= FIND PETS BY STATUS =================
	@Test(priority=3, dataProvider = "PetStatuses", dataProviderClass = DataProviders.class)
	public void testFindPetsByStatus(String status) {
		
		logger.info("======== Finding Pets by Status: {} ========", status);
		ExtentTest test = getExtentTest();
		if (test != null) {
		    test.log(Status.INFO, "Starting test: Find Pets by Status");
		    test.log(Status.INFO, "Status: " + status);
		}
		
		// ---------- API CALL ----------
		Response response = PetEndPoints.findPetsByStatus(status);
		
		logger.info("Response Body: {}", response.asString());
		
		if (test != null) {
		    test.log(Status.INFO, "API Call: GET /v2/pet/findByStatus?status=" + status);
		    test.log(Status.INFO, "Response Status Code: " + response.getStatusCode());
		}
		
		// ---------- STATUS + HEADER VALIDATION ----------
		Assert.assertEquals(response.getStatusCode(), 200, "Find pets by status request failed");
		Assert.assertTrue(response.getHeader("Content-Type").contains("application/json"));
		
		// ---------- RESPONSE IS ARRAY VALIDATION ----------
		List<Object> pets = response.jsonPath().getList("$");
		Assert.assertNotNull(pets, "Response is not an array");
		
		logger.info("Found {} pets with status: {}", pets.size(), status);
		
		// ---------- VALIDATE ALL PETS HAVE CORRECT STATUS ----------
		if (!pets.isEmpty()) {
			for (int i = 0; i < pets.size(); i++) {
				String petStatus = response.jsonPath().getString("[" + i + "].status");
				Assert.assertNotNull(petStatus, "Pet status is null at index " + i);
				Assert.assertEquals(petStatus, status, 
					"Pet at index " + i + " has incorrect status. Expected: " + status + ", Actual: " + petStatus);
				
				// Validate pet structure
				Long petId = response.jsonPath().getLong("[" + i + "].id");
				Assert.assertNotNull(petId, "Pet ID is null at index " + i);
				Assert.assertTrue(petId > 0, "Pet ID is invalid at index " + i);
			}
		}
		
		logger.info("======== Successfully found {} pets with status: {} ========", pets.size(), status);
		
		if (test != null) {
		    test.log(Status.PASS, "Successfully found " + pets.size() + " pets with status: " + status);
		}
	}
	
	
    // ================= FIND PET BY ID =================
	@Test(priority=4, dataProvider = "PetData", dataProviderClass = DataProviders.class)
	public void testGetPetById(String petId, String name, String status,
	                          String categoryId, String categoryName,
	                          String photoUrls,
	                          String tagIdsStr, String tagNamesStr) {
		
		logger.info("======== Find Pet by ID Test Started ========");
		ExtentTest test = getExtentTest();
		if (test != null) {
		    test.log(Status.INFO, "Starting test: Find Pet by ID");
		}

	    // ---------- STEP 1: CREATE PET FIRST ----------
	    logger.info("Step 1: Creating pet to get dynamic ID");
	    
	    // ---------- BASIC INPUT VALIDATION (Excel safety) ----------
	    Assert.assertNotNull(petId, "Pet ID is null in Excel");
	    Assert.assertNotNull(name, "Pet name is null in Excel");
	    Assert.assertNotNull(status, "Pet status is null in Excel");
	    Assert.assertNotNull(categoryId, "Category ID is null in Excel");
	    Assert.assertNotNull(categoryName, "Category Name is null in Excel");
	    Assert.assertNotNull(photoUrls, "Photo URLs are null in Excel");
	    Assert.assertNotNull(tagIdsStr, "Tag IDs are null in Excel");
	    Assert.assertNotNull(tagNamesStr, "Tag Names are null in Excel");

	    // ---------- CATEGORY ----------
	    Category_Pojo category = new Category_Pojo();
	    category.setId(Long.parseLong(categoryId));
	    category.setName(categoryName);

	    // ---------- PHOTO URLS ----------
	    List<String> photoUrlsList = Arrays.asList(photoUrls.split(","));
	    Assert.assertFalse(photoUrlsList.isEmpty(), "Photo URLs list is empty");

	    // ---------- TAGS ----------
	    String[] tagIds = tagIdsStr.split(",");
	    String[] tagNames = tagNamesStr.split(",");
	    Assert.assertEquals(tagIds.length, tagNames.length,
	            "Tag IDs and Tag Names count mismatch");

	    List<Tag_Pojo> tags = new ArrayList<>();
	    for (int i = 0; i < tagIds.length; i++) {
	        Tag_Pojo tag = new Tag_Pojo();
	        tag.setId(Long.parseLong(tagIds[i]));
	        tag.setName(tagNames[i]);
	        tags.add(tag);
	    }

	    // ---------- CREATE PET PAYLOAD ----------
	    Pet_Pojo petPayload = new Pet_Pojo();
	    petPayload.setId(Long.parseLong(petId));
	    petPayload.setName(name);
	    petPayload.setStatus(status);
	    petPayload.setCategory(category);
	    petPayload.setPhotoUrls(photoUrlsList);
	    petPayload.setTags(tags);

	    // ---------- CREATE PET API CALL ----------
	    Response createResponse = PetEndPoints.addNewPet(petPayload);
	    logger.info("Create Pet Response Body: {}", createResponse.asString());
	    
	    // ---------- VALIDATE CREATION SUCCESS ----------
	    Assert.assertEquals(createResponse.getStatusCode(), 200, "Pet creation failed");
	    
	    // ---------- CAPTURE ID DYNAMICALLY ----------
	    Long createdPetId = createResponse.jsonPath().getLong("id");
	    Assert.assertNotNull(createdPetId, "Created pet ID is null");
	    Assert.assertTrue(createdPetId > 0, "Created pet ID is invalid");
	    
	    logger.info("Pet created successfully with ID: {}", createdPetId);
	    
	    if (test != null) {
	        test.log(Status.INFO, "Step 1: Pet created with ID: " + createdPetId);
	    }
	    
	    // ---------- STEP 2: FIND PET BY CAPTURED ID ----------
	    logger.info("Step 2: Finding pet by dynamically captured ID: {}", createdPetId);
	    
	    Response getResponse = PetEndPoints.getPetById(createdPetId);
	    logger.info("Get Pet Response Body: {}", getResponse.asString());
	    
	    if (test != null) {
	        test.log(Status.INFO, "Step 2: API Call: GET /v2/pet/" + createdPetId);
	        test.log(Status.INFO, "Response Status Code: " + getResponse.getStatusCode());
	    }
	    
	    // ---------- STEP 3: VALIDATE STATUS CODE ----------
	    Assert.assertEquals(getResponse.getStatusCode(), 200, 
	        "Expected status code 200, but got: " + getResponse.getStatusCode());
	    
	    // ---------- STEP 4: VALIDATE SCHEMA (Response Structure) ----------
	    Assert.assertTrue(getResponse.getHeader("Content-Type").contains("application/json"),
	        "Content-Type is not application/json");
	    
	    // Validate required fields exist
	    Long retrievedPetId = getResponse.jsonPath().getLong("id");
	    Assert.assertNotNull(retrievedPetId, "Pet ID is null in response");
	    
	    String retrievedName = getResponse.jsonPath().getString("name");
	    Assert.assertNotNull(retrievedName, "Pet name is null in response");
	    
	    String retrievedStatus = getResponse.jsonPath().getString("status");
	    Assert.assertNotNull(retrievedStatus, "Pet status is null in response");
	    
	    // Validate category structure
	    Object retrievedCategoryId = getResponse.jsonPath().get("category.id");
	    Assert.assertNotNull(retrievedCategoryId, "Category ID is null");
	    Assert.assertTrue(retrievedCategoryId instanceof Number, "Category ID is not a number");
	    
	    String retrievedCategoryName = getResponse.jsonPath().getString("category.name");
	    Assert.assertNotNull(retrievedCategoryName, "Category name is null");
	    
	    // Validate photoUrls structure
	    List<Object> retrievedPhotoUrls = getResponse.jsonPath().getList("photoUrls");
	    Assert.assertNotNull(retrievedPhotoUrls, "Photo URLs list is null");
	    Assert.assertFalse(retrievedPhotoUrls.isEmpty(), "Photo URLs list is empty");
	    
	    // Validate tags structure
	    List<Object> retrievedTags = getResponse.jsonPath().getList("tags");
	    Assert.assertNotNull(retrievedTags, "Tags list is null");
	    
	    // ---------- STEP 5: VALIDATE DATA INTEGRITY (Compare Created vs Retrieved) ----------
	    // ID Integrity
	    Assert.assertEquals(retrievedPetId, createdPetId, 
	        "Pet ID mismatch. Created: " + createdPetId + ", Retrieved: " + retrievedPetId);
	    
	    // Name Integrity
	    Assert.assertEquals(retrievedName, name, 
	        "Pet name mismatch. Created: " + name + ", Retrieved: " + retrievedName);
	    
	    // Status Integrity
	    Assert.assertEquals(retrievedStatus, status, 
	        "Pet status mismatch. Created: " + status + ", Retrieved: " + retrievedStatus);
	    
	    // Category Integrity
	    Assert.assertEquals(getResponse.jsonPath().getLong("category.id"), Long.parseLong(categoryId),
	        "Category ID mismatch. Created: " + categoryId + ", Retrieved: " + retrievedCategoryId);
	    Assert.assertEquals(retrievedCategoryName, categoryName,
	        "Category name mismatch. Created: " + categoryName + ", Retrieved: " + retrievedCategoryName);
	    
	    // Photo URLs Integrity
	    Assert.assertEquals(retrievedPhotoUrls.size(), photoUrlsList.size(),
	        "Photo URLs count mismatch. Created: " + photoUrlsList.size() + ", Retrieved: " + retrievedPhotoUrls.size());
	    for (int i = 0; i < photoUrlsList.size(); i++) {
	        Assert.assertEquals(retrievedPhotoUrls.get(i), photoUrlsList.get(i),
	            "Photo URL mismatch at index " + i);
	    }
	    
	    // Tags Integrity
	    Assert.assertEquals(retrievedTags.size(), tags.size(),
	        "Tags count mismatch. Created: " + tags.size() + ", Retrieved: " + retrievedTags.size());
	    
	    for (int i = 0; i < tags.size(); i++) {
	        Long retrievedTagId = getResponse.jsonPath().getLong("tags[" + i + "].id");
	        String retrievedTagName = getResponse.jsonPath().getString("tags[" + i + "].name");
	        
	        Assert.assertEquals(retrievedTagId, Long.parseLong(tagIds[i]),
	            "Tag ID mismatch at index " + i + ". Created: " + tagIds[i] + ", Retrieved: " + retrievedTagId);
	        Assert.assertEquals(retrievedTagName, tagNames[i],
	            "Tag name mismatch at index " + i + ". Created: " + tagNames[i] + ", Retrieved: " + retrievedTagName);
	    }
	    
	    logger.info("======== Pet found successfully with ID: {} ========", retrievedPetId);
	    logger.info("Data integrity validated - All fields match between created and retrieved pet");
	    
	    if (test != null) {
	        test.log(Status.PASS, "Pet found successfully with ID: " + retrievedPetId);
	        test.log(Status.INFO, "Data integrity validated - All fields match");
	    }
	}
	
    // ================= DELETE PET =================
	@Test(priority=5, dataProvider = "PetData", dataProviderClass = DataProviders.class)
	public void testDeletePet(String petId, String name, String status,
	                          String categoryId, String categoryName,
	                          String photoUrls,
	                          String tagIdsStr, String tagNamesStr) {
		
		logger.info("======== Delete Pet Test Started ========");
		ExtentTest test = getExtentTest();
		if (test != null) {
		    test.log(Status.INFO, "Starting test: Delete Pet");
		}

	    // ---------- STEP 1: CREATE PET FIRST ----------
	    logger.info("Step 1: Creating pet to get dynamic ID for deletion");
	    
	    // ---------- BASIC INPUT VALIDATION (Excel safety) ----------
	    Assert.assertNotNull(petId, "Pet ID is null in Excel");
	    Assert.assertNotNull(name, "Pet name is null in Excel");
	    Assert.assertNotNull(status, "Pet status is null in Excel");
	    Assert.assertNotNull(categoryId, "Category ID is null in Excel");
	    Assert.assertNotNull(categoryName, "Category Name is null in Excel");
	    Assert.assertNotNull(photoUrls, "Photo URLs are null in Excel");
	    Assert.assertNotNull(tagIdsStr, "Tag IDs are null in Excel");
	    Assert.assertNotNull(tagNamesStr, "Tag Names are null in Excel");

	    // ---------- CATEGORY ----------
	    Category_Pojo category = new Category_Pojo();
	    category.setId(Long.parseLong(categoryId));
	    category.setName(categoryName);

	    // ---------- PHOTO URLS ----------
	    List<String> photoUrlsList = Arrays.asList(photoUrls.split(","));
	    Assert.assertFalse(photoUrlsList.isEmpty(), "Photo URLs list is empty");

	    // ---------- TAGS ----------
	    String[] tagIds = tagIdsStr.split(",");
	    String[] tagNames = tagNamesStr.split(",");
	    Assert.assertEquals(tagIds.length, tagNames.length,
	            "Tag IDs and Tag Names count mismatch");

	    List<Tag_Pojo> tags = new ArrayList<>();
	    for (int i = 0; i < tagIds.length; i++) {
	        Tag_Pojo tag = new Tag_Pojo();
	        tag.setId(Long.parseLong(tagIds[i]));
	        tag.setName(tagNames[i]);
	        tags.add(tag);
	    }

	    // ---------- CREATE PET PAYLOAD ----------
	    Pet_Pojo petPayload = new Pet_Pojo();
	    petPayload.setId(Long.parseLong(petId));
	    petPayload.setName(name);
	    petPayload.setStatus(status);
	    petPayload.setCategory(category);
	    petPayload.setPhotoUrls(photoUrlsList);
	    petPayload.setTags(tags);

	    // ---------- CREATE PET API CALL ----------
	    Response createResponse = PetEndPoints.addNewPet(petPayload);
	    logger.info("Create Pet Response Body: {}", createResponse.asString());
	    
	    // ---------- VALIDATE CREATION SUCCESS ----------
	    Assert.assertEquals(createResponse.getStatusCode(), 200, "Pet creation failed");
	    
	    // ---------- CAPTURE ID DYNAMICALLY ----------
	    Long createdPetId = createResponse.jsonPath().getLong("id");
	    Assert.assertNotNull(createdPetId, "Created pet ID is null");
	    Assert.assertTrue(createdPetId > 0, "Created pet ID is invalid");
	    
	    logger.info("Pet created successfully with ID: {}", createdPetId);
	    
	    if (test != null) {
	        test.log(Status.INFO, "Step 1: Pet created with ID: " + createdPetId);
	    }
	    
	    // ---------- STEP 2: VERIFY PET EXISTS BEFORE DELETION ----------
	    logger.info("Step 2: Verifying pet exists before deletion");
	    Response getBeforeDeleteResponse = PetEndPoints.getPetById(createdPetId);
	    Assert.assertEquals(getBeforeDeleteResponse.getStatusCode(), 200, 
	        "Pet should exist before deletion, but got status: " + getBeforeDeleteResponse.getStatusCode());
	    logger.info("Pet verified to exist with ID: {}", createdPetId);
	    
	    // ---------- STEP 3: DELETE PET BY CAPTURED ID ----------
	    logger.info("Step 3: Deleting pet with dynamically captured ID: {}", createdPetId);
	    
	    Response deleteResponse = PetEndPoints.deletePetById(createdPetId);
	    logger.info("Delete Pet Response Body: {}", deleteResponse.asString());
	    
	    if (test != null) {
	        test.log(Status.INFO, "Step 3: API Call: DELETE /v2/pet/" + createdPetId);
	        test.log(Status.INFO, "Delete Response Status Code: " + deleteResponse.getStatusCode());
	    }
	    
	    // ---------- STEP 4: VALIDATE STATUS CODE ----------
	    Assert.assertEquals(deleteResponse.getStatusCode(), 200, 
	        "Expected status code 200 for delete, but got: " + deleteResponse.getStatusCode());
	    
	    // ---------- STEP 5: VALIDATE DELETION SUCCESS (Verify pet no longer exists) ----------
	    logger.info("Step 4: Verifying pet is deleted by attempting to retrieve it");
	    
	    Response getAfterDeleteResponse = PetEndPoints.getPetById(createdPetId);
	    logger.info("Get Pet After Delete Response Body: {}", getAfterDeleteResponse.asString());
	    
	    // Pet should not exist after deletion (404)
	    Assert.assertEquals(getAfterDeleteResponse.getStatusCode(), 404, 
	        "Pet should be deleted (expected 404), but got status: " + getAfterDeleteResponse.getStatusCode());
	    
	    logger.info("======== Pet deleted successfully with ID: {} ========", createdPetId);
	    logger.info("Deletion verified - Pet no longer exists in the system");
	    
	    if (test != null) {
	        test.log(Status.PASS, "Pet deleted successfully with ID: " + createdPetId);
	        test.log(Status.INFO, "Deletion verified - Pet no longer exists (404)");
	    }
	}

}
