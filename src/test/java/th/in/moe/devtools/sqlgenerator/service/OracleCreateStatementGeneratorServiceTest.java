package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.util.List;

import org.junit.Test;

import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public class OracleCreateStatementGeneratorServiceTest {
	
	private File xlsxFile = new File("/tmp/Oracle_CreateTable.xlsx");
	private File sqlFile = new File("/tmp/Oracle_CreateTable.sql");
	
	@Test
	public void test_validateXlsxFile() throws GeneratedException {
		OracleCreateStatementGeneratorService generatorService = new OracleCreateStatementGeneratorService();
		
		generatorService.validateXlsxFile(xlsxFile);
	}
	
	@Test
	public void test_processXlsxFile() throws GeneratedException {
		OracleCreateStatementGeneratorService generatorService = new OracleCreateStatementGeneratorService();
		
		List<String> sqlTextList = generatorService.processXlsxFile(xlsxFile);
		sqlTextList.forEach(System.out::println);
		
		generatorService.writeSqlFile(sqlTextList, sqlFile);
	}
	
}
