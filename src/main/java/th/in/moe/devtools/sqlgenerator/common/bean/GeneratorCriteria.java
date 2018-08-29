package th.in.moe.devtools.sqlgenerator.common.bean;

public class GeneratorCriteria {

	private String databaseProductionName;
	private String generateType;

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

}
