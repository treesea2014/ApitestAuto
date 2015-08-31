package com.kaikeba.server.api.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.gxb.server.api.beans.InputData;

/**
 * 操作Excel表格的功能类
 */
public class ExcelUtils {
	private FileInputStream is;
	private FileOutputStream fo;
	private POIFSFileSystem fs;
	private HSSFWorkbook wb;
	private HSSFSheet sheet;
	private HSSFRow row;
	protected static final Logger logger = LoggerFactory
			.getLogger(ExcelUtils.class);

	/**
	 * 读取Excel数据内容
	 * 
	 * @param InputStream
	 * @return Map 包含单元格数据内容的Map对象
	 * @throws FileNotFoundException
	 */
	public Map<Integer, String[]> readExcelData(String path, String sheetName) {
		Map<Integer, String[]> content = new HashMap<Integer, String[]>();
		String[] str;
		try {
			is = new FileInputStream(path);
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
			sheet = wb.getSheet(sheetName);
		} catch (IOException e) {
			logger.error("读取文件异常，请检查数据文件路径 、sheet页名称。");
			e.printStackTrace();
		}
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		row = sheet.getRow(0);
		int colNum = row.getPhysicalNumberOfCells();
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int i = 1; i <= rowNum; i++) {
			row = sheet.getRow(i);
			int j = 0;
			str = new String[colNum];
			while (j < colNum) {
				// 也可以将每个单元格的数据设置到一个javabean的属性中
				str[j] = getCellFormatValue(row.getCell(j));
				j++;
			}
			content.put(i, str);
		}
		try {
			is.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("读取文件异常");
			e.printStackTrace();
		}
		return content;
	}

	/**
	 * 数据格式化 inputData Map<Integer, String[]> to HashSet<Object[String , Input]>
	 * 
	 * @param Map
	 *            <Integer, String[]> map
	 * @return set.Iterator<>
	 */
	public Iterator<Object[]> formatInputData(Map<Integer, String[]> map) {
		List<Object[]> set = new ArrayList<Object[]>();
		InputData inputData;
		for (Entry<Integer, String[]> entry : map.entrySet()) {
			String test_ID = entry.getKey().toString();
			String[] test_case = entry.getValue();
			inputData = new InputData();
			inputData.setID(test_case[0]);
			inputData.setTestCase(test_case[1]);
			inputData.setDescription(test_case[2]);
			inputData.setHost(test_case[3]);
			inputData.setCallSuff(test_case[4]);
			inputData.setCallType(test_case[5]);
			inputData.setAuthScheme(test_case[6]);
			inputData.setAuthCreds(test_case[7]);
			inputData.setAccept(test_case[8]);
			inputData.setContentType(test_case[9]);
			inputData.setBody(test_case[10]);
			inputData.setActualResponse(test_case[11]);
			inputData.setExpectResponse(test_case[12]);
			inputData.setLog(test_case[13]);
			inputData.setIsPass(test_case[14]);
			if (!test_ID.equals("") && !test_case.equals("")) {
				set.add(new Object[] { test_ID, inputData });
			}
		}
		
		Iterator<Object[]> iterator = set.iterator();
		return iterator;

	}

	/**
	 * 数据写入ca'p'z
	 * 
	 * @param path
	 * @param sheetName
	 * @param lineNum
	 * @param colNum
	 * @param content
	 */
	public void writeData(String path, String sheetName, int lineNum,
			int colNum, Object content) {
		try {
			FileInputStream is = new FileInputStream(path);
			FileOutputStream fo = null;
			//is = new FileInputStream(path);
			fs = new POIFSFileSystem(is);
			wb = new HSSFWorkbook(fs);
			sheet = wb.getSheet(sheetName);
			HSSFRow row = sheet.getRow(lineNum);

		} catch (Exception e) {
			logger.error("打开文件时发生异常。。。");
			e.printStackTrace();
		}
		if (null == row) {
			row = sheet.createRow(lineNum);
		}
		// 创建一个Excel的单元格
		HSSFCell cell = row.getCell(colNum);
		//cell =  sheet.getRow(lineNum).getCell(colNum);
		if(null == cell)
			cell = row.createCell(colNum);
		// 给Excel的单元格设置样式和赋值
		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		cell.setCellValue(content.toString());
		try {			
			fo = new FileOutputStream(path);
			fo.flush();
			wb.write(fo);
			//is.close();
			fo.close();
			
		} catch (Exception e) {
			logger.error("关闭文件时发生异常。。。");
			e.printStackTrace();
		}

	}
	  public void writeResultAtExcel(String path, String sheetName, int lineNum,
				int colNum, Object content) {
			FileInputStream input = null;
			FileOutputStream output = null;
			try {
				input = new FileInputStream(path);
				HSSFWorkbook workBook = new HSSFWorkbook(input);
				HSSFSheet sheet = workBook.getSheet(sheetName);
					HSSFCell cell = sheet.getRow(lineNum).createCell(colNum);
					cell.setCellValue(content.toString());
					output = new FileOutputStream(path);
					workBook.write(output);
				
			} catch (Exception e) {
				logger.error("打开文件时发生异常。。。");
				e.printStackTrace();
			} finally {
				try {
					input.close();
					output.close();
				} catch (IOException e) {
					logger.error("关闭文件时发生异常。。。");
					e.printStackTrace();
				}
			}
	    }

	/**
	 * 获取单元格数据内容为字符串类型的数据
	 * 
	 * @param cell
	 *            Excel单元格
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
	 * 
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

					// 方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
					// cellvalue = cell.getDateCellValue().toLocaleString();

					// 方法2：这样子的data格式是不带带时分秒的：2011-10-12
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
			cellvalue = " ";
		}
		return cellvalue;

	}

}