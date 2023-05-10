package gameplay;

import graphics.Camera;
import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Click {
    public static Vector2f position;
    public static boolean clicked;

    public static void update(long window) {
        clicked = checkClick(window);

        DoubleBuffer posX = BufferUtils.createDoubleBuffer(1);
        DoubleBuffer posY = BufferUtils.createDoubleBuffer(1);
        glfwGetCursorPos(window, posX, posY);
        position = new Vector2f( (float) posX.get(0), (float) posY.get(0) );
    }

    private static boolean checkClick(long window) {
        int mouseState = glfwGetMouseButton(window, GLFW_MOUSE_BUTTON_LEFT);
        if (mouseState == GLFW_PRESS)
            return true;
        return false;
    }

    public static Vector2f ConvertClickPositionToRealCoordinates(Camera camera, Vector2f input, float scale) {
        // Determine Top Left of Screen
        float x = -camera.getPosition().x - camera.getWidth() / 2;
        float y = -camera.getPosition().y + camera.getHeight() / 2;

        // Combine With
        x += input.x;
        y -= input.y;

        return new Vector2f(x/scale, y/scale);
    }
}
