package art.soft.console;

import java.awt.Point;
import java.util.ArrayList;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;

/**
 *
 * @author Artem
 */
public class AutoComplete implements DocumentListener {

    private final Point pTemp = new Point();
    private final Console console;
    private final JTextField textField;
    private final JPopupMenu findMenu;
    private final JList findList;
    public boolean isEdit = true;

    public AutoComplete(JTextField textField, Console console, JPopupMenu findMenu, JList findList) {
        this.textField = textField;
        this.console = console;
        this.findMenu = findMenu;
        this.findList = findList;
    }

    @Override
    public void changedUpdate(DocumentEvent ev) {
        //doAutoComplete(ev);
    }

    @Override
    public void removeUpdate(DocumentEvent ev) {
        if (isEdit) hide();
    }

    @Override
    public void insertUpdate(DocumentEvent ev) {
        doAutoComplete(ev);
    }

    private void doAutoComplete(DocumentEvent ev) {
        if (!isEdit) return;
        if (ev.getLength() != 1) {
            hide(); return;
        }

        int pos = ev.getOffset();
        String content = null;
        try {
            content = textField.getText(0, pos + 1);
        } catch (BadLocationException e) {
            e.printStackTrace();
            hide(); return;
        }

        if (content.length() == 0) {
            hide(); return;
        }
        if (Character.isSpaceChar(content.charAt(pos))) {
            content = content.trim();
            if (!"".equals(content)) content += " c";
            String[] args = content.split("\\s+");
            args[args.length - 1] = "";
            ArrayList<String> list = getKeys(args);
            if (list != null) {
                setWords(list.toArray(), 0);
                completion = new CompletionTask();
                SwingUtilities.invokeLater(completion);
            }
            return;
        }

        String[] args = content.trim().split("\\s+");

        if (args == null && args.length == 0) {
            hide();
            return;
        }
        String prefix = args[args.length - 1].toLowerCase();

        if (find(getKeys(args), prefix)) {
            // A completion is found
            // We cannot modify Document from within notification,
            // so we submit a task that does the change later
            completion = new CompletionTask();
            SwingUtilities.invokeLater(completion);
        }
    }

    private ArrayList<String> getKeys(String[] args) {
        if (args.length == 1) {
            return console.keysCommands;
        } else {
            Command com = console.get(args[0]);
            if (com != null) return com.getKeys(args);
        }
        return null;
    }

    private final ArrayList<String> words = new ArrayList<>();

    private boolean find(ArrayList<String> keywords, String prefix) {
        words.clear();
        if (keywords != null) {
            int size = keywords.size();
            for (int i = 0; i < size; i ++) {
                String s = keywords.get(i);
                if (s.toLowerCase().startsWith(prefix)) {
                    words.add(s);
                }
            }
            setWords(words.toArray(), 0);
            return words.size() > 0;
        }
        return true;
    }

    public void setWordsFromAnotger(ArrayList<String> words) {
        this.words.clear();
        if (words != null) {
            this.words.addAll(words);
        }
    }

    public void addToEditFromPos(int pos) {
        if (completion == null) {
            completion = new CompletionTask();
        }
    }

    public boolean enterAct() {
        if (completion != null) {
            textField.setCaretPosition(textField.getSelectionEnd());
            position = 0;
            hide();
            return false;
        } else
            return true;
    }

    public boolean canHistory() {
        return completion == null;
    }

    public void refresh() {
        if (!findList.isSelectionEmpty()) {
            findList.scrollRectToVisible(findList.getCellBounds(
                    findList.getMinSelectionIndex(), findList.getMaxSelectionIndex()));
        }
        if (completion != null) completion.run();
    }

    private CompletionTask completion;

    private class CompletionTask implements Runnable {
        @Override
        public void run() {
            String completion = getSelected();
            if (completion != null) {
                StringBuilder sb = new StringBuilder(textField.getText());
                isEdit = false;
                int pos = textField.getCaretPosition();
                int endpos = sb.length();
                for (int i = pos; i < endpos; i ++) {
                    if (Character.isSpaceChar(sb.charAt(i))) {
                        endpos = i; break;
                    }
                }
                int selpos = textField.getSelectionEnd();
                int startpos = textField.getSelectionStart();
                if (startpos > selpos) selpos = startpos;
                if (selpos > endpos) endpos = selpos;
                startpos = 0;
                for (int i = pos; i > 0; i --) {
                    if (Character.isSpaceChar(sb.charAt(i - 1))) {
                        startpos = i; break;
                    }
                }
                sb.delete(startpos, endpos);
                sb.insert(startpos, completion);
                textField.setText(sb.toString());
                position = startpos;
                startpos += completion.length();
                if (pos > startpos) {
                    textField.setCaretPosition(startpos);
                } else {
                    textField.setCaretPosition(startpos);
                    textField.moveCaretPosition(pos);
                }
                isEdit = true;
                setNewLocation();
            }
        }
    }

    private int position;

    public void setNewLocation() {
        pTemp.setLocation(textField.getLocationOnScreen());
        int offs = textField.getScrollOffset();
        String text;
        try {
            text = textField.getText(0, position);
            offs = textField.getFontMetrics(textField.getFont()).stringWidth(text) - offs;
        } catch (BadLocationException ex) {}
        pTemp.translate(offs, textField.getHeight());
        findMenu.setLocation(pTemp);
    }

    public void showPopUpWindow() {
        setNewLocation();
        if (!findMenu.isVisible()) {
            findMenu.setVisible(true);
        }
    }

    public String getSelected() {
        return (String) findList.getSelectedValue();
    }

    public void hide() {
        findList.setListData(empty);
        findMenu.setVisible(false);
        completion = null;
    }

    private final String[] empty = new String[0];
    private final static int MAX_VISIBLE_ROW = 8;

    public void setWords(Object[] words, int select) {
        int len = words.length;
        if (words == null || len == 0) {
            hide();
        } else {
            findList.setListData(words);
            findList.setSelectedIndex(select);
            findList.setVisibleRowCount(len > MAX_VISIBLE_ROW ? MAX_VISIBLE_ROW : len);
            showPopUpWindow();
        }
    }
}
