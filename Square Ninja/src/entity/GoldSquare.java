package entity;

import GameManagement.GameManager;
import graphics.Animation;
import graphics.Camera;
import graphics.Window;
import world.World;

public class GoldSquare extends Entity {
    public static final int ANIM_SIZE = 1;
    public static final int ANIM_IDLE = 0;



    public GoldSquare(Transform transform) {
        super(ANIM_SIZE, transform);
        setAnimation(ANIM_IDLE, new Animation(1, 1, "Gold"));
    }

    @Override
    public void update(float delta, Window window, Camera camera, World world) {
        MoveDownScreen(delta, speed);
    }

    /** Destroys Square When Clicked On */
    @Override
    public void DestroySquare(GameManager gameManager) {
        gameManager.score += 100;
    }

    @Override
    public void NoClickDestroy(GameManager gameManager) {

    }
}
