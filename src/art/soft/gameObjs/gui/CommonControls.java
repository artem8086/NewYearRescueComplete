package art.soft.gameObjs.gui;

import art.soft.Loader;
import art.soft.ascii.BitFont;
import static art.soft.gameObjs.gui.AnimMenu.NOACTIVE_STATE;
import static art.soft.gameObjs.gui.AnimMenu.NORMAL_MENU;
import static art.soft.gameObjs.gui.AnimMenu.NORMAL_STATE;
import static art.soft.gameObjs.gui.AnimMenu.PRESSED_STATE;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 *
 * @author Артём Святоха
 */
public class CommonControls extends AnimMenu {

    protected static final int MENU_SCALE = 2;

    protected static final int FILD_WIDTH = 13 * MENU_SCALE * BitFont.FONT_WIDTH - 2;
    
    protected int fieldOffset;

    protected int buttons[];

    protected int buttonIndx = -1;

    public CommonControls() {
        setAnimSet(Loader.getLoader().loadAnimation(Loader.ANIM_TYPE_ASCII, "menu/common"));
        //
        numItems = 5;
        //
        fillMenuState(0);
        fillIndxs(NORMAL_STATE, 4);
        fillIndxs(PRESSED_STATE, 4 + numItems);
        fillIndxs(NOACTIVE_STATE, 4 + numItems * 2);
        //
        buttons = Loader.getLoader().settings.gameKeys;
        fieldOffset = BitFont.FONT_WIDTH * MENU_SCALE * 13 + 1;
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (curState == NORMAL_MENU) {
            BitFont font = Loader.getLoader().font;
            final int w = BitFont.FONT_WIDTH * MENU_SCALE;
            final int h = BitFont.FONT_HEIGHT * MENU_SCALE;
            for (int i = buttons.length - 1; i >= 0; i--) {
                g.setColor(buttonIndx == i ? BitFont.COLORS[0xE] : BitFont.COLORS[0x3]);
                g.fillRect(menuX + fieldOffset, menuY + (2 + i) * h + 1, FILD_WIDTH, h - 2);
                if (buttonIndx != i) {
                    int key = buttons[i];
                    if (key != 0) {
                        g.setColor(BitFont.COLORS[0]);
                        font.drawString(g, KeyEvent.getKeyText(key),
                                menuX + fieldOffset + w - 1,
                                menuY + (2 + i) * h, MENU_SCALE);
                    }
                }
            }
        }
    }

    @Override
    public void closeAction() {
        Loader.getLoader().menu.controls.activate();
    }

    @Override
    public void pressed(int indx) {
        if (indx == buttons.length) {
            close();
        } else {
            buttonIndx = indx;
        }
    }

    
    @Override
    public void keyPressed(int keyCode) {
        if (buttonIndx < 0) {
            super.keyPressed(keyCode);
        }
    }

    @Override
    public void keyReleased(int keyCode) {
        if (buttonIndx < 0) {
            super.keyReleased(keyCode);
        } else {
            if (keyCode != KeyEvent.VK_ESCAPE) {
                buttons[buttonIndx] = keyCode;
                Loader.getLoader().settings.Save();
                nextButton();
            }
            buttonIndx = -1;
        }
    }
}
