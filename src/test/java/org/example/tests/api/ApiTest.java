package org.example.tests.api;

import io.restassured.response.Response;
import org.example.api.models.Pet;
import org.example.api.service.PetService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class ApiTest {
    private PetService petService;

    @BeforeClass
    public void setUp() {
        petService = new PetService();
    }

    private long newPetId() {
        return System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(1_000, 9_999);
    }

    private Pet validPet(long id) {
        return Pet.builder()
                .id(id)
                .name("Test Pet " + id)
                .photoUrls(List.of("https://test.com/" + id + ".jpg"))
                .status("available")
                .build();
    }

    @Test
    public void createUpdateGetDeletePet() {
        long id = newPetId();
        Pet validPetData = validPet(id);

        Response createResp = petService.createPetRaw(validPetData);
        Assert.assertEquals(createResp.statusCode(), 200, "Create response code should be 200");

        Response getResp = petService.getPetDetailsRaw(String.valueOf(id));
        Assert.assertEquals(getResp.statusCode(), 200, "Get response code should be 200");

        Pet petFetched = getResp.as(Pet.class);
        Assert.assertEquals(petFetched.getId(), id);
        Assert.assertEquals(petFetched.getName(), validPetData.getName());

        Pet updatedBody = validPet(id).toBuilder()
                .name("Updated Pet " + id)
                .status("sold")
                .build();

        Response updateResp = petService.updatePetRaw(updatedBody);
        Assert.assertEquals(updateResp.statusCode(), 200, "Update response code should be 200");

        Pet updated = petService.getPetDetailsRaw(String.valueOf(id)).as(Pet.class);
        Assert.assertEquals(updated.getName(), updatedBody.getName());
        Assert.assertEquals(updated.getStatus(), updatedBody.getStatus());

        Response delResp = petService.deletePet(String.valueOf(id));
        Assert.assertEquals(delResp.statusCode(), 200, "Delete response code should be 200");

        Response getAfterDelete = petService.getPetDetailsRaw(String.valueOf(id));
        Assert.assertEquals(getAfterDelete.statusCode(), 404, "Deleted pet should not be present, response code should be 404");
    }

    @Test
    public void getResults_shouldReturnList() {
        var results = petService.getPetsByStatus("available");
        Assert.assertNotNull(results);
    }

    @Test(dataProvider = "invalidCreatePetData")
    public void createPetNegative(Pet petData, int expectedStatus) {
        var results = petService.createPetRaw(petData);
        Assert.assertEquals(results.statusCode(), expectedStatus);
    }

    @DataProvider
    public Object[][] invalidCreatePetData() {
        return new Object[][]{
                { Pet.builder().name("No photo urls").build(), 405 },
                { Pet.builder().photoUrls(List.of("https://test.com/1.jpg")).build(), 405 },
                { Pet.builder().build(), 405 }
        };
    }

    @Test
    public void getNotExistingPet() {
        String nonExistingId = String.valueOf(0);
        Response resp = petService.getPetDetailsRaw(nonExistingId);
        Assert.assertEquals(resp.statusCode(), 404);
    }

    @Test
    public void getPetDetailsByInvalidId() {
        Response resp = petService.getPetDetailsRaw("abc");
        Assert.assertEquals(resp.statusCode(), 404);
    }

    @Test
    public void updateNonExistingPet() {
        long id = newPetId();
        Pet body = validPet(id).toBuilder().name("Update non-existing").build();
        Response resp = petService.updatePetRaw(body);

        int sc = resp.statusCode();
        Assert.assertTrue(sc == 404 || sc == 400 || sc == 405,
                "Expected 404/400/405, got " + sc);
    }

    @Test
    public void updatePetWithInvalidBody() {
        Pet body = Pet.builder().id(newPetId()).build();
        Response resp = petService.updatePetRaw(body);
        Assert.assertEquals(resp.statusCode(), 405);
    }

    @Test
    public void deletePetByInvalidId() {
        Response resp = petService.deletePet("abc");
        Assert.assertEquals(resp.statusCode(), 404);
    }

    @Test
    public void deleteNotExistingPet() {
        String nonExistingId = String.valueOf(newPetId());
        Response resp = petService.deletePet(nonExistingId);
        Assert.assertEquals(resp.statusCode(), 404);
    }
}
