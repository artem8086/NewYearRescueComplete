package art.soft.model;

import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class FillRect extends DrawLine {

    @Override
    public void draw(ModelAnim anim, Graphics g, boolean flipX, boolean flipY) {
        ModelSet model = anim.getData();
        int indx1 = vert1_2 & 0xFFFF;
        int indx2 = vert1_2 >>> 16;
        int x1 = model.xVerts[indx1];
        int y1 = model.yVerts[indx1];
        int x2 = model.xVerts[indx2];
        int y2 = model.yVerts[indx2];
        //
        if (x1 > x2) {
            int t = x1; x1 = x2; x2 = t;
        }
        if (y1 > y2) {
            int t = y1; y1 = y2; y2 = t;
        }
        g.setColor(color);
        g.fillRect(x1, y1, x2 - x1, y2 - y1);
    }
}
