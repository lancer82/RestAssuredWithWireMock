package qiucao.restful.framework;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Stream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import io.restassured.RestAssured;

/*
 * Junit5中对接口测试参数化的支持
 */
class TestParameter {
	@BeforeAll
	static void setUpBeforeClass() throws Exception {
		RestAssured.baseURI = "https://api.github.com/";
		RestAssured.authentication = oauth2("4d77b9abd8346cc06f47713bfb4ccc44e2fe6ce6");

	}

	/*
	 * 通过参数化实现获取不同Github用户的用户信息
	 * 指定ValueSource参数源
	 */
	@DisplayName("获取用户信息")
	@ParameterizedTest(name = "第{index}次执行，获取{0}用户数据")
	@ValueSource(strings= {"chengxiaqiucao","rest-assured","alibaba"})
	void test_valueSource(String username) {
		given()
			.pathParam("user", username)
		.when()
			.get("/users/{user}")
		.then()
			.log().body()
			.body("login", equalTo(username));
	}
	
	/*
	 * 指定枚举类型参数源
	 */
	enum checkuser {
		login,
		id,
		url
	}
	
	/*
	 * 利用EnumSource检查用户消息返回是否包含特定字段
	 */
	@DisplayName("检查用户信息字段")
	@ParameterizedTest(name="--{index}--包含{0}字段")
	@EnumSource(checkuser.class)
	public void test_enum(checkuser argu) {
		given()
			.pathParam("user", "alibaba")
		.when()
			.get("users/{user}")
		.then()
			.body("$", hasKey(argu.toString()));
	}
	
	/*
	 * 通过Csv参数源传入多个参数
	 * 获取repo信息并判断接口返回状态
	 */
	@DisplayName("获取Repo并判断状态")
	@ParameterizedTest(name="--{index}--获取{0}/{1}的状态{2}")
	@CsvSource({
		"chengxiaqiucao,RestApiTestingDemo,200",
		"error,wrong,404"
	})
	public void test_csv(String user, String repo, int status) {
		given()
			.log().uri()
			.pathParam("user", user)
			.pathParam("repo", repo)
		.when()
			.get("repos/{user}/{repo}")
		.then()
			.statusCode(status);
	}
	
	/*
	 * 通过CSVFile参数源来使用外部csv数据
	 */
	@DisplayName("CSV格式外部数据获取repo信息")
	@ParameterizedTest(name="--{index}--获取Repo：{0}/{1}")
	@CsvFileSource(resources = "/repo.csv", numLinesToSkip = 1)
	public void test_csvFile(String user, String repo, int status) {
		given()
			.log().uri()
			.pathParam("user", user)
			.pathParam("repo", repo)
		.when()
			.get("repos/{user}/{repo}")
		.then()
			.statusCode(status);
	}
	
	/*
	 * 使用MethodSource来定义excel外部数据源获取Repo信息
	 */
	@DisplayName("获取Excel外部数据源的repo信息")
	@ParameterizedTest(name="--{index}-- 获取repo:{0}/{1}")
	@MethodSource("getRepoFromExcel")
	public void test_ExcelDataFile(String user, String repo, int status) {
		given()
			.log().uri()
			.pathParam("user", user)
			.pathParam("repo", repo)
		.when()
			.get("repos/{user}/{repo}")
		.then()
			.statusCode(status);
	}
	
	/*
	 * 从Excel数据中获取Repo的参数方法
	 */
	static Stream<Arguments> getRepoFromExcel(){
		return getExcelDataFromFile(".\\src\\test\\resources\\RepoData.xlsx","Sheet1");		
	}
	
	/*
	 * 处理excel数据，返回参数流格式	
	 * 使用POI进行excel数据解析
	 */
	public static Stream<Arguments> getExcelDataFromFile(String ExcelFilePath, String SheetName){
		Stream<Arguments> returnStream = Stream.empty();
		
		try(Workbook workbook = WorkbookFactory.create(new File(ExcelFilePath))) {
			Sheet dataSheet = workbook.getSheet(SheetName);
			//excel数据格式处理对象
			DataFormatter dfm = new DataFormatter();
			 
			for(Row row: dataSheet) {
				if (row.getRowNum() == 0) {
					continue;
				}
				
				ArrayList<Object> rowList = new ArrayList<>();
				
				for(Cell cell: row) {
					rowList.add(dfm.formatCellValue(cell));
				}
				//将每行获取到的数据List转换为Arguments对象
				Arguments arg = Arguments.of(rowList.toArray());
				//组装为Stream数据流
				returnStream = Stream.concat(returnStream,Stream.of(arg));
			}
			return returnStream;
		} catch (EncryptedDocumentException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		
		return returnStream;
		
	}
}	
	