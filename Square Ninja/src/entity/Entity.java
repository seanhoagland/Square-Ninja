package entity;

import GameManagement.GameManager;
import assets.Assets;
import collision.AABB;
import collision.Collision;
import graphics.Animation;
import graphics.Camera;
import graphics.Shader;
import graphics.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import world.World;

public abstract class Entity {
    protected Animation[] animations;
    private int use_animation;
    protected AABB bounding_box;
    protected Transform transform;

    public boolean canCollide;
    public float speed;




    public Entity(int max_animations, Transform transform) {
        this.transform = transform;
        this.animations = new Animation[max_animations];
        this.use_animation = 0;

        bounding_box = new AABB(new Vector2f(transform.pos.x, transform.pos.y), new Vector2f(transform.scale.x, transform.scale.y));
    }

    protected void setAnimation(int index, Animation animation) {
        animations[index] = animation;
    }

    public void useAnimation(int index) {
        this.use_animation = index;
    }

    public abstract void update(float delta, Window window, Camera camera, World world);
    /** Destroys Square When Clicked On */
    public abstract void DestroySquare(GameManager gameManager);
    /** Destroys Square When Not Clicked On Or For Any Other Rzn */
    public abstract void NoClickDestroy(GameManager gameManager);

    public void move(Vector2f direction) {
        transform.pos.add(new Vector3f(direction,0));

        bounding_box.getCenter().set(transform.pos.x,transform.pos.y);
    }

    public void render(Shader shader, Camera camera, Window window, World world) {
        Matrix4f target = camera.getProjection();
        target.mul(world.getWorld());

        Matrix4f sampler_0 = new Matrix4f();
        shader.bind();
        shader.setUniform("sampler", sampler_0);
        shader.setUniform("projection",transform.getProjection(target));
        animations[use_animation].bind(0);
        Assets.getModel().render();
    }


    public void collideWithTiles(World world) {
        AABB[] boxes = new AABB[25];
        for (int count = 0; count < 5; count++) {
            for (int counter = 0; counter < 5; counter++) {
                boxes[count+counter*5] = world.getTileBoundingBox( (int) ((transform.pos.x / 2) + .5f) - (5/2) + count,
                        (int) ((-transform.pos.y/2) + .5f) - (5/2) + counter);

            }
        }

        AABB box = null;
        for (int count = 0; count < boxes.length; count++) {
            if (boxes[count] != null) {
                if (box == null) box = boxes[count];

                Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                Vector2f length2 = boxes[count].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

                if (length1.lengthSquared() > length2.lengthSquared()) {
                    box = boxes[count];
                }
            }
        }

        if (box != null) {
            Collision data = bounding_box.getCollision(box);
            if (data.isIntersecting) {
                bounding_box.correctPosition(box, data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }

            for (int count = 0; count < boxes.length; count++) {
                if (boxes[count] != null) {
                    if (box == null) box = boxes[count];

                    Vector2f length1 = box.getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());
                    Vector2f length2 = boxes[count].getCenter().sub(transform.pos.x, transform.pos.y, new Vector2f());

                    if (length1.lengthSquared() > length2.lengthSquared()) {
                        box = boxes[count];
                    }
                }
            }
            data = bounding_box.getCollision(box);

            if (data.isIntersecting) {
                bounding_box.correctPosition(box, data);
                transform.pos.set(bounding_box.getCenter(), 0);
            }
        }
    }

    public void collideWithEntity(Entity entity, World world, int earlyID, int lateID) {
        Collision collision = bounding_box.getCollision(entity.bounding_box);

        if (collision.isIntersecting && entity.canCollide && canCollide) {
            collision.distance.x /= 2;
            collision.distance.y /= 2;

            bounding_box.correctPosition(entity.bounding_box, collision);
            transform.pos.set(bounding_box.getCenter().x, bounding_box.getCenter().y, 0);

            entity.bounding_box.correctPosition(bounding_box, collision);
            entity.transform.pos.set(entity.bounding_box.getCenter().x, entity.bounding_box.getCenter().y, 0);
        }
    }

    public void noCollision() {
        canCollide = false;
    }

    public int getAnimationIndex() { return use_animation; }

    public Vector2f moveToward(float x, float y, float delta) {
        Vector2f movement = new Vector2f();

        if (this.transform.pos.x - speed * delta > x) {
            movement.add(-speed * delta, 0);
        } else if (this.transform.pos.x + speed * delta < x) {
            movement.add(speed * delta, 0);
        }

        if (this.transform.pos.y - speed * delta > y) {
            movement.add(0, -speed * delta);
        } else if (this.transform.pos.y + speed * delta < y) {
            movement.add(0, speed * delta);
        }

        return movement;
    }

    public Vector3f getPosition() {
        return transform.pos;
    }

    public AABB getBoundingBox() {
        return bounding_box;
    }

    public void UpdateBoundingBox() {
        this.bounding_box.SetCenter(new Vector2f(this.transform.pos.x, this.transform.pos.y));
    }


   /** GAMEMANAGEMENT BASED FUNCTIONALITIES FOR ENTITIES */


    protected void MoveDownScreen(float delta, float speed) {
        this.transform.pos.y -= speed * delta;
    }
}
