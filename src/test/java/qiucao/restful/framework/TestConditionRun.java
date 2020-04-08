package qiucao.restful.framework;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.joda.time.DateTime;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;

/*
 * Github查询Repo实例，介绍条件执行和关联执行
 */
class TestConditionRun {
	static RequestSpecification reqSpec;
	static String repoID;

	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		RestAssured.baseURI = "https://api.github.com/";
		RestAssured.basePath = "search/";
		RestAssured.authentication = oauth2("4d77b9abd8346cc06f47713bfb4ccc44e2fe6ce6");
	
		RequestSpecBuilder reqBuilder = new RequestSpecBuilder();
		reqBuilder.addParam("sort", "stars");
		reqBuilder.addParam("per_page", "25");
		reqBuilder.addHeader("Accept", "application/vnd.github.mercy-preview+json");
		reqSpec = reqBuilder.build();
	}

	
	/*
	 * 利用Nested分组实现用例的关联执行
	 */
	@Nested
	class groupRun{
	
		/*
		 * 获取最近一周包含有"自动化测试"字样的Repo信息
		 * 关注日期的处理方法
		 */
		@BeforeEach
		void search_repo() {
			String sdate,q;
			
//			通过Calendar对象计算日期
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date());
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			calendar.add(calendar.DAY_OF_MONTH, -7);
			sdate = sdf.format(calendar.getTime());
			
//			通过Joda-time库完成日期计算
			DateTime dt = new DateTime();
			sdate = dt.plusDays(-7).toString("yyyy-MM-dd");
			
			System.out.println("query date:" + sdate);
			
			q = "自动化测试+created:>"+sdate;
			
			repoID = given()
				.log().all()
				.spec(reqSpec)
				.param("q", q)
			.when()
				.get("repositories")
			.then()
				.log().body()
				.body("total_count",greaterThan(4))
				.extract()
				.path("items[4].id").toString();
		}
		
		/*
		 * 条件执行
		 * 根据repoID来获取Repo信息
		 */
		@Test
		public void demo_Get_5th_repo() {
			assumeTrue(repoID != null);
			
			RestAssured.basePath = "repositories/";
			given()
			.when()
				.get(repoID)
			.then()
				.statusCode(200)
				.log().body();
		}
	}
}
