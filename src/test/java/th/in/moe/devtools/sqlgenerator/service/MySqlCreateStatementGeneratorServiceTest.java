package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.util.List;

import org.junit.Test;

import th.in.moe.devtools.sqlgenerator.common.bean.GeneratorCriteria;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public class MySqlCreateStatementGeneratorServiceTest {
	
	private File xlsxFile = new File("/tmp/MySql_CreateTable.xlsx");
	private File sqlFile = new File("/tmp/MySql_CreateTable.sql");
	
	@Test
	public void test_validateXlsxFile() throws GeneratedException {
		MySqlCreateStatementGeneratorService generatorService = new MySqlCreateStatementGeneratorService();
		
		generatorService.validateXlsxFile(xlsxFile);
	}
	
	@Test
	public void test_processXlsxFile() throws GeneratedException {
		GeneratorCriteria criteria = new GeneratorCriteria();
		MySqlCreateStatementGeneratorService generatorService = new MySqlCreateStatementGeneratorService();
		
		List<String> sqlTextList = generatorService.processXlsxFile(criteria, xlsxFile);
		sqlTextList.forEach(System.out::println);
		
		generatorService.writeSqlFile(sqlTextList, sqlFile);
	}
	
}
