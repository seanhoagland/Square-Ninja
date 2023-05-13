package entity;

import GameManagement.GameManager;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;
import main.main;

public class IceSquare extends Entity {
    public static final int ANIM_SIZE = 1;
    public static final int ANIM_IDLE = 0;





    public IceSquare(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(1, 1, "Ice"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        MoveDownScreen(delta, speed);
    }

    /** Destroys Square When Clicked On */
    @Override
    public void DestroySquare(GameManager gameManager) {

        gameManager.tempSpeedModifier = gameManager.speedModifier; //records speedModifier
        gameManager.speedModifier = 10; //resets speedModifier
        gameManager.iceCooldown = false; //determines if an ice can spawn again

    }

    @Override
    public void NoClickDestroy(GameManager gameManager) {

    }
}
