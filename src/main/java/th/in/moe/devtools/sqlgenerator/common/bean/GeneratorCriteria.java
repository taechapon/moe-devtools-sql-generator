package th.in.moe.devtools.sqlgenerator.common.bean;

public class GeneratorCriteria {

	private String databaseProductionName;
	private String generateType;
	// Additional
	private String user;

	public String getDatabaseProductionName() {
		return databaseProductionName;
	}

	public void setDatabaseProductionName(String databaseProductionName) {
		this.databaseProductionName = databaseProductionName;
	}

	public String getGenerateType() {
		return generateType;
	}

	public void setGenerateType(String generateType) {
		this.generateType = generateType;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

}
