package art.soft.console;

import java.util.ArrayList;

/**
 *
 * @author Artem
 */
public class Exit extends Command {

    public Exit(Console console) {
        super(console);
        name = "exit";
    }

    @Override
    public boolean runCommand(String[] args) {
        System.exit(0);
        return true;
    }

    @Override
    public String getHelp() {
        return "Команда exit\n    - выход из игры";
    }

    @Override
    public ArrayList<String> getKeys(String[] args) {
        return null;
    }
    
}
