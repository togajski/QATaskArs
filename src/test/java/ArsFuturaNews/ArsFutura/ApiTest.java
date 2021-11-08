package ArsFuturaNews.ArsFutura;

import static io.restassured.RestAssured.given;

import java.util.HashMap;
import java.util.UUID;

import org.testng.Assert;
import org.testng.annotations.Test;

import io.restassured.response.Response;
import io.restassured.matcher.ResponseAwareMatcher;
import io.restassured.path.json.JsonPath;
import static org.hamcrest.Matchers.equalTo;

public class ApiTest {

	private String logInAndGetToken() {
		@SuppressWarnings("rawtypes")
		HashMap data = new HashMap();
		data.put("email", "boris@arsfutura.co");
		data.put("password", "password");

		Response res = given().contentType("application/json").body(data).when()
				.post("https://newsapi.arsfutura.co/login");

		String jsonString = res.asString();
		String bearerToken = JsonPath.from(jsonString).get("token");

		return bearerToken;

	}

	@SuppressWarnings("unchecked")
	@Test
	public void postLogIn() {
		@SuppressWarnings("rawtypes")
		HashMap data = new HashMap();
		data.put("email", "boris@arsfutura.co");
		data.put("password", "password");

		Response res = given().contentType("application/json").body(data).when()
				.post("https://newsapi.arsfutura.co/login");

		res.then().statusCode(200);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void postLogInWrong() {
		HashMap data = new HashMap();
		data.put("email", "ivan.ostrica@arsfutura.co");
		data.put("password", "wrong password");

		Response res = given().contentType("application/json").body(data).when()
				.post("https://newsapi.arsfutura.co/login").then().statusCode(401).log().body().extract().response();

		String jsonString = res.asString();
		Assert.assertEquals(jsonString.contains("Invalid username or password."), true);

	}

	@Test
	void logOut() {
		String bearerToken = logInAndGetToken();
		given().contentType("application/json").header("Authorization", "Bearer " + bearerToken).when()
				.post("https://newsapi.arsfutura.co/logout").then().statusCode(204).log();
	}

	@Test
	void getHeadlines() {
		String bearerToken = logInAndGetToken();
		given().contentType("application/json").header("Authorization", "Bearer " + bearerToken).when()
				.get("https://newsapi.arsfutura.co/news/top_headlines").then().statusCode(200);

	}

	@Test
	void searchNews() {
		String bearerToken = logInAndGetToken();
		given().contentType("application/json").header("Authorization", "Bearer " + bearerToken).when()
				.get("https://newsapi.arsfutura.co/news/everything?q=bitcoin").then().statusCode(200);

	}

	@Test
	void getUser() {
		String bearerToken = logInAndGetToken();
		Response res = given().contentType("application/json").header("Authorization", "Bearer " + bearerToken).when()
				.get("https://newsapi.arsfutura.co/users/1");
		
		res.then().assertThat().statusCode(200).body("id", equalTo(1)).body("email", equalTo("boris@arsfutura.co"));

	}

	@Test
	void getCurrentUser() {
		String bearerToken = logInAndGetToken();
		Response res = given().contentType("application/json").header("Authorization", "Bearer " + bearerToken).when()
				.get("https://newsapi.arsfutura.co/users/me");

		res.then().assertThat().statusCode(200).body("id", equalTo(1)).body("email", equalTo("boris@arsfutura.co"));
	}

	@SuppressWarnings("unchecked")
	@Test
	void registerUser() {
		String name = UUID.randomUUID().toString();

		@SuppressWarnings("rawtypes")
		HashMap userData = new HashMap();
		userData.put("email", name + "@arsfutura.co");
		userData.put("password", "password");

		HashMap data = new HashMap();
		data.put("user", userData);

		Response res = given().contentType("application/json").body(data).when()
				.post("https://newsapi.arsfutura.co/users");

		res.then().assertThat().statusCode(201).body("email", equalTo(name + "@arsfutura.co"));

	}

}