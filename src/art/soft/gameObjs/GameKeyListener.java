package art.soft.gameObjs;

/**
 *
 * @author Артём Святоха
 */
public interface GameKeyListener {

    /*
     * Вызывается при нажатии клавиши
     * @keyCode код клавиши
     */
    public void keyPressed(int keyCode);

    /*
     * Вызывается при отпускании клавиши
     * @keyCode код клавиши
     */
    public void keyReleased(int keyCode);
}
