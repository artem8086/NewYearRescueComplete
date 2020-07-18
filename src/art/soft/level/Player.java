package art.soft.level;

import art.soft.objects.UnitObj;
import art.soft.Loader;
import static art.soft.Settings.PLAYER_JUMP;
import static art.soft.Settings.PLAYER_LEFT;
import static art.soft.Settings.PLAYER_RIGHT;
import static art.soft.Settings.PLAYER_TAKE;
import art.soft.animation.AnimSet;
import art.soft.objects.StaticObj;
import art.soft.objects.items.Item;
import art.soft.objsData.UnitData;
import java.awt.event.KeyEvent;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class Player {

    public static final int STATUS_STAND = 0;

    public static final int STATUS_UP = 1;
    public static final int STATUS_DOWN = 2;
    public static final int STATUS_FIRE = 3;
    public static final int STATUS_RUN = 6;

    private static final int MASK_UP =   0x01;
    private static final int MASK_DOWN = 0x02;
    private static final int MASK_RUN =  0x04;
    private static final int MASK_FIRE = 0x08;

    private static final int[] MASK_KEYS = {
        MASK_UP, MASK_DOWN, MASK_RUN, MASK_RUN, MASK_FIRE
    };

    private int curMask;

    private UnitData[] dataSets;

    public int unitX, unitY;
    public int flipMask;

    public Layer unitLayer;

    public UnitObj unit;

    public Item item;
    
    HeroHUD heroHUD;

    public int shiftHP = 1;

    private int[] keys;
    
    public boolean active, enemy, control;
    private boolean canControl;

    private AnimSet iconAnim;
    public int iconX, iconY, iconNum;

    // Json data
    public int layer;

    public String icon;

    public String[] sets;

    public void initSets() {
        int i = sets.length;
        dataSets = new UnitData[i];
        for (i --; i >= 0; i --) {
            dataSets[i] = (UnitData) Loader.getLoader().engine.getObj(sets[i]);
        }
        sets = null;
    }

    public void jsonInit(Layer layers[]) {
        Loader loader = Loader.getLoader();
        unitLayer = loader.engine.layersArray[layer];
        //
        initSets();
        //
        iconAnim = loader.loadAnimation(icon);
    }

    public void writePlayer(DataOutputStream dos) throws IOException {
        dos.writeInt(unitX);
        dos.writeInt(unitY);
        dos.writeByte(layer);
        dos.writeBoolean(active);
        dos.writeBoolean(enemy);
        dos.writeBoolean(control);
        //
        dos.writeByte(dataSets.length);
        for (UnitData set : dataSets) {
            dos.writeShort(Loader.getLoader().engine.getIndex(set.name));
        }
        //
        Loader.getLoader().writeAnimation(icon, dos);
        dos.writeShort(iconX);
        dos.writeShort(iconY);
        dos.writeByte(iconNum);
        dos.writeShort(shiftHP);
    }
    // Json end

    public void readPlayer(DataInputStream dis) throws IOException {
        Loader loader = Loader.getLoader();
        int i, n;
        unitX = dis.readInt();
        unitY = dis.readInt();
        layer = i = dis.readUnsignedByte(); // Json
        unitLayer = loader.engine.layersArray[i];
        //
        active = dis.readBoolean();
        enemy = dis.readBoolean();
        control = dis.readBoolean();
        //
        n = dis.readUnsignedByte();
        dataSets = new UnitData[n];
        for (i = 0; i < n; i ++) {
            dataSets[i] = (UnitData) loader.engine.getObj(dis.readUnsignedShort());
        }
        //
        iconAnim = loader.loadAnimation(dis);
        iconX = dis.readShort();
        iconY = dis.readShort();
        iconNum = dis.readUnsignedByte();
        shiftHP = dis.readUnsignedShort();
    }

    public void init(int[] keys) {
        this.keys = keys;
        canControl = control;
        curMask = 0;
        //
        if (active) {
            activatePlayer();
        }
    }

    public void activatePlayer() {
        Loader loader = Loader.getLoader();
        HeroHUD hero = (HeroHUD) loader.getObj(HeroHUD.class);
        hero.setAnimSet(iconAnim);
        hero.setPos(iconX, iconY);
        hero.setAnimNum(iconNum);
        hero.setPlayer(this);
        heroHUD = hero;
        loader.level.add(hero);
        //
        UnitData set = null;
        if (item != null) {
            set = getSet(item.getSetName());
        }
        if (set == null) set = dataSets[0];
        unit = (UnitObj) (set.createObj(unitLayer, unitX, unitY, 0, null));
        unit.owner = this;
        active = true;
    }

    public UnitData getSet(String name) {
        for (UnitData set : dataSets) {
            if (name.equals(set.setName)) {
                return set;
            }
        }
        return null;
    }

    public UnitData[] getSets() {
        return dataSets;
    }

    public void setStdSet() {
        unit.setData(dataSets[0]);
    }

    public void setSet(UnitData set) {
        unit.setData(set);
    }

    public void setSet(String name) {
        unit.setData(getSet(name));
    }

    public void setSet(int setNum) {
        unit.setData(dataSets[setNum]);
    }

    public void setCanControl(boolean control) {
        canControl = control;
        curMask = 0;
        unit.setStatus(STATUS_STAND);
    }

    public boolean getCanControl() {
        return canControl;
    }

    public void act() {
        if (unit.getHP() == 0) {
            canControl = false;
            if (!enemy && unit.data == null) {
                Loader.getLoader().engine.restertAfterDead();
            }
        } else {
            if (item != null) item.act();
        }
    }

    public int getStatus() {
        return mask2status(curMask);
    }

    private int mask2status(int mask) {
        int status = STATUS_STAND;
        if ((mask & MASK_FIRE) != 0) status += STATUS_FIRE;
        if ((mask & MASK_RUN) != 0) status += STATUS_RUN;
        if ((mask & MASK_DOWN) != 0) status += STATUS_DOWN;
        else if ((mask & MASK_UP) != 0) status += STATUS_UP;
        return status;
    }

    /*
     * Вызывается при нажатии клавиши
     * @key код клавиши
     */
    public void keyPressed(int key) {
        if (canControl) {
            int newMask = curMask;
            for (int i = MASK_KEYS.length - 1; i >= 0; i --) {
                if (key == keys[i]) {
                    newMask |= MASK_KEYS[i];
                    break;
                }
            }
            if (newMask != curMask) {
                if (key == keys[PLAYER_LEFT]) unit.setFlipX(true);
                else if (key == keys[PLAYER_RIGHT]) unit.setFlipX(false);
                curMask = newMask;
                unit.setStatus(mask2status(newMask));
            }
            if (key == keys[PLAYER_JUMP]) unit.setJump(true);
        }
    }

    /*
     * Вызывается при отпускании клавиши
     * @key код клавиши
     */
    public void keyReleased(int key) {
        if (canControl) {
            /*if (key == keys[PLAYER_TAKE]) {
            
            }*/
            int newMask = curMask;
            for (int i = MASK_KEYS.length - 1; i >= 0; i --) {
                if (key == keys[i]) {
                    newMask &= ~ MASK_KEYS[i];
                    break;
                }
            }
            if (newMask != curMask) {
                curMask = newMask;
                unit.setStatus(mask2status(newMask));
            }
            if (key == keys[PLAYER_JUMP]) unit.setJump(false);
            if (key == keys[PLAYER_TAKE]) getItem();
            if (key == KeyEvent.VK_D) unit.addHP(-100);
            if (key == KeyEvent.VK_W) unit.setForce(0f, 0f);
        }
    }

    public void getItem() {
        StaticObj pred = null;
        StaticObj nextObj;
        StaticObj obj = (StaticObj) unitLayer.objs;
        int x = unit.getX();
        int y = unit.getY();
        int w = unit.data.width;
        int h = unit.data.height;
        boolean drop = true;
        while (obj != null) {
            nextObj = (StaticObj) obj.next;
            if (obj != unit && obj.data.isItem &&
                    obj.contain(x, y, w, h)) {
                //
                drop = false;
                if (((Item) obj).playerGet(this)) {
                    if (pred == null) unitLayer.objs = nextObj;
                    else pred.next = nextObj;
                    obj.next = null;
                    break;
                }
            } else {
                pred = obj;
            }
            obj = nextObj;
        }
        if (drop) dropItem();
    }

    public HeroHUD getHUD() {
        return heroHUD;
    }

    public void removeItem() {
        if (item != null) {
            setStdSet();
            item.pool();
            item = null;
        }
    }

    public void dropItem() {
        if (item != null) {
            item.setPos(unit.x, unit.y);
            item.setFlipX(unit.isFlipX());
            unitLayer.add(item);
            setStdSet();
            item = null;
        }
    }

    public void remove() {
        iconAnim.remove();
    }
}
