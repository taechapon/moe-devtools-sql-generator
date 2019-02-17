package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.util.List;

import th.in.moe.devtools.sqlgenerator.common.bean.GeneratorCriteria;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public interface CreateStatementGeneratorService extends GeneratorService {
	
	public void validateXlsxFile(File xlsxFile) throws GeneratedException;
	
	public List<String> processXlsxFile(GeneratorCriteria criteria, File xlsxFile) throws GeneratedException;
	
}
