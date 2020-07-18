package art.soft.level;

import art.soft.Loader;
import art.soft.Settings;
import art.soft.gameObjs.containers.CenterContainer;
import art.soft.stages.Stage;
import com.sun.glass.events.KeyEvent;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class levelStage extends Stage {

    private boolean load, pause;
    
    private CenterContainer inGameMenu;
    
    private final gameEngine engine;

    public levelStage() {
        engine = Loader.getLoader().engine;
    }

    @Override
    public void init() {
        if (!load) {
            initGameMenu();
            load = true;
        }
        removeAll();
        engine.init();
        Loader loader = Loader.getLoader();
        //loader.buttonMenu.setMenu(MENU_IN_GAME);
        //loader.buttonMenu.toMainMenu.setMenu(MENU_IN_GAME);
        unpause();
    }

    public void initGameMenu() {
        Loader loader = Loader.getLoader();
        inGameMenu = (CenterContainer) loader.getObj(CenterContainer.class);
        inGameMenu.setBacground(new Color(0, 0, 0, 0x77));
        //inGameMenu.add(loader.buttonMenu);
        //
        
    }

    @Override
    public void act() {
        if (pause) {
            inGameMenu.act();
        } else {
            engine.act();
            // HUD action
            super.act();
        }
    }

    @Override
    public void draw(Graphics g) {
        engine.draw(g);
        if (pause) {
            inGameMenu.draw(g);
        } else {
            // HUD draw
            super.draw(g);
        }
    }

    public void pause() {
        // setKeyListener(Loader.getLoader().buttonMenu);
        pause = true;
    }

    public void unpause() {
        setKeyListener(engine); // (level);
        pause = false;
    }

    @Override
    public void keyPressed(int keyCode) {
        if (!pause && (keyCode == Loader.getLoader().settings.gameKeys[Settings.GAME_PAUSE]
                || keyCode == KeyEvent.VK_ESCAPE)) {
            pause();
            return;
        }
        super.keyPressed(keyCode);
    }
}
