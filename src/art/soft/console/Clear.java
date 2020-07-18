package art.soft.console;

import java.util.ArrayList;


/**
 *
 * @author Artem
 */
public class Clear extends Command {

    public Clear(Console console) {
        super(console);
        name = "clear";
    }

    @Override
    public boolean runCommand(String[] args) {
        console.clear();
        return true;
    }

    @Override
    public String getHelp() {
        return "Команда clear\n    - очищает консоль";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        return null;
    }
    
}
