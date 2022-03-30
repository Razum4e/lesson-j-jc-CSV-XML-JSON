import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String[] columns = {"id", "firstName", "lastName", "country", "age"};
        List<Employee> staff;

        staff = parseCSV("data.csv", columns);
        fileWriterGSON("data.gson", staff);

        staff = parseXML("data.xml");
        fileWriterGSON("data2.gson", staff);

        staff = parseJSON("data.gson");
        staff.forEach(System.out::println);


    }

    public static List<Employee> parseCSV(String fileName, String[] columns) {
        List<Employee> line = new ArrayList<>();
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columns);
            CsvToBean<Employee> csvToBean = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();
            line = csvToBean.parse();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static List<Employee> parseXML(String fileName) {
        List<Employee> staff = new ArrayList<>();
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            Node root = document.getDocumentElement();
            NodeList nodeList = root.getChildNodes();
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (Node.ELEMENT_NODE == node.getNodeType()) {
                    Element element = (Element) node;
                    int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = element.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                    staff.add(new Employee(id, firstName, lastName, country, age));
//                    NamedNodeMap map = element.getAttributes();
//                    for (int j = 0; j < map.getLength(); j++) {
//                        String attrName = map.item(j).getNodeName();
//                        String attrValue = map.item(j).getNodeValue();
//                        System.out.println("Атрибут: " + attrName + "; значение: " + attrValue);
//                    }
                }
            }

        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static List<Employee> parseJSON(String fileName) {
        List<Employee> staff = new ArrayList<>();
        JSONParser jsonParser = new JSONParser();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(fileName))) {
            String s;
            StringBuilder sb = new StringBuilder();
            while ((s = bufferedReader.readLine()) != null) {
                sb.append(s);
            }
            JSONArray jsonArray = (JSONArray) jsonParser.parse(String.valueOf(sb));
            Gson gson = new GsonBuilder().create();
            for (Object person : jsonArray) {
                Employee employee = gson.fromJson(String.valueOf(person), Employee.class);
                staff.add(employee);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return staff;
    }

    public static void fileWriterGSON(String fileName, List<Employee> staff) {
        try (FileWriter writer = new FileWriter(fileName)) {
            Gson gson = (new GsonBuilder()).create();
            writer.write(gson.toJson(staff));
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
