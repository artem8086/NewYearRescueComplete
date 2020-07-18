package art.soft.objsData;

import art.soft.Loader;
import art.soft.model.ModelSet;
import art.soft.objects.UnitObj;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class UnitData extends ObjData {

    public String setName;

    public ModelSet animData;

    public Dynamic forms[];

    public int inAirStatus = 13;

    public float jumpX, jumpY;

    public int startStatus;

    public UnitData() {
        typeObj = UnitObj.class;
    }

    // Json data
    public String animation;
    
    @Override
    public void loadJson() {
        super.loadJson();
        animData = (ModelSet) Loader.getLoader().loadAnimation(
                        Loader.ANIM_TYPE_MODEL_EXTENDED,
                        animation);
    }

    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        super.writeData(dos);
        dos.writeUTF(setName != null ? setName : "");
        //
        dos.writeUTF(animation);
        //
        dos.writeByte(forms.length);
        for (Dynamic form : forms) {
            form.writeData(dos);
        }
        //
        dos.writeShort(inAirStatus);
        dos.writeShort(startStatus);
        //
        dos.writeFloat(jumpX);
        dos.writeFloat(jumpY);
    }
    // Json end

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        super.readData(dis);
        setName = dis.readUTF();
        if (setName.length() == 0) setName = null;
        //
        animData = (ModelSet) Loader.getLoader().loadAnimation(
                Loader.ANIM_TYPE_MODEL_EXTENDED,
                dis.readUTF());
        //
        int n = dis.readUnsignedByte();
        forms = new Dynamic[n];
        for (int i = 0; i < n; i ++) {
            forms[i] = new Dynamic();
            forms[i].readData(dis);
        }
        //
        inAirStatus = dis.readUnsignedShort();
        startStatus = dis.readUnsignedShort();
        //
        jumpX = dis.readFloat();
        jumpY = dis.readFloat();
    }

    @Override
    public void remove() {
        animData.remove();
    }
}
