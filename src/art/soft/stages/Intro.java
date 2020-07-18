package art.soft.stages;

import art.soft.Loader;
import art.soft.gameObjs.SimpleAnimation;
import art.soft.gameObjs.containers.CenterContainer;
import java.awt.Color;

/**
 *
 * @author Артём Святоха
 */
public class Intro extends Stage {

    private boolean endIntro, loading;

    @Override
    public void init() {
        loading = endIntro = false;
        Loader loader = Loader.getLoader();
        //
        CenterContainer center = (CenterContainer) loader.getObj(CenterContainer.class);
        add(center);
        center.setBacground(Color.BLACK);
        SimpleAnimation artsoft = (SimpleAnimation) loader.getObj(SimpleAnimation.class);
        artsoft.setAnimSet(loader.loadAnimation(Loader.ANIM_TYPE_ASCII, "intro"));
        artsoft.setCycle(false);
        artsoft.setPos(0, 0);
        artsoft.setAnimNum(0);
        center.add(artsoft);
        //
        (new BackgroundLoad()).start();
    }

    @Override
    public void act() {
        if ((endIntro || actIsEnded()) && loading) {
            removeAll();
            Loader loader = Loader.getLoader();
            loader.game.setStage(loader.menu);
        }
    }

    @Override
    public void keyReleased(int keyCode) {
        endIntro = true;
    }

    private class BackgroundLoad extends Thread {
        @Override
        public void run() {
            Loader.getLoader().load();
            loading = true;
        }
    }
}
