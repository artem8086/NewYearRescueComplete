package art.soft.level;

import art.soft.Loader;
import art.soft.objsData.ObjData;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class objPos {

    public static final int FLIP_X_MASK = 1;
    public static final int FLIP_Y_MASK = 2;

    public int x, y;
    public int status;

    public ObjData data, drop;

    public objPos next;

    // Json data
    public String dropName;

    public String name;

    public void loadJson() {
        Loader loader = Loader.getLoader();
        data = loader.engine.getObj(name);
        if (dropName != null) {
            drop = loader.engine.getObj(dropName);
        }
    }

    public void writeObjPos(DataOutputStream dos) throws IOException {
        gameEngine engine = Loader.getLoader().engine;
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeShort(engine.getIndex(name));
        dos.writeShort(engine.getIndex(dropName));
        name = dropName = null;
    }
    // Json end

    public void readObjPos(DataInputStream dis) throws IOException {
        gameEngine engine = Loader.getLoader().engine;
        x = dis.readInt();
        y = dis.readInt();
        data = engine.getObj(dis.readUnsignedShort());
        drop = engine.getObj(dis.readUnsignedShort());
    }

    public LayerObj addObj(Layer layer) {
        return data.createObj(layer, x, y, status & 3, drop);
    }
}
