package main;


import assets.Assets;
import gameplay.Timer;
import graphics.Camera;
import graphics.Shader;
import graphics.Window;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import world.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import GameManagement.GameManager;

// Use following command in terminal to run as jar application

public class main {
// Note 10 Spaces Indicates New Function/End of Previous Function ONLY in Main

    public static World world;
    public static StartScreen startScreen;
    public static Background background;
    public static EndScreen endScreen;
    public static double startTime;
    public static Window window;

    public static boolean gameplayBool = true;









    // Loops window so it stays open and runs various functions
    public static void loop() {
        // Create window object
        window = new Window();
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE);
        window.createWindow("CFB WIP"); // Creates window using window object
        // sets capabilities so window can make squares, textures etc.
        GL.createCapabilities();
        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE,GLFW_FALSE);

        // Transparency with entities
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA,GL_ONE_MINUS_SRC_ALPHA);

        // Creates a camera, including and rendering tiles and objects throughout the frame that is 1000x1000, meaning the camera has to be 1000x1000 to use the entire frame.
        Camera camera = new Camera(window.getWidth(), window.getHeight());
        glEnable(GL_TEXTURE_2D);

        TileRenderer tiles = new TileRenderer();
        Assets.initAsset();

        Shader shader = new Shader("shader"); // Creates a new shader, filename is singular, because in the directory, the shader files start with "shader" Shader Class Handles Names.
        glClearColor(0.0f, 1.0f, 0.0f, 0.0f); // Window Initial Color

        world = new World(camera, window);
        startScreen = new StartScreen("startScreen.png");
        background = new Background("woodBackground.png");
        endScreen = new EndScreen("endScreen.png");

        double frame_cap = 1.0 / 60.0; // Max frames per second

        double time = Timer.getTime(); // Sets First Time, enabling us to calculate the time in the future and use that with this time to get the time in between to decide whether a frame should be produced. whether

        double unprocessed = 0; // unprocessed time. The time where nothing has occured yet, but waiting in queue to produce a frame when the time gets high enugh.
        double frame_time = 0; // The total time the loop (max 1s) has been running for. When reaching 1, this will reset and output the total frames created. Used to calculate FPS
        int frames = 0; // Total Number of frames that have occured. When frame_time = 1s, this will output the frames produced in 1s (fps) and will set to 0.

        // While loop for frame to stay open while it should not close.
        while (!window.shouldClose()) {

            // Add Loop Code Here
            boolean can_render = false; // Initially, images cannot render
            double time_2 = Timer.getTime(); // Sets most recent time
            double time_passed = time_2 - time; // Calculates time passed
            unprocessed += time_passed; // unprocessed time, so that if it builds up, it will try to catch up.
            frame_time += time_passed; // time passed resets every loop, so to store total time built up, add this to a total frame time declared outside the loop.
            time = time_2; // Reset time, such that it can calculate difference between this and next frame in next loop.

            while (unprocessed >= frame_cap) { // Loop at rate of fps, only occurs when the unprocessed time is greater than time available for a frame.
                if (world.canRun)
                    world.calculateView(window,camera);

                unprocessed -= frame_cap; // The amount of unprocessed time decreases by 1 frames amount of time.
                can_render = true; // If this is set to true, then images may render. Thus it only renders at frame_cap speed. Line 166, this is used for controlling rendering at fps.

                // Window Closes when Key Escape is Pressed
                if (window.getInput().isKeyPressed(GLFW_KEY_ESCAPE)) {
                    glfwSetWindowShouldClose(window.getWindow(), true);
                }




                if (world.canRun) {
                    world.update((float) frame_cap, window, camera);
                    world.correctCamera(camera, window);
                }



                if (StartScreen.canRun) {
                    updateStartScreen();
                }

                if (EndScreen.canRun) {
                    updateEndScreen();
                }



                // updates keys
                window.update();


            }

            // Renders images only if they are enabled to render as determined by a boolean activated once every frame at a rate of the given fps, 60.
            if (can_render) {
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // Clear Framebuffer

                if (Background.canRun) {
                    background.render(shader, camera);
                }

                if (StartScreen.canRun) {
                    startScreen.render(shader, camera);
                }

                if (world.canRun) {
                    world.render(tiles, shader, camera, window);
                    if (gameplayBool){
                        startTime = Timer.getTime();
                        world = new World(camera, window);
                        gameplayBool = false;
                    }
                }

                if (EndScreen.canRun){
                    endScreen.render(shader, camera , window, world.getFinalScore(), world.getFinalTime());
                }



                frames++; // total frames increases when 1 frame render is performed

                window.swapBuffers();
            }
        }

        Assets.deleteAsset(); // Deletes Entities

    }









    public static void main(String[] args) {
        Window.setCallbacks(); // Provides Better Error Codes

        if (glfwInit() != true) {
            System.err.println("GLFW Failed to initialize!");
            System.exit(1);
        }

        System.out.println("LWJGL " + Version.getVersion());

        loop();

        glfwTerminate();
    }

    public static double getTime_passed(){
        double result = Timer.getTime() - startTime;
        return result;
    }

    public static void updateStartScreen() {
        if (window.getInput().isKeyPressed(GLFW_KEY_SPACE)) {
            World.canRun = true;
            gameplayBool = true;
            StartScreen.canRun = false;
        }
    }

    public static void updateEndScreen() {
        if (window.getInput().isKeyPressed(GLFW_KEY_SPACE)) {
            World.canRun = true;
            gameplayBool = true;
            EndScreen.canRun = false;
        }
    }




}


