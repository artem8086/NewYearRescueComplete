package art.soft.model;

import art.soft.Game;
import art.soft.Loader;
import art.soft.model.extended.BulletPos;
import art.soft.model.extended.DamageRegion;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class MeshData {
    
    float zoom;

    Face[] faces;

    private int start_end;

    private int time_next;

    private int transform[];

    public MeshData(MeshData ref, DataInputStream is, int data_size, int time_next) throws IOException {
        this.time_next = time_next;
        start_end = ref.start_end;
        readData(is, data_size);
        faces = ref.faces;
    }

    public MeshData(DataInputStream is, ModelSet model, int data_size, int time_next) throws IOException {
        this.time_next = time_next;
        int time = time_next & 0xFFFF;
        readData(is, data_size);
        if (time != 0xFFFE) {
            start_end = is.readInt();
            int n = is.readUnsignedByte();
            if (n != 0) {
                transform = new int[n+1];
                for (int i = 0; i < n; i++) {
                    transform[i] = is.readInt();
                }
                transform[n] = -1;
            }
            //
            n = is.readUnsignedShort();
            faces = new Face[n];
            for (int i = 0; i < n; i ++) {
                int vn = is.readUnsignedByte();
                Face face;
                switch (vn) {
                    case 0: face = new FaceAnim(); break;
                    case 1: face = new DrawDot(); break;
                    case 2: face = new DrawLine(); break;
                    case 3: face = new DrawRect(); break;
                    case 4: face = new FillRect(); break;
                    case 5: face = new DrawRoundRect(); break;
                    case 6: face = new FillRoundRect(); break;
                    case 7: face = new DrawArc(); break;
                    case 8: face = new FillArc(); break;
                    default:
                        face = new FillPolygon(vn - 9 + 3);
                }
                face.read(is);
                face.normal = is.readUnsignedShort();
                faces[i] = face;
            }
        }
    }

    protected void readData(DataInputStream is, int data_size) throws IOException {
        is.skip(data_size);
    }

    int getUnicVertsCount() {
        return (start_end >>> 16) + (start_end & 0xFFFF);
    }

    public int getTime() {
        return time_next & 0xFFFF;
    }

    public int getNext() {
        return time_next >>> 16;
    }

    public void draw(Graphics g, ModelAnim anim, int x, int y, boolean flipX, boolean flipY) {
        ModelSet model = anim.getData();
        Game game = Loader.getLoader().game;
        {
            int xL = Game.translateX + (game.width >> 1);
            int yL = Game.translateY + (game.height>> 1);
            x -= xL; y -= yL;
            // Расчет вершин
            float z;
            int start = 0;
            int end = model.commonVerts;
            int offset = 0;
            int tValue;
            int curTr = 1;
            int tVert = 0;
            int tIndx = -1;
            if (transform != null) {
                tValue = transform[0];
                tIndx = tValue & 0xFFFF;
                tVert = tValue >>> 16;
            }
            int mx, my, aTime = anim.getTime();
            //
            for (int i = 1; i >= 0; i --) {
                for (; start < end; start++) {
                    z = model.z[start];
                    mx = model.x[start];
                    my = model.y[start];
                    if (start == tIndx) {
                        z += model.z[tVert] * aTime;
                        mx += model.x[tVert] * aTime;
                        my += model.y[tVert] * aTime;
                        //
                        tValue = transform[curTr++];
                        tIndx = tValue & 0xFFFF;
                        tVert = tValue >>> 16;
                    }
                    if (flipX) mx = - mx;
                    if (flipY) my = - my;
                    z *= zoom;
                    model.xVerts[offset] = (int) ((mx + x) * z) + xL;
                    model.yVerts[offset] = (int) ((my + y) * z) + yL;
                    offset++;
                }
                //
                start = start_end & 0xFFFF;
                end = start_end >>> 16;
            }
        }
        //
        // Отрисовка вершин
        //
        int norm;
        for (Face face : faces) {
            norm = face.normal;
            if (norm != 0) {
                int xn = (byte) norm;
                int yn = (byte) (norm >> 8);
                if (x * (flipX ? - xn : xn) + y * (flipY ? - yn : yn) < 0) continue;
            }
            face.draw(anim, g, flipX, flipY);
        }
    }

    public int getForm() {
        return 0;
    }

    public float getForceX() {
        return 0f;
    }

    public float getForceY() {
        return 0f;
    }

    public DamageRegion[] getDamageRegions() {
        return null;
    }

    public BulletPos[] getBulletss() {
        return null;
    }
}
