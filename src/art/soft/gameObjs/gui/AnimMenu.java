package art.soft.gameObjs.gui;

import art.soft.Loader;
import static art.soft.Settings.GAME_DOWN;
import static art.soft.Settings.GAME_SELECT;
import static art.soft.Settings.GAME_UP;
import art.soft.animation.AnimSet;
import art.soft.animation.Animation;
import art.soft.gameObjs.GameKeyListener;
import art.soft.gameObjs.gameObj;
import art.soft.stages.Stage;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

/**
 *
 * @author Артём Святоха
 */
public abstract class AnimMenu extends gameObj implements GameKeyListener {

    protected Stage stage;

    protected AnimSet menuSet;
    
    protected Animation background;
    protected Animation buttons;
    
    protected int menuX, menuY;

    public static final int APPEAR_MENU = 0;
    public static final int NORMAL_MENU = 1;
    public static final int NOACTIVE_MENU = 2;
    public static final int DISAPPEAR_MENU = 3;
    public static final int HIDDEN_MENU = 4;

    protected final byte menuState[] = new byte[4];

    public static final int NORMAL_STATE = 0;
    public static final int PRESSED_STATE = 1;
    public static final int NOACTIVE_STATE = 2;

    protected final byte selectIndxState[][] = new byte[3][];

    protected int numItems;

    protected int selectIndx;

    protected int curState;

    protected void fillMenuState(int startIndx) {
        for (int i = 0; i<menuState.length; i++) {
            menuState[i] = (byte) startIndx;
            startIndx++;
        }
    }

    protected void fillIndxs(int state, int startIndx) {
        byte[] tmp = selectIndxState[state] = new byte[numItems];
        for (int i = 0; i<numItems; i++) {
            tmp[i] = (byte) startIndx;
            startIndx++;
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setPos(int x, int y) {
        menuX = x;
        menuY = y;
    }

    @Override
    public void init() {
        curState = HIDDEN_MENU;
        selectIndx = 0;
    }

    public boolean isHidden() {
        return curState == HIDDEN_MENU;
    }

    public void setAnimSet(AnimSet set) {
        pool();
        menuSet = set;
        background = set.getAnimation();
        buttons = set.getAnimation();
    }

    protected void diactivateKeyListener() {
        if (stage.getKeyListener() == this) {
            stage.setKeyListener(null);
        }
    }

    public void hide() {
        curState = HIDDEN_MENU;
        diactivateKeyListener();
    }

    public void start() {
        selectIndx = 0;
        curState = APPEAR_MENU;
        diactivateKeyListener();
        background.setAnimation(menuState[APPEAR_MENU]);
        background.next = null;
    }

    public void activate() {
        if (curState == APPEAR_MENU || curState == NOACTIVE_MENU) {
            curState = NORMAL_MENU;
            background.setAnimation(menuState[NORMAL_MENU]);
            background.next = buttons;
            setButtonAnim(NORMAL_STATE);
            stage.setKeyListener(this);
        }
    }

    public void diactivate() {
        if (curState == NORMAL_MENU) {
            curState = NOACTIVE_MENU;
            diactivateKeyListener();
            background.setAnimation(menuState[NOACTIVE_MENU]);
            background.next = buttons;
            setButtonAnim(NOACTIVE_STATE);
        }
    }

    public void close() {
        if (curState != HIDDEN_MENU) {
            curState = DISAPPEAR_MENU;
            diactivateKeyListener();
            background.setAnimation(menuState[DISAPPEAR_MENU]);
            background.next = null;
        }
    }

    @Override
    public boolean act() {
        if (curState != HIDDEN_MENU) {
            if (curState == DISAPPEAR_MENU) {
                if (background.incAnim(false)) {
                    curState = HIDDEN_MENU;
                    closeAction();
                    return true;
                }
            } else
            if (curState == APPEAR_MENU && background.incAnim(false)) {
                activate();
            } else background.incAnim(true);
        }
        return false;
    }

    @Override
    public void draw(Graphics g) {
        if (curState != HIDDEN_MENU) {
            background.play(g, menuX, menuY);
        }
    }

    @Override
    public void pool() {
        if (background != null) {
            background.next = null;
            background.pool();
        }
        if (buttons != null) buttons.pool();
        if (menuSet != null) menuSet.remove();
    }

    public abstract void closeAction();

    public abstract void pressed(int indx);

    protected void setButtonAnim(int state) {
        buttons.setAnimation(selectIndxState[state][selectIndx] & 0xFF);
    }

    protected void prevButton() {
        if (selectIndx > 0) selectIndx --;
        setButtonAnim(NORMAL_STATE);
    }

    protected void nextButton() {
        if (selectIndx < (numItems - 1)) selectIndx ++;
        setButtonAnim(NORMAL_STATE);
    }

    @Override
    public void keyPressed(int keyCode) {
        int[] gameKeys = Loader.getLoader().settings.gameKeys;
        if (keyCode == gameKeys[GAME_UP]) {
            prevButton();
        }
        if (keyCode == gameKeys[GAME_DOWN]) {
            nextButton();
        }
        if (keyCode == gameKeys[GAME_SELECT]) {
            setButtonAnim(PRESSED_STATE);
        }
    }

    @Override
    public void keyReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_ESCAPE) {
            Loader.getLoader().menu.cancel();
        } else
        if (keyCode == Loader.getLoader().settings.gameKeys[GAME_SELECT]) {
            setButtonAnim(NORMAL_STATE);
            pressed(selectIndx);
        }
    }
}
