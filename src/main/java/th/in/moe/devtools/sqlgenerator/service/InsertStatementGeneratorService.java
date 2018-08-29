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

import th.in.moe.devtools.sqlgenerator.common.bean.TableDataBean;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;
import th.in.moe.devtools.sqlgenerator.common.util.ExcelUtils;

public class InsertStatementGeneratorService {
	
	private static final Logger logger = LoggerFactory.getLogger(InsertStatementGeneratorService.class);
	
	private String SQL_INSERT_TEMPLATE;
	
	public InsertStatementGeneratorService() throws GeneratedException {
		try {
			SQL_INSERT_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/sql_insert.template"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new GeneratedException(e.getMessage(), e);
		}
	}
	
	public List<String> processXlsxFile(File xlsxFile) throws GeneratedException {
		logger.info("processXlsxFile xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<TableDataBean> tableBeanList = transformXlsx2Object(xlsxFile);
		List<String> sqlTextList = genereateSqlInsertStatement(tableBeanList);
		
		logger.info("processXlsxFile xlsxFile={} Success", xlsxFile.getAbsolutePath());
		
		return sqlTextList;
	}
	
	private List<TableDataBean> transformXlsx2Object(File xlsxFile) throws GeneratedException {
		logger.info("transformXlsx2Object xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<TableDataBean> tableDataBeanList = new ArrayList<>();
		
		try (Workbook workbook = StreamingReader.builder()
			.rowCacheSize(50)	// number of rows to keep in memory (defaults to 10)
			.bufferSize(2048)	// buffer size to use when reading InputStream to file (defaults to 1024)
			.open(new FileInputStream(xlsxFile))) {
			
			TableDataBean tableDataBean = null;
			List<String> columnNameList = null;
			List<List<String>> columnDataList = null;
			List<String> dataList = null;
			
			Iterator<Sheet> sheetIterator = workbook.iterator();
			Sheet sheet = null;
			while (sheetIterator.hasNext()) {
				sheet = sheetIterator.next();
				tableDataBean = new TableDataBean();
				tableDataBean.setTableName(sheet.getSheetName());
				
				columnNameList = new ArrayList<>();
				columnDataList = new ArrayList<List<String>>();
				
				for (Row row : sheet) {
					
					if (row.getRowNum() == 0) {
						
						// First Row, Column Header
						for (Cell cell : row) {
							columnNameList.add(StringUtils.trim(ExcelUtils.getCellValueAsString(cell)));
						}
						tableDataBean.setColumnNameList(columnNameList);
						
					} else {
						
						// Data Row
						dataList = new ArrayList<>();
						for (int i = 0; i < columnNameList.size(); i++) {
							if (row.getCell(i) != null) {
								dataList.add(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(i))));
							} else {
								dataList.add(StringUtils.EMPTY);
							}
						}
						columnDataList.add(dataList);
					}
					
				}
				tableDataBean.setColumnDataList(columnDataList);
				tableDataBeanList.add(tableDataBean);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new GeneratedException(e.getMessage(), e);
		}
		
		return tableDataBeanList;
	}
	
	private List<String> genereateSqlInsertStatement(List<TableDataBean> tableDataBeanList) {
		List<String> sqlTextList = new ArrayList<>();
		String columnName = null;
		Map<String, String> valueMap = null;
		
		for (TableDataBean tableDataBean : tableDataBeanList) {
			columnName = collectionToDelimitedString(tableDataBean.getColumnNameList(), ",", "", "");
			for (List<String> columnDataList : tableDataBean.getColumnDataList()) {
				valueMap = new HashMap<>();
				valueMap.put("tableName", tableDataBean.getTableName());
				valueMap.put("columnName", columnName);
				valueMap.put("columnData", collectionToDelimitedString(columnDataList, ",", "'", "'"));
				
				sqlTextList.add(StringSubstitutor.replace(SQL_INSERT_TEMPLATE, valueMap));
			}
		}
		
		return sqlTextList;
	}
	
	private String collectionToDelimitedString(Collection<?> collection, String delim, String prefix, String suffix) {
		if ((collection == null || collection.isEmpty())) {
			return "";
		}
		StringBuilder sb = new StringBuilder();
		Iterator<?> it = collection.iterator();
		while (it.hasNext()) {
			sb.append(prefix).append(it.next()).append(suffix);
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
