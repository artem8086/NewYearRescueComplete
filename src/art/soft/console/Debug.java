package art.soft.console;

import art.soft.Loader;
import java.util.ArrayList;

/**
 *
 * @author Артём Святоха
 */
public class Debug extends Command {

    private final ArrayList<String> list = new ArrayList<>();

    private boolean setValue;

    public Debug(Console console) {
        super(console);
        name = "debug";
        //
        list.add("/on");
        list.add("/off");
        list.add("damageRegion");
        list.add("decoration");
        list.add("dynamic");
        list.add("polyModel");
        list.add("static");
        list.add("camera");
    }

    @Override
    public boolean runCommand(String[] args) {
        Loader loader = Loader.getLoader();
        if (args.length > 1) {
            if (args.length == 2) {
                if (args[1].equals("/on")) {
                    loader.debugDamageRegion =
                    loader.debugDecoration =
                    loader.debugDynamic =
                    loader.debugPolyModel =
                    loader.debugStatic =
                    loader.debugCamera = true;
                    return true;
                } else
                if (args[1].equals("/off")) {
                    loader.debugDamageRegion =
                    loader.debugDecoration =
                    loader.debugDynamic =
                    loader.debugPolyModel =
                    loader.debugStatic =
                    loader.debugCamera = false;
                    return true;
                }
            }
            setValue = true;
            for (int i = 1; i < args.length; i ++) {
                setDebugKey(args[i]);
            }
        } else {
            console.outln(" - damageRegion: " + loader.debugDamageRegion);
            console.outln(" -   decoration: " + loader.debugDecoration);
            console.outln(" -    polyModel: " + loader.debugPolyModel);
            console.outln(" -      dynamic: " + loader.debugDynamic);
            console.outln(" -       static: " + loader.debugStatic);
            console.outln(" -       camera: " + loader.debugCamera);
        }
        return true;
    }

    void setDebugKey(String key) {
        Loader loader = Loader.getLoader();
        switch (key.toLowerCase()) {
            case "/on": setValue = true; break;
            case "/off": setValue = false; break;
            case "damageregion": loader.debugDamageRegion = setValue; break;
            case "decoration": loader.debugDecoration = setValue; break;
            case "polymodel": loader.debugPolyModel = setValue; break;
            case "dynamic": loader.debugDynamic = setValue; break;
            case "static": loader.debugStatic = setValue; break;
            case "camera": loader.debugCamera = setValue; break;
            default:
                console.outln("Неизвестный ключ: " + key);
        }
    }

    @Override
    public String getHelp() {
        return "Команда Debug </on> </off> [<ключи>]\n    - "
            + "без параметров возвращает статус отладки\n\n"
            + "    - параметр /on включает отладку\n"
            + "    - параметр /off выключает отладку\n";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        return list;
    }
}
