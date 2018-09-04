package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import th.in.moe.devtools.sqlgenerator.common.bean.ColumnBean;
import th.in.moe.devtools.sqlgenerator.common.bean.GeneratorCriteria;
import th.in.moe.devtools.sqlgenerator.common.bean.OracleColumnBean;
import th.in.moe.devtools.sqlgenerator.common.bean.TableBean;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;
import th.in.moe.devtools.sqlgenerator.common.util.BooleanToStringConverter;
import th.in.moe.devtools.sqlgenerator.common.util.ExcelUtils;

public class OracleCreateStatementGeneratorService implements CreateStatementGeneratorService {
	
	private static final Logger logger = LoggerFactory.getLogger(OracleCreateStatementGeneratorService.class);
	
	private String SQL_CREATE_TABLE_TEMPLATE;
	private String SQL_CREATE_TABLE_COLUMN_TEMPLATE;
	private String SQL_CREATE_TABLE_PK_TEMPLATE;
	private String SQL_COMMENT_TEMPLATE;
	private String SQL_CREATE_SEQUENCE_TEMPLATE;
	
	public OracleCreateStatementGeneratorService() throws GeneratedException {
		try {
			SQL_CREATE_TABLE_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/oracle/sql_create_table.template"), StandardCharsets.UTF_8);
			SQL_CREATE_TABLE_COLUMN_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/oracle/sql_create_table-column.template"), StandardCharsets.UTF_8);
			SQL_CREATE_TABLE_PK_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/oracle/sql_create_table-pk.template"), StandardCharsets.UTF_8);
			SQL_COMMENT_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/oracle/sql_comment.template"), StandardCharsets.UTF_8);
			SQL_CREATE_SEQUENCE_TEMPLATE = IOUtils.toString(this.getClass().getResource("/templates/oracle/sql_create_sequence.template"), StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new GeneratedException(e.getMessage(), e);
		}
	}
	
	@Override
	public void validateXlsxFile(File xlsxFile) throws GeneratedException {
		logger.info("validateXlsxFile xlsxFile={}", xlsxFile.getAbsolutePath());
		
		final String[] columnHeaders = {
			"No.",
			"Column Name",
			"Data Type",
			"Size",
			"Units/Time Zone",
			"PK",
			"Not Null",
			"Default",
			"Comment"
		};
		
		try (Workbook workbook = WorkbookFactory.create(new FileInputStream(xlsxFile))) {
			
			Row row = null;
			for (Sheet sheet : workbook) {
				row = sheet.getRow(0);
				for (int i = 0; i < columnHeaders.length; i++) {
					if (row.getCell(i) == null) {
						logger.warn("Missing Sheet={}, ColumnTemplate={}", sheet.getSheetName(), columnHeaders[i]);
						throw new GeneratedException("Invalid template in sheet: " + sheet.getSheetName());
					} else if (!columnHeaders[i].equals(row.getCell(i).getStringCellValue())) {
						logger.warn("Don't match Sheet={}, ColumnTemplate={}, ColumnValue={}", sheet.getSheetName(), columnHeaders[i], row.getCell(i).getStringCellValue());
						throw new GeneratedException("Invalid template in sheet: " + sheet.getSheetName());
					}
				}
			}
			
			logger.info("validateXlsxFile result=Pass");
			
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new GeneratedException(e.getMessage(), e);
		}
	}
	
	@Override
	public List<String> processXlsxFile(GeneratorCriteria criteria, File xlsxFile) throws GeneratedException {
		logger.info("processXlsxFile xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<TableBean> tableBeanList = transformXlsx2Object(xlsxFile);
		List<String> sqlTextList = genereateSqlCreateTableStatement(tableBeanList);
		
		logger.info("processXlsxFile xlsxFile={} Success", xlsxFile.getAbsolutePath());
		
		return sqlTextList;
	}
	
	private List<TableBean> transformXlsx2Object(File xlsxFile) throws GeneratedException {
		logger.info("transformXlsx2Object xlsxFile={}", xlsxFile.getAbsolutePath());
		
		List<TableBean> tableBeanList = new ArrayList<>();
		try (Workbook workbook = WorkbookFactory.create(new FileInputStream(xlsxFile))) {
			
			TableBean tableBean = null;
			OracleColumnBean columnBean = null;
			List<ColumnBean> keyList = null;
			List<ColumnBean> columnBeanList = null;
			for (Sheet sheet : workbook) {
				
				tableBean = new TableBean();
				tableBean.setTableName(sheet.getSheetName());
				keyList = new ArrayList<>();
				columnBeanList = new ArrayList<>();
				
				for (Row row : sheet) {
					if (row.getRowNum() == 0) {
						// First Row, Column Header
						// Skip it
						continue;
					}
					
					columnBean = new OracleColumnBean();
					// Column A
					// No.
					
					// Column B
					// Column Name
					if (row.getCell(1) != null) {
						columnBean.setColumnName(ExcelUtils.getCellValueAsString(row.getCell(1)).replaceAll("[\\W]", ""));
					}
					
					// Column C
					// Data Type
					if (row.getCell(2) != null) {
						columnBean.setDataType(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(2))));
					}
					
					// Column D
					// Size
					if (row.getCell(3) != null) {
						columnBean.setDataSize(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(3))));
					}
					
					// Column E
					// Units/Time Zone
					if (row.getCell(4) != null) {
						columnBean.setUnit(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(4))));
					}
					
