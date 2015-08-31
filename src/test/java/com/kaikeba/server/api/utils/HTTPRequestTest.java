package com.kaikeba.server.api.utils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;

import com.gxb.server.api.beans.InputData;
import com.jayway.restassured.response.Response;

import net.sf.json.JSONObject;

public class HTTPRequestTest {
	private ExcelUtils excel;
	private String path = "";
	private String sheetName = "";
	protected static final Logger logger = LoggerFactory
			.getLogger(HTTPRequestTest.class);

	@BeforeTest
	@Parameters({ "path", "sheetName"})
	public void initDataPath(String path, String sheetName){
		this.path  = System.getProperty("user.dir")+"/src/test/resources/testData/"+path;
		this.sheetName = sheetName;
	}
	@DataProvider(name = "iterator")
	public Iterator<Object[]> initData() {
		excel = new ExcelUtils();
		Map<Integer, String[]> map = excel.readExcelData(path, sheetName);
		return excel.formatInputData(map);
	}

	@Test(dataProvider = "iterator")
	public void run(String lineNum, InputData input)  {
		TestConfig reqSpec = new TestConfig(input.getHost(),
				input.getContentType());
		excel = new ExcelUtils();
		Response response = null;
		JSONObject jsonObject = null;

		JSONCompareResult result = null;
		switch (input.getCallType()) {
			case "GET": {
				response = reqSpec.get(input.getCallSuff());
				break;
			}
			case "POST": {
				String[] body = input.getBody().split(",");
				String resul = "";
				for(int i =0;i<body.length;i++){
					if(body[i].contains("null")){
						int a = body[i].indexOf("\"");
						int b = body[i].indexOf(",");
						
						resul = " "+body[i].substring(0, a) + body[i].substring(b+1, body[i].length());
						body[i] = resul;
					}
				System.out.println(body.toString());	
				}
				jsonObject = JSONObject.fromObject(input.getBody().replaceAll("null", " ").toString());

				response = reqSpec.post(input.getCallSuff(), jsonObject);
				break;
			}
			case "PUT": {
				//jsonObject = new JSONObject();
				jsonObject = JSONObject.fromObject(input.getBody());
				response = reqSpec.put(input.getCallSuff(), jsonObject);
				break;
			}
			case "DELETE": {
				response = reqSpec.delete(input.getCallSuff());
				break;
			}
	
			default: {
				logger.error("第 [" + input.getID() + "]行数据, 未知请求类型: ["
						+ input.getCallType() + "],只能是GET POST PUT DELETE 等等...");
			}
		
		}
		
		//返回结果写入 实际返回值,11对应实际返回reponse
		excel.writeResultAtExcel(path, sheetName, Integer.parseInt(lineNum), 11,
				response.asString());
	
		try {
			result = JSONCompare.compareJSON(input.getExpectResponse(),
					response.asString(), JSONCompareMode.NON_EXTENSIBLE);
			
		} catch (Exception e) {
			logger.error("第 [" + input.getID() + "]行数据 ,结果比对失败！");
			excel.writeResultAtExcel(path, sheetName, Integer.parseInt(lineNum), 13,
					e.getMessage());
			e.printStackTrace();
		}

		if (!result.passed()) {

			excel.writeResultAtExcel(path, sheetName, Integer.parseInt(lineNum), 15,
					result == null ? "Error" : result);

			excel.writeResultAtExcel(path, sheetName, Integer.parseInt(lineNum), 14,
					"failed");

			Assert.fail("第"+lineNum+"行数据，接口测试失败！");
		} else {
			excel.writeResultAtExcel(path, sheetName, Integer.parseInt(lineNum), 14,
					"passed");
		}

	}

	public void responseToResult(String lineNum, Response response) {
		logger.info("返回response:");
		logger.info("{}", response.asString());
		excel = new ExcelUtils();
		excel.writeResultAtExcel(path, sheetName, Integer.parseInt(lineNum), 14, "Y谢谢谢谢");

	}

}