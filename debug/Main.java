package debug;

import expression.Expression;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application {

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
            if (e.getCode() == KeyCode.ENTER && isValidInput(i)) {
                new Expression(i);
            }
        });

        root.getChildren().add(input);
        window.show();
    }

    private boolean isValidInput(String input) {
        return true;
    }

}
