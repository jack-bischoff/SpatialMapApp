package cmsc420.meeshquest.part1.Errors;

public abstract class Failure extends Throwable{
    public String err;
    public Failure(String err) {
        this.err = err;
    }
}
