package main.controllers;

import com.sun.org.apache.xpath.internal.operations.String;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.handlers.SortingHandler;
import main.handlers.XmlHandler;
import main.other.FileOperation;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created by Forrest Jones on 6/7/2016.
 */
public class MainWindow implements Initializable {
    @FXML private TableView tableView;
    @FXML private TableColumn colFile;
    @FXML private TableColumn colLastModified;
    @FXML private TableColumn colSortedDirectory;
    @FXML private CheckBox cbDateFilter;
    @FXML private DatePicker dpDateFilter;
    @FXML private CheckMenuItem cbHideUnmovedFiles;
    @FXML private Button btnMoveFiles;


    public XmlHandler xmlHandler;
    public SortingHandler sortingHandler;

    public SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");

    @Override
    public void initialize(URL fxmlFileLocation, ResourceBundle resources) {

        xmlHandler = new XmlHandler();
        sortingHandler = new SortingHandler(xmlHandler.getData());

        colFile.setCellValueFactory(new PropertyValueFactory<FileOperation, String>("previousPath"));
        colSortedDirectory.setCellValueFactory(new PropertyValueFactory<FileOperation, String>("newPath"));
        colLastModified.setCellValueFactory(new PropertyValueFactory<FileOperation, Date>("lastModified"));
        colLastModified.setCellFactory(column -> {
            return new TableCell<FileOperation, Date>() {
                @Override
                protected void updateItem(Date item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setText(null);
                        setStyle("");
                    }
                    else {
                        setText(dateFormat.format(item));
                    }

                }
            };
        });
        tableView.setRowFactory(row -> {
            return new TableRow<FileOperation>() {
                @Override
                protected void updateItem(FileOperation item, boolean empty) {
                    super.updateItem(item, empty);

                    if (item == null || empty) {
                        setStyle("");
                    } else {
                        if (item.isDisabled()) {
                            setStyle("-fx-background-color: #FFD6D6");
                            setStyle("-fx-text-background-color: red");
                        }
                        else
                            setStyle("");
                    }

                }
            };
        });
        tableView.setItems(sortingHandler.getFileList());

        colSortedDirectory.prefWidthProperty().bind(
                tableView.widthProperty()
                        .subtract(colFile.widthProperty())
                        .subtract(2)
        );

        dpDateFilter.setValue(LocalDate.now());
        onDateFilterChanged();
    }

    public void onRefreshClick() {
        sortingHandler.refresh();
        btnMoveFiles.setText("Move " + sortingHandler.getMovedFiledCount() + " file(s)");
        tableView.sort();
    }

    public void onSettingsClick() {
        SettingsDialog settingsDialog = new SettingsDialog(null, xmlHandler.getData());
        settingsDialog.showAndWait();
        onRefreshClick();
    }

    public void onSetupRulesClick() {
        RulesDialog rulesDialog = new RulesDialog(null, xmlHandler.getData());
        rulesDialog.showAndWait();
        onRefreshClick();
    }

    public void onMoveFilesClick() {
        onRefreshClick();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure?", ButtonType.YES, ButtonType.NO);
        alert.setHeaderText("Warning! You are about to permanently move " + sortingHandler.getMovedFiledCount() + " file(s). This cannot be undone.");
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            sortingHandler.moveFiles();
        }
    }

    public void onAboutClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, "© Forrest Jones 2016", ButtonType.OK);
        alert.setTitle("About");
        alert.setHeaderText("Super Sorter");
        alert.showAndWait();
    }

    public void onDateFilterChanged(){
        if (cbDateFilter.isSelected() && dpDateFilter.getValue() != null) {
            sortingHandler.setDateFilter(Date.from(dpDateFilter.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant()));
//            sortingHandler.setDateFilter(new Date());
            dpDateFilter.setDisable(false);
        }
        else {
            sortingHandler.setDateFilter(null);
            dpDateFilter.setDisable(true);
        }
        onRefreshClick();
    }

    public void onHideUnmovedFilesChanged(ActionEvent event) {
        sortingHandler.hideUnsortedFiles = cbHideUnmovedFiles.isSelected();
        onRefreshClick();
    }
}
