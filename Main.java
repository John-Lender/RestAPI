package restApi;

import org.apache.tomcat.util.json.ParseException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;


@ComponentScan
@EnableAutoConfiguration
public class Main {
    public static  String url;
    public static  String user;
    public static  String password ;
    public static  String sms_login ;
    public static  String sms_password ;
    public static void main(String[] args) {

        Database.Connect();
        SpringApplication.run(Main.class, args);
        try {
            JSONParser parser = new JSONParser();

            //Use JSONObject for simple JSON and JSONArray for array of JSON.
            JSONObject data = (JSONObject) parser.parse(
                    new FileReader("C:/Users/Andrey/Desktop/RestAPI/data"));//path to the JSON file.
            String json = data.toJSONString();
            url = (String) data.get("url");
            user = (String) data.get("user");
            password = (String) data.get("password");
            sms_login = (String) data.get("sms_login");
            sms_password = (String) data.get("sms_password");
        } catch (org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        /*
        Smsc sms= new Smsc();

        //String[] ret = sms.send_sms("79841478924", "Hi nice dick", 1, "", "", 0, "", "");
        //String[] ret = sms.get_status(12345, "79996144620", 0);
        String balance = sms.get_balance();
        System.out.println(balance);
        */
    }
}
