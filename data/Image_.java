package data;

public class Image_ {
    private double[][] data;
    private int label;

    public double[][] getData() {
        return data;
    }

    public int getLabel() {
        return label;
    }

    public Image_(double[][] data, int label) {
        this.data = data;
        this.label = label;
    }

    public String toString() {
        String s = label + ", \n";
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[0].length; j++) {
                s += data[i][j] + ", ";
            }
            s += "\n";
        }

        return s;
    }

    public void normalize() {
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                data[i][j] /= 255.0;
            }
        }
    }
}


