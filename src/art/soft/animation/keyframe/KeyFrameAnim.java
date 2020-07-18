package art.soft.animation.keyframe;

import art.soft.Loader;
import art.soft.animation.AnimSet;
import art.soft.animation.Animation;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class KeyFrameAnim extends Animation {
    
    private KeyFrameSet animSet;

    private KeyFrameData current;
    
    private int time, nFrame;

    public KeyFrameAnim() {}

    public KeyFrameAnim(KeyFrameSet animSet) {
        this.animSet = animSet;
    }

    @Override
    public void setAnimSet(AnimSet set) {
        animSet = (KeyFrameSet) set;
    }

    @Override
    public KeyFrameSet getAnimSet() {
        return animSet;
    }

    @Override
    public void setAnimation(int num) {
        KeyFrameData anim = animSet.data[num];
        if (anim != current) {
            current = anim;
            time = anim.frameTime;
            nFrame = 0;
        }
    }

    @Override
    public void reset() {
        time = current.frameTime;
        nFrame = 0;
    }

    @Override
    public void play(Graphics g, int x, int y) {
        KeyFrame frame = current.frames[nFrame];
        if (frame != null) frame.draw(g, animSet.animPack, x, y);
        if (next != null) next.play(g, x, y);
    }

    @Override
    public void play(Graphics g, int x, int y, boolean flipX, boolean flipY) {
        KeyFrame frame = current.frames[nFrame];
        if (frame != null) frame.draw(g, animSet.animPack, x, y, flipX, flipY);
        if (next != null) next.play(g, x, y, flipX, flipY);
    }

    @Override
    public void pool() {
        if (next != null) next.pool();
        animSet = null;
        current = null;
        Loader loader = Loader.getLoader();
        next = loader.frameAnimsPool;
        loader.frameAnimsPool = this;
    }

    @Override
    public boolean incAnim(boolean cycle) {
        if (next != null) next.incAnim(cycle);
        time --;
        if (time <= 0) {
            time = current.frameTime;
            nFrame ++;
            if (nFrame >= current.frames.length) {
                nFrame = cycle ? 0 : current.frames.length - 1;
                return !cycle;
            }
        }
        return false;
    }
}
