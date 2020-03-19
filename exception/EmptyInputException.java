package exception;

public class EmptyInputException extends Exception {

    public EmptyInputException() {
        System.out.println("An empty String is not an acceptable input to this method");
    }

}
