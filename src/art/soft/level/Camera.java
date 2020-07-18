package art.soft.level;

import art.soft.Loader;
import art.soft.objsData.ObjData;
import java.awt.Color;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class Camera {
    
    private static final int MASK_UP = 0x1;
    private static final int MASK_DOWN = 0x2;
    private static final int MASK_LEFT = 0x4;
    private static final int MASK_RIGHT = 0x8;

    public Color background;

    public int x, y, w, h;

    public int add_mask;
    public int del_mask;
    public int move_mask;
    public int block_mask;

            // Данные о среде
    public float gravity = 0.98f; // Сила притяжения
    public boolean flipGravity = false; // Гравитация действует в обратном направлении
                    // при этом все динамические обьекты переварачивются
    public float envirFrictionX = 0.95f; // сопративление среды по оси X
    public float envirFrictionY = 0.999f; // сопративление среды по оси Y
            //

    public float moveX_border = 0.16f, moveY_border = 0.16f;
    public boolean lock = false;

    private boolean statusLock;

    public Camera next;

    // Json data
    public int color = 0xFF;

    public void jsonLoad() {
        background = new Color(color);
    }

    public void writeCamera(DataOutputStream dos) throws IOException {
        dos.writeBoolean(lock);
        //
        dos.writeByte(color);
        dos.writeByte(color >> 8);
        dos.writeByte(color >> 16);
        //
        dos.writeInt(x);
        dos.writeInt(y);
        dos.writeInt(w);
        dos.writeInt(h);
        dos.writeByte(add_mask | (del_mask << 4));
        dos.writeByte(move_mask | (block_mask << 4));
        dos.writeBoolean(flipGravity);
        dos.writeFloat(gravity);
        dos.writeFloat(envirFrictionX);
        dos.writeFloat(envirFrictionY);
        dos.writeFloat(moveX_border);
        dos.writeFloat(moveY_border);
    }
    // Json end

    public void readCamera(DataInputStream dis) throws IOException {
        statusLock = lock = dis.readBoolean();
        // Загрузка фонового цвета
        int m = dis.readUnsignedByte();
        m |= dis.readUnsignedByte() << 8;
        m |= dis.readUnsignedByte() << 16;
        background = new Color(m);
        //
        x = dis.readInt();
        y = dis.readInt();
        w = dis.readInt();
        h = dis.readInt();
        m = dis.readUnsignedByte();
        add_mask = m & 0xF;
        del_mask = m >>> 4;
        m = dis.readUnsignedByte();
        move_mask = m & 0xF;
        block_mask = m >>> 4;
        flipGravity = dis.readBoolean();
        gravity = dis.readFloat();
        envirFrictionX = dis.readFloat();
        envirFrictionY = dis.readFloat();
        moveX_border = dis.readFloat();
        moveY_border = dis.readFloat();
    }

    public void init() {
        statusLock = lock;
    }

    public boolean contain(ObjData data, int cenX, int cenY, int objX, int objY, int x1, int y1, int mask) {
        if ((mask & MASK_UP) != 0) {
            if (objY - cenY + data.height < y1) return false;
        }
        if ((mask & MASK_LEFT) != 0) {
            if (objX - cenX + data.width < x1) return false;
        }
        Loader loader = Loader.getLoader();
        if ((mask & MASK_DOWN) != 0) {
            int y2 = loader.game.height + y1;
            if (objY - cenY > y2) return false;
        }
        if ((mask & MASK_RIGHT) != 0) {
            int x2 = loader.game.width + x1;
            if (objX - cenX > x2) return false;
        }
        return true;
    }

    public boolean contain(int x, int y) {
        return x >= this.x && x <= this.x + w && y >= this.y && y <= this.y + h;
    }

    public void setLock(boolean lock) {
        statusLock = lock;
    }

    public boolean getLock() {
        return statusLock;
    }

    public void setCenter(int x, int y) {
        gameEngine engine = Loader.getLoader().engine;
        if (!statusLock) {
            if (next != null) {
                if (next.contain(x, y)) engine.setCamera(next);
            } else {
                engine.nextLevel();
            }
        }
        //
        if (x < this.x) x = this.x; else
        if (x > this.x + w) x = this.x + w;
        //
        if (y < this.y) y = this.y; else
        if (y > this.y + h) y = this.y + h;
        //
        int mask = block_mask;
        if (mask != 0) {
            int camX = engine.camX;
            int camY = engine.camY;
            if ((mask & MASK_UP) != 0 && y < camY) y = camY;
            if ((mask & MASK_DOWN) != 0 && y > camY) y = camY;
            if ((mask & MASK_LEFT) != 0 && x < camX) x = camX;
            if ((mask & MASK_RIGHT) != 0 && x > camX) x = camX;
        }
        engine.camX = x;
        engine.camY = y;
    }

    public void draw(Graphics g) {
        g.drawRect(x, y, w, h);
    }
}
