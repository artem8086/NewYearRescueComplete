package art.soft.animation;

/**
 *
 * @author Артём Святоха
 */
public abstract class AnimSet {

    public int loadIndx;

    protected String path;

    public String getPath() {
        return path;
    }

    public abstract Animation getAnimation();

    public abstract void remove();
}
