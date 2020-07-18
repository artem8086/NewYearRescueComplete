package art.soft.level;

import art.soft.Loader;
import art.soft.objsData.ObjData;
import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Json data
 * @author Артём Святоха
 */
public class jsonLevel {
    
    private static final String LEVEL_PATH = Loader.DATA_FULL_PATH + "levels/";

    public String name;
    
    public String restartLevel;

    public String deadLevel;
    
    public String nextLevel;

    public Layer layers[];
    
    public Camera cameras[];

    public Player players[];
    
    public String objects[];


    public void writeLevel() {
        DataOutputStream dos = null;
        try {
            dos = new DataOutputStream(new BufferedOutputStream(
                    new FileOutputStream(LEVEL_PATH + name + ".lev")));
        } catch (FileNotFoundException ex) {}
        try {
            dos.writeUTF(restartLevel);
            dos.writeUTF(deadLevel);
            dos.writeUTF(nextLevel);
            // Запись данных обьектов
            ObjData[] objects = Loader.getLoader().engine.objects;
            dos.writeShort(objects.length);
            for ( ObjData obj : objects) {
                dos.writeUTF(obj.name);
                obj.saveToBin();
            }
            // Запись слоёв
            dos.writeByte(layers.length);
            for (Layer layer : layers) {
                layer.writeLayer(dos);
            }
            // Запись камер
            dos.writeShort(cameras.length);
            for (Camera camera : cameras) {
                camera.writeCamera(dos);
            }
            // Запись игроков
            dos.writeByte(players.length);
            for (Player player : players) {
                player.writePlayer(dos);
            }
            dos.close();
        } catch (IOException ex) {}
    }
}
