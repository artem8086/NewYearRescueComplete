package art.soft.gameObjs.gui;

import art.soft.Loader;
import art.soft.Settings;
import art.soft.ascii.BitFont;
import static art.soft.gameObjs.gui.AnimMenu.NOACTIVE_STATE;
import static art.soft.gameObjs.gui.AnimMenu.NORMAL_STATE;
import static art.soft.gameObjs.gui.AnimMenu.PRESSED_STATE;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class GraphicsMenu extends AnimMenu {
    
    private static final int MENU_SCALE = 2;

    public GraphicsMenu() {
        setAnimSet(Loader.getLoader().loadAnimation(Loader.ANIM_TYPE_ASCII, "menu/graphics"));
        //
        numItems = 8;
        //
        fillMenuState(0);
        fillIndxs(NORMAL_STATE, 4);
        fillIndxs(PRESSED_STATE, 4 + numItems);
        fillIndxs(NOACTIVE_STATE, 4 + numItems * 2);
    }

    @Override
    public void draw(Graphics g) {
        super.draw(g);
        if (curState == NORMAL_MENU || curState == NOACTIVE_MENU) {
            Settings settings = Loader.getLoader().settings;
            BitFont font = Loader.getLoader().font;
            final int w = BitFont.FONT_WIDTH * MENU_SCALE;
            final int h = BitFont.FONT_HEIGHT * MENU_SCALE;
            g.setColor(curState == NORMAL_MENU ? BitFont.COLORS[0] : BitFont.COLORS[7]);
            if (settings.fullscreen) {
                font.drawChar(g, 'X', menuX + 3 * w + MENU_SCALE, menuY + 2 * h, MENU_SCALE);
            }
            if (settings.showFPS) {
                font.drawChar(g, 'X', menuX + 3 * w + MENU_SCALE, menuY + 3 * h, MENU_SCALE);
            }
            byte[] mapRender = settings.mapRender;
            for (int i = mapRender.length - 1; i >= 0; i--) {
                if (mapRender[i] != 0) {
                    font.drawChar(g, 'X', menuX + 3 * w + MENU_SCALE, menuY + (4 + i) * h, MENU_SCALE);
                }
            }
            if (settings.lineAntializing) {
                font.drawChar(g, 'X', menuX + 3 * w + MENU_SCALE, menuY + 8 * h, MENU_SCALE);
            }
        }
    }

    @Override
    public void closeAction() {
        Loader.getLoader().menu.settings.activate();
    }

    @Override
    public void pressed(int indx) {
        Loader loader = Loader.getLoader();
        Settings settings = loader.settings;
        switch (indx) {
            case 0:
                settings.fullscreen ^= true;
                settings.Save();
                loader.game.resetWindow();
                break;
            case 1:
                settings.showFPS ^= true;
                settings.Save();
                break;
            case 2:
            case 3:
            case 4:
            case 5:
                settings.mapRender[indx - 2] ^= 1;
                settings.Save();
                settings.applyRender(loader.game.getGameGraphics());
                break;
            case 6:
                settings.lineAntializing ^= true;
                settings.Save();
                break;
            case 7:
                close();
        }
    }    
}
