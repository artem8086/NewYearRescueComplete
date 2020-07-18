package art.soft.objsData;

import art.soft.Loader;
import art.soft.animation.AnimSet;
import art.soft.objects.Decoration;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class Decor extends ObjData {

    public AnimSet animData;
    public int anim;

    public Decor() {
        typeObj = Decoration.class;
    }

    // Json data
    public String animation;
    
    @Override
    public void loadJson() {
        super.loadJson();
        animData = Loader.getLoader().loadAnimation(animation);
    }

    @Override
    protected void writeData(DataOutputStream dos) throws IOException {
        super.writeData(dos);
        if (Loader.getLoader().writeAnimation(animation, dos)) dos.writeByte(anim);
    }
    // Json end

    @Override
    protected void readData(DataInputStream dis) throws IOException {
        super.readData(dis);
        // Load
        if ((animData = Loader.getLoader().loadAnimation(dis))!= null) {
            anim = dis.readUnsignedByte();
            //loader.game.log("Load decor (" + name + ") anim = " + animName);
        }
    }

    @Override
    public void remove() {
        animData.remove();
    }
}
