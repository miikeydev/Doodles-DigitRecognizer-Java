package data;

import org.nd4j.linalg.api.ndarray.INDArray;


import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

public class ImageDisplay {

    public static void displayImage(INDArray imageArray) {
        int width = 28;  // assuming image width is 28
        int height = 28;  // assuming image height is 28

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int grayValue = (int) (imageArray.getDouble(y * width + x) * 255);  // scaling value to 0-255 range
                int rgb = (grayValue << 16) | (grayValue << 8) | grayValue;
                image.setRGB(x, y, rgb);
            }
        }

        // Scale the image by a factor of 10
        Image scaledImage = image.getScaledInstance(width * 20, height * 20, Image.SCALE_DEFAULT);

        ImageIcon icon = new ImageIcon(scaledImage);
        JFrame frame = new JFrame();
        JLabel label = new JLabel(icon);
        frame.setLayout(new FlowLayout());
        frame.setSize(width * 20 + 10, height * 20 + 10);  // adjust the frame size to fit the scaled image
        frame.add(label);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        List<Image_> images = new DataReader().readData("data/train.csv");
        List<Image_> trainData = new ArrayList<>(images.subList(1000, images.size()));
        trainData.forEach(Image_::normalize);

        INDArray[] data = DataConverter.convertToINDArrays(trainData);
        INDArray imageData = data[0];
        INDArray labels = data[1];

        // Display original images
        for (int i = 0; i < 5; i++) {
            INDArray singleImage = imageData.getRow(i);
            displayImage(singleImage);
        }

        // Augment the images
        INDArray augmentedImageData = DataCreator.augmentData(imageData);



        // Display augmented images
        for (int i = 0; i < 5; i++) {
            INDArray singleImage = augmentedImageData.getRow(i);
            displayImage(singleImage);
        }
    }

}
