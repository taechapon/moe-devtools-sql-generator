package th.in.moe.devtools.sqlgenerator.common.bean;

import java.util.List;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class UpdateDataTableBean {

	private String tableName;
	private List<String> updateColumnList;
	private List<List<String>> updateDataList;
	private List<String> updateConditionColumnList;
	private List<List<String>> updateConditionList;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getUpdateColumnList() {
		return updateColumnList;
	}

	public void setUpdateColumnList(List<String> updateColumnList) {
		this.updateColumnList = updateColumnList;
	}

	public List<List<String>> getUpdateDataList() {
		return updateDataList;
	}

	public void setUpdateDataList(List<List<String>> updateDataList) {
		this.updateDataList = updateDataList;
	}

	public List<String> getUpdateConditionColumnList() {
		return updateConditionColumnList;
	}

	public void setUpdateConditionColumnList(List<String> updateConditionColumnList) {
		this.updateConditionColumnList = updateConditionColumnList;
	}

	public List<List<String>> getUpdateConditionList() {
		return updateConditionList;
	}

	public void setUpdateConditionList(List<List<String>> updateConditionList) {
		this.updateConditionList = updateConditionList;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
	}

}
