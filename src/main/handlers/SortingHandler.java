package main.handlers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import main.controllers.ProgressDialog;
import main.other.FileOperation;
import main.other.Rule;
import main.xmltemplates.XmlData;

import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Forrest Jones on 6/7/2016.
 */
public class SortingHandler {

    private final Pattern yearPattern = Pattern.compile(" \\b(19|20)\\d{2}\\b ");

    private XmlData xmlData;
    private List<Rule> rules;

    private ObservableList<FileOperation> fileList;
    public ObservableList<FileOperation> getFileList() { return fileList; }

    private Date date;


    // View options
    public boolean hideUnsortedFiles = false;

    public SortingHandler(XmlData xmlData) {
        this.xmlData = xmlData;

        fileList = FXCollections.observableArrayList();
        rules = xmlData.getRules();
    }

    public void setDateFilter(Date date) {
        this.date = date;
    }

    public void refresh() {
        System.out.println("refreshing...");
        if (!isSettingsComplete())
            return;

        fileList.clear();
        rules = xmlData.getRules();

        for (File file : new File(xmlData.getUnsortedDir()).listFiles()) {
            FileOperation o = new FileOperation(file.getName(), getDestination(file.getName()), new Date(file.lastModified()));
            checkIfDisabled(o);
            if (!hideUnsortedFiles || (hideUnsortedFiles && !o.isDisabled())) {
                fileList.add(o);
            }
        }
    }

    public void checkIfDisabled(FileOperation o) {
        o.setDisabled(false);
        if (date != null) {
            o.setDisabled(o.getLastModified().after(date));
        }
        if (o.getNewPath().isEmpty())
            o.setDisabled(true);
    }

    private Task createMoveFilesTask() {
        return new Task() {
            @Override
            protected Object call() throws Exception {
                int totalAmount = getMovedFiledCount();
                int currentAmount = 1;
                int currentErrors = 0;
                int currentSuccess = 0;
                for (FileOperation fileOperation : getFileList()) {
                    if (!fileOperation.getNewPath().isEmpty() && !fileOperation.isDisabled()) {
                        File oldFile = new File(xmlData.getUnsortedDir() + File.separator + fileOperation.getPreviousPath());
                        File newFile = new File(fileOperation.getNewPath() + File.separator + oldFile.getName());
                        File newFilePath = new File(fileOperation.getNewPath() + File.separator);
                        if (!newFilePath.exists()) {
                            System.out.println("Path not found. Creating");
                            if (newFilePath.mkdirs())
                                System.out.println("Path created successfully");
                            else {
                                System.out.println("Path not created. Aborting");
                                currentErrors++;
                                break;
                            }
                        }

                        try {
                            System.out.println("moving " + oldFile.toString() + " to " + newFile.toString());
                            Files.move(oldFile.toPath(), newFile.toPath());
//                            Thread.sleep(500);
                            currentSuccess++;
                        } catch (Exception e) {
                            currentErrors++;
                            e.printStackTrace();
                        }

                        updateMessage("Moving " + oldFile.getName() + "|" + currentAmount + "/" + totalAmount + "|" + currentSuccess + "|" + currentErrors);
                        updateProgress(currentAmount, totalAmount);
                        currentAmount++;
                    }
                }
                return true;
            }
        };
    }


    public void moveFiles() {
        ProgressDialog progressDialog;
        progressDialog = new ProgressDialog(null, createMoveFilesTask());
        progressDialog.showAndWait();

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Files Moved");
        alert.setHeaderText("Successfully moved " + progressDialog.successes + " file(s). " + progressDialog.errors + " error(s).");
        alert.showAndWait();
        refresh();
    }


    public int getMovedFiledCount() {
        int amount = 0;
        for (FileOperation fileOperation : getFileList())
            if (!fileOperation.getNewPath().isEmpty() && !fileOperation.isDisabled())
                amount++;
        return amount;
    }

    public boolean isSettingsComplete() {
        boolean complete = xmlData.getUnsortedDir() != null && xmlData.getSortedDir() != null;
        if (!complete) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Please fill out the fields located in settings.", ButtonType.OK);
            alert.showAndWait();
        }

        return complete;
    }

    private String getDestination(String fileName) {
        Rule matchedRule = null;

        for (String prefix : getPrefixesSorted()) {
            if (fileName.toLowerCase().startsWith(prefix)) {
                for (Rule rule : getRulesWithPrefix(prefix)) {
                    if (fileName.toLowerCase().contains(rule.getKeyword().toLowerCase())) {
                        matchedRule = rule;
                        break;
                    }
                }
            }
        }

        if (matchedRule != null) {
            if (matchedRule.getDateSubfolder()) {
                Matcher m = yearPattern.matcher(fileName);
                if (m.find()) {
                    String year = m.group().replaceAll("\\s", "");
                    if (matchedRule.getDateSuffix() != null && !matchedRule.getDateSuffix().isEmpty())
                        return matchedRule.getOutputFolder() + File.separator + year + " " + matchedRule.getDateSuffix();
                    else
                        return matchedRule.getOutputFolder() + File.separator + year;
                }
            }
            else
                return matchedRule.getOutputFolder();
        }

        return "";
    }

    private ArrayList<String> getPrefixesSorted() {
        ArrayList<String> prefixes = new ArrayList<String>();

        if (rules != null)
            for (Rule rule : rules) {
                if (!prefixes.contains(rule.getPrefix().toLowerCase())) {
                    prefixes.add(rule.getPrefix().toLowerCase());
                }
            }

        sortByStringLength(prefixes);
        return prefixes;
    }

    private ArrayList<Rule> getRulesWithPrefix(String prefix) {
        ArrayList<Rule> rulesWithPrefix = new ArrayList<Rule>();

        for (Rule rule : rules) {
            if (rule.getPrefix().toLowerCase().equals(prefix.toLowerCase())) {
                rulesWithPrefix.add(rule);
            }
        }

        return rulesWithPrefix;
    }

    private void sortByStringLength(ArrayList<String> arrayList) {
        arrayList.sort(new StringLengthComparator());
    }

    private class StringLengthComparator implements Comparator<String> {
        @Override
        public int compare(String o1, String o2) {
            return Integer.compare(o2.length(), o1.length());
        }
    }
}
