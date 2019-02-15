package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.monitorjbl.xlsx.StreamingReader;

import th.in.moe.devtools.sqlgenerator.common.bean.TableInsertDataBean;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;
import th.in.moe.devtools.sqlgenerator.common.util.ExcelUtils;

public class InsertStatementGeneratorService {
	
	private static final Logger logger = LoggerFactory.getLogger(InsertStatementGeneratorService.class);
	
	private String SQL_INSERT_TEMPLATE;
	private static final String ORACLE_NEXTVAL = "NEXTVAL";
	private static final String NULL = "NULL";
	
	public InsertStatementGeneratorService() throws GeneratedException {
		try {
			SQL_INSERT_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/sql_insert.template"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new GeneratedException(e.getMessage(), e);
		}
	}
	
	public List<String> processXlsxFile(File xlsxFile) throws GeneratedException {
		logger.info("processXlsxFile xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<TableInsertDataBean> tableBeanList = transformXlsx2Object(xlsxFile);
		List<String> sqlTextList = genereateSqlInsertStatement(tableBeanList);
		
		logger.info("processXlsxFile xlsxFile={} Success", xlsxFile.getAbsolutePath());
		
		return sqlTextList;
	}
	
	private List<TableInsertDataBean> transformXlsx2Object(File xlsxFile) throws GeneratedException {
		logger.info("transformXlsx2Object xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<TableInsertDataBean> tableDataBeanList = new ArrayList<>();
		
		try (Workbook workbook = StreamingReader.builder()
				.rowCacheSize(50)	// number of rows to keep in memory (defaults to 10)
				.bufferSize(2048)	// buffer size to use when reading InputStream to file (defaults to 1024)
				.open(new FileInputStream(xlsxFile))) {
			
			TableInsertDataBean tableDataBean = null;
			List<String> insertColumnList = null;
			List<List<String>> insertDataList = null;
			List<String> dataList = null;
			
			Iterator<Sheet> sheetIterator = workbook.iterator();
			Sheet sheet = null;
			while (sheetIterator.hasNext()) {
				sheet = sheetIterator.next();
				tableDataBean = new TableInsertDataBean();
				tableDataBean.setTableName(sheet.getSheetName());
				
				insertColumnList = new ArrayList<>();
				insertDataList = new ArrayList<List<String>>();
				
				for (Row row : sheet) {
					
					if (row.getRowNum() == 0) {
						
						// First Row, Column Header
						for (Cell cell : row) {
							insertColumnList.add(StringUtils.trim(ExcelUtils.getCellValueAsString(cell)));
						}
						tableDataBean.setInsertColumnList(insertColumnList);
						
					} else {
						
						// Data Row
						dataList = new ArrayList<>();
						for (int i = 0; i < insertColumnList.size(); i++) {
							if (row.getCell(i) != null) {
								dataList.add(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(i))));
							} else {
								dataList.add(StringUtils.EMPTY);
							}
						}
						insertDataList.add(dataList);
					}
					
				}
				tableDataBean.setInsertDataList(insertDataList);
				tableDataBeanList.add(tableDataBean);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new GeneratedException(e.getMessage(), e);
		}
		
		return tableDataBeanList;
	}
	
	private List<String> genereateSqlInsertStatement(List<TableInsertDataBean> tableDataBeanList) {
		List<String> sqlTextList = new ArrayList<>();
		String columnName = null;
		Map<String, String> valueMap = null;
		
		for (TableInsertDataBean tableDataBean : tableDataBeanList) {
			columnName = collectionToDelimitedString(tableDataBean.getInsertColumnList(), ",", "", "");
			for (List<String> columnDataList : tableDataBean.getInsertDataList()) {
				valueMap = new HashMap<>();
				valueMap.put("tableName", tableDataBean.getTableName());
				valueMap.put("columnName", columnName);
				valueMap.put("columnData", collectionToDelimitedString(columnDataList, ",", "'", "'"));
				
				sqlTextList.add(StringSubstitutor.replace(SQL_INSERT_TEMPLATE, valueMap));
			}
		}
		
		return sqlTextList;
	}
	
	private String collectionToDelimitedString(Collection<String> collection, String delim, String prefix, String suffix) {
		if ((collection == null || collection.isEmpty())) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = collection.iterator();
		String text = null;
		while (it.hasNext()) {
			text = it.next();
			if (text.endsWith(ORACLE_NEXTVAL)) {
				sb.append(text);
			} else {
				if (NULL.equalsIgnoreCase(text)) {
					sb.append(NULL);
				} else {
					sb.append(prefix).append(text).append(suffix);
				}
			}
			if (it.hasNext()) {
				sb.append(delim);
			}
		}
		return sb.toString();
	}
	
	public void writeSqlFile(List<String> sqlTextList, File sqlFile) throws GeneratedException {
		try (FileOutputStream fos = new FileOutputStream(sqlFile)) {
			IOUtils.writeLines(sqlTextList, System.lineSeparator(), fos, StandardCharsets.UTF_8);
			fos.flush();
			logger.info("Write SQL file={} success", sqlFile.getAbsolutePath());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new GeneratedException(e.getMessage(), e);
		}
	}
	
}
