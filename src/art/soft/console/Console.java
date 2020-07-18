package art.soft.console;

import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

/**
 *
 * @author Artem
 */
public class Console {

    public final static String CONSOLE_VERSION = "v0.0.6";

    private String[] args;

    public final ConFrame conFrame;

    final HashMap<String, Command> commands = new HashMap<>();

    public Console() {
        ConFrame.setStyle();
        conFrame = new ConFrame();
        initCommands();
    }

    public void clear() {
        conFrame.getConOut().setText("");
    }

    public boolean isFocus() {
        return conFrame.isFocused();
    }

    public void outln() {
        out("\n");
    }

    public void outln(String s) {
        out(s + "\n");
    }

    public void outln(Object o) {
        out(o == null ? "null\n" : o.toString() + "\n");
    }

    public void out(Object o) {
        out(o == null ? "null" : o.toString());
    }

    public void out(String s) {
        JTextArea conOut = conFrame.getConOut();
        conOut.append(s);
        conOut.setCaretPosition(conOut.getDocument().getLength());
    }

    public void startConsole() {
        conFrame.setVisible(!conFrame.isVisible());
        conFrame.requestFocus();
    }

    public void start() {
        conFrame.setCosole(this);
        outln("        ╔══════════════════════════════════════════════════════════╗");
        outln("        ║     ___    ____  ______    _____  ____   ______ ______   ║");
        outln("        ║    /   |  / __ \\/_  __/   / ___/ / __ \\ / ____//_  __/   ║");
        outln("        ║   / /| | / /_/ / / /      \\__ \\ / / / // /__    / /      ║");
        outln("        ║  / ___ |/ _, _/ / /      ___/ // /_/ // ___/   / /       ║");
        outln("        ║ /_/  |_/_/ |_| /_/      /____/ \\____//_/      /_/  inc.  ║");
        outln("        ║                                                          ║");
        outln("        ║                  Console " + CONSOLE_VERSION + " start...                 ║");
        outln("        ╠══════════════════════════════════════════════════════════╣");
        outln("        ║             Type 'help' to get command list              ║");
        outln("        ╠══════════════════════════════════════════════════════════╣");
        outln("        ║      Type 'help <command>' to get more information       ║");
        outln("        ╚══════════════════════════════════════════════════════════╝");
        outln();
    }

    public void runCommand(String in, boolean asynch) {
        outln("> " + in);
        if (in != null) {
            args = in.trim().split("\\s+");
            if (asynch) SwingUtilities.invokeLater(() -> runComnand());
            else runComnand();
        }
    }

    private void runComnand() {
        outln();
        if (args!=null && args.length > 0 && !"".equals(args[0])) {
            String command = args[0].toLowerCase();
            Command com = get(command);
            if (com == null) {
                outln("Команда " + command + " не найдена!");
            } else if (!com.runCommand(args)) {
                outln("\nКоманда " + command + " завершилась с ошибкой!");
            }
        }
        outln();
    }

    public Command get(String name) {
        return commands.get(name.toLowerCase());
    }

    public final void initCommands() {
        commands.clear();
        commandPut(new About(this));
        commandPut(new Clear(this));
        commandPut(new Debug(this));
        commandPut(new Help(this));
        commandPut(new Immortal(this));
        commandPut(new LoadJson(this));
        commandPut(new Memory(this));
        commandPut(new Exit(this));
    }

    ArrayList<String> keysCommands = new ArrayList<>();

    private void commandPut(Command com) {
        commands.put(com.name, com);
        keysCommands.add(com.name);
    }
}
