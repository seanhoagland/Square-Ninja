package world;
public class Tile {
    public static Tile tiles[] = new Tile[255];
    public static byte not = 0;

    // .setSolid() sets a tile solid meaning entities cannot walk over it.

    public static final Tile brown = new Tile("tiles/woodBackground"); // id 0
    public static final Tile normalGrass = new Tile("tiles/grass"); // id 1


    private byte id; // Represents constant textures above
    private boolean solid; // represents if tile should collide with entities.
    private String texture; // Texture location

    public Tile(String texture) {
        this.id = not;
        not++;
        this.solid = false;
        this.texture = texture;
        if (tiles[id] != null) {
            throw new IllegalStateException("Tiles at: [" + id + "] is already being used!");
        }
        tiles[id] = this;
    }

    public Tile setSolid() { this.solid = true; return this; }
    public boolean isSolid() { return solid; }

    public byte getId() {
        return id;
    }

    public String getTexture() {
        return texture;
    }
}
