package collision;
import org.joml.Vector2f;

public class Collision {
    public Vector2f distance;
    public boolean isIntersecting;
    //contains data to check collision
    public Collision(Vector2f distance, boolean intersecting) {
        this.distance = distance;
        this.isIntersecting = intersecting;
    }
}
