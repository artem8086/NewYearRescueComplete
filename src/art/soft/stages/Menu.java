package art.soft.stages;

import art.soft.Loader;
import art.soft.gameObjs.SimpleAnimation;
import art.soft.gameObjs.containers.CenterContainer;
import art.soft.gameObjs.gui.AnimMenu;
import art.soft.gameObjs.gui.CommonControls;
import art.soft.gameObjs.gui.ControlsMenu;
import art.soft.gameObjs.gui.GraphicsMenu;
import art.soft.gameObjs.gui.MainMenu;
import art.soft.gameObjs.gui.PlayerControls;
import art.soft.gameObjs.gui.SettingsMenu;
import java.awt.Color;

/**
 *
 * @author Артём Святоха
 */
public class Menu extends Stage {

    private static final int MENU_ANIM = 0;
    private static final int TO_TITLE_ANIM = 1;
    private static final int FROM_TITLE_ANIM = 2;

    private SimpleAnimation menuAnim;

    private static final int MENU_STATUS = 0;
    private static final int TO_TITLE_STATUS = 1;
    private static final int TITLE_STATUS = 2;
    private static final int FROM_TITLE_STATUS = 3;

    private int status;

    // Menu data
    public AnimMenu current;
    public MainMenu mainmenu;
    public SettingsMenu settings;
    public ControlsMenu controls;
    public PlayerControls player;
    public CommonControls common;
    public GraphicsMenu graphics;

    public void load() {
        Loader loader = Loader.getLoader();
        loader.menu = this;
        CenterContainer center = (CenterContainer) loader.getObj(CenterContainer.class);
        add(center);
        //
        mainmenu = new MainMenu();
        mainmenu.init();
        mainmenu.setPos(218, 336);
        mainmenu.setStage(this);
        //
        settings = new SettingsMenu();
        settings.init();
        settings.setPos(304, 240);
        settings.setStage(this);
        //
        controls = new ControlsMenu();
        controls.init();
        controls.setPos(256, 336);
        controls.setStage(this);
        //
        player = new PlayerControls();
        player.init();
        player.setPos(224, 96);
        player.setStage(this);
        //
        common = new CommonControls();
        common.init();
        common.setPos(176, 168);
        common.setStage(this);
        //
        graphics = new GraphicsMenu();
        graphics.init();
        graphics.setPos(80, 192);
        graphics.setStage(this);
        //
        menuAnim = (SimpleAnimation) loader.getObj(SimpleAnimation.class);
        menuAnim.setAnimSet(loader.loadAnimation(Loader.ANIM_TYPE_MODEL, "menu/anim"));
        //
        center.add(graphics);
        center.add(common);
        center.add(player);
        center.add(controls);
        center.add(settings);
        center.add(mainmenu);
        center.add(menuAnim);
        //
        center.setBacground(new Color(0xFF_99D9EA));
        //
        /*try {
            Sequencer sequencer = MidiSystem.getSequencer();
            sequencer.open();
            sequencer.setSequence(loader.openFile("music/ice_climber.mid"));
            sequencser.setLoopCount(Sequencer.LOOP_CONTINUOUSLY);
            sequencer.start();
        } catch (MidiUnavailableException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (InvalidMidiDataException ex) {
            ex.printStackTrace();
        }*/
        
    }

    @Override
    public void init() {
        status = MENU_STATUS;
        menuAnim.setAnimNum(MENU_ANIM);
        //
        current = mainmenu;
        mainmenu.start();
        settings.init();
        controls.init();
        player.init();
        common.init();
        graphics.init();
    }

    public void cancel() {
        current.activate();
        settings.hide();
        controls.hide();
        player.hide();
        common.hide();
        graphics.hide();
    }

    public void toTitle() {
        status = TO_TITLE_STATUS;
    }

    @Override
    public void act() {
        int old_st = status;
        if (actIsEnded()) {
            switch (status) {
                case TO_TITLE_STATUS:
                    if (old_st == MENU_STATUS) {
                        menuAnim.setAnimNum(TO_TITLE_ANIM);
                    } else {
                        status = TITLE_STATUS;
                    }
                    break;
                case FROM_TITLE_STATUS:
                    menuAnim.setAnimNum(MENU_ANIM);
                    mainmenu.start();
                    status = MENU_STATUS;
                    break;
            }
        }
    }

    @Override
    public void keyReleased(int keyCode) {
        super.keyReleased(keyCode);
        if (status == TITLE_STATUS) {
            status = FROM_TITLE_STATUS;
            menuAnim.setAnimNum(FROM_TITLE_ANIM);
        }
    }
}
