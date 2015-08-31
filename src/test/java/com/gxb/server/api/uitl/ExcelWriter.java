package com.gxb.server.api.uitl;

import static org.hamcrest.Matchers.equalTo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.hamcrest.Matchers;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

/**
 * 操作Excel表格的功能类
 */
public class ExcelWriter {
	private FileInputStream is;
    private POIFSFileSystem fs;
    private HSSFWorkbook wb;
    private HSSFSheet sheet;
    private HSSFRow row;
    

    /**
     * 读取Excel表格表头的内容
     * @param InputStream
     * @return String 表头内容的数组
     * @throws IOException 
     */
    public String[] readExcelTitle(String path ,String sheetName) throws IOException {
        try {
        	FileInputStream   is = new FileInputStream(path);
            fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = wb.getSheet(sheetName);
        row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        System.out.println("colNum:" + colNum);
        String[] title = new String[colNum];
        for (int i = 0; i < colNum; i++) {
            //title[i] = getStringCellValue(row.getCell((short) i));
            title[i] = getCellFormatValue(row.getCell((short) i));
        }
        is.close();
        return title  ;
    }

    /**
     * 读取Excel数据内容
     * @param InputStream
     * @return Map 包含单元格数据内容的Map对象
     * @throws IOException 
     */
    public Map<Integer, String[]> readExcelContent(String path ,String sheetName) throws IOException {
    	
    	//String[] title = readExcelTitle( path , sheetName) ;
        Map<Integer,  String []> content = new HashMap<Integer,  String[]>();
        Map<String, String> line ;
        FileInputStream  is = new FileInputStream(path);

        String []str =null ;
        try {
			fs = new POIFSFileSystem(is);
            wb = new HSSFWorkbook(fs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        sheet = wb.getSheet(sheetName);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum; i++) {
        	line = new HashMap<String, String>();
            row = sheet.getRow(i);
            int j = 0;
            str = new String[colNum];
            while (j < colNum) {
                // 每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据
                // 也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean
                // str += getStringCellValue(row.getCell((short) j)).trim() +
                // "-";
                //str += getCellFormatValue(row.getCell((short) j)).trim() + "    ";
                str[j] = getCellFormatValue(row.getCell(j));

                //line.put(title[j],str);
                j++;
            }
            //System.out.println(line);

            content.put(i, str);
        }
        is.close();
        return content;
    }

    /**
     * 获取单元格数据内容为字符串类型的数据
     * 
     * @param cell Excel单元格
     * @return String 单元格数据内容
     */
    private String getStringCellValue(HSSFCell cell) {
        String strCell = "";
        switch (cell.getCellType()) {
        case HSSFCell.CELL_TYPE_STRING:
            strCell = cell.getStringCellValue();
            break;
        case HSSFCell.CELL_TYPE_NUMERIC:
            strCell = String.valueOf(cell.getNumericCellValue());
            break;
        case HSSFCell.CELL_TYPE_BOOLEAN:
            strCell = String.valueOf(cell.getBooleanCellValue());
            break;
        case HSSFCell.CELL_TYPE_BLANK:
            strCell = "";
            break;
        default:
            strCell = "";
            break;
        }
        if (strCell.equals("") || strCell == null) {
            return "";
        }
        if (cell == null) {
            return "";
        }
        return strCell;
    }

    /**
     * 获取单元格数据内容为日期类型的数据
     * 
     * @param cell
     *            Excel单元格
     * @return String 单元格数据内容
     */
    private String getDateCellValue(HSSFCell cell) {
        String result = "";
        try {
            int cellType = cell.getCellType();
            if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
                Date date = cell.getDateCellValue();
                result = (date.getYear() + 1900) + "-" + (date.getMonth() + 1)
                        + "-" + date.getDate();
            } else if (cellType == HSSFCell.CELL_TYPE_STRING) {
                String date = getStringCellValue(cell);
                result = date.replaceAll("[年月]", "-").replace("日", "").trim();
            } else if (cellType == HSSFCell.CELL_TYPE_BLANK) {
                result = "";
            }
        } catch (Exception e) {
            System.out.println("日期格式不正确!");
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据HSSFCell类型设置数据
     * @param cell
     * @return
     */
    private String getCellFormatValue(HSSFCell cell) {
        String cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
            // 如果当前Cell的Type为NUMERIC
            case HSSFCell.CELL_TYPE_NUMERIC:
            case HSSFCell.CELL_TYPE_FORMULA: {
                // 判断当前的cell是否为Date
                if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    // 如果是Date类型则，转化为Data格式
                    
                    //方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
                    //cellvalue = cell.getDateCellValue().toLocaleString();
                    
                    //方法2：这样子的data格式是不带带时分秒的：2011-10-12
                    Date date = cell.getDateCellValue();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    cellvalue = sdf.format(date);
                    
                }
                // 如果是纯数字
                else {
                    // 取得当前Cell的数值
                    cellvalue = String.valueOf(cell.getNumericCellValue());
                }
                break;
            }
            // 如果当前Cell的Type为STRIN
            case HSSFCell.CELL_TYPE_STRING:
                // 取得当前的Cell字符串
                cellvalue = cell.getRichStringCellValue().getString();
                break;
            // 默认的Cell值
            default:
                cellvalue = " ";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;

    }
 
    
    
    public static void main(String[] args) {
        /*          // 对读取Excel表格标题测试
		InputStream is = new FileInputStream("/Users/caojing/Documents/requestdata.xls");
		ExcelReader excelReader = new ExcelReader();
		String[] title = excelReader.readExcelTitle(is,"input");
		System.out.println("获得Excel表格的标题:");
		for (String s : title) {
		    System.out.print(s + " ");
		}*/
		ExcelWriter excelReader = new ExcelWriter();
    }
    @DataProvider(name="dp")
    public Object[][] initData() throws IOException{
        ExcelWriter excelReader = new ExcelWriter();
        Object[][]data = null; 
        try {
			Map<Integer, String[]> map = excelReader.readExcelContent("/Users/caojing/Documents/requestdata.xls","input");
			data = new Object[map.size()][map.get(1).length];
			for(int i=0;i<map.size();i++){
				data[i] = map.get(i+1);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        /*for(int i =0 ; i <data[0].length;i++){
        	for(int j = 0 ;j< data[i])
        }*/
    	return data;
    }
   /**
    * 数据写入功能
    * @param path
    * @param sheetName
    * @param lineNum
    * @param colNumß
    * @param content
 * @throws IOException 
    */
    public void writeData(String path,String sheetName,int lineNum,int colNum ,Object content) throws IOException{
    	 is = new FileInputStream(path);
         fs = new POIFSFileSystem(is);
         wb = new HSSFWorkbook(fs);
         sheet = wb.getSheet(sheetName);
         HSSFRow  row  = sheet.getRow(lineNum);   

         if(null == row ){  
             //如果不做空判断，你必须让你的模板文件画好边框，beginRow和beginCell必须在边框最大值以内  
             //否则会出现空指针异常  
             row = sheet.createRow(lineNum);  
         } 
         // 创建一个Excel的单元格
         HSSFCell cell = row.createCell(colNum);
         // 给Excel的单元格设置样式和赋值
         cell.setCellType(HSSFCell.CELL_TYPE_STRING);  
         cell.setCellValue(content.toString());
         FileOutputStream fileOutputStream = new FileOutputStream(path);  
         wb.write(fileOutputStream);  
         fileOutputStream.close();
    }
    
    public void testWrite() throws IOException{
    	this.writeData("/Users/caojing/Documents/requestdata.xls", "input", 9, 9, "写入数据测试");
    }
    @DataProvider(name="dp1")
    public Object[][] initdatax(){
    	return new Object[][]{
    		{"1","2","3","4","5","6","7","8","9","10","11","12","13"},
    		{"1","2","3","4","5","6","7","8","9","10","11","12","13"},
    		{"1","2","3","4","5","6","7","8","9","10","11","12","13"},
    	};
    }
   // @Test(dataProvider="dp")
    public void test01(Object o,Object o1,Object o2,Object o3,Object o4,Object o5,Object o6,Object o7,Object o8,Object o9,Object o10,Object o11,Object o12){
    	
    	System.out.println("****************");
    	System.out.println(o);
    	System.out.println(o1);
    	System.out.println(o2);
    	System.out.println(o3);
    	System.out.println(o4);
    	System.out.println(o5);
    	System.out.println(o6);
    	System.out.println(o7);
    	System.out.println(o8);
    	System.out.println(o9);
    	System.out.println(o10);
    	System.out.println(o11);
    	System.out.println(o12);
    }
    
    
    public class GetUsersInfo {
        public void getInfo(){
            RequestSpecification requestSpecification= TestConfig.requestSpecification();

            requestSpecification.when()
                    .get("/users?access_token="+TestConfig.getToken("kauth/authorize?uid=239&cid=www&tenant_id=1")).
                    then()
                            //.log().all()
                    .assertThat().statusCode(200).body("data.name", Matchers.equalTo("开课吧"));
        }
    }

    
        public void getInfo(){
            RequestSpecification requestSpecification= TestConfig.requestSpecification();
            requestSpecification.when()
                    .get("/users?access_token="+TestConfig.getToken("kauth/authorize?uid=239&cid=www&tenant_id=1")).
                    then()
                    .assertThat().statusCode(200).body("data.name", Matchers.equalTo("开课吧"));
        }
    
        @Test(dataProvider="dp")
        public void test011(Object o,Object o1,Object o2,Object o3,Object o4,Object o5,Object o6,Object o7,Object o8,Object o9,Object o10,Object o11,Object o12) throws JSONException{
        	
        	System.out.println("****************");
        	System.out.println(o);
        	System.out.println(o1);
        	System.out.println(o2);
        	System.out.println(o3);
        	System.out.println(o4);
        	System.out.println(o5);
        	System.out.println(o6);
        	System.out.println(o7);
        	System.out.println(o8);
        	System.out.println(o9);
        	System.out.println(o10);
        	System.out.println(o11);
        	System.out.println(o12);
        	
        	String accesstoken = "12354";
    		Response response = TestConfig.getOrDeleteExecu(o1.toString(), o2.toString()+o3.toString() );
    		//Assert.assertEquals(o10.toString().trim(), response.asString());
    		System.out.println(response.asString());
    		response.then().assertThat().
    				statusCode(200).
    				body("status", equalTo(true))
    				;


        }    
        
       
}