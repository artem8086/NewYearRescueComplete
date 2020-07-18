package art.soft.gameObjs.gui;

import art.soft.Loader;
import static art.soft.gameObjs.gui.AnimMenu.NOACTIVE_STATE;
import static art.soft.gameObjs.gui.AnimMenu.NORMAL_STATE;
import static art.soft.gameObjs.gui.AnimMenu.PRESSED_STATE;
import art.soft.stages.Menu;

/**
 *
 * @author Артём Святоха
 */
public class ControlsMenu extends AnimMenu {

    public ControlsMenu() {
        setAnimSet(Loader.getLoader().loadAnimation(Loader.ANIM_TYPE_ASCII, "menu/controls"));
        //
        numItems = 4;
        //
        fillMenuState(0);
        fillIndxs(NORMAL_STATE, 4);
        fillIndxs(PRESSED_STATE, 4 + numItems);
        fillIndxs(NOACTIVE_STATE, 4 + numItems * 2);
    }

    @Override
    public void closeAction() {
        Loader.getLoader().menu.settings.activate();
    }

    @Override
    public void pressed(int indx) {
        Menu menu = Loader.getLoader().menu;
        switch (indx) {
            case 0:
            case 1:
                diactivate();
                menu.player.setPlayer(indx);
                menu.player.start();
                break;
            case 2:
                diactivate();
                menu.common.start();
                break;
            case 3:
                close();
        }
    }
}