package main.other;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created by Forrest Jones on 6/7/2016.
 */

public class Rule {

    private String prefix;
    public String getPrefix() { return prefix; }
    @XmlElement( name = "prefix" )
    public void setPrefix(String value) { prefix = value; }

    private String includedKeywords;
    public String getIncludedKeywords() { return includedKeywords; }
    @XmlElement( name = "included_keywords" )
    public void setIncludedKeywords(String value) { includedKeywords = value; }

    private String excludedKeywords;
    public String getExcludedKeywords() { return excludedKeywords; }
    @XmlElement( name = "excluded_keywords" )
    public void setExcludedKeywords(String value) { excludedKeywords = value; }

    private String outputFolder;
    public String getOutputFolder() { return outputFolder; }
    @XmlElement( name = "output_folder" )
    public void setOutputFolder(String value) { outputFolder = value; }

    private boolean dateSubfolder;
    public boolean getDateSubfolder() { return dateSubfolder; }
    @XmlElement ( name = "date_subfolder" )
    public void setDateSubfolder(boolean value) { dateSubfolder = value; }

    private String dateSuffix;
    public String getDateSuffix() { return dateSuffix; }
    @XmlElement( name = "date_suffix" )
    public void setDateSuffix(String value) { dateSuffix = value; }

    public Rule() {     }
    public Rule(String prefix, String includedKeywords, String excludedKeywords, String outputFolder, boolean dateSubfolder, String dateSuffix) {
        this.prefix = prefix;
        this.includedKeywords = includedKeywords;
        this.excludedKeywords = excludedKeywords;
        this.outputFolder = outputFolder;
        this.dateSubfolder = dateSubfolder;
        this.dateSuffix = dateSuffix;
    }
}
