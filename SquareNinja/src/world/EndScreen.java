package world;

import assets.Assets;
import graphics.*;
import org.joml.Matrix4f;
import org.joml.Vector2f;

import java.rmi.MarshalException;
import java.util.ArrayList;
import java.util.List;
import GameManagement.GameManager;

public class EndScreen {
    private Texture texture;
    private Matrix4f scale;
    private Matrix4f translation;
    public List<Text> textList;
    public static boolean canRun = false;

    public EndScreen(String texture) {
        this.texture = new Texture("/tiles/" + texture);

        scale = new Matrix4f();
        translation = new Matrix4f();
    }

    //renders image over screen and displays record stats and round stats on the screen
    public void render(Shader shader, Camera camera, Window window, String finalScore, String finalTime, String highScore, String highFinalTime) {
        Matrix4f mat = new Matrix4f();
        camera.getUntransformedProjection().scale(320, 240, 1, mat);
        shader.bind();
        shader.setUniform("projection", mat);
        this.bind(0, 0, shader);
        Assets.getModel().render();
        textList = new ArrayList<Text>();
        textList.add(new Text(new Vector2f(-7, 7), finalScore, window, 16, 1.85f));
        textList.add(new Text(new Vector2f(13, 7), finalTime, window, 16, 1.85f));
        textList.add(new Text(new Vector2f(-9, -5.75f), highScore, window, 12, 1.75f));
        textList.add(new Text(new Vector2f(14f, -5.75f), highFinalTime, window, 12, 1.75f));

        for (Text text : textList){
            text.render();
        }
    }
    public void bind(float x, float y, Shader shader) {
        scale.translate(x, y, 0, translation);

        shader.setUniform("sampler", new Matrix4f());
        shader.setUniform("texModifier", translation);
        texture.bind(0);
    }
}