					// Column F
					// PK
					if (row.getCell(5) != null) {
						columnBean.setPrimaryKeyFlag(BooleanToStringConverter.convertToBeanAttribute(
							ExcelUtils.getCellValueAsString(row.getCell(5)).toUpperCase().replaceAll("[^Y]", "")
						));
					}
					
					// Column G
					// Not Null
					if (row.getCell(6) != null) {
						columnBean.setMandatoryFlag(BooleanToStringConverter.convertToBeanAttribute(
							ExcelUtils.getCellValueAsString(row.getCell(6)).toUpperCase().replaceAll("[^Y]", "")
						));
					}
					
					// Column H
					// Default
					if (row.getCell(7) != null) {
						columnBean.setDefaultValue(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(7))));
					}
					
					// Column I
					// Comment
					if (row.getCell(8) != null) {
						columnBean.setComment(
							StringUtils.defaultIfEmpty(StringUtils.trim(ExcelUtils.getCellValueAsString(row.getCell(8))), StringUtils.EMPTY)
								.replaceAll("'", "''")
						);
					}
					
					if (columnBean.isPrimaryKeyFlag()) {
						keyList.add(columnBean);
					}
					columnBeanList.add(columnBean);
				}
				
				tableBean.setKeyList(keyList);
				tableBean.setColumnList(columnBeanList);
				tableBeanList.add(tableBean);
				
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new GeneratedException(e.getMessage(), e);
		}
		
		return tableBeanList;
	}
	
	private List<String> genereateSqlCreateTableStatement(List<TableBean> tableBeanList) {
		logger.info("Generate SQL Create Table Statement");
		
		StringBuilder sqlText = null;
		List<String> sqlTextList = new ArrayList<>(tableBeanList.size());
		Map<String, String> valueMap = null;
		
		for (TableBean tableBean : tableBeanList) {
			valueMap = new HashMap<>();
			valueMap.put("tableName", tableBean.getTableName());
			valueMap.put("columnList", generateTableColumn(tableBean.getColumnList()));
			valueMap.put("primaryKeyList", generateTablePrimaryKey(tableBean));
			valueMap.put("commentList", generateComment(tableBean));
			
			sqlText = new StringBuilder(StringSubstitutor.replace(SQL_CREATE_TABLE_TEMPLATE, valueMap));
			sqlText.append(generateSequence(tableBean.getTableName()));
			logger.info("Generate SQL create statement of table={} success", tableBean.getTableName());
			sqlTextList.add(sqlText.toString());
		}
		
		return sqlTextList;
	}
	
	private String generateTableColumn(List<ColumnBean> columnBeanList) {
		boolean pkFlag = false;
		StringBuilder builder = new StringBuilder();
		OracleColumnBean columnBean = null;
		Map<String, String> valueMap = null;
		
		for (int i = 0; i < columnBeanList.size(); i++) {
			columnBean = (OracleColumnBean) columnBeanList.get(i);
			valueMap = new HashMap<>();
			valueMap.put("columnName", columnBean.getColumnName());
			valueMap.put("dataType", getColumnDataTypeAndSize(columnBean));
			valueMap.put("optional", getColumnOptional(columnBean));
			
			builder.append(StringSubstitutor.replace(SQL_CREATE_TABLE_COLUMN_TEMPLATE, valueMap));
			
			if (i < columnBeanList.size() - 1) {
				builder.append(System.lineSeparator());
			}
			if (columnBean.isPrimaryKeyFlag()) {
				pkFlag = true;
			}
		}
		
		if (!pkFlag) {
			builder.deleteCharAt(builder.length() - 1);
		}
		
		return builder.toString();
	}
	
	private String getColumnDataTypeAndSize(OracleColumnBean columnBean) {
		String dataTypeAndSize = null;
		
		if (StringUtils.isNotEmpty(columnBean.getDataSize())) {
			if (("CHAR".equals(columnBean.getDataType()) || "VARCHAR2".equals(columnBean.getDataType()))
					&& ("CHAR".equals(columnBean.getUnit()) || "BYTE".equals(columnBean.getUnit())))  {
				dataTypeAndSize = columnBean.getDataType() + "(" + columnBean.getDataSize() + " " + columnBean.getUnit() + ")";
			} else {
				dataTypeAndSize = columnBean.getDataType() + "(" + columnBean.getDataSize() + ")";
			}
		} else {
			if ("TIMESTAMP".equals(columnBean.getDataType())
					&& ("TIME ZONE".equals(columnBean.getUnit()) || "LOCAL TIME ZONE".equals(columnBean.getUnit())))  {
				dataTypeAndSize = columnBean.getDataType() + " WITH " + columnBean.getUnit();
			} else {
				dataTypeAndSize = columnBean.getDataType();
			}
		}
		
		return dataTypeAndSize;
	}
	
	private String getColumnOptional(OracleColumnBean columnBean) {
		StringBuilder builder = new StringBuilder();
		
		if (StringUtils.isNotBlank(columnBean.getDefaultValue())) {
			builder.append(" DEFAULT " + columnBean.getDefaultValue());
		}
		if (columnBean.isMandatoryFlag()) {
			builder.append(" NOT NULL");
		}
		
		return builder.toString();
	}
	
	private String generateTablePrimaryKey(TableBean tableBean) {
		String primaryKeyText = null;
		
		if (!tableBean.getKeyList().isEmpty()) {
			StringBuilder builder = new StringBuilder();
			for (ColumnBean columnBean : tableBean.getKeyList()) {
				if (columnBean.isPrimaryKeyFlag()) {
					builder.append("\"" + columnBean.getColumnName() + "\",");
				}
			}
			builder.deleteCharAt(builder.length() - 1);
			
			Map<String, String> valueMap = new HashMap<>();
			valueMap.put("tableName", tableBean.getTableName());
			valueMap.put("pkList", builder.toString());
			
			primaryKeyText = StringSubstitutor.replace(SQL_CREATE_TABLE_PK_TEMPLATE, valueMap);
		} else {
			primaryKeyText = "";
		}
		
		return primaryKeyText;
	}
	
	private String generateComment(TableBean tableBean) {
		StringBuilder builder = new StringBuilder();
		Map<String, String> valueMap = null;
		
		for (ColumnBean columnBean : tableBean.getColumnList()) {
			if (StringUtils.isNotBlank(columnBean.getComment())) {
				valueMap = new HashMap<>();
				valueMap.put("tableName", tableBean.getTableName());
				valueMap.put("columnName", columnBean.getColumnName());
				valueMap.put("comment", columnBean.getComment());
				
				builder.append(StringSubstitutor.replace(SQL_COMMENT_TEMPLATE, valueMap));
			}
		}
		
		return builder.toString();
	}
	
	private String generateSequence(String tableName) {
		Map<String, String> valueMap = new HashMap<>();
		valueMap.put("tableName", tableName);
		
		return StringSubstitutor.replace(SQL_CREATE_SEQUENCE_TEMPLATE, valueMap);
	}
	
	@Override
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
