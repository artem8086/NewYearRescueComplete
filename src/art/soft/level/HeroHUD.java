package art.soft.level;

import art.soft.Loader;
import art.soft.animation.Animation;
import art.soft.ascii.BitFont;
import art.soft.gameObjs.SimpleAnimation;
import art.soft.objects.items.Item;
import art.soft.objsData.ItemData;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class HeroHUD extends SimpleAnimation {

    private int old_hp = 0;
    private Animation iconAnim;
    private Player player;

    public void setPlayer(Player player) {
        this.player = player;
    }

    @Override
    public void draw(Graphics g) {
        int hp = player.unit.getHP();
        if (hp != 0) {
            animation.play(g, x, y);
            //
            if (hp > 0) {
                int shift = player.shiftHP;
                if (shift >=0) hp <<= shift; else hp >>= - shift;
                int old = old_hp;
                if (old > hp) {
                    g.setColor(Color.WHITE);
                    g.fillRect(x + 42 + hp, y - 24, old - hp, 20);
                    old_hp -= 4;
                } else {
                    old_hp = hp;
                }
                //
                g.setColor(Color.RED);
                g.fillRect(x + 42, y - 24, hp, 20);
                //
                int max = player.unit.getMaxHP();
                if (shift >=0) max <<= shift; else max >>= - shift;
                g.setColor(Color.BLACK);
                g.drawRect(x + 40, y - 26, max + 4, 24);
            }
            //
            if (player.item != null) {
                if (iconAnim != null) {
                    iconAnim.incAnim(true);
                    //
                    iconAnim.play(g, x + 42, y + 2);
                }
                int ammo = player.item.getAmmo();
                if (ammo >= 0) {
                    g.setColor(Color.BLACK);
                    BitFont font = Loader.getLoader().font;
                    font.drawChar(g, 'x', x + 84, y + 20, 2);
                    font.drawString(g, Integer.toString(ammo),
                            x + 84 + (BitFont.FONT_WIDTH << 2), y + 20, ammo);
                }
            }
        }
    }

    public void setItem(Item item) {
        ItemData data = (ItemData) item.data;
        if (iconAnim != null) {
            iconAnim.pool();
        }
        if (data.icon != null) {
            iconAnim = data.icon.getAnimation();
            iconAnim.setAnimation(data.iconAnim);
        } else {
            iconAnim = null;
        }    
    }

    @Override
    public void pool() {
        super.pool();
        player = null;
    }
}
