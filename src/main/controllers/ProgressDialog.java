package main.controllers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.Main;

import javax.swing.event.HyperlinkEvent;
import java.io.IOException;

/**
 * Created by Forrest Jones on 6/10/2016.
 */
public class ProgressDialog extends Stage {
    @FXML private Label lblCurrentFile;
    @FXML private Label lblProgress;
    @FXML private ProgressBar pbProgressBar;

    public int successes = 0;
    public int errors = 0;

    public ProgressDialog(Parent parent, Task task) {
        setTitle("Moving Files");
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/ProgressDialog.fxml"));
        fxmlLoader.setController(this);

        try {
            setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        pbProgressBar.progressProperty().unbind();
        pbProgressBar.progressProperty().bind(task.progressProperty());
        task.messageProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                lblCurrentFile.setText(newValue.split("\\|")[0]);
                lblProgress.setText(newValue.split("\\|")[1]);
                successes = Integer.parseInt(newValue.split("\\|")[2]);
                errors = Integer.parseInt(newValue.split("\\|")[3]);
            }
        });
        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                close();
            }
        });
        new Thread(task).start();
    }
}
