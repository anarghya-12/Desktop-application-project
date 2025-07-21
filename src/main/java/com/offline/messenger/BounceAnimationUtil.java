package com.offline.messenger;

import java.awt.*;
import java.awt.image.BufferedImage;

public class BounceAnimationUtil {

    // Smooth image scaling with high-quality rendering
    public static Image createScaledImage(Image original, float scale) {
        int w = original.getWidth(null);
        int h = original.getHeight(null);
        int newW = Math.max(1, Math.round(w * scale));
        int newH = Math.max(1, Math.round(h * scale));

        BufferedImage resized = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = resized.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.drawImage(original, 0, 0, newW, newH, null);
        g2d.dispose();
        return resized;
    }

    // Apply transparency smoothly (alpha from 0.0f to 1.0f)
    public static Image applyAlpha(Image img, float alpha) {
        int w = img.getWidth(null);
        int h = img.getHeight(null);

        BufferedImage transparent = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = transparent.createGraphics();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha));

        g2.drawImage(img, 0, 0, null);
        g2.dispose();

        return transparent;
    }
}
