package qiucao.RestAssuredDemo;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.config.HeaderConfig;
import io.restassured.config.LogConfig;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

/*
 * RestAssured辅助功能
 * 配置功能、模板功能、过滤功能
 */
class Lesson4 {
	
	static RequestSpecification reqSpec;
	static ResponseSpecification resSpec;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		RestAssured.baseURI = "https://api.github.com/";
		RestAssured.authentication = oauth2("4d77b9abd8346cc06f47713bfb4ccc44e2fe6ce6");
		RestAssured.port = 443;
		RestAssured.basePath = "repos/";
		
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.addPathParam("owner", "chengxiaqiucao");
		reqBuilder.addPathParam("repo", "RestApiTestingDemo");
		reqSpec = reqBuilder.build();
		
		ResponseSpecBuilder resBuilder = new ResponseSpecBuilder();
		resBuilder.expectStatusCode(200);
		resBuilder.expectBody("owner.login",equalTo("chengxiaqiucao"));
		resSpec = resBuilder.build();
	}

	/*
	 * Header配置实例
	 */
	@Test
	void header_config() {
		RestAssured.config = RestAssured.config().headerConfig(HeaderConfig.headerConfig().overwriteHeadersWithName("test"));
		
		given()
			.log().all()
			.pathParam("owner", "chengxiaqiucao")
			.pathParam("repo", "RestApiTestingDemo")
			.header("test","aaa")
			.header("test","bbb")
		.when()
			.get("{owner}/{repo}")
		.then()
			.statusCode(200);
					
	}
	
	/*
	 * Log配置实例
	 */
	@Test
	public void log_config() {
		RestAssured.config = RestAssured.config()
							.logConfig(LogConfig.logConfig().enablePrettyPrinting(true));
		
		given()
			.log().all()
			.pathParam("owner", "chengxiaqiucao")
			.pathParam("repo", "RestApiTestingDemo")
		.when()
			.get("{owner}/{repo}")
		.then()
			.log().body()
			.statusCode(200);
	}
	
	/*
	 * 使用模板功能来简化测试用例编写
	 */
	@Test
	public void test_specification() {
		given()
			.log().all()
			.spec(reqSpec)
		.when()
			.get("{owner}/{repo}")
		.then()
			.log().all()
			.spec(resSpec);
	}
	
	/*
	 * 利用Filter过滤器实现request请求的修改
	 */
	@Test
	public void Filter_Request() {
		given()
			.filter(new Filter() {

				@Override
				public Response filter(FilterableRequestSpecification requestSpec,
						FilterableResponseSpecification responseSpec, FilterContext ctx) {
					requestSpec.pathParam("owner", "rest-assured");
					requestSpec.pathParam("repo", "rest-assured");
					return ctx.next(requestSpec, responseSpec);
				}				
			})
			.spec(reqSpec)
		.when()
			.get("{owner}/{repo}")
		.then()
			.log().all()
			.spec(resSpec);
	}
	
	/*
	 * 修改响应内容实例
	 */
	@Test
	public void change_response() {
		Response response = given()
				.spec(reqSpec)
				.when()
				.get("{owner}/{repo}");
		
		Response newResponse = new ResponseBuilder().clone(response)
				.setBody("这是一个修改response的测试demo").build();
		
		newResponse.then()
			.log().all()
			.spec(resSpec);
		
	}
	
}
