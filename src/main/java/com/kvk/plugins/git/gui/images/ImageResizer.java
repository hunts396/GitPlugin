package com.kvk.plugins.git.gui.images;

import java.awt.*;
import java.awt.image.BufferedImage;

public class ImageResizer {
    public static Image resizeImage(Image img, int width, int height){
        BufferedImage resizedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(img, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }
}
