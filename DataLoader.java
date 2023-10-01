import com.opencsv.CSVReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.opencsv.exceptions.CsvValidationException;

public class DataLoader {

    private String filePath;
    private List<int[]> data;

    public DataLoader(String filePath) {
        this.filePath = filePath;
    }

    public List<int[]> loadData() throws IOException, CsvValidationException {
        data = new ArrayList<>();
        try (CSVReader csvReader = new CSVReader(new FileReader(filePath))) {
            String[] nextRecord;

            csvReader.readNext(); // Skip the header row

            while ((nextRecord = csvReader.readNext()) != null) {
                int[] intRow = new int[nextRecord.length];
                for (int i = 0; i < nextRecord.length; i++) {
                    intRow[i] = Integer.parseInt(nextRecord[i]);
                }
                data.add(intRow);
            }
        }
        return data;
    }

    public List<int[]> getDevData() {
        return data.subList(0, 1000);
    }

    public List<int[]> getTrainData() {
        return data.subList(1000, data.size());
    }

    public int[] getTrainLabels() {
        List<int[]> trainDataList = getTrainData();
        int[] labels = new int[trainDataList.size()];
        for (int i = 0; i < trainDataList.size(); i++) {
            labels[i] = trainDataList.get(i)[0];
        }
        return labels;
    }

    public int[] getDevLabels() {
        List<int[]> devDataList = getDevData();
        int[] labels = new int[devDataList.size()];
        for (int i = 0; i < devDataList.size(); i++) {
            labels[i] = devDataList.get(i)[0];
        }
        return labels;
    }

    private double[][] convertTo2DArray(List<int[]> dataList) {
        double[][] dataArray = new double[dataList.size()][dataList.get(0).length - 1];
        for (int i = 0; i < dataList.size(); i++) {
            for (int j = 1; j < dataList.get(i).length; j++) {
                dataArray[i][j-1] = dataList.get(i)[j] / 255.0;
            }
        }
        return dataArray;
    }

    public double[][] getTrainDataArray() {
        return convertTo2DArray(getTrainData());
    }

    public double[][] getDevDataArray() {
        return convertTo2DArray(getDevData());
    }

    public static void main(String[] args) {

        DataLoader dataLoader = new DataLoader("src/main/ressources/train.csv");
        try {
            dataLoader.loadData();
            List<int[]> trainData = dataLoader.getTrainData();
            // Get the first image's pixel values
            int[] firstImage = trainData.get(0);

            // Print the pixel values of the first image
            for (int pixel : firstImage) {
                System.out.print(pixel + " ");
            }

            List<int[]> devData = dataLoader.getDevData();
            //Collections.shuffle(trainData);

        } catch (IOException | CsvValidationException e) {
            e.printStackTrace();
        }



    }
}
