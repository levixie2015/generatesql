package com.lw.sql;

import com.lw.IO.WriteOperate;
import com.lw.columns.ColumnsOperate;
import com.lw.excel.ExcelOperate;
import com.lw.util.PropertiesUtil;
import org.apache.commons.collections4.CollectionUtils;

import java.io.IOException;
import java.util.*;

public class SqlOperate {
    private static final String SQL_CHARACTER = "`";
    private static final String BLANK = " ";

    public static void main(String[] args) throws IOException {
        PropertiesUtil p = new PropertiesUtil("config.properties");
        String excelPath = p.getProperties().get("config.excelPath").toString();
        String tableName = p.getProperties().get("config.tableName").toString();
        String columns = p.getProperties().get("config.columns").toString();
        String customData = p.getProperties().get("config.custom.tableName.columnValue").toString();
        String filePath = p.getProperties().get("config.file.path").toString();

        System.out.println("excel文件路径[ " + filePath + "]");
        List<Map<String, String>> columnsList = ColumnsOperate.getColumns(columns);// 表字段及数据类型
        System.out.println("tableName共有[" + columnsList.size() + "]个字段");
        List<Map<String, String>> excelDataList = ExcelOperate.getExcelData(excelPath);// excel数据
        System.out.println("excel共有数据[" + excelDataList.size() + "]条");
        List<Map<String, String>> customDataList = getCustomData(customData, tableName, columnsList);// 指定的字段、字段值
        System.out.println("指定字段的值" + customDataList);

        // 生成sql
        List<String> sql = getSql(tableName, columnsList, excelDataList, customDataList);
        System.out.println("生成sql的总条数[" + sql.size() + "],格式：sql = " + sql.get(0));


        StringBuilder sqlStr = new StringBuilder();
        for (String str : sql) {
            sqlStr.append(str).append(";").append("\n");
        }
        sqlStr.deleteCharAt(sqlStr.lastIndexOf("\n"));
        WriteOperate.writeFile(sqlStr.toString(), filePath);

    }

    /**
     * 指定字段的值
     *
     * @param customData
     * @return
     */
    private static List<Map<String, String>> getCustomData(String customData, String tableName, List<Map<String, String>> columnsList) {
        if (Objects.equals(customData, "")) {
            System.out.println(" 没有需要指定字段的值.");
            return null;
        }

        List<Map<String, String>> customDataList = new ArrayList<Map<String, String>>();

        String[] dataArray = customData.split("\\|");
        Map<String, String> dataMap = new HashMap<String, String>();
        for (String data : dataArray) {
            String[] dataValueArray = data.split("\\=");
            String name = dataValueArray[0];// 指定字段名称
            String value = dataValueArray[1];// 指定字段值
            dataMap.put(name, checkColumnName(name, value, tableName, columnsList));
        }
        customDataList.add(dataMap);
        return customDataList;
    }

    /**
     * 检查指定的字段名称是否有误
     * <p>格式：[{dataType=int, columnName=RABATE_MAIN_ID}]</p>
     *
     * @param name
     * @param value
     * @param tableName
     * @param columnsList
     * @return
     */
    private static String checkColumnName(String name, String value, String tableName, List<Map<String, String>> columnsList) {
        String columnType = "";// 字段类型
        for (Map<String, String> dataMap : columnsList) {// 数据集合
            String columnName = dataMap.get("columnName");
            String dataType = dataMap.get("dataType");
            if (Objects.equals(name, columnName)) {
                columnType = dataType;
                break;
            }
        }

        if (Objects.equals(columnType, "")) {
            throw new RuntimeException("表[" + tableName + "],不存在指定字段[" + name + "].请检查[config.custom.tableName.columnValue]");
        }

        if (Objects.equals("varchar", columnType)) {
            return ExcelOperate.VALUE_CHARACTER + value + ExcelOperate.VALUE_CHARACTER;
        }
        return value;
    }

    /**
     * 生成sql
     *
     * @param tableName      表名
     * @param columnsList    表字段及数据类型
     * @param excelDataList  excel数据
     * @param customDataList 指定字段的值
     * @return
     */
    private static List<String> getSql(String tableName, List<Map<String, String>> columnsList, List<Map<String, String>> excelDataList, List<Map<String, String>> customDataList) {
        List<String> sqlList = new ArrayList<String>();
        StringBuilder preSql = getSqlPre(tableName, columnsList);

        for (Map<String, String> dataMap : excelDataList) {// 数据集合
            List<String> sqlValueList = getSqlValue(dataMap, columnsList, customDataList);

            // 生成sql的后缀
            StringBuilder sql = new StringBuilder(preSql);
            for (String sqlValue : sqlValueList) {// 单个数据
                sql.append(sqlValue).append(",");
            }

            sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
            sqlList.add(sql.toString());
        }
        return sqlList;
    }

    /**
     * 获取所有字段的值
     *
     * @param dataMap     excel中的一行数据
     * @param columnsList 数据库表的所有字段
     * @return
     */
    private static List<String> getSqlValue(Map<String, String> dataMap, List<Map<String, String>> columnsList, List<Map<String, String>> customDataList) {
        List<String> resultList = new ArrayList<String>();

        for (Map<String, String> columnsMap : columnsList) {// 字段集合
            String columnName = columnsMap.get("columnName");
            String dataType = columnsMap.get("dataType");

            String columnValue = "";
            for (Map.Entry<String, String> entry : dataMap.entrySet()) {// 单个数据
                String key = entry.getKey();// excel title
                String value = entry.getValue();// excel值
                if (Objects.equals(columnName, key)) {
                    // 检查配置文件是否指定字段的值
                    if (!CollectionUtils.isEmpty(customDataList)){// 优先取指定的值
                        columnValue = getCustomData(columnName, customDataList);
                        if (Objects.equals(columnValue,"")){
                            columnValue = value;
                        }
                    }else{// 取excel中的值
                        columnValue = value;
                    }
                }
            }

            // excel数据中没有该字段的值,且该字段的类型为"datetime",默认值为now()
            if (Objects.equals(columnValue, "")) {// 时间类型
                if (Objects.equals("datetime", dataType)) {
                    columnValue = "now()";
                } else {
                    columnValue = "null";
                }
            }

            resultList.add(columnValue);
        }
        return resultList;
    }

    /**
     * 获取配置文件是字段指定的值
     * <p>格式：[{REBATE_AMOUNT=0, DEPARAT_NAME='1111', REBATE_USED=0, REBATE_USABLE=0}]</p>
     *
     * @param columnName
     * @param customDataList
     * @return
     */
    private static String getCustomData(String columnName, List<Map<String, String>> customDataList) {
        for (Map<String, String> dataMap : customDataList) {
            if (dataMap.containsKey(columnName)) {
                return dataMap.get(columnName);
            }
        }
        return "";
    }

    /**
     * sql前缀
     *
     * @param tableName
     * @param columnsList
     * @return
     */
    private static StringBuilder getSqlPre(String tableName, List<Map<String, String>> columnsList) {
        StringBuilder preSql = new StringBuilder("insert into " + SQL_CHARACTER + tableName + SQL_CHARACTER + "(");
        for (Map<String, String> columnsMap : columnsList) {
            String columnName = columnsMap.get("columnName");
            String dataType = columnsMap.get("dataType");
            preSql.append(SQL_CHARACTER).append(columnName).append(SQL_CHARACTER).append(",");
        }
        preSql.deleteCharAt(preSql.lastIndexOf(",")).append(") values (");
        return preSql;
    }

}
