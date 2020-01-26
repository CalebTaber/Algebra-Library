package exception;

public class MalformedExpressionException extends Exception {

    public MalformedExpressionException(String malformedExpression, String reason) {
        System.out.println("The expression: " + malformedExpression + " is not a valid expression. " + reason);
    }

}
