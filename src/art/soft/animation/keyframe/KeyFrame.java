package art.soft.animation.keyframe;

import java.awt.Graphics;
import java.awt.Image;

/**
 *
 * @author Артём Святоха
 */
public class KeyFrame {

    public int x1, y1, x2, y2;
    public int centerX, centerY;
    public int width, height;

    public void draw(Graphics g, Image im, int x, int y) {
        x -= centerX;
        y -= height - centerY;
        g.drawImage(im, x, y, x + width, y + height, x1, y1, x2, y2, null);
    }
    
    public void draw(Graphics g, Image im, int x, int y, boolean flipX, boolean flipY) {
        x -= flipX ? - centerX : centerX;
        y -= flipY ? centerY - height : height - centerY;
        g.drawImage(im, x, y, x + (flipX ? - width : width),
                y + (flipY ? - height : height), x1, y1, x2, y2, null);
    }
}
