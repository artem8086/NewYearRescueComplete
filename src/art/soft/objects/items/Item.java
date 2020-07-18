package art.soft.objects.items;

import art.soft.level.Player;
import art.soft.objects.DynamicObj;
import art.soft.objsData.ItemData;
import art.soft.objsData.ObjData;
import art.soft.objsData.UnitData;

/**
 *
 * @author Артём Святоха
 */
public class Item extends DynamicObj {

    protected Player player;
    protected int ammo;

    @Override
    public void init(ObjData data, ObjData drop, int flipMask) {
        super.init(data, drop, flipMask);
        ammo = ((ItemData) data).ammo;
    }

    public boolean playerGet(Player player) {
        ItemData iData = (ItemData) data;
        if (player.item != null && player.item.data == data) {
            player.item.addAmmo(ammo);
            pool();
            return true;
        } else {
            UnitData uData = player.getSet(iData.setName);
            if (uData != null) {
                this.player = player;
                player.dropItem();
                player.item = this;
                player.setSet(uData);
                player.getHUD().setItem(this);
                return true;
            }
        }
        return false;
    }

    public int getAmmo() {
        return ammo;
    }

    public void decAmmo(int ammo) {
        if (this.ammo >= 0) {
            this.ammo -= ammo;
            if (this.ammo <= 0) {
                this.ammo = 0;
                player.removeItem();
            }
        }
    }

    public void addAmmo(int ammo) {
        if (this.ammo >= 0) {
            this.ammo += ammo;
        }
    }

    public void rangeFire(int amount) {
        if (amount > 0) decAmmo(1);
    }

    public void meleeFire(int amount) {
        if (amount > 0) decAmmo(1);
    }

    public String getSetName() {
        return ((ItemData) data).setName;
    }
}
