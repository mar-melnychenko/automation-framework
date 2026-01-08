package org.example.api.service;

import io.restassured.common.mapper.TypeRef;
import io.restassured.response.Response;
import org.example.api.client.BaseApiClient;
import org.example.api.models.Pet;

import java.util.List;

public class PetService extends BaseApiClient {
    private final String basePath = "/pet";

    public Response createPetRaw(Pet petData) {
        return postRaw(basePath, petData);
    }

    public Response updatePetRaw(Pet petData) {
        return putRaw(basePath, petData);
    }

    public Response getPetDetailsRaw(String id) {
        return getRaw(basePath + "/" + id);
    }

    public List<Pet> getPetsByStatus(String status) {
        return get(basePath + "/findByStatus?status=" + status, new TypeRef<>() {});
    }

    public Response deletePet(String id) {
        return delete(basePath + "/" + id);
    }
}
