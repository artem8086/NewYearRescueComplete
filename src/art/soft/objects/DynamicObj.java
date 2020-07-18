package art.soft.objects;

import art.soft.Loader;
import art.soft.level.Layer;
import art.soft.level.gameEngine;
import art.soft.objsData.Dynamic;
import art.soft.objsData.ObjData;
import art.soft.objsData.Static;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class DynamicObj extends StaticObj {

    private static final int ON_GROUND_MASK = 4;

    protected static StaticObj hCollObj, vCollObj;

    protected float vx, vy;
    protected float friction, impulseX;

    @Override
    public void init(ObjData data, ObjData drop, int flipMask) {
        super.init(data, drop, flipMask);
        friction = 1f;
        impulseX = 0;
    }

    public boolean isOnGround() {
        return (status & ON_GROUND_MASK) != 0;
    }

    public void setOnGround(boolean onGround) {
        if (onGround) status |= ON_GROUND_MASK;
        else status ^= ~ON_GROUND_MASK;
    }

    @Override
    public boolean act(Layer layer) {
        boolean del = super.act(layer);
        gameEngine engine = Loader.getLoader().engine;
        Dynamic dynamic = (Dynamic) data;
        if (isOnGround()) vx *= friction;
        modifyForce();
        if (!isOnGround()) {
            vx *= engine.envirFrictionX;
            vy *= engine.envirFrictionY;
        }
        vy += engine.gravity * dynamic.mass;
        int max = dynamic.max_speedX;
        if (vx > max) vx = max; else if (vx < - max) vx = - max;
        max = dynamic.max_speedY;
        if (vy > max) vy = max; else if (vy < - max) vy = - max;
        int old_x = x;
        int old_y = y;
        x += vx;
        if (engine.flipGravity) {
            setFlipY(true);
            y -= vy;
        } else {
            setFlipY(false);
            y += vy;
        }
        // Обнаружение коллизий
        StaticObj obj = (StaticObj) layer.objs;
        hCollObj = vCollObj = null;
        setOnGround(false);
        while (obj != null) {
            if (obj != this) {
                dealsDamage(obj);
                if ((obj.getColision() & dynamic.coll_mask) != 0) {
                    if (vy != 0 && collisionDetect(old_x, old_y, old_x, y, obj, true)) {
                        vCollObj = obj;
                    } else
                    if (vx != 0 && collisionDetect(old_x, old_y, x, old_y, obj, false)) {
                        hCollObj = obj;
                    }
                }
            }
            obj = (StaticObj) obj.next;
        }
        // Обработка столкновения
        if (hCollObj != null) collisonProcessing(hCollObj, true);
        if (vCollObj != null) collisonProcessing(vCollObj, false);
        return del;
    }

    protected void dealsDamage(StaticObj obj) {}

    protected void modifyForce() {}

    protected void collisonProcessing(StaticObj collObj, boolean collX) {
        Dynamic dynamic = (Dynamic) data;
        // Просчёт урона
        float x = vx * dynamic.damageX_reduct;
        float y = vy * dynamic.damageY_reduct;
        float damage = (float) Math.sqrt(x * x + y * y) - dynamic.min_damage;
        if (damage > 0) {
            damage *= dynamic.mass;
            //data.loader.game.log(data.type + " get damage = " + (int) damage + ", hp = " + hp);
            collObj.addHP(- (int) damage);
            this.addHP(- (int) damage);
        }
        //
        collObj.addForce(collX ? vx : 0, collX ? 0 : vy > 0 ? vy : vy / dynamic.mass);
        if (collX) {
            impulseX = vx = 0;
            //vy -= vy * friction;
        } else {
            vx += collObj.getImpulseX();
            impulseX = vx;
            friction = ((Static) collObj.data).friction + dynamic.friction;
            friction /= dynamic.mass * 2;
            vy = 0;
        }
    }

    @Override
    public float getImpulseX() {
        return impulseX;
    }

    @Override
    public void addForce(float x, float y) {
        vx += x; vy += y;
    }

    @Override
    public void setForce(float x, float y) {
        vx = x; vy = y;
    }

    @Override
    public float getVX() {
        return vx;
    }

    @Override
    public float getVY() {
        return vy;
    }

    public boolean collisionDetect(int old_x, int old_y, int x, int y, StaticObj obj, boolean vert) {
        Static thisData = (Static) data;
        Static objData = (Static) obj.data;
        //
        int cenX = getCenterX();
        int cenY = getCenterY();
        int rx = old_x - cenX;
        int ry = old_y - cenY;
        int rw = rx + thisData.width;
        int rh = ry + thisData.height;
        int t = old_x - x;
        if (t >= 0) rx -= t; else rw -= t;
        t = old_y - y;
        if (t < 0) rh -= t; else ry -= t;
        int tx = obj.getX();
        int ty = obj.getY();
        int tw = tx + objData.width;
        int th = ty + objData.height;
        //
        if (rw > tx && rh > ty && tw > rx && th > ry) {
            if (vert) {
                if (Loader.getLoader().engine.flipGravity) {
                    if (rh < th) {
                        if (vy <= 0) this.y = ty - thisData.height + cenY;
                    } else {
                        if (vy >= 0) {
                            this.y = th + cenY;
                            setOnGround(true);
                        }
                    }
                } else
                if (ry < ty) {
                    if (vy >= 0) {
                        this.y = ty - thisData.height + cenY;
                        setOnGround(true);
                    }
                } else {
                    if (vy <= 0) this.y = th + cenY;
                }
            } else {
                if (rx < tx) {
                    if (vx >= 0) this.x = tx - thisData.width + cenX;
                } else {
                    if (vx <= 0) this.x = tw + cenX;
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void draw(Graphics g) {
        if (anim != null) anim.play(g, x, y, isFlipX(), isFlipY());
        //
        if (Loader.getLoader().debugDynamic) {
            g.setColor(Color.RED);
            g.drawRect(getX(), getY(), data.width, data.height);
        }
    }
}
