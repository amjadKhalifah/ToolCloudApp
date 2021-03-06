package toolcloud.tum.toolcloudapp.model;

import java.io.Serializable;

public class Machine {

	private String machineId;
	private String name;
	private String der;//?
	private String companyId;
    private String cad;
    private String location;
	public Machine() {

	}
	public Machine(String id, String name, String der, String companyId) {
		super();
		this.machineId = id;
		this.name = name;
		this.der = der;
		this.companyId = companyId;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String id) {
		this.machineId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDer() {
		return der;
	}
	public void setDer(String der) {
		this.der = der;
	}
	public String getCompanyId() {
		return companyId;
	}
	public void setCompanyId(String companyId) {
		this.companyId = companyId;
	}



    public String getCad() {
        return cad;
    }
    public void setCad(String cad) {
        this.cad = cad;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
	public String toString() {
		return "<Machine machineId='" + machineId + "' name='" + name + "' der='" + der
				+ "' companyId='" + companyId + "'/>";
	}
	
	
}
