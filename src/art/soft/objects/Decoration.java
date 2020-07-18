package art.soft.objects;

import art.soft.Loader;
import art.soft.animation.Animation;
import art.soft.level.Layer;
import art.soft.level.LayerObj;
import art.soft.objsData.Decor;
import art.soft.objsData.ObjData;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class Decoration extends LayerObj {

    protected Animation anim;
    
    @Override
    public void init(ObjData data, ObjData drop, int flipMask) {
        this.data = data;
        status = flipMask;
        Decor decor = (Decor) data;
        if (decor.animData != null) {
            anim = decor.animData.getAnimation();
            anim.setAnimation(decor.anim);
        }
    }

    @Override
    public void draw(Graphics g) {
        anim.play(g, x, y);
        //
        if (Loader.getLoader().debugDecoration) {
            g.setColor(Color.GREEN);
            g.drawRect(x - getCenterX(), y - getCenterY(), data.width, data.height);
        }
    }

    @Override
    public boolean act(Layer layer) {
        anim.incAnim(false);
        return false;
    }

    @Override
    public void pool() {
        super.pool();
        anim.pool();
        anim = null;
    }
}
