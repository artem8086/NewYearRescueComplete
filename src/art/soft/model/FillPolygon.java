package art.soft.model;

import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class FillPolygon extends ColorFace {

    private final static int[] xPoly = new int[3], yPoly = new int[3];

    short verts[];

    FillPolygon(int vertsNum) {
        verts = new short[vertsNum];
    }

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        //
        for (int i = 0; i < verts.length; i ++) {
            verts[i] = is.readShort();
        }
    }

    @Override
    public void draw(ModelAnim anim, Graphics g, boolean flipX, boolean flipY) {
        g.setColor(color);
        ModelSet model = anim.getData();
        int n = verts.length;
        //
        int indx;
        for (int i = n - 3; i >= 0; i --) {
            indx = (int) verts[i] & 0xFFFF;
            xPoly[0] = model.xVerts[indx];
            yPoly[0] = model.yVerts[indx];
            indx = (int) verts[i + 1] & 0xFFFF;
            xPoly[1] = model.xVerts[indx];
            yPoly[1] = model.yVerts[indx];
            indx = (int) verts[i + 2] & 0xFFFF;
            xPoly[2] = model.xVerts[indx];
            yPoly[2] = model.yVerts[indx];
            g.fillPolygon(xPoly, yPoly, 3);
        }
    }
}
