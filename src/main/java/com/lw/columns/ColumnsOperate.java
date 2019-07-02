package com.lw.columns;

import com.lw.util.PropertiesUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 获取表字段，字段数据类型(格式：字段1-varchar,字段2-varchar,...)
 * SELECT GROUP_CONCAT(CONCAT_WS('-', column_name, data_type))
 * FROM information_schema.columns
 * WHERE table_name = 't_fa_rebate_qx_main'
 * AND table_schema = 'dev_db_fund';
 */
public class ColumnsOperate {
    public static void main(String[] args) {
        PropertiesUtil p = new PropertiesUtil("config.properties");
        String columnsStr = p.getProperties().get("config.columns").toString();
        List<Map<String, String>> columnList = getColumns(columnsStr);
        System.out.println("columnList = " + columnList);
    }

    /**
     * 数据库表解析
     *
     * <p>格式：字段1-varchar,字段2-varchar,...</p>
     *
     * @param columnsStr
     * @return
     */
    public static List<Map<String, String>> getColumns(String columnsStr) {
        // columnName 字段名称    dataType 数据类型
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        String[] columnList = columnsStr.split(",");
        for (String column : columnList) {
            Map<String, String> map = new HashMap<String, String>();
            String[] split = column.split("-");

            map.put("columnName", split[0]);
            map.put("dataType", split[1]);

            list.add(map);
        }
        return list;
    }

}
