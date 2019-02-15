package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.util.List;

import org.junit.Test;

import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public class UpdateStatementGeneratorServiceTest {
	
	private File xlsxFile = new File("/tmp/Thailand_Update_Data.xlsx");
	private File sqlFile = new File("/tmp/Thailand_Update_Data.sql");
	
	@Test
	public void test_processXlsxFile() throws GeneratedException {
		UpdateStatementGeneratorService generatorService = new UpdateStatementGeneratorService();
		
		List<String> sqlTextList = generatorService.processXlsxFile(xlsxFile);
		sqlTextList.forEach(System.out::println);
		
		generatorService.writeSqlFile(sqlTextList, sqlFile);
	}
	
}
