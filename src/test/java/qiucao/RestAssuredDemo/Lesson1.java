package qiucao.RestAssuredDemo;

import static io.restassured.RestAssured.*;

import org.junit.jupiter.api.Test;


public class Lesson1 {

	/*
	 * 完成api.github.com/users/chengxiaqiucao信息查询
	 */
	@Test
	public void FirstDemo() {
		given().
			pathParam("username","chengxiaqiucao").
		when().
			get("https://api.github.com/users/{username}").
		then().
			log().body();
	}
	
	/*
	 * 完成查询当前用户的Repo信息
	 * token : 62ed1c9208aa6168896b075a9cbe8a8d7bb12e28
	 */
	@Test
	public void authDemo() {
		given().
			log().all().
			//显式地将鉴权token信息在header中携带
//			auth().preemptive().oauth2("62ed1c9208aa6168896b075a9cbe8a8d7bb12e28").
			//直接指定header参数
//			header("Authorization","token 62ed1c9208aa6168896b075a9cbe8a8d7bb12e28").
			//使用basic鉴权
			auth().preemptive().basic("chengxiaqiuao","thisisatest").
		when().
			get("https://api.github.com/user/repos").
		then().
			log().all();
	}
}
