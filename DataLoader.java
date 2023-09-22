import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.opencsv.exceptions.CsvValidationException;

public class DataLoader {

    private String filePath;
    private List<String[]> data;

    /**
     * Constructor for the DataLoader class.
     * @param filePath - path to the CSV file to be loaded.
     */
    public DataLoader(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Loads data from the given CSV file.
     * @return List of string arrays where each array represents a row from the CSV.
     * @throws IOException if there's an error reading the file.
     * @throws CsvValidationException if there's an error validating the CSV content.
     */
    public List<String[]> loadData() throws IOException, CsvValidationException {
        data = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] nextRecord;
            while ((nextRecord = csvReader.readNext()) != null) {
                data.add(nextRecord);
            }
        }
        return data;
    }

    /**
     * Retrieves the development data set (first 1000 rows).
     * @return List of string arrays representing the development data.
     */
    public List<String[]> getDevData() {
        return data.subList(0, 1000);
    }

    /**
     * Retrieves the training data set (all rows excluding the first 1000).
     * @return List of string arrays representing the training data.
     */
    public List<String[]> getTrainData() {
        return data.subList(1000, data.size());
    }

    public static void main(String[] args) {
        DataLoader dataLoader = new DataLoader("src/main/ressources/train.csv");
        try {
            dataLoader.loadData(); // Load data from the CSV file.
            List<String[]> trainData = dataLoader.getTrainData(); // Retrieve training data.
            List<String[]> devData = dataLoader.getDevData(); // Retrieve development data.

            // Shuffle the training data to ensure randomness.
            Collections.shuffle(trainData);

            // You can now proceed with further processing of the data.
        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }
    }
}
