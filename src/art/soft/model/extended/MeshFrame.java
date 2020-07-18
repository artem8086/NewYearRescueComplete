package art.soft.model.extended;

import art.soft.model.MeshData;
import art.soft.model.ModelSet;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class MeshFrame extends MeshData {

    private int form;

    private float forceX, forceY;

    private DamageRegion dmgRegs[];

    private BulletPos bullets[];


    public MeshFrame(MeshData ref, DataInputStream is, int data_size, int time_next) throws IOException {
        super(ref, is, data_size, time_next);
    }

    public MeshFrame(DataInputStream is, ModelSet model, int data_size, int time_next) throws IOException {
        super(is, model, data_size, time_next);
    }

    private final static int READ_MASK_FORM = 0x01;
    private final static int READ_MASK_FORCE_X = 0x02;
    private final static int READ_MASK_FORCE_Y = 0x04;
    private final static int READ_MASK_DMG_REGS = 0x08;
    private final static int READ_MASK_BULLETS = 0x10;

    @Override
    protected void readData(DataInputStream is, int data_size) throws IOException {
        int mask = is.readByte();
        if ((mask & READ_MASK_FORM) != 0) {
            form = is.readUnsignedByte();
        }
        if ((mask & READ_MASK_FORCE_X) != 0) {
            forceX = is.readFloat();
        }
        if ((mask & READ_MASK_FORCE_Y) != 0) {
            forceY = is.readFloat();
        }
        if ((mask & READ_MASK_DMG_REGS) != 0) {
            int n = is.readUnsignedByte();
            dmgRegs = new DamageRegion[n];
            for (int i = 0; i < n; i++) {
                (dmgRegs[i] = new DamageRegion()).readDmgReg(is);
            }
        }
        if ((mask & READ_MASK_BULLETS) != 0) {
            int n = is.readUnsignedByte();
            bullets = new BulletPos[n];
            for (int i = 0; i < n; i++) {
                (bullets[i] = new BulletPos()).readBulletPos(is);
            }
        }
    }

    @Override
    public int getForm() {
        return form;
    }

    @Override
    public float getForceX() {
        return forceX;
    }

    @Override
    public float getForceY() {
        return forceY;
    }

    @Override
    public DamageRegion[] getDamageRegions() {
        return dmgRegs;
    }

    @Override
    public BulletPos[] getBulletss() {
        return bullets;
    }
}
