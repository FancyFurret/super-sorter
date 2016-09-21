package main.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import main.handlers.XmlHandler;

import java.io.File;
import java.io.IOException;

/**
 * Created by Forrest Jones on 6/7/2016.
 */
public class SettingsDialog extends Stage{

    main.xmltemplates.XmlData xmlData;

    @FXML private TextField txtUnsortedDir;
    @FXML private TextField txtSortedDir;

    public SettingsDialog(Parent parent, main.xmltemplates.XmlData xmlData)
    {
        this.xmlData = xmlData;
        setTitle("Settings");
        initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/SettingsDialog.fxml"));
        fxmlLoader.setController(this);

        try {
            setScene(new Scene(fxmlLoader.load()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        txtUnsortedDir.setText(xmlData.getUnsortedDir());
        txtSortedDir.setText(xmlData.getSortedDir());
    }

    @FXML
    void onSaveClick() {
        xmlData.setUnsortedDir(txtUnsortedDir.getText());
        xmlData.setSortedDir(txtSortedDir.getText());
        XmlHandler.getInstance().saveSettings();
        close();
    }
    @FXML
    void onCancelClick() {

        close();
    }

    @FXML
    void onUnsortedDirBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(this);


        if (!file.getPath().isEmpty()) {
            txtUnsortedDir.setText(file.getPath());
        }
    }

    @FXML
    void onSortedDirBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        File file = directoryChooser.showDialog(this);

        if (!file.getPath().isEmpty()) {
            txtSortedDir.setText(file.getPath());
        }
    }
}
