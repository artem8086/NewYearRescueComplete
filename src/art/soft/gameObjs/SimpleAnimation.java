package art.soft.gameObjs;

import art.soft.animation.AnimSet;
import art.soft.animation.Animation;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class SimpleAnimation extends gameObj {

    public Animation animation;
    public int x, y;
    public boolean cycle = true;

    public void setAnim(Animation animation) {
        this.animation = animation;
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimSet(AnimSet set) {
        if (animation != null) {
            animation.pool();
        }
        animation = set.getAnimation();
    }

    public void setAnimNum(int num) {
        animation.setAnimation(num);
    }

    public void setCycle(boolean cycle) {
        this.cycle = cycle;
    }

    public void setPos(int x, int y) {
        this.x = x; this.y = y;
    }

    @Override
    public void init() {
        cycle = true;
    }

    @Override
    public boolean act() {
        return animation.incAnim(cycle);
    }

    @Override
    public void draw(Graphics g) {
        animation.play(g, x, y);
    }

    @Override
    public void pool() {
        animation.pool();
        animation = null;
    }
}
