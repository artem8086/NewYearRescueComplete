package art.soft.level;

import art.soft.Game;
import art.soft.Loader;
import static art.soft.Settings.NUM_PLAYERS;
import art.soft.gameObjs.GameKeyListener;
import art.soft.gameObjs.gameObj;
import art.soft.objsData.ObjData;
import com.esotericsoftware.jsonbeans.Json;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author Артём Святоха
 */
public class gameEngine extends gameObj implements GameKeyListener {

    private final static String START_LEVEL = "start";

    private final static String LEVEL_PATH = "levels/";

    private String currentLevel, restartLevel, deadLevel, nextLevel;

    public ObjData[] objects;

    public Layer layers, curLayer;

    public Layer[] layersArray;
    
    public int camX, camY; // Текущие координаты камеры

            // Переменные среды
    public float gravity; // Сила притяжения
    public boolean flipGravity; // Гравитация действует в обратном направлении
                    // при этом все динамические обьекты переварачивются
    public float envirFrictionX; // сопративление среды по оси X
    public float envirFrictionY; // сопративление среды по оси Y
            //

    public int numPlayers;

    public Player players[];
    
    public Camera cameras;
    
    public Camera curCamera;

    private HashMap<String, ObjData> objs = new HashMap<>();

    // Json data
    private final static String JSON_OBJS_PATH = "level/objs/";
    public final static String JSON_LEVEL_PATH = "level/";

    public String jsonLoadFileName;
    private File jsonFile;
    private final Json json = new Json();

    private int temp;

    public void jsonLoad(String name) {
        Loader loader = Loader.getLoader();
        loader.poolAllObj(layers);
        loader.level.removeAll();
        objs.clear();
        
        jsonFile = new File(JSON_LEVEL_PATH + name + ".json");
        if (jsonFile.exists()) {
            jsonLevel lev = json.fromJson(jsonLevel.class, jsonFile);
            currentLevel = lev.name;
            restartLevel = lev.restartLevel;
            deadLevel = lev.deadLevel;
            nextLevel = lev.nextLevel;
            //
            Camera lastCam = null;
            for (Camera camera : lev.cameras) {
                camera.jsonLoad();
                if (lastCam == null) {
                    lastCam = cameras = camera;
                } else {
                    lastCam.next = camera;
                    lastCam = camera;
                }
            }
            Layer layerEnd = null;
            layersArray = lev.layers;
            for (Layer layer : lev.layers) {
                if (layerEnd == null) {
                    layerEnd = layers = layer;
                } else {
                    layerEnd.next = layer;
                    layerEnd = layer;
                }
                layer.initJson();
                layer.init();
                objPos obj = layer.objsPos;
                while (obj != null) {
                    obj.loadJson();
                    obj = obj.next;
                }
            }
            layerEnd.next = null;
            Camera camera = cameras;
            while (camera != null) {
                camera.init();
                camera = camera.next;
            }
            players = lev.players;
            if (players != null) {
                int i = 0;
                for (; i < numPlayers; i ++) {
                    players[i].active = true;
                    players[i].jsonInit(lev.layers);
                    players[i].init(loader.settings.playerKeys[i]);
                }
                for (; i < NUM_PLAYERS; i ++) {
                    players[i].active = false;
                    players[i].initSets();
                }
                for (; i < players.length; i ++) {
                    players[i].jsonInit(lev.layers);
                    players[i].init(null);
                }
            }
            //
            camX = camY = 0;
            setCamera(cameras);
            //
            postInit(lev.objects);
            lev.writeLevel();
        } else {
            loader.game.log("Файл " + name + ".json не существует!");
        }
    }

    private void jsonFastLoad() {
        objs.clear();
        if (jsonFile == null) {
            jsonFile = new File(JSON_LEVEL_PATH + START_LEVEL + ".json");
        }
        if (jsonFile.exists()) {
            jsonLevel lev = json.fromJson(jsonLevel.class, jsonFile);
            Layer layerEnd = null;
            layersArray = lev.layers;
            for (Layer layer : lev.layers) {
                if (layerEnd == null) {
                    layerEnd = layers = layer;
                } else {
                    layerEnd.next = layer;
                    layerEnd = layer;
                }
                layer.initJson();
                layer.init();
                objPos obj = layer.objsPos;
                while (obj != null) {
                    obj.loadJson();
                    obj = obj.next;
                }
            }
            layerEnd.next = null;
            //
            for (int i = 0; i < numPlayers; i ++) {
                Player player = players[i];
                layersArray[player.layer].add(player.unit);
            }
            for (int i = NUM_PLAYERS; i < players.length; i ++) {
                Player player = players[i];
                layersArray[player.layer].add(player.unit);
            }
        }
    }

