package art.soft.gameObjs.containers;

import art.soft.Game;
import art.soft.Loader;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class CenterContainer extends SimpleContainer {
    
    private Color background;

    public void setBacground(Color background){
        this.background = background;
    }

    @Override
    public void draw(Graphics g) {
        Loader loader = Loader.getLoader();
        if (background != null) {
            g.setColor(background);
            g.fillRect(0, 0, loader.game.width, loader.game.height);
        }
        //
        int centerX = (Game.PREFER_GAME_WIDTH - loader.game.width) >> 1;
        Game.translate(g, - centerX, 0);
        //
        super.draw(g);
        //
        Game.translate(g, centerX, 0);
    }

    @Override
    public void pool() {
        super.pool();
        background = null;
    }
}
