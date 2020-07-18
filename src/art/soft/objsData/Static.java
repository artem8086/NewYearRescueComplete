package art.soft.objsData;

import art.soft.objects.StaticObj;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class Static extends Decor {
    public int deathAnim; // анимация смерти

    public int hp = -1; // Immortal
    public float friction; // Трение обьекта
    public int collision;
    public int flip;

    public Static() {
        typeObj = StaticObj.class;
    }

    // Json data
    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        super.writeData(dos);
        dos.writeByte(deathAnim);
        dos.writeByte(flip);
        dos.writeShort(hp == -1 ? 0 : hp);
        dos.writeShort(collision);
        dos.writeFloat(friction);
    }
    // Json end

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        super.readData(dis);
        deathAnim = dis.readUnsignedByte();
        flip = dis.readUnsignedByte();
        hp = dis.readUnsignedShort();
        if (hp == 0) hp = -1;
        collision = dis.readUnsignedShort();
        friction = dis.readFloat();
    }
}
