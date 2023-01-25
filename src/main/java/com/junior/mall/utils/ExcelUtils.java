package com.junior.mall.utils;

import org.apache.poi.ss.usermodel.Cell;

public class ExcelUtils {
    public static Object getCellValue(Cell cell){
        //获取Cell类型并，返回不同的值
        switch (cell.getCellType()){
            case STRING:return cell.getStringCellValue();
            case BOOLEAN:return cell.getBooleanCellValue();
            case NUMERIC:return cell.getNumericCellValue();
        }
        return null;
    }
}
