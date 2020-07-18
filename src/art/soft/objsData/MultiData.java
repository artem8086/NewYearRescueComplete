package art.soft.objsData;

import art.soft.Loader;
import art.soft.model.ModelSet;
import art.soft.objects.InteractiveObj;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class MultiData extends ObjData {

    public ModelSet animData;

    public Static forms[];

    public int startStatus;

    public MultiData() {
        typeObj = InteractiveObj.class;
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
        //
        dos.writeUTF(animation);
        //
        dos.writeByte(forms.length);
        for (Static form : forms) {
            form.writeData(dos);
        }
        //
        dos.writeShort(startStatus);
    }
    // Json end

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        super.readData(dis);
        animData = (ModelSet) Loader.getLoader().loadAnimation(
                Loader.ANIM_TYPE_MODEL_EXTENDED,
                dis.readUTF());
        //
        int n = dis.readUnsignedByte();
        forms = new Static[n];
        for (int i = 0; i < n; i ++) {
            forms[i] = new Static();
            forms[i].readData(dis);
        }
        //
        startStatus = dis.readUnsignedShort();
    }

    @Override
    public void remove() {
        animData.remove();
        //
        if (forms != null) {
            for (Static form : forms) {
                form.remove();
            }
        }
    }    
}
