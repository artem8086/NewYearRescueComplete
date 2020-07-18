package art.soft.console;

import art.soft.Loader;
import art.soft.level.Player;
import art.soft.level.gameEngine;
import art.soft.objects.UnitObj;
import art.soft.objsData.Dynamic;
import art.soft.objsData.UnitData;
import java.util.ArrayList;

/**
 *
 * @author Артём Святоха
 */
public class Immortal extends Command {

    public Immortal(Console console) {
        super(console);
        name = "immortal";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        return null;
    }

    @Override
    public boolean runCommand(String[] args) {
        gameEngine engine = Loader.getLoader().engine;
        if (engine.players != null) {
            int i = 0;
            for (Player player : engine.players) {
                UnitObj unit = player.unit;
                UnitData data = unit.mData;
                Dynamic form = (Dynamic) unit.data;
                //form.coll_mask = 0;
                //form.collision = 0;
                form.hp = -1;
                form.mass = 0f;
                form.max_speedY = 20;
                form.max_speedX = 40;
                //
                unit.hp = -1;
                /*data.forceX[6] = data.forceY[2] = data.forceY[2 + data.inAirStatus]
                        = data.forceY[8] = data.forceY[8 + data.inAirStatus]
                        = data.forceX[6 + data.inAirStatus] = 5;
                data.forceY[1] = data.forceY[1 + data.inAirStatus]
                        = data.forceY[7] = data.forceY[7 + data.inAirStatus] = -5;*/
                //
                i ++;
                if (i == engine.numPlayers) break;
            }
        }
        return true;
    }

    @Override
    public String getHelp() {
        return "Команда help [<команда>]\n    - Делает пресонажей игроков неуязвимыми";
    }
    
}
