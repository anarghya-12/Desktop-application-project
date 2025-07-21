package com.offline.messenger;

import javax.swing.*;
import java.awt.*;

public class ChatBubble extends JPanel {
    private final String text;
    public String messageId;
    private final boolean isSentByMe;
    private static final int MAX_WIDTH = 300;
    private static final int PADDING = 10;

    private ImageIcon loadIcon(String path) {
        java.net.URL iconURL = getClass().getClassLoader().getResource(path);
        if (iconURL != null) {
            ImageIcon icon = new ImageIcon(iconURL);
            Image scaledImage = icon.getImage().getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
        return null;
    }


    public ChatBubble(String text, boolean isSentByMe) {
        this.text = text;
        this.isSentByMe = isSentByMe;
        setOpaque(false);
        
        setMaximumSize(getPreferredSize());

    }
    
    public ChatBubble(String text, boolean isSentByMe, String messageId, String status) {
        this(text, isSentByMe);  // call the original constructor
        this.messageId = messageId;

        JLabel statusLabel = new JLabel(status);
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        statusLabel.setForeground(Color.GRAY);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));

        this.setLayout(new BorderLayout());
        if (isSentByMe) {
            this.add(statusLabel, BorderLayout.SOUTH);
        }

        // Register this bubble's status label for updates
        ChatFrame.statusLabelMap.put(messageId, statusLabel);
        ChatFrame.currentStatusMap.put(messageId, status);
    }

    @Override
    public Dimension getPreferredSize() {
        Font font = new Font("SansSerif", Font.PLAIN, 14);
        FontMetrics fm = getFontMetrics(font);

        int lineHeight = fm.getHeight();
        int width = 0;
        int height = lineHeight;

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line + word + " ";
            int testWidth = fm.stringWidth(testLine);
            if (testWidth > MAX_WIDTH - 2 * PADDING) {
                width = Math.max(width, fm.stringWidth(line.toString()));
                height += lineHeight;
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        width = Math.max(width, fm.stringWidth(line.toString()));

        return new Dimension(width + 2 * PADDING, height + 2 * PADDING);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Font font = new Font("SansSerif", Font.PLAIN, 14);
        g2.setFont(font);
        FontMetrics fm = g2.getFontMetrics();

        int width = getWidth();
        int height = getHeight();
        int arc = 25;

        Color sentColor = new Color(124, 77, 255);      // Deep purple
        Color recvColor = new Color(200, 180, 255);     // Light purple

        // Draw bubble
        g2.setColor(isSentByMe ? sentColor : recvColor);
        g2.fillRoundRect(0, 0, width, height, arc, arc);

        // Draw text
        g2.setColor(isSentByMe ? Color.WHITE : Color.BLACK);

        int x = PADDING;
        int y = PADDING + fm.getAscent();

        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            String testLine = line + word + " ";
            int testWidth = fm.stringWidth(testLine);
            if (testWidth > MAX_WIDTH - 2 * PADDING) {
                g2.drawString(line.toString(), x, y);
                y += fm.getHeight();
                line = new StringBuilder(word + " ");
            } else {
                line.append(word).append(" ");
            }
        }
        g2.drawString(line.toString(), x, y);
        g2.dispose();
    }
}
