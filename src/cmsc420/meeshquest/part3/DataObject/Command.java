package cmsc420.meeshquest.part3.DataObject;

import cmsc420.meeshquest.part3.Xmlable;
import org.w3c.dom.Element;

public class Command implements Xmlable {
    private String name;
    private String id;

    public Command(String name, String id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public Element toXml() {
        Element Command = getBuilder().createElement("command");
        Command.setAttribute("name", name);
        if (!id.isEmpty()) Command.setAttribute("id", id);
        return Command;
    }
}
