package com.kaikeba.server.api.utils;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import net.sf.json.JSONObject;

import static com.jayway.restassured.RestAssured.given;




/**
 * treesea
 */
public class TestConfig {

	public static String path;
	public static String contentType;

	public TestConfig(String path, String contentType) {
		TestConfig.path = path;
		TestConfig.contentType = contentType;

	}

	public static RequestSpecification requestSpecification() {
		if (!"".equals(contentType))
			return given().baseUri(path).and()
					.header("Content-Type", contentType).port(80);

		else {
			return given().baseUri(path).port(80);
		}

	}

	public Response get(String url) {
		RequestSpecification requestSpecification = requestSpecification()
				.contentType(ContentType.JSON);
		Response response = requestSpecification.when().get(url);
		return response;
	}

	public Response delete(String url) {

		RequestSpecification requestSpecification = requestSpecification()
				.contentType(ContentType.JSON);
		Response response = requestSpecification.when().delete(url);
		return response;

	}

	public Response post(String url, JSONObject jsonObject) {
        RequestSpecification requestSpecification= TestConfig.requestSpecification().body(jsonObject);
	
		Response response = requestSpecification.when().post(url);
		return response;
	}

	public Response put(String url, JSONObject jsonObject) {

		RequestSpecification requestSpecification = TestConfig
				.requestSpecification().body(jsonObject);
		Response response = requestSpecification.when().put(url);
		return response;

	}

}
