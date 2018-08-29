package th.in.moe.devtools.sqlgenerator.common.bean;

public class MySqlColumnBean extends ColumnBean {

	private boolean unsignedFlag;
	private boolean autoIncrementFlag;

	public boolean isUnsignedFlag() {
		return unsignedFlag;
	}

	public void setUnsignedFlag(boolean unsignedFlag) {
		this.unsignedFlag = unsignedFlag;
	}

	public boolean isAutoIncrementFlag() {
		return autoIncrementFlag;
	}

	public void setAutoIncrementFlag(boolean autoIncrementFlag) {
		this.autoIncrementFlag = autoIncrementFlag;
	}

}
