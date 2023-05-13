package entity;

import GameManagement.GameManager;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;
import main.main;

public class FrenzySquare extends Entity {
    public static final int ANIM_SIZE = 1;
    public static final int ANIM_IDLE = 0;



    public FrenzySquare(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(1, 1, "Frenzy"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        MoveDownScreen(delta, speed);
    }

    /** Destroys Square When Clicked On */
    @Override
    public void DestroySquare(GameManager gameManager) {
        GameManager.frenzyTime = true; //activiates frenzy config
        GameManager.frenzyStartTime = main.getTime_passed(); //records the start time when frenzy was hit
    }

    @Override
    public void NoClickDestroy(GameManager gameManager) {

    }
}
