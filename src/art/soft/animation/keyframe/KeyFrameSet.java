package art.soft.animation.keyframe;

import art.soft.Loader;
import art.soft.animation.AnimSet;
import art.soft.animation.Animation;
import java.awt.Image;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class KeyFrameSet extends AnimSet {

    Image animPack;
    
    KeyFrameData data[];

    public KeyFrameSet(Image img, String path) {
        this.path = path;
        animPack = img;
        
        DataInputStream dis = Loader.getLoader().openFile(path + ".pak");
        try {
            int num = dis.readUnsignedByte();
            data = new KeyFrameData[num];
            for (int i = 0; i < num; i ++) {
                KeyFrameData aData = data[i] = new KeyFrameData();
                aData.frameTime = dis.readUnsignedByte();
                int nFrames = dis.readShort();
                aData.frames = new KeyFrame[nFrames];
                for (int j = 0; j < nFrames; j ++) {
                    KeyFrame frame = aData.frames[j] = new KeyFrame();
                    frame.x1 = dis.readShort();
                    frame.y1 = dis.readShort();
                    frame.x2 = dis.readShort();
                    frame.y2 = dis.readShort();
                    frame.centerX = dis.readShort();
                    frame.centerY = dis.readShort();
                    frame.width = dis.readShort();
                    frame.height = dis.readShort();
                    if (frame.width <= 0 || frame.height <= 0) {
                        aData.frames[j] = null;
                    }
                }
            }
            dis.close();
        } catch (IOException ex) {
            //Loader.getLoader().game.log("Error while read from animation file!");
            //Loader.getLoader().game.log(ex.getMessage());
        }
    }

    @Override
    public void remove() {
        Loader.getLoader().removeKeyFrame(this);
    }

    @Override
    public Animation getAnimation() {
        Loader loader = Loader.getLoader();
        KeyFrameAnim anim = loader.frameAnimsPool;
        if (anim != null) {
            loader.frameAnimsPool = (KeyFrameAnim) anim.next;
            anim.next = null;
            anim.setAnimSet(this);
            return anim;
        }
        return new KeyFrameAnim(this);
    }
}
