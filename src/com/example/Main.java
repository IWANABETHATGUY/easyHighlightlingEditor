package com.example;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Main implements XmlDocument{
    public static void main(String[] args) {
        Main main = new Main();
        main.parseXML("c_minus_config.xml");
    }


    @Override
    public HashMap<String, String> parseXML(String fileName) {
        HashMap<String, String> map = new HashMap();
        try {
            File inputXml = new File(fileName);
            SAXReader reader = new SAXReader();
            Document document = reader.read(inputXml);
            Element users = document.getRootElement();
            for (Iterator i = users.elementIterator(); i.hasNext();) {
                Element user = (Element) i.next();
                String type, value;
                if (user.attribute("type") != null && user.attribute("value") != null) {
                    map.put(user.attributeValue("type"), user.attributeValue("value"));
                }
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return map;
    }
}
