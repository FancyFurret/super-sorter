package main.controllers;

import com.sun.xml.internal.ws.util.xml.XMLReaderComposite;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.DirectoryChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import main.handlers.XmlHandler;
import main.other.Rule;

import java.io.File;
import java.io.IOException;

/**
 * Created by Forrest Jones on 6/7/2016.
 */
public class EditRuleDialog extends Stage {

    Rule rule;

    @FXML private TextField txtPrefix;
    @FXML private TextField txtKeyword;
    @FXML private TextField txtOutputFolder;
    @FXML private TextField txtDateSuffix;
    @FXML private CheckBox cbDateSubfolder;

    public EditRuleDialog(Parent parent, Rule rule)
    {
        setTitle("Edit Rule");
        initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/EditRuleDialog.fxml"));
        fxmlLoader.setController(this);

        try
        {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        this.rule = rule;

        if (rule.getPrefix() != null)
            txtPrefix.setText(rule.getPrefix());
        if (rule.getKeyword() != null)
            txtKeyword.setText(rule.getKeyword());
        if (rule.getOutputFolder() != null)
            txtOutputFolder.setText(rule.getOutputFolder());

        cbDateSubfolder.setSelected(rule.getDateSubfolder());
        onDateSubfolderChanged();
        if (rule.getDateSuffix() != null)
            txtDateSuffix.setText(rule.getDateSuffix());
    }

    @FXML
    public void onSaveClick() {
        rule.setPrefix(txtPrefix.getText().toUpperCase());
        rule.setKeyword(txtKeyword.getText().toLowerCase());
        rule.setOutputFolder(txtOutputFolder.getText());
        rule.setDateSubfolder(cbDateSubfolder.isSelected());
        if (cbDateSubfolder.isSelected())
            rule.setDateSuffix(txtDateSuffix.getText());
        else
            rule.setDateSuffix("");


        close();
    }
    @FXML
    public void onCancelClick() {

        close();
    }

    public void onOutputFolderBrowse() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        if (txtOutputFolder.getText() != null && !txtOutputFolder.getText().isEmpty() && new File(txtOutputFolder.getText()).exists())
            directoryChooser.setInitialDirectory(new File(txtOutputFolder.getText()));
        else if (XmlHandler.getInstance().getData().getSortedDir() != null && new File(XmlHandler.getInstance().getData().getSortedDir()).exists())
            directoryChooser.setInitialDirectory(new File(XmlHandler.getInstance().getData().getSortedDir()));

        File file = directoryChooser.showDialog(this);

        if (!file.getPath().isEmpty()) {
            txtOutputFolder.setText(file.getPath());
        }
    }

    public void onDateSubfolderChanged() {
        System.out.println("changed");
        txtDateSuffix.setDisable(!cbDateSubfolder.isSelected());
    }
}
