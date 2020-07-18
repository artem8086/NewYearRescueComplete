package art.soft.model;

import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class DrawDot extends ColorFace {

    private int vert;

    @Override
    public void read(DataInputStream is) throws IOException {
        super.read(is);
        //
        vert = is.readUnsignedShort();
    }

    @Override
    public void draw(ModelAnim anim, Graphics g, boolean flipX, boolean flipY) {
        ModelSet model = anim.getData();
        g.setColor(color);
        g.fillRect(model.xVerts[vert], model.yVerts[vert], 1, 1);
    }
}
