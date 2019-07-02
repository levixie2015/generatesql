package com.lw.excel;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import com.lw.util.PropertiesUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.*;

public class ExcelOperate {
    public static final String VALUE_CHARACTER = "'";
    public static void main(String[] args) {
        PropertiesUtil p = new PropertiesUtil("config.properties");
        String path = p.getProperties().get("config.excelPath").toString();

        try {
            List<Map<String, String>> dataList = getExcelData(path);
            System.out.println("dataList = " + dataList);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static List<Map<String, String>> getExcelData(String path) throws IOException {
        boolean isE2007 = false; //判断是否是excel2007格式
        if (path.endsWith("xlsx")) {
            isE2007 = true;
        }

        InputStream input = new FileInputStream(new File(path));  //建立输入流
        Workbook wb = null;

        if (isE2007) {// 根据文件格式(2003或者2007)来初始化
            wb = new XSSFWorkbook(input);
        } else {
            wb = new HSSFWorkbook(input);
        }


        List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
        //获得第一个表单
        Sheet sheet = wb.getSheetAt(0);
        //表单的总行数,比如有3行,此处返回的是2,行数的下标从0计数
        int rowNum0 = sheet.getLastRowNum();
//        System.out.println("行数=" + rowNum0);
        //表单的总行数,比如有3行,此处返回的是3
        int rowNum1 = sheet.getPhysicalNumberOfRows();
//        System.out.println("行数=" + rowNum1);
        //获得第一行,下标从0开始计算
        Row firstRow = sheet.getRow(1);
        //根据第一行,得到总的列数
        int columnNum = firstRow.getPhysicalNumberOfCells();
//        System.out.println("列数=" + columnNum);

        List<String> titleList = getDataList(columnNum, sheet.getRow(0),false);
//        System.out.println("titleList = " + titleList);

        for (int i = 1; i < rowNum1; i++) {// 行
            Row row = sheet.getRow(i);
            List<String> dataList = getDataList(columnNum, row,true);
//            System.out.println("dataList [" + i + "]= " + dataList);
            Map<String, String> dataMap = new HashMap<String, String>();
            for (int j = 0; j < titleList.size() - 1; j++) {
                dataMap.put(titleList.get(j), dataList.get(j));
            }
            retList.add(dataMap);
        }
        return retList;
    }

    /**
     * 获取每行数据
     *
     * @param totalColumnNum 总列数(包含表头数据)
     * @param row            行数据信息
     * @param hasCharacter   是否处理字符
     */
    private static List<String> getDataList(int totalColumnNum, Row row, boolean hasCharacter) {
        List<String> retList = new ArrayList<String>();
        for (int j = 0; j < totalColumnNum; j++) {// 列
            String cellValue = getCellValue(j, row, hasCharacter);
            retList.add(cellValue);
        }
        return retList;
    }

    /**
     * 获取单元格的值
     *
     * @param j
     * @param row
     * @param hasCharacter
     * @return
     */
    private static String getCellValue(int j, Row row, boolean hasCharacter) {
        Cell cell = row.getCell(j);
        if (Objects.equals(cell,null)){
            return "";
        }
        int cellType = cell.getCellType();
        String cellValue = "";
        switch (cellType) {
            case Cell.CELL_TYPE_STRING: // 字符串类型
                cellValue = cell.getStringCellValue().trim();
                cellValue = Objects.equals(cellValue, "") ? "" : cellValue;
                if (hasCharacter) {
                    cellValue = VALUE_CHARACTER + cellValue + VALUE_CHARACTER;
                }
                break;
            case Cell.CELL_TYPE_BOOLEAN:  //布尔类型
                cellValue = String.valueOf(cell.getBooleanCellValue());
                break;
            case Cell.CELL_TYPE_NUMERIC: // 数值类型
                if (HSSFDateUtil.isCellDateFormatted(cell)) {  //判断日期类型
//                            cellValue =  DateUtil.formatDateByFormat(cell.getDateCellValue(), "yyyy-MM-dd");
                    cellValue = "";
                } else {  //否
                    cellValue = new DecimalFormat("#.######").format(cell.getNumericCellValue());
                }
                break;
            case Cell.CELL_TYPE_BLANK:
                cellValue = "";
            default: //其它类型，取空串吧
                cellValue = "";
                break;
        }
        return cellValue;

    }

}
