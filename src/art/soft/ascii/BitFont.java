package art.soft.ascii;

import art.soft.Loader;
import java.awt.Color;
import java.awt.Graphics;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class BitFont {

    public static int FONT_WIDTH = 8;
    public static int FONT_HEIGHT = 12;

    public static Color COLORS[] = {
        new Color(0x000000), new Color(0x0000AA),
        new Color(0x00AA00), new Color(0x00AAAA),
        new Color(0xAA0000), new Color(0xAA00AA),
        new Color(0xAA5500), new Color(0xAAAAAA),
        new Color(0x555555), new Color(0x5555FF),
        new Color(0x55FF55), new Color(0x55FFFF),
        new Color(0xFF5555), new Color(0xFF55FF),
        new Color(0xFFFF55), new Color(0xFFFFFF)
    };

    private final byte[] data;

    public BitFont(String file) {
        data = new byte[256 * 12];
        try {
            Loader.getLoader().openFile(file).read(data);
        } catch (IOException ex) {}
    }

    public void drawChar(Graphics g, char c, int x, int y, int scale) {
        int bd = (c & 0xFF) * FONT_HEIGHT;
        int width = (FONT_WIDTH * scale) - 1;
        x += FONT_WIDTH * scale;
        for (int y2 = FONT_HEIGHT; y2 > 0; y2--) {
            int bits = data[bd];
            for (int x2 = width; x2 >= 0; x2-=scale) {
                if ((bits & 0x80) != 0) {
                    g.fillRect(x - x2, y, scale, scale);
                }
                bits <<= 1;
            }
            y += scale;
            bd++;
        }
    }

    public void drawString(Graphics g, String str, int x, int y, int scale) {
        char c;
        int x1 = x;
        int w = FONT_WIDTH * scale;
        int h = FONT_HEIGHT * scale;
        for (int i = 0; i < str.length(); i++) {
            c = str.charAt(i);
            drawChar(g, c, x1, y, scale);
            if (c == '\n') {
                x1 = x;
                y += h;
            } else {
                x1 += w;
            }
        }
    }

    public int drawString(Graphics g, byte[] str,
            int offset, int lenght, int x, int y, int scale) {
        char c;
        int x0 = 0, y0 = 0;
        int x1 = x;
        int w = FONT_WIDTH * scale;
        int h = FONT_HEIGHT * scale;
        for (int i = 0; i < lenght; i++) {
            c = (char) str[offset++];
            drawChar(g, c, x1, y, scale);
            if (c == '\n') {
                x1 = x;
                x0 = 0;
                y += h;
                y0 += h;
            } else {
                x1 += w;
                x0 += w;
            }
        }
        return (x0 & 0xFFFF) | (y0 << 16);
    }

    public void drawBytes(Graphics g, byte[] data, int offset,
            int x, int y, int width, int height, int scale) {
        int w = FONT_WIDTH * scale;
        int h = FONT_HEIGHT * scale;
        for (; height>0; height--) {
            for (; width > 0; width --) {
                drawChar(g, (char) data[offset++], x, y, scale);
                x += w;
            }
            y += h;
        }
    }

    public void fillChar(Graphics g, char c,
            int x, int y, int width, int height, int scale) {
        int w = FONT_WIDTH * scale;
        int h = FONT_HEIGHT * scale;
        int wt, tx;
        for (; height > 0; height--) {
            tx = x;
            for (wt = width; wt > 0; wt --) {
                drawChar(g, c, tx, y, scale);
                tx += w;
            }
            y += h;
        }
    }
}
