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

//        Set<Map.Entry<String, String>> entrySet = map.entrySet();
//        Iterator iterator = entrySet.iterator();
//        while (iterator.hasNext()) {
//            Map.Entry<String, String> entry = (Map.Entry<String, String>) iterator.next();
//            System.out.println(entry.getKey() + ":" + entry.getValue());
//        }
        return map;
    }
}
