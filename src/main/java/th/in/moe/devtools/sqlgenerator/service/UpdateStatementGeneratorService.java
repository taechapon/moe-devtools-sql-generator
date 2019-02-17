package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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

import th.in.moe.devtools.sqlgenerator.common.bean.UpdateDataTableBean;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;
import th.in.moe.devtools.sqlgenerator.common.util.ExcelUtils;

public class UpdateStatementGeneratorService implements GeneratorService {
	
	private static final Logger logger = LoggerFactory.getLogger(UpdateStatementGeneratorService.class);
	
	private String SQL_UPDATE_TEMPLATE;
	private static final String NULL = "NULL";
	private static final String WHERE = "WHERE";
	private static final String SEPERATOR = "}\\|}";
	
	public UpdateStatementGeneratorService() throws GeneratedException {
		try {
			SQL_UPDATE_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/sql_update.template"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new GeneratedException(e.getMessage(), e);
		}
	}
	
	@Override
	public Logger getLogger() {
		return logger;
	}
	
	public List<String> processXlsxFile(File xlsxFile) throws GeneratedException {
		logger.info("processXlsxFile xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<UpdateDataTableBean> tableBeanList = transformXlsx2Object(xlsxFile);
		List<String> sqlTextList = genereateSqlUpdateStatement(tableBeanList);
		
		logger.info("processXlsxFile xlsxFile={} Success", xlsxFile.getAbsolutePath());
		
		return sqlTextList;
	}
	
	private List<UpdateDataTableBean> transformXlsx2Object(File xlsxFile) throws GeneratedException {
		logger.info("transformXlsx2Object xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<UpdateDataTableBean> tableDataBeanList = new ArrayList<>();
		
		try (Workbook workbook = StreamingReader.builder()
			.rowCacheSize(50)	// number of rows to keep in memory (defaults to 10)
			.bufferSize(2048)	// buffer size to use when reading InputStream to file (defaults to 1024)
			.open(new FileInputStream(xlsxFile))) {
			
			UpdateDataTableBean tableUpdateDataBean = null;
			List<String> updateColumnList = null;
			List<List<String>> updateDataList = null;
			List<String> dataList = null;
			List<String> updateConditionColumnList = null;
			List<List<String>> updateConditionList = null;
			List<String> conditionList = null;
			String columnValue = null;
			String[] columnValues = null;
			
			Iterator<Sheet> sheetIterator = workbook.iterator();
			Sheet sheet = null;
			while (sheetIterator.hasNext()) {
				sheet = sheetIterator.next();
				tableUpdateDataBean = new UpdateDataTableBean();
				tableUpdateDataBean.setTableName(sheet.getSheetName());
				
				updateColumnList = new ArrayList<>();
				updateConditionColumnList = new ArrayList<>();
				updateDataList = new ArrayList<List<String>>();
				updateConditionList = new ArrayList<List<String>>();
				
				for (Row row : sheet) {
					
					if (row.getRowNum() == 0) {
						// First Row, Column Header and Condition
						for (Cell cell : row) {
							columnValue = StringUtils.trim(ExcelUtils.getCellValueAsString(cell));
							if (columnValue.startsWith(WHERE)) {
								columnValues = columnValue.split(SEPERATOR, -1);
								if (columnValues.length == 2) {
									updateConditionColumnList.add(columnValues[1]);
								} else {
									throw new GeneratedException("Wrong 'Where' Coloun Format");
								}
							} else {
								updateColumnList.add(columnValue);
							}
						}
						tableUpdateDataBean.setUpdateColumnList(updateColumnList);
						tableUpdateDataBean.setUpdateConditionColumnList(updateConditionColumnList);
					} else {
						// Data
						dataList = new ArrayList<>();
						for (int i = 0; i < updateColumnList.size(); i++) {
							if (row.getCell(i) != null) {
								dataList.add(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(i))));
							} else {
								dataList.add(StringUtils.EMPTY);
							}
						}
						updateDataList.add(dataList);
						
						// Condition
						conditionList = new ArrayList<>();
						for (int i = updateColumnList.size(); i < updateColumnList.size() + updateConditionColumnList.size(); i++) {
							if (row.getCell(i) != null) {
								conditionList.add(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(i))));
							} else {
								conditionList.add(StringUtils.EMPTY);
							}
						}
						updateConditionList.add(conditionList);
					}
					
				}
				tableUpdateDataBean.setUpdateDataList(updateDataList);
				tableUpdateDataBean.setUpdateConditionList(updateConditionList);
				tableDataBeanList.add(tableUpdateDataBean);
			}
			
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new GeneratedException(e.getMessage(), e);
		}
		
		return tableDataBeanList;
	}
	
	private List<String> genereateSqlUpdateStatement(List<UpdateDataTableBean> tableDataBeanList) {
		List<String> sqlTextList = new ArrayList<>();
		Map<String, String> valueMap = null;
		
		for (UpdateDataTableBean tableUpdateDataBean : tableDataBeanList) {
			int size = tableUpdateDataBean.getUpdateDataList().size();
			for (int i = 0; i < size; i++) {
				valueMap = new HashMap<>();
				valueMap.put("tableName", tableUpdateDataBean.getTableName());
				valueMap.put("updateValue", generateUpdateValue(tableUpdateDataBean.getUpdateColumnList(), tableUpdateDataBean.getUpdateDataList().get(i), "'", "'"));
				valueMap.put("updateCondition", generateUpdateCondition(tableUpdateDataBean.getUpdateConditionColumnList(), tableUpdateDataBean.getUpdateConditionList().get(i), "'", "'"));
				
				sqlTextList.add(StringSubstitutor.replace(SQL_UPDATE_TEMPLATE, valueMap));
			}
		}
		
		return sqlTextList;
	}
	
	private String generateUpdateValue(List<String> updateColumnList, List<String> dataList, String prefix, String suffix) {
		StringBuilder builder = new StringBuilder();
		int size = updateColumnList.size();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				builder.append(",");
			}
			builder.append(updateColumnList.get(i)).append("=");
			if (NULL.equalsIgnoreCase(dataList.get(i))) {
				builder.append(NULL);
			} else {
				builder.append(prefix).append(dataList.get(i)).append(suffix);
			}
		}
		return builder.toString();
	}
	
	private String generateUpdateCondition(List<String> updateConditionColumnList, List<String> conditionList, String prefix, String suffix) {
		StringBuilder builder = new StringBuilder();
		int size = updateConditionColumnList.size();
		for (int i = 0; i < size; i++) {
			if (i > 0) {
				builder.append(" AND ");
			}
			builder.append(updateConditionColumnList.get(i)).append("=").append(prefix).append(conditionList.get(i)).append(suffix);
		}
		return builder.toString();
	}
	
}
