package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.util.List;

import org.junit.Test;

import th.in.moe.devtools.sqlgenerator.common.bean.GeneratorCriteria;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public class H2CreateStatementGeneratorServiceTest {
	
	private File xlsxFile = new File("/tmp/H2_CreateTable.xlsx");
	private File sqlFile = new File("/tmp/H2_CreateTable.sql");
	
	@Test
	public void test_validateXlsxFile() throws GeneratedException {
		H2CreateStatementGeneratorService generatorService = new H2CreateStatementGeneratorService();
		
		generatorService.validateXlsxFile(xlsxFile);
	}
	
	@Test
	public void test_processXlsxFile() throws GeneratedException {
		GeneratorCriteria criteria = new GeneratorCriteria();
		H2CreateStatementGeneratorService generatorService = new H2CreateStatementGeneratorService();
		
		List<String> sqlTextList = generatorService.processXlsxFile(criteria, xlsxFile);
		sqlTextList.forEach(System.out::println);
		
		generatorService.writeSqlFile(sqlTextList, sqlFile);
	}
	
}
