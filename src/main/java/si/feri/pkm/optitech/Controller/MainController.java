package si.feri.pkm.optitech.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import si.feri.pkm.optitech.Database.SQLCarImage;
import si.feri.pkm.optitech.Database.SQLCarsDatabase;
import si.feri.pkm.optitech.Database.SQLDrive;
import si.feri.pkm.optitech.Database.SQLFuelType;
import si.feri.pkm.optitech.Entity.Drive;
import si.feri.pkm.optitech.Entity.FuelType;
import si.feri.pkm.optitech.Entity.Vehicle;

import java.text.ParseException;
import java.util.ArrayList;

@Controller
public class MainController {

    @RequestMapping(value = {"/", "/index"}, method = RequestMethod.GET)
    public String index(Model model) {
        return "index";
    }

    @RequestMapping(value = {"/carsList"}, method = RequestMethod.GET)
    public String seznamVozil(Model model, @RequestParam(value = "id", required = false) Integer id) throws ParseException {

        // V vehicles maš hranjene vse avte, ki jih dobim nazaj tipa Vehicle,
        // pol pa z getterji pa setterji pridobivaj podatke ki jih rabiš za izpis.
        ArrayList<Vehicle> vehicles = SQLCarsDatabase.getInsertedVehicles();

        model.addAttribute("vehicles", vehicles);

        Vehicle vehicle = null;
        String linkSlika = "";
        String fuel = "";
        String drive = "";

        if (id == null) {
            id = 217;
        }

        vehicle = SQLCarsDatabase.getSelectedVehicle(id);

        if (vehicle != null) {
            linkSlika = SQLCarImage.getCarImage(id);
            fuel = loadFuel(vehicle);
            drive = loadDrive(vehicle);
        }

        model.addAttribute("slika", linkSlika);
        model.addAttribute("fuel", fuel);
        model.addAttribute("drive", drive);
        model.addAttribute("vehicle", vehicle);

        return "carsList";
    }

    @RequestMapping(value = {"/carDetails"}, method = RequestMethod.GET)
    public String carDetails(Model model, @RequestParam(value = "id") int id) throws ParseException {

        Vehicle vehicle = SQLCarsDatabase.getSelectedVehicle(id);
        String linkImage = "";
        String fuel = "";
        String drive = "";

        if(vehicle != null){
             linkImage = SQLCarImage.getCarImage(id);
             fuel = loadFuel(vehicle);
             drive = loadDrive(vehicle);
        }

        model.addAttribute("imageURL", linkImage);
        model.addAttribute("vehicle", vehicle);
        return "carDetails";
    }

    public static String loadFuel(Vehicle vehicle) {

        String fuel = "";
        ArrayList<FuelType> fuelTypes = SQLFuelType.getAllFuelTypes();

            for (FuelType f : fuelTypes) {
                if (f.getId() == vehicle.getFuelTypeId()) {
                    fuel = f.getNaziv();
                }
            }
        return fuel;
    }

    public static String loadDrive(Vehicle vehicle){

        String drive = "";
        ArrayList<Drive> drives = SQLDrive.getAllDriveTypes();

            for (Drive d : drives) {
                if (d.getId() == vehicle.getFuelTypeId()) {
                    drive = d.getNaziv();
                }
            }
        return drive;
    }
}