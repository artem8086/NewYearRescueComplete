package art.soft.objsData;

import art.soft.Loader;
import art.soft.objects.DynamicObj;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class Dynamic extends Static {

    public int min_damage;
    public float damageX_reduct = 1f;
    public float damageY_reduct = 1f;

    public int max_speedX; // Максимальная скорость обьекта по оси X
    public int max_speedY; // Максимальная скорость обьекта по оси Y
    public float mass = 1; // Масса обьекта
    public int coll_mask;

    public Dynamic() {
        typeObj = DynamicObj.class;
    }

    // Json data
    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        super.writeData(dos);
        dos.writeShort(coll_mask);
        dos.writeShort(min_damage);
        dos.writeShort(max_speedX);
        dos.writeShort(max_speedY);
        dos.writeFloat(mass);
        dos.writeFloat(damageX_reduct);
        dos.writeFloat(damageY_reduct);
    }
    // Json end

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        super.readData(dis);
        coll_mask = dis.readUnsignedShort();
        min_damage = dis.readUnsignedShort();
        max_speedX = dis.readUnsignedShort();
        max_speedY = dis.readUnsignedShort();
        mass = dis.readFloat();
        damageX_reduct = dis.readFloat();
        damageY_reduct = dis.readFloat();
    }
}
