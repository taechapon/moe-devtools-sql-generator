package th.in.moe.devtools.sqlgenerator.common.util;

import th.in.moe.devtools.sqlgenerator.common.constant.CommonConstants.FLAG;

public abstract class BooleanToStringConverter {
	
	public static String convertToDatabaseColumn(Boolean value) {
		return (value != null && value) ? FLAG.Y_FLAG : FLAG.N_FLAG;
	}
	
	public static Boolean convertToBeanAttribute(String value) {
		return FLAG.Y_FLAG.equals(value);
	}
	
}
