package toolcloud.tum.toolcloudapp.xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import toolcloud.tum.toolcloudapp.model.DetailsResult;
import toolcloud.tum.toolcloudapp.model.Event;
import toolcloud.tum.toolcloudapp.model.Intake;
import toolcloud.tum.toolcloudapp.model.Machine;
import toolcloud.tum.toolcloudapp.model.Tool;

/**
 * Created by IBM on 28-Dec-14.
 */
public class ResultsHandler extends DefaultHandler {
    Boolean currentEvent = false;
    String currentValue = "";
    DetailsResult result;
    Machine machine;
    Tool intakeTool, tool;
    Intake intake;
    String type;

    public DetailsResult getResult() {
        return result;
    }

    public ResultsHandler(String type) {
        this.type = type;
    }

    // Called when tag starts
    @Override
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {

        currentValue = "";
        currentEvent = true;
        if (localName.equals("Results")) {
            result = new DetailsResult(type);
        }
        if (localName.equals("Machine")) {
            machine = new Machine();
        }
        if (localName.equals("Intake")) {
            intake = new Intake();
        }
        if (localName.equals("Intake_Tool")) {
            intakeTool = new Tool();
        }
        if (localName.equals("Tool")) {
            tool = new Tool();
        }

    }

    // Called when tag closing
    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        currentEvent = false;
        /** set value */
        if (localName.equalsIgnoreCase("machineId")) {
            machine.setMachineId(currentValue);
        } else if (localName.equalsIgnoreCase("name")) {
            machine.setName(currentValue);
        } else if (localName.equalsIgnoreCase("der")) {
            machine.setDer(currentValue);
        } else if (localName.equalsIgnoreCase("companyId")) {
            machine.setCompanyId(currentValue);
        } else if (localName.equalsIgnoreCase("Machine")) {
            result.setMachine(machine);
        } else if (localName.equalsIgnoreCase("Intake_intakeId")) {
            intake.setIntakeId(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_name")) {
            intake.setName(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_length")) {
            intake.setLength(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_height")) {
            intake.setHeight(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_machineId")) {
            intake.setMachineId(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_Tool_toolId")) {
            intakeTool.setToolId(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_Tool_name")) {
            intakeTool.setName(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_Tool_length")) {
            intakeTool.setLength(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_Tool_height")) {
            intakeTool.setHeight(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_Tool_intakeId")) {
            intakeTool.setIntakeId(currentValue);
        } else if (localName.equalsIgnoreCase("Intake_Tool")) {
            intake.setTool(intakeTool);
        } else if (localName.equalsIgnoreCase("Intake")) {
            result.addIntake(intake);
        } else if (localName.equalsIgnoreCase("Tool_toolId")) {
            tool.setToolId(currentValue);
        } else if (localName.equalsIgnoreCase("Tool_name")) {
            tool.setName(currentValue);
        } else if (localName.equalsIgnoreCase("Tool_length")) {
            tool.setLength(currentValue);
        } else if (localName.equalsIgnoreCase("Tool_height")) {
            tool.setHeight(currentValue);
        } else if (localName.equalsIgnoreCase("Tool_intakeId")) {
            tool.setIntakeId(currentValue);
        } else if (localName.equalsIgnoreCase("Tool")) {
            result.addTool(tool);
        }


    }

    // Called to get tag characters
    @Override
    public void characters(char[] ch, int start, int length)
            throws SAXException {

        if (currentEvent) {
            currentValue = currentValue + new String(ch, start, length);
        }

    }

}
