package art.soft.objsData;

import art.soft.Loader;
import art.soft.animation.AnimSet;
import art.soft.objects.items.Item;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class ItemData extends Dynamic {

    public String setName;
    
    public AnimSet icon;

    public int ammo, iconAnim;

    public ItemData() {
        typeObj = Item.class;
        isItem = true;
    }

    // Json data
    public String iconName;
    
    @Override
    public void loadJson() {
        super.loadJson();
        icon = Loader.getLoader().loadAnimation(iconName);
    }

    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        super.writeData(dos);
        dos.writeUTF(setName != null ? setName : "");
        if (Loader.getLoader().writeAnimation(iconName, dos)) {
            dos.writeByte(iconAnim);
        }
        dos.writeShort(ammo);
    }
    // Json end

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        super.readData(dis);
        String set = dis.readUTF();
        setName = set.length() != 0 ? set : null;
        //
        // Load
        if ((icon = Loader.getLoader().loadAnimation(dis)) != null) {
            iconAnim = dis.readUnsignedByte();
        }
        ammo = dis.readUnsignedShort();
        if (ammo == 0xFFFF) ammo = -1;
    }
}
