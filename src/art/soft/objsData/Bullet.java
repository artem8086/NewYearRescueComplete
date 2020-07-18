package art.soft.objsData;

import art.soft.objects.BulletObj;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class Bullet extends Static {

    public int damage, range = -1;
    public int coll_mask;
    public int velX, velY;
    public float forceX, forceY;

    public Bullet() {
        typeObj = BulletObj.class;
    }

    // Json data
    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        super.writeData(dos);
        dos.writeShort(damage);
        dos.writeShort(range);
        dos.writeShort(coll_mask);
        dos.writeShort(velX);
        dos.writeShort(velY);
        dos.writeFloat(forceX);
        dos.writeFloat(forceY);
    }
    // Json end

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        super.readData(dis);
        damage = dis.readShort();
        range = dis.readUnsignedShort();
        if (hp == 0xFFFF) range = -1;
        coll_mask = dis.readUnsignedShort();
        velX = dis.readShort();
        velY = dis.readShort();
        forceX = dis.readFloat();
        forceY = dis.readFloat();
    }
}
