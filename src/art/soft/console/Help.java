package art.soft.console;

import java.util.ArrayList;

/**
 *
 * @author Artem
 */
public class Help extends Command {

    public Help(Console console) {
        super(console);
        name = "help";
    }

    @Override
    public boolean runCommand(String[] args) {
        if (args.length > 1) {
            Command com = console.get(args[1]);
            if (com == null) {
                console.outln("Команда " + args[1] + " не найдена!");
            } else
                console.outln(com.getHelp());
        } else {
            console.outln("Список команд:");
            for (String key : console.keysCommands) {
                console.outln("    - " + key);
            }
        }
        return true;
    }

    @Override
    public String getHelp() {
        return "Команда help [<команда>]\n    - "
            + "без параметров возвращает список всех команд\n\n"
            + "    - возвращает справочную информацию\n"
            + "      по команде указаной в параметре\n";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        if (args.length == 2) {
            return console.keysCommands;
        }
        return null;
    }
    
}
