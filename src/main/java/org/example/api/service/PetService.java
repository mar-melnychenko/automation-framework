package org.example.api.service;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.example.api.client.BaseApiClient;
import org.example.api.models.Pet;

import java.util.List;

public class PetService extends BaseApiClient {
    private final String basePath = "/pet";

    public Pet createPet(Pet petData) {
        return post(basePath, petData, Pet.class);
    }

    public Response createPetRaw(Pet petData) {
        return postRaw(basePath, petData);
    }

    public Pet updatePet(String id, Pet petData) {
        return put(basePath + "/" + id, petData, Pet.class);
    }

    public Pet getPetDetails(String id) {
        return get(basePath + "/" + id, Pet.class);
    }

    public List<Pet> getPetsByStatus(String status) {
        return get(basePath + "/findByStatus?status=" + status, new TypeRef<>() {});
    }

    public Response deleteSearch(String id) {
        return delete(basePath + "/" + id);
    }
}
