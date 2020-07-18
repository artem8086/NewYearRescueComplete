package art.soft;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class Settings {

    private static final int CURRENT_VERSION = 1;

    public static final String SETTINGS_FILE = "settings.cfg";

    private static final Key KEYS[] = {
        RenderingHints.KEY_ALPHA_INTERPOLATION,
        //RenderingHints.KEY_ANTIALIASING,
        RenderingHints.KEY_COLOR_RENDERING,
        //RenderingHints.KEY_DITHERING,
        //RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.KEY_INTERPOLATION,
        //RenderingHints.KEY_RENDERING,
        RenderingHints.KEY_STROKE_CONTROL,
        //RenderingHints.KEY_TEXT_ANTIALIASING
    };
    private static final Object VALUES[] = {
        RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED,
        RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY,
        //RenderingHints.VALUE_ANTIALIAS_OFF,
        //RenderingHints.VALUE_ANTIALIAS_ON,
        RenderingHints.VALUE_COLOR_RENDER_SPEED,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY,
        //RenderingHints.VALUE_DITHER_DISABLE,
        //RenderingHints.VALUE_DITHER_ENABLE,
        //RenderingHints.VALUE_FRACTIONALMETRICS_OFF,
        //RenderingHints.VALUE_FRACTIONALMETRICS_ON,
        RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR,
        RenderingHints.VALUE_INTERPOLATION_BILINEAR,
        //RenderingHints.VALUE_RENDER_SPEED,
        //RenderingHints.VALUE_RENDER_QUALITY,
        RenderingHints.VALUE_STROKE_PURE,
        RenderingHints.VALUE_STROKE_NORMALIZE,
        //RenderingHints.VALUE_TEXT_ANTIALIAS_OFF,
        //RenderingHints.VALUE_TEXT_ANTIALIAS_ON
    };

    public static final int GAME_KEYS = 4;
    //
    public static final int GAME_UP = 0;
    public static final int GAME_DOWN = 1;
    public static final int GAME_SELECT = 2;
    public static final int GAME_PAUSE = 3;

    public static final int NUM_PLAYERS = 2;
    
    public static final int PLAYER_KEYS = 7;
    //
    public static final int PLAYER_UP = 0;
    public static final int PLAYER_DOWN = 1;
    public static final int PLAYER_LEFT = 2;
    public static final int PLAYER_RIGHT = 3;
    public static final int PLAYER_FIRE = 4;
    public static final int PLAYER_JUMP = 5;
    public static final int PLAYER_TAKE = 6;

    public int gameKeys[];
    
    public int playerKeys[][];

    public boolean fullscreen;

    public boolean showFPS;

    public boolean lineAntializing;

    public byte mapRender[];

    public void reset() {
        mapRender = new byte[KEYS.length];
        mapRender[0] = mapRender[1] = mapRender[2] = mapRender[3] = 1;

        gameKeys = new int[GAME_KEYS];
        gameKeys[GAME_UP] = KeyEvent.VK_UP;
        gameKeys[GAME_DOWN] = KeyEvent.VK_DOWN;
        gameKeys[GAME_SELECT] = KeyEvent.VK_ENTER;
        gameKeys[GAME_PAUSE] = KeyEvent.VK_ENTER;

        playerKeys = new int[NUM_PLAYERS][];
        //
        for (int i = NUM_PLAYERS - 1; i >= 0; i --) {
            playerKeys[i] = new int[PLAYER_KEYS];
        }
        //
        int player[] = playerKeys[0];
        player[PLAYER_UP] = KeyEvent.VK_UP;
        player[PLAYER_DOWN] = KeyEvent.VK_DOWN;
        player[PLAYER_LEFT] = KeyEvent.VK_LEFT;
        player[PLAYER_RIGHT] = KeyEvent.VK_RIGHT;
        player[PLAYER_FIRE] = KeyEvent.VK_Z;
        player[PLAYER_JUMP] = KeyEvent.VK_X;
        player[PLAYER_TAKE] = KeyEvent.VK_C;

        lineAntializing = true;
        showFPS = fullscreen = false;
    }

    public void Load() {
        reset();
        DataInputStream dis;
        try {
            dis = new DataInputStream(new FileInputStream(SETTINGS_FILE));
        } catch (FileNotFoundException ex) {
            return;
        }
        try {
            if (dis.read() == CURRENT_VERSION) {
                int prop = dis.readByte();
                fullscreen = (prop & 1) != 0;
                showFPS = (prop & 2) != 0;
                lineAntializing = (prop & 4) != 0;
                for (int i = 0; i < mapRender.length; i ++) {
                    mapRender[i] = (byte) dis.read();
                }
                for (int i = 0; i < gameKeys.length; i ++) {
                    gameKeys[i] = dis.readInt();
                }
                for (int[] keys : playerKeys) {
                    for (int i = 0; i < keys.length; i ++) {
                        keys[i] = dis.readInt();
                    }
                }
            } else {
                Loader.getLoader().game.log("Current version settings [" + CURRENT_VERSION +
                        "] not equals version in file!");
            }
        } catch (IOException ex) {
            Loader.getLoader().game.log("Error while read from settings file!");
            Loader.getLoader().game.log(ex.getMessage());
        }
    }

    public void applyRender(Graphics g) {
        for (int i = mapRender.length - 1; i>=0; i--) {
            ((Graphics2D) g).setRenderingHint(KEYS[i], VALUES[mapRender[i] + (i << 1)]);
        }
    }

    public void Save() {
        DataOutputStream dos;
        try {
            dos = new DataOutputStream(new FileOutputStream(SETTINGS_FILE));
        } catch (FileNotFoundException ex) {
            Loader.getLoader().game.log("Settings file (" + SETTINGS_FILE + ") not found!");
            Loader.getLoader().game.log(ex.getMessage());
            return;
        }
        try {
            dos.write(CURRENT_VERSION);
            dos.writeByte((fullscreen ? 1 : 0) | (showFPS ? 2 : 0) |
                    (lineAntializing ? 4 : 0));
            for (int i : mapRender) {
                dos.write(i);
            }
            for (int i : gameKeys) {
                dos.writeInt(i);
            }
            for (int[] keys : playerKeys) {
                for (int i : keys) {
                    dos.writeInt(i);
                }
            }
            dos.close();
        } catch (IOException ex) {
            Loader.getLoader().game.log("Can't write to settings file!");
            Loader.getLoader().game.log(ex.getMessage());
        }
    }
}
