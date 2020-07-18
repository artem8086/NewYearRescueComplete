package art.soft.gameObjs.containers;

import art.soft.Loader;
import art.soft.gameObjs.gameObj;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class SimpleContainer extends gameObj {

    public gameObj objects;

    @Override
    public boolean act() {
        boolean end = false;
        gameObj obj = objects;
        while (obj != null) {
            end |= obj.act();
            obj = obj.next;
        }
        return end;
    }

    @Override
    public void draw(Graphics g) {
        gameObj obj = objects;
        while (obj != null) {
            obj.draw(g);
            obj = obj.next;
        }
    }

    @Override
    public void pool() {
        removeAll();
    }

    /*
     * Добавление обьекта на сцену
     */
    public void add(gameObj obj) {
        obj.next = objects;
        objects = obj;
    }

    /*
     * Удаляет все добавленные обьекты
     */
    public void removeAll() {
        Loader.getLoader().poolAllObj(objects);
        objects = null;
    }

    @Override
    public void init() {}
}
