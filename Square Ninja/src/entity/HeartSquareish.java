package entity;

import GameManagement.GameManager;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class HeartSquareish extends Entity {
    public static final int ANIM_SIZE = 1;
    public static final int ANIM_IDLE = 0;



    public HeartSquareish(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(1, 1, "Heart"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        MoveDownScreen(delta, speed);
    }

    @Override
    public void DestroySquare(GameManager gameManager) {
        if (gameManager.lives > 0 && gameManager.lives <=2) { //if the player has room to gain lives
            gameManager.lives++; //adds to lives
            System.out.println("LIVES: " + gameManager.lives);
        }

    }

    @Override
    public void NoClickDestroy(GameManager gameManager) {

    }
}
