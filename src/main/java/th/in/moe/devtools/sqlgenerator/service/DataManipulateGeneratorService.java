package th.in.moe.devtools.sqlgenerator.service;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;

import th.in.moe.devtools.sqlgenerator.common.exception.GeneratedException;

public interface DataManipulateGeneratorService {
	
	public static final char DOLLAR_SIGN = '$';
	
	public Logger getLogger();
	
	public default String toExcelFixedValue(String address) {
		StringBuilder builder = new StringBuilder();
		
		for (int i = 0; i < address.length(); i++) {
			builder.append(DOLLAR_SIGN).append(address.charAt(i));
		}
		
		return builder.toString();
	}
	
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
