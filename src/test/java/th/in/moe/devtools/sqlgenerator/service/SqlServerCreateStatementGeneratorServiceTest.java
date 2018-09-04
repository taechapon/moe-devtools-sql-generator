package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.util.List;

import org.junit.Test;

import th.in.moe.devtools.sqlgenerator.common.bean.GeneratorCriteria;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public class SqlServerCreateStatementGeneratorServiceTest {
	
	private File xlsxFile = new File("/tmp/SqlServer_CreateTable.xlsx");
	private File sqlFile = new File("/tmp/SqlServer_CreateTable.sql");
	
	@Test
	public void test_validateXlsxFile() throws GeneratedException {
		SqlServerCreateStatementGeneratorService generatorService = new SqlServerCreateStatementGeneratorService();
		
		generatorService.validateXlsxFile(xlsxFile);
	}
	
	@Test
	public void test_processXlsxFile() throws GeneratedException {
		GeneratorCriteria criteria = new GeneratorCriteria();
		criteria.setUser("dbo");
		SqlServerCreateStatementGeneratorService generatorService = new SqlServerCreateStatementGeneratorService();
		
		List<String> sqlTextList = generatorService.processXlsxFile(criteria, xlsxFile);
		sqlTextList.forEach(System.out::println);
		
		generatorService.writeSqlFile(sqlTextList, sqlFile);
	}
	
}
