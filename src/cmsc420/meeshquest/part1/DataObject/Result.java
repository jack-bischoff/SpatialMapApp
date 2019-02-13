package cmsc420.meeshquest.part1.DataObject;

import java.util.ArrayList;

public class Result {
    public String err = null;
    public ArrayList<City> payload = null;

    public Result(ArrayList<City> payload, String err) {
        this.payload = payload;
        this.err = err;
    }
    public Result() {}
}
