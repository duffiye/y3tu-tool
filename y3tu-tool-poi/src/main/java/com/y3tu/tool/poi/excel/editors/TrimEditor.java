package com.y3tu.tool.poi.excel.editors;

import com.y3tu.tool.core.text.StringUtils;
import com.y3tu.tool.poi.excel.cell.CellEditor;
import org.apache.poi.ss.usermodel.Cell;


/**
 * 去除String类型的单元格值两边的空格
 *
 * @author Looly
 */
public class TrimEditor implements CellEditor {

    @Override
    public Object edit(Cell cell, Object value) {
        if (value instanceof String) {
            return StringUtils.trim((String) value);
        }
        return value;
    }

}
