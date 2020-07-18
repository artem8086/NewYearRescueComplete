package art.soft.console;

import java.util.ArrayList;


/**
 *
 * @author Artem
 */
public class Memory extends Command {

    public Memory(Console console) {
        super(console);
        name = "memory";
    }

    @Override
    public boolean runCommand(String[] args) {
        long free = Runtime.getRuntime().freeMemory();
        long total = Runtime.getRuntime().totalMemory();
        console.outln("    Свободно памяти: " + (free >>> 10) + " Кб");
        console.outln("    Использовано памяти: " + ((total - free) >>> 10) + " Кб");
        console.outln("    Всего памяти: " + (total >>> 10) + " Кб");
        return true;
    }

    @Override
    public String getHelp() {
        return "Команда memory \n    - возвращает данные об использовании памяти\n";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        return null;
    }
}
