package cmsc420.meeshquest.part1.DataObject;

import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;
import java.util.Collection;

public class Response {
    public Object payload;
     public String status;

    public Response(String status, Object payload) {
        this.status = status;
        this.payload = payload;
    }

}
