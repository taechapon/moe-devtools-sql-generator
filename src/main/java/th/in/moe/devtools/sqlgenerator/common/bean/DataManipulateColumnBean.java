package th.in.moe.devtools.sqlgenerator.common.bean;

public class DataManipulateColumnBean {

	private String value;
	private String address;

	public DataManipulateColumnBean(String value, String address) {
		super();
		this.value = value;
		this.address = address;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

}
