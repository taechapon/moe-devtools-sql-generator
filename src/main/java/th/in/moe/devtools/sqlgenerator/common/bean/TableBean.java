package th.in.moe.devtools.sqlgenerator.common.bean;

import java.util.List;

public class TableBean {

	private String tableName;
	private List<ColumnBean> keyList;
	private List<ColumnBean> columnList;

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public List<ColumnBean> getKeyList() {
		return keyList;
	}

	public void setKeyList(List<ColumnBean> keyList) {
		this.keyList = keyList;
	}

	public List<ColumnBean> getColumnList() {
		return columnList;
	}

	public void setColumnList(List<ColumnBean> columnList) {
		this.columnList = columnList;
	}

}
