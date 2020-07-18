package art.soft.objects.items;

import art.soft.level.Player;
import art.soft.model.ModelSet;
import art.soft.objsData.ItemData;

/**
 *
 * @author Артём Святоха
 */
public class Skin extends Item {

    @Override
    public boolean playerGet(Player player) {
        ModelSet sets = player.unit.mData.animData;
        if (sets.animSets[ammo] != null) {
            sets.animSets[ammo].remove();
        }
        sets.animSets[ammo] = ((ItemData) data).animData;
        ((ItemData) data).animData.loadIndx ++;
        player.setSet(player.unit.mData);
        return false;
    }
}
