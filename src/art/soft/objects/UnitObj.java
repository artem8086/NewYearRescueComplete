package art.soft.objects;

import art.soft.Loader;
import art.soft.level.Layer;
import art.soft.level.Player;
import art.soft.model.MeshData;
import art.soft.model.ModelAnim;
import art.soft.model.extended.BulletPos;
import art.soft.model.extended.DamageRegion;
import art.soft.objsData.UnitData;
import art.soft.objsData.ObjData;
import art.soft.objsData.Static;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class UnitObj extends DynamicObj {

    private static final int JUMP_MASK = 8;

    public UnitData mData;
    public Player owner;

    @Override
    public void init(ObjData data, ObjData drop, int flipMask) {
        mData = (UnitData) data;
        anim = mData.animData.getAnimation();
        anim.setAnimation(mData.startStatus);
        super.init(mData.forms[getCurFrame().getForm()], drop, flipMask);
    }

    public MeshData getCurFrame() {
        return ((ModelAnim) anim).getCurAnim();
    }

    public void setData(UnitData mData) {
        this.mData = mData;
        anim.pool();
        anim = mData.animData.getAnimation();
        int st;
        if (owner != null) {
            st = owner.getStatus();
        } else st = mData.startStatus;
        setStatus(st);
    }

    public void setStatus(int status) {
        if (!isOnGround()) status += mData.inAirStatus;
        anim.setAnimation(status);
    }

    public int getStatus() {
        return ((ModelAnim) anim).getCurrentNum();
    }

    @Override
    public void setDeathAnim() {
        if (owner != null) owner.setStdSet();
        setStatus(((Static) data).deathAnim);
    }

    @Override
    protected void modifyForce() {
        if ((status & JUMP_MASK) != 0 && isOnGround() && hp != 0) {
            addForce(isFlipX() ? - mData.jumpX : mData.jumpX, mData.jumpY);
        }
        MeshData frame = getCurFrame();
        float forceX = frame.getForceX();
        float forceY = frame.getForceY();
        addForce(isFlipX() ? - forceX : forceX, Loader.getLoader().engine.flipGravity ? - forceY : forceY);
    }

    public void setJump(boolean jump) {
        if (jump) status |= JUMP_MASK;
        else status ^= ~JUMP_MASK;
    }

    @Override
    public boolean act(Layer layer) {
        MeshData frame = getCurFrame();
        data = mData.forms[frame.getForm()];
        //
        BulletPos[] bullets = frame.getBulletss();
        if (bullets != null) {
            for (BulletPos bullet : bullets) {
                bullet.addObj(this, layer);
            }
            if (owner != null && owner.item != null) {
                owner.item.rangeFire(bullets.length);
            }
        }
        //
        if (super.act(layer)) return true;
        //
        int curStatus = getStatus();
        if (isOnGround()) {
            if (curStatus >= mData.inAirStatus) {
                setStatus(curStatus - mData.inAirStatus);
            }
        } else {
            if (curStatus < mData.inAirStatus) {
                setStatus(curStatus - mData.inAirStatus);
            }
        }
        return false;
    }

    @Override
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
            if (owner != null && owner.item != null) {
                owner.item.meleeFire(dmgRegs.length);
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
