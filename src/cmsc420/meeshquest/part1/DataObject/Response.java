package cmsc420.meeshquest.part1.DataObject;

import cmsc420.meeshquest.part1.Xmlable;
import org.w3c.dom.Element;
import java.util.Collection;

public class Response {
    public Object payload;
    public boolean error;

    public Response() {
        this.error = false;
        this.payload = null;
    }

    public Response(Object payload) {
        this.error = false;
        this.payload = payload;
    }

    public Response(boolean error, Object payload) {
        this.error = error;
        this.payload = payload;
    }


}
