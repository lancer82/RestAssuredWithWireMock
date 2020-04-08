package qiucao.restful.framework;

import org.junit.jupiter.api.BeforeAll;

import static io.restassured.RestAssured.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import io.restassured.RestAssured;


/*
 * 通过RestAssured发送不同的Request
 * 指定执行顺序
 */
public class TestRunOrder {

	@BeforeAll
	public static void setupEnv() {
		RestAssured.baseURI = "https://api.github.com/";
		RestAssured.authentication = oauth2("4d77b9abd8346cc06f47713bfb4ccc44e2fe6ce6");
	}
	
	/*
	 * 通过参数化和反射方法来指定测试执行顺序
	 */
	@ParameterizedTest(name = "--{index}-- 执行{0}")
	@ValueSource(strings = {"test002_PostMethod",
			"test003_PatchMethod",
			"test004_PutMethod",
			"test005_DeleteMethod"})
	public void testAllCase(String MethodName) throws InstantiationException, IllegalAccessException, NoSuchMethodException, SecurityException, IllegalArgumentException, InvocationTargetException {
		Object testClass = TestRunOrder.class.newInstance();
		Method testCase = TestRunOrder.class.getDeclaredMethod(MethodName);
		testCase.invoke(testClass);
	}
	
	
	/*
	 * 发送Get请求
	 */
	@Test
	public void test001_GetMethod() {
		given().
			log().all().
//			auth().oauth2("62ed1c9208aa6168896b075a9cbe8a8d7bb12e28").
		when().
			get("user/repos").
		then().
			log().status();
	}
	
	/*
	 * 提交Post请求 - 创建Hello-imooc Repo
	 */
//	@Test
	public void test002_PostMethod() {
		String postBody = "{\r\n" + 
				"  \"name\": \"Hello-imooc\",\r\n" + 
				"  \"description\": \"This is your first repository\",\r\n" + 
				"  \"homepage\": \"https://github.com\",\r\n" + 
				"  \"private\": false,\r\n" + 
				"  \"has_issues\": true,\r\n" + 
				"  \"has_projects\": true,\r\n" + 
				"  \"has_wiki\": true\r\n" + 
				"}";
		
		given().
			log().all().
			body(postBody).
		when().
			post("user/repos").
		then().
//			log().all().
			statusCode(201);
		
	}
	
	/*
	 * 提交Patch修改请求 - 修改Repo
	 * 通过pathParam来添加路径参数
	 */
//	@Test
	public void test003_PatchMethod() {
		String Editbody = "{\r\n" + 
				"  \"name\": \"Hello-imooc\",\r\n" + 
				"  \"description\": \"This is RestAssured Test demo\",\r\n" + 
				"  \"homepage\": \"https://github.com\",\r\n" + 
				"  \"private\": false,\r\n" + 
				"  \"has_issues\": false,\r\n" + 
				"  \"has_projects\": false,\r\n" + 
				"  \"has_wiki\": false\r\n" + 
				"}";
		
		given().
			log().all().
			pathParam("owner","chengxiaqiucao").
			pathParam("repo","Hello-imooc").
			body(Editbody).
		when().
			patch("/repos/{owner}/{repo}").
		then().
			statusCode(200);
		
	}
	
	/*
	 * 提交Put请求 - 修改topic
	 */
//	@Test
	public void test004_PutMethod() {
		String putbody = "{\r\n" + 
				"  \"names\": [\r\n" + 
				"    \"restassured\",\r\n" + 
				"    \"qiucao\",\r\n" + 
				"    \"imooc\"\r\n" + 
				"  ]\r\n" + 
				"}";
		
		given().
			log().all().
			pathParam("owner","chengxiaqiucao").
			pathParam("repo","Hello-imooc").
			header("Accept","application/vnd.github.mercy-preview+json").
			body(putbody).
		when().
			put("repos/{owner}/{repo}/topics").
		then().
			log().status().
			statusCode(200);
	}
	
	/*
	 * 提交Delete方法- 删除Repo
	 */
//	@Test
	public void test005_DeleteMethod() {
		given().
			log().all().
			pathParam("owner","chengxiaqiucao").
			pathParam("repo","Hello-imooc").
		when().
			delete("repos/{owner}/{repo}").
		then().
			log().status().
			statusCode(204);
	}
	
}
