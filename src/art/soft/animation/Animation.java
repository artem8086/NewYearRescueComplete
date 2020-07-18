package art.soft.animation;

import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public abstract class Animation {
    public Animation next;

    public Animation() {}

    public abstract void setAnimSet(AnimSet set);

    public abstract AnimSet getAnimSet();

    public abstract void setAnimation(int num);

    public abstract void reset();

    public abstract void play(Graphics g, int x, int y);

    public abstract void play(Graphics g, int x, int y, boolean flipX, boolean flipY);

    public abstract void pool();

    public abstract boolean incAnim(boolean cycle);
}
