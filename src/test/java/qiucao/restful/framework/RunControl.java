package qiucao.restful.framework;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;

import static io.restassured.RestAssured.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;

import io.restassured.RestAssured;


/*
 * 用例执行选择的方法
 */
class RunControl {

	@BeforeAll
	public static void setupEnv() {
		RestAssured.baseURI = "https://api.github.com/";
		RestAssured.authentication = oauth2("4d77b9abd8346cc06f47713bfb4ccc44e2fe6ce6");
	}
	
	/*
	 * 发送Get请求
	 * 匹配系统属性os.name=Windows 10则忽略执行
	 */
	@Test
//	@DisabledIfSystemProperty(matches = "Windows 10", named = "os.name")
	@Tag("demo")
	@Tag("rest")
	void test001_GetMethod() {
		System.out.println(System.getProperty("os.name"));
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
	 * 匹配系统环境变量RunEnv=QiucaoDemo，则当前用例执行
	 */
	@Test
	@EnabledIfEnvironmentVariable(matches = "QiucaoDemo", named = "RunEnv")
	@Tag("demo")
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
	@Test
//	@Disabled
	@Tag("rest")
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
	@Test
//	@Disabled
	@Tag("demo")
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
	 * 上午9点前当前用例禁止执行
	 */
	@Test
//	@DisabledIf(value = { "var time = new Date()",
//			"time.getHours() > 9"})
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
