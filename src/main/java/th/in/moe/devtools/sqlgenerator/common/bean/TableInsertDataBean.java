package th.in.moe.devtools.sqlgenerator.common.bean;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class TableInsertDataBean {

	private String tableName;
	private List<String> insertColumnList;
	private List<List<String>> insertDataList;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getInsertColumnList() {
		return insertColumnList;
	}

	public void setInsertColumnList(List<String> insertColumnList) {
		this.insertColumnList = insertColumnList;
	}

	public List<List<String>> getInsertDataList() {
		return insertDataList;
	}

	public void setInsertDataList(List<List<String>> insertDataList) {
		this.insertDataList = insertDataList;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
