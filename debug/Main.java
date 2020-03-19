package debug;

import exception.MalformedExpressionException;
import expression.Expression;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import term.Decimal;

public class Main /*extends Application*/ {

    public static void main(String[] args) {
        /*
        {
            String[] exps = {
                    "2 + 7",
                    "2x - 7^42",
                    "-2 + -7",
                    "2 - -7z^(1)",
                    "-2^(1) + 1 + 7",
                    "-9x(4y^5*6)",
                    "2*3-9x(4y^5/6)+7x^2y^(.5)/8^3-1",
                    "xy",
                    "x"
            };

            String[] ans = {
                    "2^(1)+7^(1)",
                    "2^(1)x^(1)-7^(42^(1))",
                    "-2^(1)-7^(1)",
                    "2^(1)+7^(1)z^(1)",
                    "-2^(1)+1+7^(1)",
                    "-9^(1)x^(1)(4^(1)y^(5^(1))*6^(1))",
                    "2^(1)*3^(1)-9^(1)x^(1)(4^(1)y^(5^(1))/6^(1))+7^(1)x^(2^(1))y^(.5^(1))/8^(3^(1))-1",
                    "x^(1)y^(1)",
                    "x^(1)"
            };

            try {
                for (int i = 0; i < exps.length; i++) {
                    String x = Expression.format(exps[i]);
                    System.out.println(x + "\t" + x.equals(ans[i]));
                }
                // System.out.println(Expression.format("2 + 7^(+)"));
            } catch (MalformedExpressionException e) {
                e.printStackTrace();
            }
        }

        {
            String[] terms = {
                    "2x",
                    "x",
                    "-xy",
                    "-3x^2",
                    "3x^2y^(-1)"
            };

            String[] termAns = {
                    "2^(1)x^(1)",
                    "x^(1)",
                    "-1^(1)x^(1)y^(1)",
                    "-3^(1)x^(2)",
                    "3^(1)x^(2)y^(-1)"
            };
            try {
                for (int i = 0; i < terms.length; i++) {
                    String tmp = Expression.format(terms[i]);
                    // System.out.println(tmp + ", " + termAns[i].equals(Decimal.parse(tmp)));
                }
            } catch (MalformedExpressionException e) {
                e.printStackTrace();
            }
        }
        */

        System.out.println("OUT: " + new Expression("2+4").toString());
    }

/*
    public void start(Stage window) {
        window = new Stage();
        Pane root = new Pane();
        Scene scene = new Scene(root);
        window.setScene(scene);
        window.setTitle("Calculator");
        window.setWidth(400);
        window.setHeight(400);

        TextField input = new TextField();
        input.setPrefWidth(window.getWidth());
        scene.setOnKeyPressed(e -> {
            String i = input.getText().replaceAll(" ", "");
            if (e.getCode() == KeyCode.ENTER) {
                new Expression(i);
            }
        });

        root.getChildren().add(input);
        window.show();
    }
    */
}
