package restApi;

import java.util.concurrent.atomic.AtomicLong;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.springframework.validation.DataBinder;
import org.springframework.web.bind.annotation.*;

import javax.xml.crypto.Data;

import static org.apache.logging.log4j.message.MapMessage.MapFormat.JSON;

@RestController
public class Controller {
    Smsc sms= new Smsc();
    private static final String status = "{\"status\": \"%b\", \"Error\": \"%s\"}";
    private final AtomicLong counter = new AtomicLong();
    private  static ObjectMapper mapper = new ObjectMapper();
    /*
    @GetMapping("/greeting")
    public Greeting greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }*/
    /*public Person person(JsonNode json){
        ObjectMapper mapper = new ObjectMapper();
        Person person = mapper.readValue(json, Person.class);
    }*/
/*

    public Person person(String name, String login, String password, String phoneNumber, String email) {
        return new Person(login, password, name, email, phoneNumber);
    }
    */
    @GetMapping("/add_person")
    public JsonNode person(@RequestBody Person person) throws JsonProcessingException {
        int id = Database.addPerson(person);
        String answer = "{\"status\": \"%b\",\"id\": \"%d\" , \"Error\": \"%s\"}";
        if (id > 0){
            return  mapper.readValue(String.format(answer, true, id, null), JsonNode.class);
        }else{
            return mapper.readValue(String.format(status, false, "Something wrong"), JsonNode.class);
        }
    }
    @GetMapping("/authentication")
    public JsonNode authentication(@RequestBody Person person) throws JsonProcessingException {
        //добвать провеку на null и венуть JsonNode
        if (person.getLogin() != null || person.getPassword() != null){
            if (Database.authentication(person.getLogin(),person.getPassword())){
                return  mapper.readValue(String.format(status, true, null), JsonNode.class);
            }else{
                return mapper.readValue(String.format(status, false , "Wrong Login or Password"), JsonNode.class);
            }
        }
        return mapper.readValue(String.format(status, false, "Login or Password is null"), JsonNode.class);
    }
    @GetMapping("/send_sms")
    public JsonNode sendSms(@RequestBody Person person) throws JsonProcessingException {

        int code = Database.sendSms(person.getId());
        String answer = "{\"status\": \"%b\",\"code\": \"%d\" , \"Error\": \"%s\"}";
        if (code > 0){
            sms.send_sms(person.getPhoneNumber(),Integer.toString(code),1, "", "", 0, "", "");
            System.out.println(sms.get_balance());
            return mapper.readValue(String.format(answer, true, code, null), JsonNode.class);

        }else{
           return mapper.readValue(String.format(status, false, "code equal 0"), JsonNode.class);
        }
    }
    @GetMapping("/verify_code")
    public JsonNode verifyCode(@RequestBody Person person ) throws JsonProcessingException {
        int codeDB = Database.verifyCode(person.getId());
        if (codeDB > 0){
            if (codeDB == person.getCode()){
                Database.correctCode(person.getId());
                return  mapper.readValue(String.format(status, true, null), JsonNode.class);
            }else{
                return  mapper.readValue(String.format(status, false, "Code is't correct" ), JsonNode.class);
            }
        }else{
            return mapper.readValue(String.format(status, false, "Database wrong"), JsonNode.class);
        }
    }

    /*
    @GetMapping("/get_data")
    public Person getData(@RequestBody Person person){

    }
    */

}