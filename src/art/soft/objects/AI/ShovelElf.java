package art.soft.objects.AI;

import art.soft.Loader;
import art.soft.level.Layer;
import art.soft.level.Player;
import art.soft.objects.UnitObj;

/**
 *
 * @author Артём Святоха
 */
public class ShovelElf extends UnitObj {

    @Override
    public boolean act(Layer layer) {
        boolean del = super.act(layer);
        if (hp != 0) {
            boolean flip = isFlipX();
            Player[] players = Loader.getLoader().engine.players;
            int xt = getX()- 40;
            int w = data.width + 80;
            int yt = getY();
            int h = data.height;
            boolean attack = false;
            for (Player player : players) {
                if (player.active && player.enemy == false) {
                    UnitObj unit = player.unit;
                    if (unit.isAlive()) {
                        attack |= unit.contain(xt, yt, w, h);
                        if (attack) flip = x > unit.x;
                    }
                }
            }
            if (attack) {
                setStatus(Player.STATUS_FIRE);
                setFlipX(flip);
            } else {
                setStatus(Player.STATUS_RUN);
                if (hCollObj != null) setFlipX(!flip);
            }
        }
        return del;
    }
}
