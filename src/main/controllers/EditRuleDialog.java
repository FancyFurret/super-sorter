package main.controllers;

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
    @FXML private TextField txtIncludedKeywords;
    @FXML private TextField txtExcludedKeywords;
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
        if (rule.getIncludedKeywords() != null)
            txtIncludedKeywords.setText(rule.getIncludedKeywords());
        if (rule.getExcludedKeywords() != null)
            txtExcludedKeywords.setText(rule.getExcludedKeywords());
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
        rule.setIncludedKeywords(txtIncludedKeywords.getText().toLowerCase());
        rule.setExcludedKeywords(txtExcludedKeywords.getText().toLowerCase());
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
