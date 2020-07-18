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
public class DrawLine extends ColorFace {

    protected int vert1_2;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        //
        vert1_2 = is.readUnsignedShort();
        vert1_2 |= is.readUnsignedShort() << 16;
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
        g.drawLine(model.xVerts[indx1], model.yVerts[indx1],
                model.xVerts[indx2], model.yVerts[indx2]);
        //
        if (lineAnt) {
            ((Graphics2D) g).setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }
    }
}
