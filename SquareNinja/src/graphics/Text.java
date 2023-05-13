package graphics;

import assets.Assets;
import org.joml.Matrix4f;
import org.joml.Vector2f;

public class Text {
    private static final String texture = "ASCII.png";
    private static final int tiles = 16;
    private static final TileSheet ASCII = new TileSheet(texture, tiles);
    private static Camera camera;
    private static Shader shader = new Shader("gui");;
    private Vector2f position;
    private String characters;
    private int scale;
    private float characterSpacing;

    public Text(Vector2f position, String characters, Window window, int scale, float characterSpacing) {
        this.position = position;
        this.characters = characters;
        this.scale = scale;
        this.characterSpacing = characterSpacing;

        if (camera == null)
            camera = new Camera(window.getWidth(), window.getHeight());
    }

    public void render() {
        Matrix4f mat = new Matrix4f();
        camera.getUntransformedProjection().scale(scale, mat);
        mat.translate(position.x, position.y,0);
        int characterPointer;
        shader.bind();

        for (int i = 0; i < characters.length(); i++) {
            shader.setUniform("projection", mat);
            characterPointer = characters.charAt(i);
            ASCII.bindTile(shader, characterPointer);
            Assets.getModel().render();

            /** Matrix Translations Post Render */
            if (characterPointer == 87) // W Is Too Big
                mat.translate(1.5f * characterSpacing, 0, 0);
            else if (characterPointer == 105 || characterPointer == 108) // l & i too small
                mat.translate(characterSpacing * .75f, 0, 0);
            else
                mat.translate(characterSpacing, 0, 0);
        }
    }

    public void SetCharacters(String characters) {
        this.characters = characters;
    }
}
