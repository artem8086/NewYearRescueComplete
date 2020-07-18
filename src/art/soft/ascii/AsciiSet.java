package art.soft.ascii;

import art.soft.Loader;
import art.soft.animation.AnimSet;
import art.soft.animation.Animation;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class AsciiSet extends AnimSet {

    private byte operations[][];

    private byte data[][];

    public AsciiSet(String path) {
        this.path = path;
        DataInputStream dis = Loader.getLoader().openFile(path + ".ascii");
        try {
            int n = dis.readUnsignedByte();
            operations = new byte[n][];
            int len;
            for (int i = 0; i < n; i++) {
                len = dis.readUnsignedShort();
                operations[i] = new byte[len];
                dis.read(operations[i]);
            }
            //
            n = dis.readUnsignedByte();
            data = new byte[n][];
            for (int i = 0; i < n; i++) {
                len = dis.readUnsignedByte();
                data[i] = new byte[len];
                dis.read(data[i]);
            }
            dis.close();
            //System.out.println("Load:");
            //System.out.println("\tOperations count: " + operations.length);
            //System.out.println("\tData count: " + data.length);
        } catch (IOException ex) {
            //Loader.getLoader().game.log("Error while read from ascii file!");
            //Loader.getLoader().game.log(ex.getMessage());
        }
    }

    public byte[] getOperatons(int n) {
        return operations[n];
    }

    public byte[] getData(int n) {
        return data[n];
    }

    @Override
    public void remove() {
        Loader.getLoader().removeAscii(this);
    }

    @Override
    public Animation getAnimation() {
        Loader loader = Loader.getLoader();
        AsciiAnim anim = loader.asciiAnimsPool;
        if (anim != null) {
            loader.asciiAnimsPool = (AsciiAnim) anim.next;
            anim.next = null;
            anim.setAnimSet(this);
            return anim;
        }
        return new AsciiAnim(this);
    }
}
