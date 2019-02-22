package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;

import org.junit.Test;

import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public class OracleDataManipulateGeneratorServiceTest {
	
//	private File xlsxFile = new File("/tmp/Thailand_Data.xlsx");
	private File xlsxFile = new File("/tmp/SYS_InsertData.xlsx");
	
	@Test
	public void test_processXlsxFile() throws GeneratedException {
		OracleDataManipulateGeneratorService generatorService = new OracleDataManipulateGeneratorService();
		generatorService.processXlsxFile(xlsxFile);
	}
	
}
