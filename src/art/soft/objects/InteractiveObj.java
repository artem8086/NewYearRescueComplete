package art.soft.objects;

import art.soft.Loader;
import art.soft.level.Layer;
import art.soft.model.MeshData;
import art.soft.model.ModelAnim;
import art.soft.model.extended.BulletPos;
import art.soft.model.extended.DamageRegion;
import art.soft.objsData.MultiData;
import art.soft.objsData.ObjData;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class InteractiveObj extends StaticObj {

    protected MultiData mData;

    @Override
    public void init(ObjData data, ObjData drop, int flipMask) {
        mData = (MultiData) data;
        anim = mData.animData.getAnimation();
        anim.setAnimation(mData.startStatus);
        super.init(mData.forms[getCurFrame().getForm()], drop, flipMask);
    }

    public MeshData getCurFrame() {
        return ((ModelAnim) anim).getCurAnim();
    }

    public void setData(MultiData mData) {
        this.mData = mData;
        anim.pool();
        anim = mData.animData.getAnimation();
        setStatus(mData.startStatus);
    }

    public void setStatus(int status) {
        anim.setAnimation(status);
    }

    public int getStatus() {
        return ((ModelAnim) anim).getCurrentNum();
    }

    @Override
    public boolean act(Layer layer) {
        MeshData frame = getCurFrame();
        data = mData.forms[frame.getForm()];
        //
        x += frame.getForceX();
        y += frame.getForceY();
        //
        BulletPos[] bullets = frame.getBulletss();
        if (bullets != null) {
            for (BulletPos bullet : bullets) {
                bullet.addObj(this, layer);
            }
        }
        //
        if (frame.getDamageRegions() != null) {
            StaticObj obj = (StaticObj) layer.objs;
            while (obj != null) {
                if (obj != this) dealsDamage(obj);
                obj = (StaticObj) obj.next;
            }
        }
        //
        return super.act();
    }

    protected void dealsDamage(StaticObj obj) {
        DamageRegion[] dmgRegs = getCurFrame().getDamageRegions();
        if (dmgRegs != null) {
            for (DamageRegion region : dmgRegs) {
                if ((obj.getColision() & region.getMask()) != 0) {
                    int w = region.w;
                    int h = region.h;
                    int x1 = isFlipX() ? x - region.x - w : x + region.x;
                    int y1 = isFlipY() ? y - region.y - h : y + region.y;
                    if (obj.contain(x1, y1, w, h)) {
                        obj.addForce(isFlipY() ? - region.forceX : region.forceX,
                                isFlipY() ? - region.forceY : region.forceY);
                        obj.addHP(region.getDamage());
                    }
                }
            }
        }
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        //
        if (Loader.getLoader().debugDamageRegion) {
            g.setColor(Color.YELLOW);
            DamageRegion[] dmgRegs = getCurFrame().getDamageRegions();
            if (dmgRegs != null) {
                for (DamageRegion region : dmgRegs) {
                    int w = region.w;
                    int h = region.h;
                    int x1 = isFlipX() ? x - region.x - w : x + region.x;
                    int y1 = isFlipY() ? y - region.y - h : y + region.y;
                    g.drawRect(x1, y1, w, h);
                }
            }
        }
    }

    @Override
    public void pool() {
        super.pool();
        mData = null;
    }
}
