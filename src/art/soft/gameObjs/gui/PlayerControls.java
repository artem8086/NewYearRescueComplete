package art.soft.gameObjs.gui;

import art.soft.Loader;
import art.soft.ascii.BitFont;
import static art.soft.gameObjs.gui.AnimMenu.NOACTIVE_STATE;
import static art.soft.gameObjs.gui.AnimMenu.NORMAL_STATE;
import static art.soft.gameObjs.gui.AnimMenu.PRESSED_STATE;
import static art.soft.gameObjs.gui.CommonControls.MENU_SCALE;

/**
 *
 * @author Артём Святоха
 */
public class PlayerControls extends CommonControls {

    public PlayerControls() {
        setAnimSet(Loader.getLoader().loadAnimation(Loader.ANIM_TYPE_ASCII, "menu/player"));
        //
        numItems = 8;
        //
        fillMenuState(0);
        fillIndxs(NORMAL_STATE, 4);
        fillIndxs(PRESSED_STATE, 4 + numItems);
        fillIndxs(NOACTIVE_STATE, 4 + numItems * 2);
        //
        fieldOffset = BitFont.FONT_WIDTH * MENU_SCALE * 9 + 1;
    }

    public void setPlayer(int player) {
        buttons = Loader.getLoader().settings.playerKeys[player];
    }
}
