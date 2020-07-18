package art.soft.model;

import art.soft.animation.Animation;
import java.awt.Graphics;
import java.io.DataInputStream;
import java.io.IOException;

/**
 *
 * @author Артём Святоха
 */
public class FaceAnim extends Face {

    private int anim_frame_flip, vert;

    @Override
    public void read(DataInputStream is) throws IOException {
        int anim = is.readUnsignedByte();
        int frame = is.readUnsignedByte();
        int flip = is.readUnsignedByte();
        int vert = is.readUnsignedShort();
        anim_frame_flip = anim | (frame << 8) | (flip << 16);
        this.vert = vert;
    }

    @Override
    public void draw(ModelAnim animation, Graphics g, boolean flipX, boolean flipY) {
        ModelSet model = animation.getData();
        int animNum = anim_frame_flip & 0xFF;
        int frame = (anim_frame_flip >> 8) & 0xFF;
        flipX ^= (anim_frame_flip & 0x10000) != 0;
        flipY ^= (anim_frame_flip & 0x20000) != 0;
        Animation anim = animation.getAnimation(animNum);
        anim.setAnimation(frame);
        if (!flipX && !flipY) {
            anim.play(g, model.xVerts[vert], model.yVerts[vert]);
        } else {
            anim.play(g, model.xVerts[vert], model.yVerts[vert], flipX, flipY);
        }
    }
}
