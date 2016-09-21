package main.controllers;

import com.sun.javafx.scene.control.skin.TableViewSkinBase;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.Main;
import main.handlers.XmlHandler;
import main.other.Rule;
import main.xmltemplates.XmlData;

import java.io.IOException;
import java.util.Comparator;

/**
 * Created by Forrest Jones on 6/7/2016.
 */
public class RulesDialog extends Stage {
    XmlData xmlData;

    @FXML TableView tableView;
    @FXML TableColumn colPrefix;
    @FXML TableColumn colIncludedKeywords;
    @FXML TableColumn colExcludedKeywords;
    @FXML TableColumn colOutputFolder;
    @FXML TableColumn colDateSubfolder;

    private ObservableList<Rule> observableRules;

    public RulesDialog(Parent parent, XmlData xmlData)
    {
        this.xmlData = xmlData;
        setTitle("Rules Editor");
        initModality(Modality.APPLICATION_MODAL);

        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("ui/RulesDialog.fxml"));
        fxmlLoader.setController(this);

        try
        {
            setScene(new Scene((Parent) fxmlLoader.load()));
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        observableRules = FXCollections.observableArrayList();
        if (xmlData.getRules() != null)
            observableRules.setAll(xmlData.getRules());
        colPrefix.setCellValueFactory(new PropertyValueFactory<Rule, String>("prefix"));
        colIncludedKeywords.setCellValueFactory(new PropertyValueFactory<Rule, String>("includedKeywords"));
        colExcludedKeywords.setCellValueFactory(new PropertyValueFactory<Rule, String>("excludedKeywords"));
        colOutputFolder.setCellValueFactory(new PropertyValueFactory<Rule, String>("outputFolder"));
        colDateSubfolder.setCellValueFactory(new PropertyValueFactory<Rule, Boolean>("dateSubfolder"));

        colDateSubfolder.setCellFactory(column -> new TableCell<Rule, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty) {
                    if (item) {
                        if (((Rule)getTableRow().getItem()) != null &&
                                ((Rule)getTableRow().getItem()).getDateSuffix() != null &&
                                !((Rule)getTableRow().getItem()).getDateSuffix().isEmpty())
                            setText("YYYY " + ((Rule)getTableRow().getItem()).getDateSuffix());
                        else
                            setText("YYYY");
                    }
                    else
                        setText("");
                }
            }
        });


        tableView.setItems(observableRules);

        colOutputFolder.prefWidthProperty().bind(
                tableView.widthProperty()
                        .subtract(colPrefix.widthProperty())
                        .subtract(colIncludedKeywords.widthProperty())
                        .subtract(colExcludedKeywords.widthProperty())
                        .subtract(colDateSubfolder.widthProperty())
                        .subtract(2)
        );

        tableView.setRowFactory( table -> {
            TableRow<Rule> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (! row.isEmpty()) ) {
                    editRule(row.getItem());
                }
            });
            return row ;
        });
    }

    private void editRule(Rule rule) {
        EditRuleDialog editRuleDialog = new EditRuleDialog(null, rule);
        editRuleDialog.showAndWait();
        if (rule.getPrefix() == null || rule.getPrefix().isEmpty()  ||
                rule.getIncludedKeywords() == null || rule.getIncludedKeywords().isEmpty() ||
                rule.getExcludedKeywords() == null ||
                rule.getOutputFolder() == null || rule.getOutputFolder().isEmpty())
            observableRules.remove(rule);

        sortByPrefix();

        tableView.getColumns().clear();
        tableView.getColumns().addAll(colPrefix, colIncludedKeywords, colExcludedKeywords, colDateSubfolder, colOutputFolder);
    }

    @FXML
    void onNewRuleClick() {
        Rule rule = new Rule();
        observableRules.add(rule);
        editRule(rule);
    }

    @FXML
    void onEditRuleClick() {
        editRule((Rule)tableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onCopyRuleClick() {
        Rule selectedRule = (Rule)tableView.getSelectionModel().getSelectedItem();
        Rule rule = new Rule(selectedRule.getPrefix(), selectedRule.getIncludedKeywords(), selectedRule.getExcludedKeywords(), selectedRule.getOutputFolder(), selectedRule.getDateSubfolder(), selectedRule.getDateSuffix());
        observableRules.add(rule);
        editRule(rule);
    }


    @FXML
    void onDeleteRuleClick() {
        observableRules.remove(tableView.getSelectionModel().getSelectedItem());
    }

    @FXML
    void onSaveClick() {
        sortByPrefix();
        xmlData.setRules(tableView.getItems());
        XmlHandler.getInstance().saveSettings();
        close();
    }
    @FXML
    void onCancelClick() {
        close();
    }

    private void sortByPrefix() {
        observableRules.sort(new AlphabeticalPrefixSort());
    }

    private class AlphabeticalPrefixSort implements Comparator<Rule> {
        @Override
        public int compare(Rule o1, Rule o2) {
            return o1.getPrefix().compareTo(o2.getPrefix());
        }
    }
}
