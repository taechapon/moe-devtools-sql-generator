package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import th.in.moe.devtools.sqlgenerator.common.bean.GeneratorCriteria;
import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public interface CreateStatementGeneratorService {
	
	public Logger getLogger();
	
	public void validateXlsxFile(File xlsxFile) throws GeneratedException;
	
	public List<String> processXlsxFile(GeneratorCriteria criteria, File xlsxFile) throws GeneratedException;
	
	public default void writeSqlFile(List<String> sqlTextList, File sqlFile) throws GeneratedException {
		try (FileOutputStream fos = new FileOutputStream(sqlFile)) {
			IOUtils.writeLines(sqlTextList, System.lineSeparator(), fos, StandardCharsets.UTF_8);
			fos.flush();
			getLogger().info("Write SQL file={} success", sqlFile.getAbsolutePath());
		} catch (Exception e) {
			getLogger().error(e.getMessage(), e);
			throw new GeneratedException(e.getMessage(), e);
		}
	}
	
}
