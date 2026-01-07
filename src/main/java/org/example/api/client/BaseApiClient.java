package org.example.api.client;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.filter.Filter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static org.example.config.ConfigProps.APP_PROPERTY;

public abstract class BaseApiClient {

  protected final RequestSpecification spec;

  protected BaseApiClient() {
    this.spec = defaultSpec();
    RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
  }

  protected RequestSpecification defaultSpec() {
    Filter allureFilter = new AllureRestAssured();

    return new RequestSpecBuilder()
            .setBaseUri(APP_PROPERTY.baseApiUrl())
            .setContentType(ContentType.JSON)
            .setAccept(ContentType.JSON)
            .addFilter(allureFilter)
            .setRelaxedHTTPSValidation()
            .addFilter(new RequestLoggingFilter())
            .addFilter(new ResponseLoggingFilter())
            .build();
  }

  protected <T> T get(String path, Class<T> clazz) {
    return given().spec(spec).when().get(path).then().extract().response().as(clazz);
  }

  protected <T> T get(String path, TypeRef<T> typeRef) {
    return given().spec(spec).when().get(path).then().extract().as(typeRef);
  }

  protected <T> T post(String path, Object body, Class<T> clazz) {
    return postRaw(path, body).as(clazz);
  }

  protected Response postRaw(String path, Object body) {
    return given().spec(spec).body(body).when().post(path).then().extract().response();
  }

  protected <T> T put(String path, Object body, Class<T> clazz) {
    return given().spec(spec).body(body).when().put(path).then().extract().response().as(clazz);
  }

  protected Response delete(String path) {
    return given().spec(spec).when().delete(path).then().extract().response();
  }

}
