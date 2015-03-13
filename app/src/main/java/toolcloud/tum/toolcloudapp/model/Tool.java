package toolcloud.tum.toolcloudapp.model;

import java.io.Serializable;

public class Tool  {
	
	
	private String toolId;
	private String name;
	private String length;
	private String height;
	private String machineId;
	private String intakeId;
    private String cad;
	public Tool() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Tool(String id, String name, String length, String height,
			String machineId, String intakeId) {
		super();
		this.toolId = id;
		this.name = name;
		this.length = length;
		this.height = height;
		this.machineId = machineId;
		this.intakeId = intakeId;
	}
	public String getToolId() {
		return toolId;
	}
	public void setToolId(String id) {
		this.toolId = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getLength() {
		return length;
	}
	public void setLength(String length) {
		this.length = length;
	}
	public String getHeight() {
		return height;
	}
	public void setHeight(String height) {
		this.height = height;
	}
	public String getMachineId() {
		return machineId;
	}
	public void setMachineId(String machineId) {
		this.machineId = machineId;
	}
	public String getIntakeId() {
		return intakeId;
	}
	public void setIntakeId(String intakeId) {
		this.intakeId = intakeId;
	}

    public String getCad() {
        return cad;
    }
    public void setCad(String cad) {
        this.cad = cad;
    }

    @Override
	public String toString() {
//		return "<Tool toolId='" + toolId + "' name='" + name + "' length='" + length
//				+ "' height='" + height + "' machineId='" + machineId
//				+ "' intakeId='" + intakeId + "'/>";
        return "Tool: "+name;
	}
	
}
