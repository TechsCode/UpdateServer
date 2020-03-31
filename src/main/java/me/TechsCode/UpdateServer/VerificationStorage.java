package me.TechsCode.UpdateServer;

import java.sql.*;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class VerificationStorage {

    private Config.MySQLCredentials mySQLCredentials;
    private HashMap<String, String> verifications;

    public VerificationStorage(Config.MySQLCredentials mySQLCredentials) {
        this.mySQLCredentials = mySQLCredentials;
        this.verifications = retrieveVerifications();

        new Thread(){
            @Override
            public void run() {
                while (true){
                    try {
                        sleep(TimeUnit.MINUTES.toMillis(5));

                        verifications = retrieveVerifications();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    public HashMap<String, String> retrieveVerifications() {
        String connectString = "jdbc:mysql://" + mySQLCredentials.getHost() + ":" + mySQLCredentials.getPort() + "/" + mySQLCredentials.getDatabase() + "?useSSL=false&characterEncoding=utf-8";

        HashMap<String, String> ret = new HashMap<>();

        try {
            Connection connection = DriverManager.getConnection(connectString, mySQLCredentials.getUsername(), mySQLCredentials.getPassword());
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Verifications;");
            ResultSet rs = preparedStatement.executeQuery();

            while (rs.next()){
                ret.put(rs.getString("discordid"), rs.getString("userid"));
            }

            rs.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ret;
    }

    public String getSpigotUserId(String discordId){
        return verifications.getOrDefault(discordId, null);
    }
}
