package com.kvk.plugins.git.gui;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Comparator;

public class ImageComparator implements Comparator<Image> {
    public BufferedImage fromImage(Image img){
        if (img instanceof BufferedImage)
        {
            return (BufferedImage) img;
        }

        BufferedImage bimage =
                new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        return bimage;
    }
    @Override
    public int compare(Image o1, Image o2) {
        BufferedImage img1 = fromImage(o1);
        BufferedImage img2 = fromImage(o2);
        if (img1.getWidth() == img2.getWidth() && img1.getHeight() == img2.getHeight()) {
            for (int x = 0; x < img1.getWidth(); x++) {
                for (int y = 0; y < img1.getHeight(); y++) {
                    int rgb1 = img1.getRGB(x, y);
                    int rgb2 = img2.getRGB(x, y);
                    if (rgb1 != rgb2)
                        return Integer.compare(rgb1, rgb2);
                }
            }
        } else {
            return img1.getWidth() == img2.getWidth() ?
                    Integer.compare(img1.getWidth(), img2.getWidth())
                    :Integer.compare(img1.getHeight(), img2.getHeight());
        }
        return 0;
    }
}
