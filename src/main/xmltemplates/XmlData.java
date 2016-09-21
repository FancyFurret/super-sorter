package main.xmltemplates;

import main.other.Rule;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement( name = "super_sorter" )
public class XmlData {

    @XmlElement( name = "settings" )
    Settings settings = new Settings();

    public String getUnsortedDir() { return settings.unsortedDir; }
    public void setUnsortedDir(String value) { settings.unsortedDir = value; }

    public String getSortedDir() { return settings.sortedDir; }
    public void setSortedDir(String value) { settings.sortedDir = value; }

    private List<Rule> rules;
    public List<Rule> getRules() { return rules; }
    @XmlElementWrapper (name = "rules" )
    @XmlElement( name = "rule" )
    public void setRules(List<Rule> value) { rules = value; }

    public static class Settings {
        @XmlElement( name = "unsorted_directory" )
        private String unsortedDir;

        @XmlElement( name = "sorted_directory" )
        private String sortedDir;

    }
}
