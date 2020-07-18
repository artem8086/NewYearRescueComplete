package art.soft.gameObjs.gui;

import art.soft.Loader;
import art.soft.Settings;
import static art.soft.gameObjs.gui.AnimMenu.NOACTIVE_STATE;
import static art.soft.gameObjs.gui.AnimMenu.NORMAL_STATE;
import static art.soft.gameObjs.gui.AnimMenu.PRESSED_STATE;

/**
 *
 * @author Артём Святоха
 */
public class SettingsMenu extends AnimMenu {

    public SettingsMenu() {
        setAnimSet(Loader.getLoader().loadAnimation(Loader.ANIM_TYPE_ASCII, "menu/settings"));
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
        Loader.getLoader().menu.current.activate();
    }

    @Override
    public void pressed(int indx) {
        Loader loader = Loader.getLoader();
        switch (indx) {
            case 0:
                diactivate();
                loader.menu.controls.start();
                break;
            case 1:
                diactivate();
                loader.menu.graphics.start();
                break;
            case 2:
                Settings settings = loader.settings;
                settings.reset();
                settings.Save();
                loader.game.resetWindow();
                break;
            case 3:
                close();
        }
    }
}
