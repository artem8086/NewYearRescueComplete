package art.soft.model;

import art.soft.Loader;
import art.soft.animation.AnimSet;
import art.soft.animation.Animation;
import art.soft.model.extended.MeshFrame;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class ModelSet extends AnimSet {

    int[] xVerts = new int[0], yVerts = new int[0];

    public AnimSet animSets[];

    short[] x, y;
    float z[];

    int commonVerts;

    MeshData[] meshes;

    public ModelSet(String file, boolean isExtended) {
        Loader loader = Loader.getLoader();
        path = file;
        DataInputStream is = loader.openFile(file + ".vec");
        try {
            int n = is.readUnsignedByte();
            if (n != 0) {
                animSets = new AnimSet[n];
                for (int i = 0; i < n; i ++) {
                    animSets[i] = loader.loadAnimation(is);
                }
            }
            //
            int max = 0;
            commonVerts = is.readUnsignedShort();
            n = is.readUnsignedShort();
            x = new short[n];
            y = new short[n];
            z = new float[n];
            for (int i = 0; i < n; i ++) {
                x[i] = is.readShort();
                y[i] = is.readShort();
                z[i] = is.readFloat();
            }
            n = is.readUnsignedShort();
            meshes = new MeshData[n];
            MeshData mesh;
            for (int i = 0; i < n; i ++) {
                int num = is.readUnsignedShort();
                //
                int time = is.readUnsignedShort();
                if (time != 0xFFFF) {
                    time |= is.readUnsignedShort() << 16;
                }
                //
                int data_size = is.readUnsignedShort();
                if (data_size != 0 && isExtended) {
                    mesh = i != num ?
                            new MeshFrame(meshes[num], is, data_size, time) :
                            new MeshFrame(is, this, data_size, time);
                } else {
                    mesh = i != num ?
                            new MeshData(meshes[num], is, data_size, time) :
                            new MeshData(is, this, data_size, time);
                }
                meshes[i] = mesh;
                if ((time & 0xFFFF) != 0xFFFE) {
                    mesh.zoom = is.readFloat();
                }
                int count = mesh.getUnicVertsCount();
                if (count > max) max = count;
            }
            is.close();
            //
            max += commonVerts;
            xVerts = new int[max];
            yVerts = new int[max];
        } catch (IOException ex) {
            //loader.game.log("Error while loading vector model file!");
            //loader.game.log(ex.getMessage());
        }
    }

    @Override
    public void remove() {
        Loader.getLoader().removeModel(this);
    }

    @Override
    public Animation getAnimation() {
        Loader loader = Loader.getLoader();
        ModelAnim anim = loader.modelAnimsPool;
        if (anim != null) {
            loader.modelAnimsPool = (ModelAnim) anim.next;
            anim.next = null;
            anim.setAnimSet(this);
            return anim;
        }
        return new ModelAnim(this);
    }
}
