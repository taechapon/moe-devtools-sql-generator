package th.in.moe.devtools.sqlgenerator.common.bean;

public class ColumnBean {

	private String columnName;
	private String dataType;
	private String dataSize;
	private boolean primaryKeyFlag;
	private boolean mandatoryFlag;
	private String defaultValue;
	private String comment;

	public String getColumnName() {
		return columnName;
	}

	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	public String getDataSize() {
		return dataSize;
	}

	public void setDataSize(String dataSize) {
		this.dataSize = dataSize;
	}

	public boolean isPrimaryKeyFlag() {
		return primaryKeyFlag;
	}

	public void setPrimaryKeyFlag(boolean primaryKeyFlag) {
		this.primaryKeyFlag = primaryKeyFlag;
	}

	public boolean isMandatoryFlag() {
		return mandatoryFlag;
	}

	public void setMandatoryFlag(boolean mandatoryFlag) {
		this.mandatoryFlag = mandatoryFlag;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

}
