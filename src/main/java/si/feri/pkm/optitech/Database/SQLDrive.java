package si.feri.pkm.optitech.Database;

import si.feri.pkm.optitech.Entity.Drive;
import si.feri.pkm.optitech.Entity.FuelType;

import java.sql.*;
import java.util.ArrayList;

import static si.feri.pkm.optitech.Database.SQLDatabaseConnection.connectionUrl;

public class SQLDrive {
    //Fuction which returns all possible DriveTypes of a car. It creates Entities Drive which is visible in Entity.
    public static ArrayList<Drive> getAllDriveTypes() {
        ResultSet resultSet;
        ArrayList<Drive> drives = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {

            String selectSql = "SELECT * FROM OptiTech.reg.DrivenWheels;";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                Drive d = new Drive(resultSet.getInt(1), resultSet.getString(2));
                drives.add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return drives;
    }

}
