package world;

import GameManagement.GameManager;
import collision.AABB;
import entity.Entity;
import graphics.Camera;
import graphics.Shader;
import graphics.Window;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class World {
    private int viewX;
    private int viewY;
    private int[] tiles;
    private AABB[] bounding_boxes;
    private List<Entity> entities;
    private int width;
    private int height;
    private int scale;
    public static boolean canRun = true;
    public GameManager gameManager;

    private Matrix4f world;
    public Window window;




/*
    public World(String background, Window window) { // Load world from file

        try {
             BufferedImage tile_sheet = ImageIO.read(new File("res/backgrounds/" + background + ".png"));

             width = tile_sheet.getWidth();
             height = tile_sheet.getHeight();
             scale = 16;

            int[] colorTileSheet = tile_sheet.getRGB(0,0,tile_sheet.getWidth(),tile_sheet.getHeight(), null, 0, tile_sheet.getWidth());

            this.world = new Matrix4f().setTranslation(new Vector3f(0));
            this.world.scale(scale);// Tiles are 32x32, since scale = 16 * 2 due to renderTile using 2*length

            tiles = new int[width*height];
            bounding_boxes = new AABB[width * height];
            entities = new ArrayList<Entity>();

            for (int y = 0; y < tile_sheet.getHeight(); y++) { // x and y represent id of tile
                for (int x = 0; x < tile_sheet.getWidth(); x++) {
                    int red = (colorTileSheet[x + y * tile_sheet.getWidth()] >> 16) & 0xFF; // represents red on tile of grid of map

                    Tile t;
                    try {
                        t = Tile.tiles[red];
                    } catch (ArrayIndexOutOfBoundsException e) {
                        t = null;
                    }

                    if (t != null) {
                        setTile(t, x, y); // sets tile
                    }

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/





    public World(Camera camera, Window window) { // straight red world
        width = 64; // width of world
        height = 64; // height of world
        scale = 16;

        tiles = new int[width*height];
        bounding_boxes = new AABB[width*height];

        world = new Matrix4f().setTranslation(new Vector3f(0));
        world.scale(scale);// Tiles are 32x32, since scale = 16 * 2 due to renderTile using 2*length

        entities = new ArrayList<Entity>();
        this.window = window;

        gameManager = new GameManager(camera, this, window,3, 0);
    }






    public void render(TileRenderer render, Shader shader, Camera camera, Window window) {
        int posX = ((int) camera.getPosition().x / (scale * 2));
        int posY = ((int) camera.getPosition().y / (scale * 2));

        for (int count = 0; count < viewX; count++) {
            for (int counter = 0; counter < viewY; counter++) {
                Tile t = getTile(count - posX - (viewX / 2) + 1, counter + posY - (viewY / 2));
                if (t != null) {
                    render.renderTile(t, count - posX - (viewX / 2) + 1, -counter - posY + (viewY / 2), shader, world, camera, this);
                }
            }
        }

        for (Entity entity : entities) {
            entity.render(shader, camera, window, this);
        }

        for (int count = 0; count < entities.size(); count++) {
            entities.get(count).collideWithTiles(this);
        }
    }








    public void update(float delta, Window window, Camera camera) {
        for (Entity entity : entities) {
            entity.UpdateBoundingBox();
            entity.update(delta,window,camera,this);
        }

        for (int count = 0; count < entities.size(); count++) {
            entities.get(count).collideWithTiles(this);
            for (int counter = count + 1; counter < entities.size(); counter++) {
                entities.get(count).collideWithEntity(entities.get(counter), this, count, counter);
            }
            entities.get(count).collideWithTiles(this);
        }

        gameManager.update();
    }








    public void correctCamera(Camera camera, Window window) {
        Vector3f pos = camera.getPosition();

        int w = -width * scale * 2; // Width of world, multiply by 2 once again due to the 2* in renderTile()
        int h = height * scale * 2; // Height of world


       if (pos.x > -(window.getWidth()/2) + scale) { // Camera corrections, cannot leave world on -x side.
            pos.x = -(window.getWidth()/2) + scale; // add scale to prevent camera from stopping at half the tile.
        }

        if (pos.x < w + (window.getWidth()/2) + scale) { // Corrects camera such that it may not leave world on x side.
            pos.x = w + (window.getWidth()/2 + scale);
        }

        if (pos.y < (window.getHeight()/2) - scale) { // Camera cannot leave world on y side
            pos.y = (window.getHeight()/2)-scale;
        }

        if (pos.y > h - (window.getHeight()/2) - scale) { // Camera cannot leave world on -y side
            pos.y = h - (window.getHeight()/2) - scale;
        }
    }











    public void setTile(Tile tile, int x, int y) {
        tiles[x + y * width] = tile.getId();
        if (tile.isSolid()) {
            bounding_boxes[x+y*width] = new AABB(new Vector2f(x*2, -y*2), new Vector2f(1,1));
        } else {
            bounding_boxes[x+y*width] = null;
        }
    }









    public Tile getTile(int x, int y) {
        try {
            return Tile.tiles[tiles[x+y*width]];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }








    public AABB getTileBoundingBox(int x, int y) {
        try {
            return bounding_boxes[x+y*width];
        } catch (ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }

    public void CreateEntity(Entity entity) {
        entities.add(entity);
    }

    public void calculateView(Window window, Camera cam) {
        viewX = (int) ((window.getWidth() * cam.getProjMultiplierX())/(scale*2)) + 4;
        viewY = (int) (((window.getHeight() * cam.getProjMultiplierY())/(scale*2)) + 4);
    }

    public Entity getCountingUpEntity(int index) { return entities.get(index); }
    public void RemoveEntity(int index) {
        entities.set(index, null);
        entities.remove(index);
    }

    public int totalEntities() { return entities.size(); }

    public int getScale() { return scale; }
    public Matrix4f getWorld() { return world;}
}