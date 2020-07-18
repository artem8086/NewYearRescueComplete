package art.soft.level;

import art.soft.gameObjs.gameObj;
import static art.soft.level.objPos.FLIP_X_MASK;
import static art.soft.level.objPos.FLIP_Y_MASK;
import art.soft.objsData.ObjData;

/**
 *
 * @author Артём Святоха
 */
public abstract class LayerObj extends gameObj {

    public ObjData data;
    public int x, y;
    //
    protected int status;

    public final void setFlipX(boolean flipX) {
        if (flipX) status |= FLIP_X_MASK;
        else status ^= ~FLIP_X_MASK;
    }

    public final void setFlipY(boolean flipY) {
        if (flipY) status |= FLIP_Y_MASK;
        else status ^= ~FLIP_Y_MASK;
    }

    public final boolean isFlipX() {
        return (status & FLIP_X_MASK) != 0;
    }

    public final boolean isFlipY() {
        return (status & FLIP_Y_MASK) != 0;
    }

    @Override
    public boolean act() {
        return false;
    }

    public abstract boolean act(Layer layer);

    @Override
    public void init() {}

    public abstract void init(ObjData data, ObjData drop, int flipMask);

    public int getX() {
        return x - getCenterX();
    }

    public int getY() {
        return y - getCenterY();
    }

    public final int getCenterX() {
        return isFlipX() ? data.width - data.cenX : data.cenX;
    }

    public final int getCenterY() {
        return isFlipY() ? data.height - data.cenY : data.cenY;
    }

    public final void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public void pool() {
        data = null;
    }
}
