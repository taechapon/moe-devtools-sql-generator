package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.util.List;

import org.junit.Test;

import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public class InsertStatementGeneratorServiceTest {
	
	private File xlsxFile = new File("/tmp/Thailand_Data.xlsx");
	private File sqlFile = new File("/tmp/Thailand_Data.sql");
	
	@Test
	public void test_processXlsxFile() throws GeneratedException {
		InsertStatementGeneratorService generatorService = new InsertStatementGeneratorService();
		
		List<String> sqlTextList = generatorService.processXlsxFile(xlsxFile);
		sqlTextList.forEach(System.out::println);
		
		generatorService.writeSqlFile(sqlTextList, sqlFile);
	}
	
}
