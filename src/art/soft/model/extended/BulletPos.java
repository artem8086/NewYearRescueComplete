package art.soft.model.extended;

import art.soft.Loader;
import art.soft.level.Layer;
import art.soft.level.LayerObj;
import art.soft.objects.StaticObj;
import art.soft.objsData.ObjData;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class BulletPos {

    public boolean flipY = false;

    public int x, y;

    public ObjData data;

    public void readBulletPos(DataInputStream dis) throws IOException {
        flipY = dis.readBoolean();
        x = dis.readShort();
        y = dis.readShort();
        data = Loader.getLoader().engine.getObj(dis.readUTF());
    }

    public LayerObj addObj(StaticObj owner, Layer layer) {
        Loader loader = Loader.getLoader();
        StaticObj obj = (StaticObj) loader.getObj(data.typeObj);
        if (obj != null) {
            obj.init(data, null, 0);
            obj.setFlipY(flipY);
            obj.setPos(owner.x + (owner.isFlipX() ? - x : x), owner.y + (owner.isFlipY() ? - y : y));
            if (owner.isFlipX()) obj.setFlipX(!obj.isFlipX());
            if (owner.isFlipY()) obj.setFlipY(!obj.isFlipY());
            layer.add(obj);
        } else
            loader.game.log("Cann't create object " + data.typeObj);
        return obj;
    }
}
