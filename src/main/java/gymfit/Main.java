package gymfit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/gymfit/gymfit.fxml"));
        stage.setTitle("GymFit Manager v2.3 · Alta de socio");
        stage.setScene(new Scene(root, 1280, 800));
        stage.setMinWidth(1100);
        stage.setMinHeight(650);
        stage.show();
    }
    public static void main(String[] args) { launch(args); }
}
