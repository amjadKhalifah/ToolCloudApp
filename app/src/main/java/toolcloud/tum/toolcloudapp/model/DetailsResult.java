package toolcloud.tum.toolcloudapp.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IBM on 28-Feb-15.
 */
public class DetailsResult  {

    private static DetailsResult instance;

    public static void setInstance(DetailsResult instance) {
        DetailsResult.instance = instance;
    }

    public static DetailsResult getInstance() {
        return instance;
    }

    // type of data, Machine-scan, intake-scan or tool-scan
    private String type;
    // could be null based on type
    private Machine machine;
    // could be one item list or empty or multi
    private List<Intake> intakes;
    private List<Tool> tools;

    public DetailsResult(String type) {
        this.type = type;
        intakes = new ArrayList<>();
        tools = new ArrayList<>();

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    public List<Intake> getIntakes() {
        return intakes;
    }

    public void setIntakes(List<Intake> intakes) {
        this.intakes = intakes;
    }
    public void addIntake(Intake intake) {
        this.intakes.add(intake);
    }
    public List<Tool> getTools() {
        return tools;
    }

    public void setTools(List<Tool> tools) {
        this.tools = tools;
    }
    public void addTool(Tool tool) {
        this.tools.add(tool);
    }


    @Override
    public String toString() {
        return "DetailsResult{" +
                "type='" + type + '\'' +
                ", machine=" + machine +
                ", intakes=" + intakes +
                ", tools=" + tools +
                '}';
    }
}
