package art.soft.console;

import art.soft.Loader;
import static art.soft.level.gameEngine.JSON_LEVEL_PATH;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Артём Святоха
 */
public class LoadJson extends Command {

    private ArrayList<String> fileList = new ArrayList<>();

    public LoadJson(Console console) {
        super(console);
        name = "loadjson";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        if (args.length == 2) {
            fileList.clear();
            File file = new File(JSON_LEVEL_PATH);
            String[] files = file.list();
            for (String name : files) {
                if (name.endsWith(".json")) {
                    fileList.add(name.substring(0, name.length() - 5));
                }
            }
            return fileList;
        } else {
            return null;
        }
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length > 1) {
            Loader loader = Loader.getLoader();
            loader.engine.numPlayers = 1;
            loader.engine.jsonLoadFileName = args[1];
            if (loader.game.getStage() == loader.level) {
                loader.engine.init();
            } else {
                loader.game.setStage(loader.level);
            }
        } else {
            console.outln(getHelp());
        }
        return true;
    }

    @Override
    public String getHelp() {
        return "Команда loadjson [<json файл>]\n"
            + "    - загружает уровень из json файла\n"
            + "      указанный в параметре\n";
    }    
}
