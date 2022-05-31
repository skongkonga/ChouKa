package q3139771198.console.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static q3139771198.console.ChouKa.isDebug;

public class MySQL implements database {
    public static boolean IS_CONVERTING = false;

    private final Logger log = Logger.getLogger("Minecraft.HackNotifies");

    private String file;

    private Connection con;

    Statement st;

    public String mysql_username;
    public String mysql_password;

    public MySQL(String file, String mysql_username, String mysql_password, List<String> enabled, String type) {
        this.file = file;
        this.mysql_password = mysql_password;
        this.mysql_username = mysql_username;
        openConnection(enabled, type);
    }

    public void openConnection(List<String> enabled, String type) {
        try {
            this.con = DriverManager.getConnection("jdbc:mysql://" + this.file, mysql_username, mysql_password);
            this.st = this.con.createStatement();
            this.st.setQueryTimeout(30);
            String cmd = "CREATE TABLE IF NOT EXISTS "+type+" (Id VARCHAR(20) NOT NULL";
            for (String name : enabled){
                cmd += "," + name + " VARCHAR(20) DEFAULT 0";
            }
            cmd += ");";
            this.st.executeUpdate(cmd);
        } catch (SQLException e) {
            this.log.log(Level.SEVERE, "Failed to open MySQL connection", e);
            if (isDebug){
                e.printStackTrace();
            }
        }
    }


    public void closeConnection() {
        try {
            if (this.con != null)
                this.con.close();
        } catch (SQLException e) {
            this.log.log(Level.SEVERE, "Failed to close MySQL connection", e);
            if (isDebug){
                e.printStackTrace();
            }
        }
    }

    public String getCardTimes(String name, String cardname) {//获取卡组抽取次数
        try {
            ResultSet rs = this.st.executeQuery("SELECT * FROM `cishu` WHERE Id='" + name + "';");
            if (rs.next())
                return rs.getString(cardname);
        } catch (Exception e) {
            if (isDebug){
                e.printStackTrace();
            }
        }
        return "0";
    }

    public String getCard(String name, String card) {//获取拥有卡片个数
        try {
            ResultSet rs = this.st.executeQuery("SELECT * FROM `card` WHERE Id='" + name + "';");
            if (rs.next())
                return rs.getString(card);
        } catch (Exception e) {
            if (isDebug){
                e.printStackTrace();
            }
        }
        return "0";
    }

    public boolean checkForAddColumn(List<String> enabled, String type) {
        List<String> needToAdd = new ArrayList<>();
        List<String> allColumn = new ArrayList<>();
        try {
            ResultSet rs = this.st.executeQuery("SELECT * FROM information_schema.columns WHERE table_name = '" + type + "';");
            //for (int i = 2; rs.getString(i) != null; i+=5)
            while (rs.next()) {
                allColumn.add(rs.getString(4));
            }
            if (isDebug) log.info(allColumn.toString());
            for (String name : enabled){
                if (!allColumn.contains(name)){
                    needToAdd.add(name);
                }
            }
            if (isDebug) log.info(needToAdd.toString());
            for (String name : needToAdd){
                String cmd = "ALTER TABLE " + type + " ADD COLUMN " + name + " VARCHAR(20) DEFAULT 0;";
                this.st.executeUpdate(cmd);
            }
            return true;
        } catch (Exception e) {
            if (isDebug){
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean isRegisteredTime(String name) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT * FROM `cishu` WHERE Id='" + name + "';");
            if (rs.next())
                return true;
        } catch (Exception e) {
            if (isDebug){
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    public boolean isRegisteredCard(String name) {
        try {
            ResultSet rs = this.st.executeQuery("SELECT * FROM `card` WHERE Id='" + name + "';");
            if (rs.next())
                return true;
        } catch (Exception e) {
            if (isDebug){
                e.printStackTrace();
            }
            return false;
        }
        return false;
    }

    public boolean updateTime(String Id, String cardname, Integer times) {
        String cmd;
        if (!isRegisteredTime(Id)){
            cmd = "INSERT INTO `cishu` (`Id`," + cardname + ") VALUES('" + Id + "','" + times + "');";
        } else {
            cmd = "UPDATE `cishu` SET " + cardname + " = '" + times + "' WHERE Id = '" + Id + "';";
        }
        try {
            this.st.executeUpdate(cmd);
            return true;
        } catch (SQLException e) {
            if (isDebug){
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean updateCard(String Id, String card, Integer times) {
        String cmd;
        if (!isRegisteredCard(Id)){
            cmd = "INSERT INTO `card` (`Id`," + card + ") VALUES('" + Id + "','" + times + "');";
        } else {
            cmd = "UPDATE `card` SET " + card + " = '" + times + "' WHERE Id = '" + Id + "';";
        }
        try {
            this.st.executeUpdate(cmd);
            return true;
        } catch (SQLException e) {
            if (isDebug){
                e.printStackTrace();
            }
            return false;
        }
    }

    public boolean register(String Id, List<String> enabled, String cardname, Integer times) {
        try {
//            if (!isRegistered(Id)) {
//                String cmd = "INSERT INTO `cishu` (`Id`";
//                for (String name : enabled){
//                    cmd += ",'" + name + "'";
//                }
//                cmd += ") VALUES('" + Id + "'";
//                for (String name : enabled){
//                    cmd += ",'0'";
//                }
//                cmd += ");";
//                this.st.executeUpdate(cmd);
//            }
            String cmd = "INSERT INTO `cishu` (`Id`,'" + cardname + "') VALUES('" + Id + "','" + times + "');";
            this.st.executeUpdate(cmd);
            return true;
        } catch (SQLException e) {
            if (isDebug){
                e.printStackTrace();
            }
            return false;
        }
    }

    public Connection getConnection() {
        return this.con;
    }

}
