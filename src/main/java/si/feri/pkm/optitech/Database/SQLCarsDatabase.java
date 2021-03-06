package si.feri.pkm.optitech.Database;

import org.json.JSONObject;
import si.feri.pkm.optitech.Entity.Vehicle;

import java.sql.*;
import java.util.ArrayList;

import static si.feri.pkm.optitech.Database.SQLDatabaseConnection.connectionUrl;

public class SQLCarsDatabase {

    //Function that returns generates and returns every vehicle in database.
    public static ArrayList<Vehicle> getInsertedVehicles() {
        ResultSet resultSet;
        ArrayList<Vehicle> vehicles = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {

            String selectSql = "select Vehicles.carModelId, carMakerId,carModel,vehicleSubtypeId,countryID,carMaker,vehicleId,vin,vehicleTitle,regNumber,carModelYear,fuelTypeId,drivenWheelsId,engineSize,enginePower,dateRegStart,dateRegEnd from (select prvi.carModelId, carMakerId, carModel, vehicleSubtypeId, countryID, carMaker  from (select carModel, vehicleSubtypeId, carMakerId, carModelId from optitech.reg.carModels where carModelId IN (select carModelId from OptiTech.biz.Vehicles Where vehicleId IN  (select distinct vehicleId from optitech.tlm.DriveData))) as prvi left join (select carModelId,countryId,carMaker from optitech.reg.carModels LEFT JOIN optitech.reg.CarMakers on optitech.reg.CarMakers.carMakerId = optitech.reg.CarModels.carMakerId) as drugi on prvi.carModelId = drugi.carModelId) AS tabela LEFT join  OptiTech.biz.Vehicles  ON tabela.carModelId = optitech.biz.vehicles.carModelId WHERE engineSize != 0 AND countryID != 'XY' AND vehicleId != 1357;";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                Vehicle vehicle = null;
                vehicle = createVehicleFromDb(resultSet);
                vehicles.add(vehicle);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicles;
    }

    //Function which returns selected vehicle by ID.
    public static Vehicle getSelectedVehicle(int carID) {
        Vehicle vehicle = null;
        ResultSet resultSet;
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {

            String selectSql = "SELECT Vehicles.carModelId, carMakerId,carModel,vehicleSubtypeId,countryID,carMaker,vehicleId,vin,vehicleTitle,regNumber,carModelYear,fuelTypeId,drivenWheelsId,engineSize,enginePower,dateRegStart,dateRegEnd from (select prvi.carModelId, carMakerId, carModel, vehicleSubtypeId, countryID, carMaker  from (select carModel, vehicleSubtypeId, carMakerId, carModelId from optitech.reg.carModels where carModelId IN (select carModelId from OptiTech.biz.Vehicles Where vehicleId IN  (select distinct vehicleId from optitech.tlm.DriveData))) as prvi left join (select carModelId,countryId,carMaker from optitech.reg.carModels LEFT JOIN optitech.reg.CarMakers on optitech.reg.CarMakers.carMakerId = optitech.reg.CarModels.carMakerId) as drugi on prvi.carModelId = drugi.carModelId) AS tabela LEFT join  OptiTech.biz.Vehicles  ON tabela.carModelId = optitech.biz.vehicles.carModelId WHERE vehicleId =" + carID + ";";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                vehicle = createVehicleFromDb(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return vehicle;
    }

    // Function, which is called from getSelectedVehicle and getInsertedVehicles, which creates entity Vehicle,
    // that contains 17 attributes. Which are visible in si.feri.pkm.optitech.Entity.Vehicle.
    private static Vehicle createVehicleFromDb(ResultSet resultSet) {

        Vehicle vehicle = null;

        try {
            vehicle = new Vehicle(resultSet.getLong(1),
                    resultSet.getLong(2),
                    resultSet.getString(3),
                    resultSet.getInt(4),
                    resultSet.getString(5),
                    resultSet.getString(6),
                    resultSet.getInt(7),
                    resultSet.getString(8),
                    resultSet.getString(9),
                    resultSet.getString(10),
                    resultSet.getInt(11),
                    resultSet.getInt(12),
                    resultSet.getInt(13),
                    resultSet.getInt(14),
                    resultSet.getInt(15),
                    resultSet.getDate(16),
                    resultSet.getDate(17));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return vehicle;
    }

    //Function which returns max speed, which has ever been driven with the selected car. Filtration VssMax < 200, is because
    // data in the database is not fully correct.
    public static int getMaxSpeedForSelectedCar(int carId) {
        ResultSet resultSet;

        int maxSpeed = -1;
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {
            String selectSql = "select MAX(VssMax) from Optitech.tlm.DriveData where vehicleId=" + carId + " AND VssMax < 200;";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                maxSpeed = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maxSpeed;
    }


    //Function which returns max Rounds Per Minute, which has ever been driven with the selected car. Filtration RpmMax < 8000, is because
    // data in the database is not fully correct.
    public static int getMaxRpmForSelectedCar(int carId) {
        ResultSet resultSet;

        int rpmSpeed = -1;
        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {
            String selectSql = "select MAX(RpmMax) from Optitech.tlm.DriveData where vehicleId=" + carId + " AND RpmMax < 8000;";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                rpmSpeed = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return rpmSpeed;
    }

    public static JSONObject getErrorsOnSelectedCar(int carId, String from, String to) {
        ResultSet resultSet;
        ArrayList<String> descriptions = new ArrayList<String>();
        ArrayList<Integer> codes = new ArrayList<Integer>();
        ArrayList<Date> dates = new ArrayList<Date>();

        try (Connection connection = DriverManager.getConnection(connectionUrl);
             Statement statement = connection.createStatement()) {
            String selectSql = "SELECT Convert(varchar(11), dateMsg, 23) AS dateMsg, optitech.tlm.DtcInfo.dtc, DtcCode, dtcDescription FROM optitech.tlm.dtcinfo LEFT JOIN (SELECT dtcDescription, dtc FROM optitech.reg.DtcCodes) AS prvi ON optitech.tlm.dtcinfo.dtc = prvi.dtc WHERE vehicleId != 0 AND dtcDescription != 'null' AND vehicleId =" + carId + " AND dateMsg > Convert(datetime, \'" + from + "\') AND dateMsg < Convert(datetime, \'" + to + "\') GROUP BY DtcInfo.dtc, Convert(varchar(11), dateMsg, 23), DtcCode, DtcInfo.dtc, dtcDescription ORDER BY dateMsg DESC";
            resultSet = statement.executeQuery(selectSql);

            while (resultSet.next()) {
                dates.add(resultSet.getDate(1));
                codes.add(resultSet.getInt(3));
                descriptions.add(resultSet.getString(4));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JSONObject json = new JSONObject();
        json.put("descriptions", descriptions);
        json.put("codes", codes);
        json.put("dates", dates);

        return json;
    }

}