package art.soft.objects;

import art.soft.Loader;
import art.soft.level.Layer;
import art.soft.objsData.Bullet;
import art.soft.objsData.ObjData;

/**
 *
 * @author Артём Святоха
 */
public class BulletObj extends StaticObj {

    public int range;

    @Override
    public void init(ObjData data, ObjData drop, int flipMask) {
        super.init(data, drop, flipMask);
        range = ((Bullet) data).range;
    }

    @Override
    public boolean act(Layer layer) {
        boolean del = super.act(layer);
        if (range == 0) return true;
        if (range > 0) range --;
        //
        Bullet bullet = (Bullet) data;
        x += isFlipX() ? - bullet.velX : bullet.velX;
        y += isFlipY() ? - bullet.velY : bullet.velY;
        StaticObj obj = (StaticObj) layer.objs;
        int xt = getX();
        int yt = getY();
        int w = data.width;
        int h = data.height;
        while (obj != null) {
            if (obj != this) {
                if ((obj.getColision() & bullet.coll_mask) != 0 && obj.contain(xt, yt, w, h)) {
                    obj.addForce(isFlipX() ? - bullet.forceX : bullet.forceX,
                                 isFlipY() ? - bullet.forceY : bullet.forceY);
                    obj.addHP(bullet.damage);
                    return true;
                }
            }
            obj = (StaticObj) obj.next;
        }
        return del;
    }
}