    public void postInit(String[] preLoadObjects) {
        objects = new ObjData[objs.size()];
        temp = 0;
        if (preLoadObjects != null) {
            for (String objName : preLoadObjects) {
                objects[temp++] = getObj(objName);
            }
        }
        //
        objs.forEach((name, obj) -> {
            if (preLoadObjects != null) {
                for (int i = preLoadObjects.length - 1; i >= 0; i --) {
                    if (name.equals(preLoadObjects[i])) {
                        preLoadObjects[i] = null;
                        return;
                    }
                }
            }
            objects[temp++] = obj;
        });
    }

    public ObjData getObj(String objName) {
        ObjData obj = objs.get(objName);
        if (obj == null) {
            obj = json.fromJson(ObjData.class, new File(JSON_OBJS_PATH + objName + ".json"));
            obj.name = objName;
            obj.loadJson();
            objs.put(objName, obj);
        }
        return obj;
    }

    public int getIndex(String objName) {
        if (objName != null) {
            for (int i = objects.length - 1; i >= 0; i --) {
                if (objects[i].name.equals(objName)) {
                    return i;
                }
            }
        }
        return -1;
    }
    // Json end

    /*
    public ObjData getObj(String objName) {
        return objs.get(objName);
    }
    */

    private void readLevel(String name) {
        Loader loader = Loader.getLoader();
        DataInputStream dis = Loader.getLoader().openFile(LEVEL_PATH + name + ".lev");
        int i, n;
        try {
            restartLevel = dis.readUTF();
            deadLevel = dis.readUTF();
            nextLevel = dis.readUTF();
            // Загрузка данных обьектов
            HashMap<String, ObjData> oldObjs = objs;
            objs = new HashMap<>();
            //
            n = dis.readUnsignedShort();
            objects = new ObjData[n];
            ObjData obj;
            for (i = 0; i < n; i ++) {
                name = dis.readUTF();
                obj = oldObjs.get(name);
                if (obj == null) {
                    obj = ObjData.loadObj(name);
                } else {
                    oldObjs.remove(name);
                }
                objects[i] = obj;
                objs.put(name, obj);
            }
            oldObjs.forEach((nameO, objD) -> objD.remove());
            oldObjs.clear();
            // Загрузка слоёв
            Layer layerEnd = null;
            Layer layer;
            n = dis.readUnsignedByte();
            layersArray = new Layer[n];
            for (i = 0; i < n; i ++) {
                layer = (Layer) loader.getObj(Layer.class);
                layer.readLayer(dis);
                layersArray[i] = layer;
                //
                if (layerEnd == null) {
                    layerEnd = layers = layer;
                } else {
                    layerEnd.next = layer;
                    layerEnd = layer;
                }
            }
            // Загрузка камер
            Camera lastCam = null, camera;
            n = dis.readUnsignedShort();
            for (i = n - 1; i >= 0; i --) {
                camera = new Camera();
                camera.readCamera(dis);
                //
                if (lastCam == null) {
                    lastCam = cameras = camera;
                } else {
                    lastCam.next = camera;
                    lastCam = camera;
                }
            }
            // Загрузка игроков
            n = dis.readUnsignedByte();
            Player[] oldPlayers = players;
            players = new Player[n];
            Player player;
            for (i = 0; i < n; i ++) {
                player = new Player();
                player.readPlayer(dis);
                if (i < numPlayers || i > NUM_PLAYERS) {
                    if (i < numPlayers) {
                        player.active = true;
                        if (oldPlayers != null) {
                            player.item = oldPlayers[i].item;
                        }
                    }
                    player.init(i < NUM_PLAYERS ?
                            loader.settings.playerKeys[i] : null);
                }
                players[i] = player;
            }
            dis.close();
        } catch (IOException ex) {
            loader.game.log("Cann't load level :\n" + ex.getMessage());
        }
    }

    public void loadLevel(String name) {
        int i;
        Loader loader = Loader.getLoader();
        loader.level.removeAll();
        if (name.equals(currentLevel)) {
            Layer layer = layers;
            while (layer != null) {
                layer.init();
                layer = (Layer) layer.next;
            }
            //
            Camera camera = cameras;
            while (camera != null) {
                camera.init();
                camera = camera.next;
            }
            //
            if (players != null) {
                for (i = 0; i < numPlayers; i ++) {
                    players[i].active = true;
                    players[i].init(loader.settings.playerKeys[i]);
                }
                for (; i < NUM_PLAYERS; i ++) {
                    players[i].active = false;
                }
                for (i = NUM_PLAYERS; i < players.length; i ++) {
                    players[i].init(null);
                }
            }
        } else {
            if (players != null) {
                for (i = players.length - 1; i >= 0; i --) {
                    players[i].remove();
                }
            }
            loader.poolAllObj(layers);
            layers = null;
            cameras = null;
            //
            currentLevel = name;
            readLevel(name);
        }
        camX = camY = 0;
        setCamera(cameras);
        //
        System.gc();
    }

