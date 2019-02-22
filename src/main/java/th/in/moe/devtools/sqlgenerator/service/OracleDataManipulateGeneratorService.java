package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.in.moe.devtools.sqlgenerator.common.bean.DataManipulateColumnBean;
import th.in.moe.devtools.sqlgenerator.common.bean.DataManipulateTableBean;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;
import th.in.moe.devtools.sqlgenerator.common.util.ExcelUtils;

public class OracleDataManipulateGeneratorService implements DataManipulateGeneratorService {
	
private static final Logger logger = LoggerFactory.getLogger(OracleDataManipulateGeneratorService.class);
	
	private static final String ORACLE_NEXTVAL = "NEXTVAL";
	private static final String NULL = "NULL";
	private static final String SYSDATE = "SYSDATE";
	
	@Override
	public Logger getLogger() {
		return logger;
	}
	
	public void processXlsxFile(File xlsxFile) throws GeneratedException {
		logger.info("processXlsxFile xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<DataManipulateTableBean> tableBeanList = transformXlsx2Object(xlsxFile);
		writeSqlInXlxsFile(tableBeanList, xlsxFile);
		
		logger.info("processXlsxFile xlsxFile={} Success", xlsxFile.getAbsolutePath());
	}
	
	private List<DataManipulateTableBean> transformXlsx2Object(File xlsxFile) throws GeneratedException {
		logger.info("transformXlsx2Object xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<DataManipulateTableBean> tableDataBeanList = new ArrayList<>();
		
		try (Workbook workbook = new XSSFWorkbook(new FileInputStream(xlsxFile))) {
			
			DataManipulateTableBean tableDataBean = null;
			List<String> manipulateColumnList = null;
			List<List<DataManipulateColumnBean>> manipulateDataList = null;
			List<DataManipulateColumnBean> dataList = null;
			
			Iterator<Sheet> sheetIterator = workbook.iterator();
			Sheet sheet = null;
			while (sheetIterator.hasNext()) {
				sheet = sheetIterator.next();
				tableDataBean = new DataManipulateTableBean();
				tableDataBean.setTableName(sheet.getSheetName());
				
				manipulateColumnList = new ArrayList<>();
				manipulateDataList = new ArrayList<List<DataManipulateColumnBean>>();
				
				for (Row row : sheet) {
					
					if (row.getRowNum() == 0) {
						
						// First Row, Column Header
						for (Cell cell : row) {
							manipulateColumnList.add(cell.getAddress().formatAsString());
						}
						tableDataBean.setManipulateColumnList(manipulateColumnList);
						
					} else {
						
						// Data Row
						dataList = new ArrayList<>();
						for (int i = 0; i < manipulateColumnList.size(); i++) {
							if (row.getCell(i) != null) {
								dataList.add(new DataManipulateColumnBean(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(i))), row.getCell(i).getAddress().formatAsString()));
							} else {
								dataList.add(new DataManipulateColumnBean(StringUtils.EMPTY, manipulateColumnList.get(i).substring(0, 1) + (row.getRowNum() + 1)));
							}
						}
						manipulateDataList.add(dataList);
						break;
					}
					
				}
				tableDataBean.setManipulateDataList(manipulateDataList);
				tableDataBeanList.add(tableDataBean);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new GeneratedException(e.getMessage(), e);
		}
		
		return tableDataBeanList;
	}
	
	private void writeSqlInXlxsFile(List<DataManipulateTableBean> tableBeanList, File xlsxFile) {
		StringBuilder builder = null;
		String insertColumn = null;
		
		for (DataManipulateTableBean tableBean : tableBeanList) {
			insertColumn = generateColumn(tableBean.getTableName(), tableBean.getManipulateColumnList());
			
			for (List<DataManipulateColumnBean> dataList : tableBean.getManipulateDataList()) {
				builder = new StringBuilder();
				builder.append(insertColumn);
				builder.append(generateValue(dataList));
				System.out.println(builder);
			}
		}
	}
	
	private String generateColumn(String tableName, List<String> manipulateColumnList) {
		StringBuilder builder = new StringBuilder();
		
		builder.append("=\"INSERT INTO ").append(tableName).append(" (\"");
		for (int i = 0; i < manipulateColumnList.size(); i++) {
			if (i > 0) {
				builder.append(" & \",\"");
			}
			builder.append(" & ").append(toExcelFixedValue(manipulateColumnList.get(i)));
		}
		builder.append(" & \")\" ");
		
		return builder.toString();
	}
	
	private Object generateValue(List<DataManipulateColumnBean> dataList) {
		StringBuilder builder = new StringBuilder();
		DataManipulateColumnBean columnBean = null;
		
		builder.append(" & \" VALUES (\"");
		for (int i = 0; i < dataList.size(); i++) {
			columnBean = dataList.get(i);
			if (i > 0) {
				builder.append(" & \",\"");
			}
			if (columnBean.getValue().endsWith(ORACLE_NEXTVAL)
					|| SYSDATE.equalsIgnoreCase(columnBean.getValue())) {
				builder.append(" & ").append(columnBean.getAddress());
			} else {
				// IF(ISBLANK(G2),"NULL","'" & G2 & "'")
				builder.append(" & ").append("IF(ISBLANK(").append(columnBean.getAddress()).append("),\"NULL\",\"'\" & ").append(columnBean.getAddress()).append(" & \"'\")");
				//builder.append(" & ").append("\"'\" & ").append(columnBean.getAddress()).append(" & \"'\"");
			}
		}
		builder.append(" & \");\"");
		
		return builder.toString();
	}
	
}
