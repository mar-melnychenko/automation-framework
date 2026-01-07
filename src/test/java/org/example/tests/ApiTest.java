package org.example.tests;

import org.example.api.models.Pet;
import org.example.api.service.PetService;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;

public class ApiTest {
    private PetService petService;

    @BeforeClass
    public void setUp() {
        petService = new PetService();
    }
    @Test
    public void getResults_shouldReturnList() {
        var results = petService.getPetsByStatus("available");
        Assert.assertNotNull(results);
    }

    @Test(dataProvider = "createPetData")
    public void createPet(Pet petData, int expectedStatus) {
        var results = petService.createPetRaw(petData);
        Assert.assertEquals(results.statusCode(), expectedStatus);
    }

    @DataProvider
    public Object[][] createPetData() {
        return new Object[][]{
                { Pet.builder().name("Test name").photoUrls(List.of("123")).build(), 200 },
                { Pet.builder().name("Test name").build(), 405 },
                { Pet.builder().photoUrls(List.of("123")).build(), 405 }
        };
    }
}
