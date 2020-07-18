package art.soft.ascii;

import art.soft.Loader;
import art.soft.animation.AnimSet;
import art.soft.animation.Animation;
import java.awt.Graphics;

/**
 *
 * @author Артём Святоха
 */
public class AsciiAnim extends Animation {

    private AsciiSet data;

    private byte[] operations;

    private int offset, time;

    private int curX, curY, scale = 2;

    public AsciiAnim() {}

    public AsciiAnim(AsciiSet set) {
        data = set;
    }

    @Override
    public void setAnimSet(AnimSet set) {
        data = (AsciiSet) set;
    }

    @Override
    public AnimSet getAnimSet() {
        return data;
    }

    @Override
    public void setAnimation(int num) {
        byte[] op = data.getOperatons(num);
        if (operations != op) {
            operations = op;
            curX = curY =
            offset = time = 0;
            scale = 2;
        }
    }

    @Override
    public void reset() {
        curX = curY =
        offset = time = 0;
        scale = 2;
    }

    private int readWord() {
        int word = (operations[offset++] & 0xFF) << 8;
        word |= (operations[offset++] & 0xFF);
        return word;
    }

    private int readByte() {
        return operations[offset++] & 0xFF;
    }

    @Override
    public void play(Graphics g, int x, int y) {
        {
            //boolean lineAnt = Loader.getLoader().settings.lineAntializing;
            int position = offset;
            int op, col;
            int tx = curX, ty = curY;
            int tScale = scale;
            boolean endCycle = false;
            BitFont font = Loader.getLoader().font;
            for (; offset < operations.length;) {
                op = operations[offset++];
                col = (op >> 4) & 0xF;
                g.setColor(BitFont.COLORS[col]);
                switch (op & 0xF) {
                case 0: // Повторять блок n раз
                    if (time > 0) {
                        endCycle = true;
                    } else {
                        time = readByte() | (col << 8);
                        position = offset;
                        scale = tScale;
                        curX = tx;
                        curY = ty;
                    }
                    break;
                case 1: // Установить масштаб (scale) шрифта
                    scale = (readByte() | (col << 8)) + 1;
                    break;
                case 2: // Установка позици курсора
                    curX = (short) readWord();
                    curY = (short) readWord();
                   break;
                case 3: // Сместить курсор на x/y символов
                    curX += operations[offset++] * BitFont.FONT_WIDTH * scale;
                    curY += operations[offset++] * BitFont.FONT_HEIGHT * scale;
                    break;
                case 4: // Вывести символ
                    font.drawChar(g, (char) readByte(), x + curX, y + curY, scale);
                    curX += BitFont.FONT_WIDTH * scale;
                    break;
                case 5: // Нарисовать прямоугольник
                    int w = readWord();
                    int h = readWord();
                    g.fillRect(x + curX, y + curY, w, h);
                    break;
                case 6: // Залить символами область
                    w = readByte();
                    h = readByte();
                    font.fillChar(g, (char) readByte(), x + curX, y + curY, w, h, scale);
                    break;
                case 7: // Залить данными область
                    byte[] dTmp = data.getData(readByte());
                    int offs = readByte();
                    w = readByte();
                    h = readByte();
                    font.drawBytes(g, dTmp, offs, x + curX, y + curY, w, h, scale);
                    break;
                case 8: // Вывести строку
                    dTmp = data.getData(readByte());
                    int coord = font.drawString(g, dTmp, 0, dTmp.length, x + curX, y + curY, scale);
                    curX += (short) coord;
                    curY += coord >> 16;
                    break;
                case 9: // Вывести подстроку
                    dTmp = data.getData(readByte());
                    offs = readByte();
                    int len = readByte();
                    coord = font.drawString(g, dTmp, offs, len, x + curX, y + curY, scale);
                    curX += (short) coord;
                    curY += coord >> 16;
                    break;
                }
                if (endCycle) break;
            }
            if (time > 0) {
                offset = position;
            }
            scale = tScale;
            curX = tx;
            curY = ty;
        }
        //
        if (next != null) next.play(g, x, y);
    }

    @Override
    public void play(Graphics g, int x, int y, boolean flipX, boolean flipY) {
        Animation temp = next;
        next = null;
        play(g, x, y);
        next = temp;
        if (temp != null) temp.play(g, x, y, flipX, flipY);
    }

    @Override
    public void pool() {
        if (next != null) next.pool();
        data = null;
        operations = null;
        Loader loader = Loader.getLoader();
        next = loader.asciiAnimsPool;
        loader.asciiAnimsPool = this;
    }

    @Override
    public boolean incAnim(boolean cycle) {
        if (next != null) next.incAnim(cycle);
        if (time > 0) time--; else
        if (offset >= operations.length) {
            if (cycle) {
                time = offset = 0;
            } else {
                return true;
            }
        }
        return false;
    }
}
