package com.chs.wheel.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;

public class DOCUtils {
	
	/**
	 * 
	 * <p>readWord</p>
	 * Description: 读取Word文档
	 * 
	 * @author chenhaishan
	 * @date 2019-08-05 12:11
	 *
	 * @param path
	 * @return
	 */
	public static String readWord(String filePath){
		
		String buffer = "";
        try {
            if (filePath.endsWith(".doc")) {
                InputStream is = new FileInputStream(new File(filePath));
                WordExtractor ex = new WordExtractor(is);
                buffer = ex.getText();
                ex.close();
            } else if (filePath.endsWith("docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(filePath);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                buffer = extractor.getText();
                extractor.close();
            } else {
                System.out.println(filePath+"\n此文件不是word文件！");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return buffer;
	}
	
	/**
	 * 
	 * <p>readExcel</p>
	 * Description: 读取Excel
	 * 
	 * @author chenhaishan
	 * @date 2019-08-05 13:56
	 *
	 * @param filePath
	 * @return
	 */
	@SuppressWarnings("deprecation")
	public static Map<String,List<List<String>>> readExcel(String filePath){  
		
		try {
			if (filePath.endsWith(".xls")||filePath.endsWith(".xlsx")) {
				
				Workbook workbook = null;  
				
				InputStream is = new FileInputStream(new File(filePath));
	            if(filePath.endsWith(".xls")){  
	                workbook = new HSSFWorkbook(is);  
	            }else if(filePath.endsWith(".xlsx")){  
	                workbook = new XSSFWorkbook(is);  
	            }
	            
	            Map<String,List<List<String>>> excel = new HashMap<String,List<List<String>>>();  
	            if(workbook != null){  
	                for(int sheetNum = 0;sheetNum < workbook.getNumberOfSheets();sheetNum++){  
	                    
	                    Sheet sheet = workbook.getSheetAt(sheetNum);  
	                    if(sheet == null){  
	                        continue;  
	                    }
	                     
	                    int firstRowNum  = sheet.getFirstRowNum();  
	                     
	                    int lastRowNum = sheet.getLastRowNum();
	                    
	                    List<List<String>> sheets = new ArrayList<List<String>>();  
	                     
	                    for(int rowNum = firstRowNum;rowNum <= lastRowNum;rowNum++){
	                         
	                        Row row = sheet.getRow(rowNum);  
	                        if(row == null){  
	                            continue;  
	                        }  
	                         
	                        int firstCellNum = row.getFirstCellNum();  
	                         
	                        int lastCellNum = row.getLastCellNum();
	                        
	                        List<String> cells = new ArrayList<String>();  
	                         
	                        for(int cellNum = firstCellNum; cellNum < lastCellNum;cellNum++){  
	                            Cell cell = row.getCell(cellNum);  
	                            
	                            String cellValue = "";  
	                            if(cell != null){
	                            	if(cell.getCellType() == CellType.NUMERIC)cell.setCellType(CellType.STRING);
	                            	if(cell.getCellType() == CellType.NUMERIC) {
	                            		cellValue = String.valueOf(cell.getNumericCellValue());
	                            	}else if(cell.getCellType() == CellType.STRING) {
	                            		cellValue = String.valueOf(cell.getStringCellValue());  
	                            	}else if(cell.getCellType() == CellType.BOOLEAN) {
	                            		cellValue = String.valueOf(cell.getBooleanCellValue());  
	                            	}else if(cell.getCellType() == CellType.FORMULA) {
	                            		cellValue = String.valueOf(cell.getCellFormula());
	                            	}else if(cell.getCellType() == CellType.BLANK) {
	                            		cellValue = "";  
	                            	}else if(cell.getCellType() == CellType.ERROR) {
	                            		cellValue = "非法字符";
	                            	}else {
	                            		cellValue = "未知类型";
	                            	}
	                            }
	                            
	                            cells.add(cellValue);  
	                        }
	                        sheets.add(cells);  
	                    }
	                    if(sheets.size()>0)excel.put(sheet.getSheetName(), sheets);
	                }
	                workbook.close();  
	            }
	            
	            return excel;
			}
		} catch (Exception e) {
            e.printStackTrace();
        } 
        
        return null;  
    }  
}
