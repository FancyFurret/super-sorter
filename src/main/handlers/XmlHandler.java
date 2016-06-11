package main.handlers;

import main.xmltemplates.XmlData;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

/**
 * Created by Forrest Jones on 6/7/2016.
 */
public class XmlHandler {
    private XmlData data;
    public XmlData getData() { return data; }

    private String settingsPath = System.getProperty("user.dir") + "\\settings.xml";

    JAXBContext jaxbContext;
    Marshaller jaxbMarshaller;
    Unmarshaller jaxbUnmarshaller;

    private static XmlHandler _instance;
    public static XmlHandler getInstance() { return _instance; }

    public XmlHandler() {
        _instance = this;

        try {
            jaxbContext = JAXBContext.newInstance(XmlData.class);
            jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbUnmarshaller = jaxbContext.createUnmarshaller();
        } catch (JAXBException e) {
            System.out.println("Failed to create marshaller: " + e.getMessage());
        }

        loadSettings();
    }

    public void loadSettings() {
        File file = new File(settingsPath);
        try {
            data = (XmlData)jaxbUnmarshaller.unmarshal(file);
        } catch (JAXBException e) {
            System.out.println("Failed to unmarshal xmlHandler, creating blank xmlHandler." + e.getStackTrace());
            data = new XmlData();
            saveSettings();
        }
    }

    public void saveSettings() {
        try {
            jaxbMarshaller.marshal(data, new File(settingsPath));
        } catch (Exception e) {
            System.out.println("Failed to marshal xmlHandler");
        }
    }
}
