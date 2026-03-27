import java.awt.image.BufferedImage;

public class Steganography {
    public static BufferedImage hideFileInImage(BufferedImage image, byte[] fileBytes) {
        int width = image.getWidth();
        int height = image.getHeight();
        int byteIndex = 0;
        int bitIndex = 0;

        outer:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int blue = rgb & 0xff;

                if (byteIndex < fileBytes.length) {
                    int bit = (fileBytes[byteIndex] >> (7 - bitIndex)) & 1;
                    blue = (blue & 0xFE) | bit;

                    rgb = (rgb & 0xFFFFFF00) | blue;
                    image.setRGB(x, y, rgb);

                    bitIndex++;
                    if (bitIndex == 8) {
                        bitIndex = 0;
                        byteIndex++;
                    }
                } else {
                    break outer;
                }
            }
        }

        return image;
    }

    public static byte[] extractFileFromImage(BufferedImage image, int fileLength) {
        int width = image.getWidth();
        int height = image.getHeight();
        byte[] fileBytes = new byte[fileLength];
        int byteIndex = 0;
        int bitIndex = 0;

        outer:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = image.getRGB(x, y);
                int blue = rgb & 0xff;

                fileBytes[byteIndex] = (byte) ((fileBytes[byteIndex] << 1) | (blue & 1));
                bitIndex++;

                if (bitIndex == 8) {
                    bitIndex = 0;
                    byteIndex++;
                    if (byteIndex == fileLength) break outer;
                }
            }
        }

        return fileBytes;
    }
}