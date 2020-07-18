package art.soft;

import art.soft.ascii.BitFont;
import art.soft.console.Console;
import art.soft.stages.Intro;
import art.soft.stages.Stage;
import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.AffineTransform;

/**
 *
 * @author Артём Святоха
 */
public class Game implements Runnable, ComponentListener, KeyListener {

    public static final String GAME_TITLE = "Спасение Нового Года Complete Edition";
    public static final String VERSION_TEXT = "version: 0.1.1";

    private static final int SET_GAME_WIDTH = 1024;
    private static final int SET_GAME_HEIGHT = 679;

    public static final int PREFER_GAME_WIDTH = 800;
    public static final int PREFER_GAME_HEIGHT = 640;
    
    public static final int TIME_FRAME = 33;
    
    public static final String DATA_DIR = "data/";

    // Для обработчика клавиатуры
    private final static int PRESSED_KEY_BIT  = 0x80000000;
    private final static int PRESSED_KEY_MASK = ~ PRESSED_KEY_BIT;
    private final static int KEY_BUFFER_SIZE = 512;
    private final int keyBuffer[] = new int[KEY_BUFFER_SIZE]; // буфер клавишь
    private int curKey = -1;

    private Console console;

    public int widthSet, heightSet;

    private Frame frame;
    private Renderer renderer;
    
    // Всё для рендера
    private Image buffer;
    private Graphics2D graphics;
    public int width, height;
    private AffineTransform transform;

    private void init() {
        Loader loader = new Loader(this);

        // Только для версии разработчика
        console = new Console();
        console.start();

        renderer = new Renderer();
        //
        frameInit();
        //
        loader.init();
        //
        Intro intro = new Intro();
        setStage(intro);
        //
        Thread thread = new Thread(this);
        thread.start();
    }

    public Frame getFrame() {
        return frame;
    }

    private class Renderer extends javax.swing.JComponent {
        Renderer() {
            setDoubleBuffered(false);
            setVisible(true);
            setFocusable(false);
        }

        @Override
        public void update(Graphics g){
            paint(g);
        }

        @Override
        public void paint(Graphics g){
            g.drawImage(buffer, 0, 0, this);
        }
    }

    private void frameInit() {
        synchronized (frame = new Frame(GAME_TITLE)) {
            frame.addWindowListener(new WindowAdapter(){
                @Override
                public void windowClosing(WindowEvent ev){
                    System.exit(0);
                }
            });
            frame.add(renderer);
            //
            Loader loader = Loader.getLoader();
            GraphicsDevice myDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            DisplayMode dMode = myDevice.getDisplayMode();
            width = dMode.getWidth();
            height = dMode.getHeight();
            if (loader.settings.fullscreen && myDevice.isFullScreenSupported()) {
                // Вход в полноэкранный режим
                frame.setUndecorated(true);
                frame.setResizable(false);
                frame.setSize(width, height);
                myDevice.setFullScreenWindow((Window) frame);
            } else {
                frame.setUndecorated(false);
                frame.setResizable(true);
                frame.setSize(SET_GAME_WIDTH, SET_GAME_HEIGHT);
                frame.setLocation((width - SET_GAME_WIDTH) >> 1,
                        (height - SET_GAME_HEIGHT) >> 1);
            }
            //
            frame.setFocusable(true);
            frame.setEnabled(true);
            frame.setVisible(true);
            //
            // Init graphics
            if (buffer == null) {
                buffer = frame.createImage(width, height);
                graphics = (Graphics2D) buffer.getGraphics();
                transform = graphics.getTransform();
            }
            resizeTransform(renderer.getWidth(), renderer.getHeight());
            //
            loader.settings.applyRender(graphics);
            //
            loader.initFrame();
            frame.addKeyListener(this);
            frame.addComponentListener(this);
            System.gc();
        }
    }

    private Stage curStage, newStage;

    public void setStage(Stage stage) {
        if (stage != curStage) newStage = stage;
    }

