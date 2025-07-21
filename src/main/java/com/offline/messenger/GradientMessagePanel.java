package com.offline.messenger;

import javax.swing.*;
import java.awt.*;

public class GradientMessagePanel extends JPanel {
    
    public GradientMessagePanel() {
        setOpaque(false); // allow background gradient to show
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS)); // or whatever layout you need
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create(); // clone Graphics to avoid side-effects

         GradientPaint gp = new GradientPaint(
            0, 0, new Color(230, 230, 250), //lavender
            0, getHeight(), new Color(150, 240, 255)  // Light Aqua Sky
        );

        g2d.setPaint(gp);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.dispose();

        super.paintComponent(g); // draw children (chat bubbles)
    }
}