    public ObjData getObj(int objIndx) {
        if (objIndx != 0xFFFF) {
            return objects[objIndx];
        } else {
            return null;
        }
    }

    @Override
    public void init() {
        if (jsonLoadFileName != null) {
            jsonLoad(jsonLoadFileName);
            jsonLoadFileName = null;
        } else {
            loadLevel(START_LEVEL);
        }
        playersItemReset();
    }

    private void playersItemReset() {
        if (players != null) {
            for (Player player : players) {
                player.removeItem();
            }
        }
    }

    public void restartLevel() {
        loadLevel(restartLevel);
        playersItemReset();
    }


    public void nextLevel() {
        loadLevel(nextLevel);
    }

    public void restertAfterDead() {
        loadLevel(deadLevel);
        playersItemReset();
    }

    public void setCamera(Camera camera) {
        curCamera = camera;
        gravity = camera.gravity;
        flipGravity = camera.flipGravity;
        envirFrictionX = camera.envirFrictionX;
        envirFrictionY = camera.envirFrictionY;
    }

    @Override
    public boolean act() {
        Loader loader = Loader.getLoader();
        //
        for (Player player : players) {
            if (player.active && !player.enemy) {
                
            }
        }
        //
        Layer layer = curLayer = layers;
        while (layer != null) {
            layer.act();
            curLayer = layer = (Layer) layer.next;
        }
        //
        int qw = loader.game.width >> 1;
        int qh = loader.game.height >> 1;
        //
        int cenX = 0;
        int cenY = 0;
        int numP = 0;
        for (Player player : players) {
            if (player.active) {
                player.act();
                if (!player.enemy) {
                    int px = player.unit.x;
                    int py = player.unit.y;
                    //
                    if (px > camX + qw) px = camX + qw; else
                    if (px < camX - qw) px = camX - qw;

                    if (py > camY + qh) py = camY + qh; else
                    if (py < camY - qh) py = camY - qh;
                    //
                    player.unit.x = px;
                    player.unit.y = py;
                }
                cenX += player.unit.x;
                cenY += player.unit.y;
                numP ++;
            }
        }
        cenX /= numP;
        cenY /= numP;
        //
        int deltaX = camX - cenX;
        if (deltaX < 0) deltaX = - deltaX;
        int deltaY = camY - cenY;
        if (deltaY < 0) deltaY = - deltaY;
        //
        qw = (int) (loader.game.width * curCamera.moveX_border);
        qh = (int) (loader.game.height * curCamera.moveY_border);
        curCamera.setCenter((deltaX >= qw) ? (cenX > camX ? cenX - qw : cenX + qw) : camX,
                (deltaY >= qh) ? (cenY > camY ? cenY - qh : cenY + qh) : camY);
        return false;
    }

    @Override
    public void draw(Graphics g) {
        Loader loader = Loader.getLoader();
        //
        g.setColor(curCamera.background);
        g.fillRect(0, 0, loader.game.width, loader.game.height);
        //
        Layer layer = curLayer = layers;
        while (layer != null) {
            layer.draw(g);
            curLayer = layer = (Layer) layer.next;
        }
        if (loader.debugCamera) {
            int x = camX - (loader.game.width >> 1);
            int y = camY - (loader.game.height >> 1);
            Game.translate(g, - x, - y);
            g.setColor(Color.GREEN);
            Camera camera = cameras;
            while (camera != null) {
                camera.draw(g);
                camera = camera.next;
            }
            Game.translate(g, x, y);
        }
    }

    @Override
    public void pool() {
        Loader.getLoader().poolAllObj(layers);
        layers = null;
    }

    @Override
    public void keyPressed(int keyCode) {
        //
        if (players != null) {
            for (int i = numPlayers - 1; i >= 0; i --) {
                players[i].keyPressed(keyCode);
            }
        }
    }

    @Override
    public void keyReleased(int keyCode) {
        if (keyCode == KeyEvent.VK_F) flipGravity ^= true;
        if (keyCode == KeyEvent.VK_F5) init();
        if (keyCode == KeyEvent.VK_F6) jsonFastLoad();
        //
        if (players != null) {
            for (int i = numPlayers - 1; i >= 0; i --) {
                players[i].keyReleased(keyCode);
            }
        }
    }
}
