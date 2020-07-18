package art.soft.model;

import art.soft.Loader;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class DrawArc extends DrawLine {

    protected int angleSA;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        //
        angleSA = is.readUnsignedShort();
        angleSA |= is.readUnsignedShort() << 16;
    }

    @Override
    public void draw(ModelAnim anim, Graphics g, boolean flipX, boolean flipY) {
        ModelSet model = anim.getData();
        int indx1 = vert1_2 & 0xFFFF;
        int indx2 = vert1_2 >>> 16;
        g.setColor(color);
        //
        boolean lineAnt = Loader.getLoader().settings.lineAntializing;
        if (lineAnt) {
            ((Graphics2D) g).setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
        }
        //
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
        g.drawArc(x1, y1, x2 - x1, y2 - y1, (short) angleSA, angleSA >> 16);
        //
        if (lineAnt) {
            ((Graphics2D) g).setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }
}
