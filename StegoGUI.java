import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import javax.imageio.ImageIO;

public class StegoGUI {
    private JFrame frame;
    private JTextField imagePathField;
    private JTextField filePathField;
    private JTextField outputPathField;
    private JButton hideButton, extractButton;

    public void createAndShowGUI() {
        frame = new JFrame("Image File Hider (Steganography)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 3, 5, 5));

        imagePathField = new JTextField();
        filePathField = new JTextField();
        outputPathField = new JTextField();

        JButton browseImage = new JButton("Browse");
        browseImage.addActionListener(e -> selectFile(imagePathField));

        JButton browseFile = new JButton("Browse");
        browseFile.addActionListener(e -> selectFile(filePathField));

        JButton browseOutput = new JButton("Browse");
        browseOutput.addActionListener(e -> selectSaveLocation(outputPathField));

        hideButton = new JButton("Hide File in Image");
        extractButton = new JButton("Extract File from Image");

        hideButton.addActionListener(e -> hideFile());
        extractButton.addActionListener(e -> extractFile());

        panel.add(new JLabel("Select Image:"));
        panel.add(imagePathField);
        panel.add(browseImage);

        panel.add(new JLabel("File to Hide:"));
        panel.add(filePathField);
        panel.add(browseFile);

        panel.add(new JLabel("Output Image Location:"));
        panel.add(outputPathField);
        panel.add(browseOutput);

        panel.add(hideButton);
        panel.add(extractButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private void selectFile(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION) {
            textField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void selectSaveLocation(JTextField textField) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Save As");
        if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            textField.setText(chooser.getSelectedFile().getAbsolutePath());
        }
    }

    private void hideFile() {
        try {
            File imageFile = new File(imagePathField.getText());
            File fileToHide = new File(filePathField.getText());
            File outputImageFile = new File(outputPathField.getText());

            BufferedImage image = ImageIO.read(imageFile);
            byte[] fileBytes = Utils.readFileBytes(fileToHide);
            byte[] lengthBytes = Utils.intToBytes(fileBytes.length);
            byte[] finalBytes = Utils.merge(lengthBytes, fileBytes);

            BufferedImage stegoImage = Steganography.hideFileInImage(image, finalBytes);
            ImageIO.write(stegoImage, "png", outputImageFile);

            JOptionPane.showMessageDialog(frame, "File hidden successfully.");

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }

    private void extractFile() {
        try {
            File imageFile = new File(imagePathField.getText());
            BufferedImage image = ImageIO.read(imageFile);

            byte[] extractedLengthBytes = Steganography.extractFileFromImage(image, 4);
            int extractedLength = Utils.bytesToInt(extractedLengthBytes);
            byte[] extractedData = Steganography.extractFileFromImage(image, extractedLength + 4);

            byte[] actualFile = new byte[extractedLength];
            System.arraycopy(extractedData, 4, actualFile, 0, extractedLength);

            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Save Extracted File");
            if (chooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                File saveFile = chooser.getSelectedFile();
                FileOutputStream fos = new FileOutputStream(saveFile);
                fos.write(actualFile);
                fos.close();
                JOptionPane.showMessageDialog(frame, "File extracted successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(frame, "Error: " + e.getMessage());
        }
    }
}
