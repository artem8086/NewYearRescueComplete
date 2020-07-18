package art.soft.model;

import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public abstract class Face {

    public int normal;

    public abstract void draw(ModelAnim anim, Graphics g, boolean flipX, boolean flipY);

    public abstract void read(DataInputStream is) throws IOException;
}
