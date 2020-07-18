package art.soft.model.extended;

import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class DamageRegion {

    public int x, y, w, h;

    private int damage_mask;

    public float forceX, forceY;

    public void readDmgReg(DataInputStream dis) throws IOException {
        x = dis.readShort();
        y = dis.readShort();
        w = dis.readUnsignedShort();
        h = dis.readUnsignedShort();
        damage_mask = dis.readUnsignedShort();
        damage_mask |= dis.readUnsignedShort() << 16;
        forceX = dis.readFloat();
        forceY = dis.readFloat();
    }

    public int getDamage() {
        return (short) damage_mask;
    }

    public int getMask() {
        return damage_mask >>> 16;
    }
}