    private void setStage() {
        curStage = newStage;
        curStage.init();
        newStage = null;
            // Очистка буфера клавиатуры
        curKey = -1;
    }

    public Stage getStage() {
        return curStage;
    }

    public void log(String message) {
        if (console != null) {
            console.outln(message);
        } else {
            System.out.println(message);
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Game game = new Game();
        game.init();
    }

    private int fps, fps_t, fps_n;

    @Override
    public void run() {
        boolean frameSkip = false;
        fps = fps_n = fps_t = 30;
        do {
            long cycleTime = System.currentTimeMillis();
            if (newStage != null) setStage();
            // Обработка клавиатуры
            if (curKey >= 0) {
                synchronized (keyBuffer) {
                    for (int i = 0; i <= curKey; i ++) {
                        int key = keyBuffer[i];
                        if (key < 0) 
                            curStage.keyPressed(key & PRESSED_KEY_MASK);
                        else curStage.keyReleased(key);
                    }
                    curKey = -1;
                }
            }
            //
            curStage.act();
            //
            if (!frameSkip) {
                //
                graphics.setRenderingHint(
                    RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
                //
                graphics.setTransform(transform);
                curStage.draw(graphics);
                //
                if (Loader.getLoader().settings.showFPS) {
                    graphics.setColor(Color.red);
                    BitFont myFont = Loader.getLoader().font;
                    myFont.drawString(graphics, "fps:", 32, height - 40, 2);
                    myFont.drawString(graphics, Integer.toString(fps),
                            32 + (BitFont.FONT_WIDTH * 10), height - 40, 2);
                }
                //
                //repaint();
                renderer.getGraphics().drawImage(buffer, 0, 0, null);
            }
            //
            cycleTime += TIME_FRAME - System.currentTimeMillis();
            if (cycleTime > 0) {
                try{
                    Thread.sleep(cycleTime);
                }catch(InterruptedException e) {}
                frameSkip = false;
            } else {
                frameSkip = true;
                fps_t --;
            }
            //
            fps_n --;
            if (fps_n == 0) {
                fps = fps_t;
                fps_t = fps_n = 30;
            }
            frame.setSize(widthSet, heightSet);
        } while (true);
    }

    public Graphics getGameGraphics() {
        return graphics;
    }

    private void resizeTransform(int w, int h) {
        int borderW = frame.getWidth() - w;
        int borderH = frame.getHeight() - h;
        if (((float) w / h) > 2f) {
            w = h << 1;
        }
        widthSet = w + borderW;
        heightSet = h + borderH;
        renderer.setSize(w, h);
        double sy = (double) h / PREFER_GAME_HEIGHT;
        width = (int) (w / sy);
        if ((int) (width * sy) != w) width++;
        //log("width = " + width + ", w = " + w + ", h = " + h);
        height = PREFER_GAME_HEIGHT;
        transform.setToScale(sy, sy);
        //graphics2D.setTransform(transform);
    }

    @Override
    public void componentResized(ComponentEvent e) {
        resizeTransform(renderer.getWidth(), renderer.getHeight());
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        if (frame.isUndecorated()) frame.setLocation(0, 0);
    }

    @Override
    public void componentShown(ComponentEvent e) {}

    @Override
    public void componentHidden(ComponentEvent e) {}

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static int translateX = 0, translateY = 0;

    public static void translate(Graphics g, int x, int y) {
        g.translate(x, y);
        translateX -= x;
        translateY -= y;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        curKey ++;
        if (curKey == KEY_BUFFER_SIZE) curKey = KEY_BUFFER_SIZE - 1;
        keyBuffer[curKey] = e.getKeyCode() | PRESSED_KEY_BIT;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (console != null && key == KeyEvent.VK_F1) {
            console.startConsole();
        }
        curKey ++;
        if (curKey == KEY_BUFFER_SIZE) curKey = KEY_BUFFER_SIZE - 1;
        keyBuffer[curKey] = key;
    }

    public void resetWindow() {
        frame.setVisible(false);
        frame.dispose();
        frameInit();
    }
}
