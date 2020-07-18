package art.soft.model;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public abstract class ColorFace extends Face {

    protected Color color;

    @Override
    public void read(DataInputStream is) throws IOException {
        color = new Color(is.readInt(), true);
    }
}
