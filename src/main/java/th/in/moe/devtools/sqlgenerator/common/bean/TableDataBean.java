package th.in.moe.devtools.sqlgenerator.common.bean;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TableDataBean {

	private String tableName;
	private List<String> columnNameList;
	private List<List<String>> columnDataList;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getColumnNameList() {
		return columnNameList;
	}

	public void setColumnNameList(List<String> columnNameList) {
		this.columnNameList = columnNameList;
	}

	public List<List<String>> getColumnDataList() {
		return columnDataList;
	}

	public void setColumnDataList(List<List<String>> columnDataList) {
		this.columnDataList = columnDataList;
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
