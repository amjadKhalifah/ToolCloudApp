package toolcloud.tum.toolcloudapp.model;

/**
 * @author Amjad
 *
 */
public class Company {
	String companyId;
	String name;
	public Company(String companyId, String name) {

		this.companyId = companyId;
		this.name = name;
	}
	public Company() {

	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@Override
	public String toString() {
		return "<Company companyId='" + companyId + "' name='" + name + "'/>";
	}
	

}
