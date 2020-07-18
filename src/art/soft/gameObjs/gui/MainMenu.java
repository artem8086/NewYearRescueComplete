package art.soft.gameObjs.gui;

import art.soft.Loader;

/**
 *
 * @author Артём Святоха
 */
public class MainMenu extends AnimMenu {

    public MainMenu() {
        setAnimSet(Loader.getLoader().loadAnimation(Loader.ANIM_TYPE_ASCII, "menu/main"));
        //
        numItems = 5;
        //
        fillMenuState(0);
        fillIndxs(NORMAL_STATE, 4);
        fillIndxs(PRESSED_STATE, 4 + numItems);
        fillIndxs(NOACTIVE_STATE, 4 + numItems * 2);
    }

    @Override
    public void closeAction() {
        if (selectIndx == 3) {
            Loader.getLoader().menu.toTitle();
        } else
        if (selectIndx == 4) {
            System.exit(0);
        }
    }

    @Override
    public void pressed(int indx) {
        switch (indx) {
        case 2:
            diactivate();
            Loader.getLoader().menu.settings.start();
            break;
        case 3:
        case 4:
            close();
        }
    }
    
}
