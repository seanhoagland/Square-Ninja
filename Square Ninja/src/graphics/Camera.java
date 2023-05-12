package graphics;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {
    private Vector3f position;
    private Matrix4f projection;
    private float projMultiplierX = 1;
    private float projMultiplierY = 1;
    private float width;
    private float height;


    // Map Starts at -19x16, 14x16. Thus, -19x16, 14x16 is your new (0,0), but only for the camera. Squares are 2x2. Think about it like this,
    // The Camera's position is the center of the screen. If the camera's position were (0,0) half the map wouldn't render because there are no tiles left or
    // above (0,0). -304, 224 represents the leftmost, upmost position the camera can be in before entities render improperly. There is 160x160 extra room for
    // entities coming down.

    //camera position
    public Camera(int width, int height) {
            position = new Vector3f(-29*16,24*16,0); // multiply actual position on screen for entities by 16.
            setProjection(640,480);

            this.width = width;
            this.height = height;
    }
    public void setPosition(Vector3f position) {
        this.position = position;
    }

    public void addPosition(Vector3f position) {
        this.position.add(position);
    }

    public void setProjection(float width, float height) {
        projection = new Matrix4f().setOrtho2D(-width/2,width/2,-height/2,height/2);
    }

    public Vector3f getPosition() { return position;}

    public Matrix4f getUntransformedProjection() {
        return projection;
    }

    public Matrix4f getProjection() {
        return projection.translate(position, new Matrix4f());
    }

    public float getProjMultiplierX() { return projMultiplierX; }

    public float getProjMultiplierY() { return projMultiplierY; }

    public void setProjMultiplierY(float setter) { projMultiplierY = setter; }

    public void setProjMultiplierX(float setter) { projMultiplierX = setter; }

    public float getWidth() { return width; }

    public float getHeight() { return height; }



}
