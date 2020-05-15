package restApi;

import java.sql.*;
import java.util.Random;

public class Database {
    private  static  Random random = new Random();
    public static Connection connection = null;

    public static int addPerson(Person person) {
        String req = "INSERT INTO person(name, login, password, mail, phonenumber) VALUES(?,?,?,?,?) returning id";
        try {
            PreparedStatement st = connection.prepareStatement(req); // Обрабатываем запрос
            st.setString(1, person.getName()); // Проводим замену первого знака вопроса в запросе
            st.setString(2, person.getLogin()); // Проводим замену первого знака вопроса в запросе
            st.setString(3, person.getPassword()); // Проводим замену первого знака вопроса в запросе
            st.setString(4, person.getMail()); // Проводим замену первого знака вопроса в запросе
            st.setString(5, person.getPhoneNumber()); // Проводим замену первого знака вопроса в запросе
            ResultSet rs = st.executeQuery();
            if (rs.next()){
                return rs.getInt("id");
            }else{
                return 0;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e);
            // e.printStackTrace();
        }
        return 0;
    }
    public static Boolean authentication(String login, String password) {
        String req = "select exists(select 1 from person where login=? and password=?);";
        try {
            ResultSet rs = null;
            PreparedStatement st = connection.prepareStatement(req);
            st.setString(1, login);
            st.setString(2, password);
            rs = st.executeQuery();
            if (!rs.next()) throw new Exception("rs.next() return false");
            return rs.getBoolean("exists");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    public Boolean updateInfo(String name, String login, String mail, String phonenumber){
        String req = "update person set name=?, login=?, mail=? where phonenumber=?";
        try {
            ResultSet rs = null;
            PreparedStatement st = connection.prepareStatement(req);
            st.setString(1, name);
            st.setString(2, login);
            st.setString(3, mail);
            st.setString(4, phonenumber);
            rs = st.executeQuery();
            if (!rs.next()) throw new Exception("rs.next() return false");
            return rs.getBoolean("bool");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    //установить время жизни поля в таблице sms_confirming и таймер на нажатие кнопки переотправить смс
    public static int sendSms(int id){
        int code = random.nextInt(8999)+1000;
        String req_f = "insert into sms_confirming(id_person, sms_code) values(?,?)";
        String req_t = "update sms_confirming set sms_code=? where id_person=? ";
        String req1 = "select exists(select 1 from sms_confirming where id_person=?);";
        try {
            ResultSet rs = null;
            PreparedStatement st1 = connection.prepareStatement(req1);
            st1.setInt(1,id);
            rs = st1.executeQuery();
            if (!rs.next()) throw new Exception("rs.next() return false");
            if (rs.getBoolean("exists")){
                PreparedStatement st = connection.prepareStatement(req_t);
                st.setInt(1, code);
                st.setInt(2, id);
                st.executeUpdate();
            }else{
                PreparedStatement st = connection.prepareStatement(req_f);
                st.setInt(1, id);
                st.setInt(2, code);
                st.executeUpdate();
            }
            return code;
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    public static int verifyCode(int id){
        String req = "select sms_code from sms_confirming where id_person=?";
        try {
            ResultSet rs = null;
            PreparedStatement st = connection.prepareStatement(req);
            st.setInt(1, id);
            rs = st.executeQuery();
            if (!rs.next()){
                throw new Exception("rs.next() return false");
            }
            return rs.getInt("sms_code");
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return 0;
    }
    public static void correctCode(int id) {
        String req = "update person set phonenumber_is_confirmed = TRUE where id=?";
        String req1 = "delete from sms_confirming where id_person=?";
        try {
            ResultSet rs = null;
            PreparedStatement st = connection.prepareStatement(req);
            PreparedStatement st1 = connection.prepareStatement(req1);
            st.setInt(1, id);
            st.executeUpdate();
            st1.setInt(1,id);
            st1.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
    public static void Connect() {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return;
        }
        System.out.println("-------- PostgreSQL " + "JDBC Connect ------------");
        try {
            connection =
                    DriverManager.getConnection(
                            Main.url,
                            Main.user,
                            Main.password);

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }
}
