package art.soft.gameObjs;

import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public abstract class gameObj {

    public gameObj next;

    public abstract void init();

    public abstract boolean act();

    public abstract void draw(Graphics g);

    public abstract void pool();
}
