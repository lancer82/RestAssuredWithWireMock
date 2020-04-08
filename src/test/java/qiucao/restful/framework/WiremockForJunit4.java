package qiucao.restful.framework;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;

import io.restassured.RestAssured;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.restassured.RestAssured.*;
/*
 * 在Junit4中使用Wiremock来实现服务端响应模拟功能
 */
public class WiremockForJunit4 {
	
	WireMockServer wireMockServer;
	
	/*
	 * 参数：port,https-port
	 * options().port(8088).httpsPort(8089)
	 */
	@ClassRule
	public static WireMockRule wiremockrule = new WireMockRule(8088,8089);

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RestAssured.baseURI = "http://localhost:8088";
	
	}

	@Test
	public void test006() {
		wiremockrule.stubFor(get(urlEqualTo("/rest/mock"))
				.willReturn(aResponse()
						.withBody("从Junit4中返回的信息")
						.withStatus(200)));
		given()
		.when()
			.get("/rest/mock")
		.then()
			.log().all();
	}
	
	/*
	 * WireMock 的消息录制功能
	 */
	
	@Test
	public void test007() {
		RestAssured.baseURI =  "https://localhost:8089";
		RestAssured.useRelaxedHTTPSValidation();
		
		wiremockrule.startRecording("https://api.github.com");
		
		given()
		.when()
			.get("/users/rest-assured")
		.then()
			.statusCode(200)
			.log().all();
		
		wiremockrule.stopRecording();
		
		given()
		.when()
			.get("/users/rest-assured")
		.then()
			.statusCode(200)
			.log().all();
	}
	
	@Test
	@DisplayName("从文件中获取响应内容")
	public void test008() {
		wiremockrule.stubFor(get(urlEqualTo("/Rest/file"))
				.willReturn(aResponse()
						.withStatus(200)
						.withBodyFile("filebody.json")));
		given()
		.when()
			.get("/Rest/file")
		.then()
			.log().all();
	}
	

}
