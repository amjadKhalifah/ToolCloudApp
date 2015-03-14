package toolcloud.tum.toolcloudapp.model;

public class ToolCloudObject {

	private String name;
	private String type;
	private String id;
    private boolean aggregatedAsChild;
    private boolean aggregatedAsParent;
    private String location;


    public ToolCloudObject() {

	}

    public ToolCloudObject(String name, String id, String type) {
        this.name = name;
        this.id = id;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    public boolean isAggregatedAsParent() {
        return aggregatedAsParent;
    }

    public void setAggregatedAsParent(boolean aggregatedAsParent) {
        this.aggregatedAsParent = aggregatedAsParent;
    }

    public boolean isAggregatedAsChild() {
        return aggregatedAsChild;
    }

    public void setAggregatedAsChild(boolean aggregatedAsChild) {
        this.aggregatedAsChild = aggregatedAsChild;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    @Override
    public String toString() {
        return "Object{" +
                "name='" + name + '\'' +
                ", type='" + type + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
