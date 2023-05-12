package graphics;

import gameplay.Click;
import gameplay.Input;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.glfw.GLFW.*;

public class Window {
    private long window ; // Window handle

    private int width, height; // Height and Width of Window
    private boolean fullscreen;

    private Input input;

    private int cursorWidth;
    private int cursorHeight;

    public Window() {
        setSize(640, 480);
        setFullscreen(false);
    }

    public void closeWindow() {
        glfwSetWindowShouldClose(window, true);
    }

    public void createWindow(String title) {
        window = glfwCreateWindow(width, height, title, fullscreen ?
                glfwGetPrimaryMonitor() : 0, 0); // If fullscreen is true then pass Primary Monitor, if false then pass 0

        if (window == 0) {
            throw new IllegalStateException("Failed to create window");
        }


        if (!fullscreen) {
            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
            glfwSetWindowPos(window, (vid.width() - width) / 2,
                    (vid.height() - height) / 2);
            glfwShowWindow(window);
        }

        glfwMakeContextCurrent(window);
        GL.createCapabilities();

        input = new Input(window);

        setCursor("Knife");
    }

    public static void setCallbacks() {
        glfwSetErrorCallback(new GLFWErrorCallback() {
            @Override
            public void invoke(int error_code, long description) {
                throw new
                        IllegalStateException(GLFWErrorCallback.getDescription(description));
            }
        });
    }

    public boolean shouldClose() {
        return glfwWindowShouldClose(window) != false;
    }

    public void swapBuffers() {
        glfwSwapBuffers(window);
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setSizeWhileRunning(int width, int height) {
        glfwSetWindowSize(window, width, height);
    }

    public void setSizeOnCMDF() { // Goes in update method
        if (this.getInput().isKeyDown(GLFW_KEY_LEFT_SUPER) &&
                this.getInput().isKeyPressed(GLFW_KEY_F)) {
            GLFWVidMode vid = glfwGetVideoMode(glfwGetPrimaryMonitor());
            if (width == vid.width()) {
                setSizeWhileRunning(640, 480);
                setSize(640, 480);
            } else {
                setSizeWhileRunning(vid.width(), vid.height() - 48);
                setSize(vid.width(), vid.height() - 48);
            }

            glfwSetWindowPos(window, (vid.width() - width) / 2,
                    (vid.height() - height) / 2);
        }
    }

    public void update() { // Updates keys pressed and checks if an event occurs
        setSizeOnCMDF();
        input.update();
        SetWidthAndHeightOnUpdate();
        Click.update(window);
        glfwPollEvents();
    }

    public int getWidth() { return width; } // Returns window width
    public int getHeight() { return height; } // Returns window height

    public long getWindow() { return window; } // returns window handle
    public Input getInput() { return input; } // returns the current input (Mouse and Keyboard)

    private void SetWidthAndHeightOnUpdate() {
        IntBuffer w = BufferUtils.createIntBuffer(1);
        IntBuffer h = BufferUtils.createIntBuffer(1);

        glfwGetWindowSize(window, w, h);
        width = w.get(0);
        height = h.get(0);
    }

    private void setCursor(String path) {
        try {
            BufferedImage image = ImageIO.read(new File("res/cursors/"
                    + path + ".png"));

            cursorWidth = image.getWidth();
            cursorHeight = image.getHeight();

            int[] pixels = new int[cursorWidth * cursorHeight];
            image.getRGB(0, 0, cursorWidth, cursorHeight, pixels, 0,
                    cursorWidth);

            // convert image to RGBA format
            ByteBuffer buffer =
                    BufferUtils.createByteBuffer(cursorWidth * cursorHeight * 4);

            for (int y = 0; y < cursorHeight; y++) {
                for (int x = 0; x < cursorWidth; x++) {
                    int pixel = pixels[y * cursorWidth + x];

                    buffer.put((byte) ((pixel >> 16) & 0xFF));  // red
                    buffer.put((byte) ((pixel >> 8) & 0xFF));   // green
                    buffer.put((byte) (pixel & 0xFF));          // blue
                    buffer.put((byte) ((pixel >> 24) & 0xFF));  // alpha
                }
            }
            buffer.flip(); // this will flip the cursor image vertically

            // create a GLFWImage
            GLFWImage cursorImg = GLFWImage.create();
            cursorImg.width(cursorWidth);     // set up image width
            cursorImg.height(cursorHeight);   // set up image height
            cursorImg.pixels(buffer);   // pass image data

            // the hotspot indicates the displacement of the sprite to the
            // position where mouse clicks are registered (see image below)
            int hotspotX = cursorWidth / 2;
            int hotspotY = cursorHeight / 2;

            // create custom cursor and store its ID
            long cursorID = GLFW.glfwCreateCursor(cursorImg, hotspotX,
                    hotspotY);

            // set current cursor
            glfwSetCursor(window, cursorID);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int GetCursorWidth() { return cursorWidth; }
    public int GetCursorHeight() { return cursorHeight; }
}
