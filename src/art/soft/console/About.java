package art.soft.console;

import art.soft.Game;
import java.util.ArrayList;

/**
 *
 * @author Artem
 */
public class About extends Command {

    public About(Console console) {
        super(console);
        name = "about";
    }

    @Override
    public boolean runCommand(String[] args) {
        console.outln(getHelp());
        return true;
    }

    @Override
    public String getHelp() {
        return Game.GAME_TITLE + "\n"
                + "    ArtSoft corp.\n"
                + "    Rabbit is a trademark\n\n"
                + "    " + Game.VERSION_TEXT + "\n\n"
                + "    creator, artist and main developer:\n"
                + "      - Артём Святоха\n\n";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        return null;
    }
}
