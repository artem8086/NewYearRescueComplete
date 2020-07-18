package art.soft.objects;

import art.soft.Loader;
import art.soft.level.Layer;
import art.soft.objsData.ObjData;
import art.soft.objsData.Static;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class StaticObj extends Decoration {

    public int hp;
    //
    protected ObjData drop;

    @Override
    public void init(ObjData data, ObjData drop, int flipMask) {
        super.init(data, drop, flipMask);
        this.drop = drop;
        Static sData = (Static) data;
        status ^= sData.flip;
        hp = sData.hp;
    }

    public boolean isAlive() {
        return hp != 0;
    }

    public int getHP() {
        return hp;
    }

    public int getMaxHP() {
        return ((Static) data).hp;
    }

    public void addHP(int dmg) {
        if (hp >= 0) {
            hp += dmg;
            int max = ((Static) data).hp;
            if (hp > max) hp = max;
            else if (hp <= 0) {
                setDeathAnim();
                hp = 0;
            }
        }
    }

    public boolean contain(int x, int y) {
        int tx = this.x - getCenterX();
        int ty = this.y - getCenterY();
        return x >= tx && x <= tx + data.width && y >= ty && y <= ty + data.height;
    }

    public boolean contain(int tx, int ty, int w, int h) {
        int rx = getX();
        int ry = getY();
        return rx + data.width > tx && ry + data.height > ty
                && tx + w > rx && ty + h > ry;
    }

    public void setDeathAnim() {
        if (anim != null) anim.setAnimation(((Static) data).deathAnim);
    }

    public int getColision() {
        return hp == 0 ? 0 : ((Static) data).collision;
    }

    public void addForce(float x, float y) {}

    public void setForce(float x, float y) {}

    public float getVX() {
        return 0f;
    }

    public float getVY() {
        return 0f;
    }

    public float getImpulseX() {
        return 0f;
    }

    @Override
    public boolean act(Layer layer) {
        return anim != null ? anim.incAnim(hp != 0) : false;
    }

    @Override
    public void draw(Graphics g) {
        if (anim != null) anim.play(g, x, y, isFlipX(), isFlipY());
        //
        if (Loader.getLoader().debugStatic) {
            g.setColor(Color.BLUE);
            g.drawRect(getX(), getY(), data.width, data.height);
        }
    }

    @Override
    public void pool() {
        if (drop != null && hp == 0) {
            drop.createObj(Loader.getLoader().engine.curLayer, x, y, status & 3, null);
        }
        hp = 0;
        drop = data = null;
    }
    
}
