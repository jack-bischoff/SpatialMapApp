package cmsc420.meeshquest.part1;

import java.util.ArrayList;

public class Result {
    String err = null;
    ArrayList<City> payload = null;

    Result(ArrayList<City> payload, String err) {
        this.payload = payload;
        this.err = err;
    }
    Result() {}
}
