package th.in.moe.devtools.sqlgenerator.common.bean;

import java.util.List;

public class DataManipulateTableBean {

	private String tableName;
	private List<String> manipulateColumnList;
	private List<List<DataManipulateColumnBean>> manipulateDataList;
	private List<String> updateConditionColumnList;
	private List<List<DataManipulateColumnBean>> updateConditionList;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<String> getManipulateColumnList() {
		return manipulateColumnList;
	}

	public void setManipulateColumnList(List<String> manipulateColumnList) {
		this.manipulateColumnList = manipulateColumnList;
	}

	public List<List<DataManipulateColumnBean>> getManipulateDataList() {
		return manipulateDataList;
	}

	public void setManipulateDataList(List<List<DataManipulateColumnBean>> manipulateDataList) {
		this.manipulateDataList = manipulateDataList;
	}

	public List<String> getUpdateConditionColumnList() {
		return updateConditionColumnList;
	}

	public void setUpdateConditionColumnList(List<String> updateConditionColumnList) {
		this.updateConditionColumnList = updateConditionColumnList;
	}

	public List<List<DataManipulateColumnBean>> getUpdateConditionList() {
		return updateConditionList;
	}

	public void setUpdateConditionList(List<List<DataManipulateColumnBean>> updateConditionList) {
		this.updateConditionList = updateConditionList;
	}

}
