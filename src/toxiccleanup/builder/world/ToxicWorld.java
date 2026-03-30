package toxiccleanup.builder.world;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.Tickable;
import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.entities.tiles.ToxicField; // add
import toxiccleanup.builder.ui.RenderableGroup;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A World implementation for the ToxicCleanup game.
 *
 * @stage1
 */
public class ToxicWorld implements World, Tickable, RenderableGroup {

    // stores all tiles in the world
    // maps a grid cell string "5,3" to list of tiles at that cell
    // because multiple entities can stack on same tile
    private final Map<String, List<Tile>> tileGrid;

    /**
     * Create an empty world with no tiles
     */
    public ToxicWorld() {
        this.tileGrid = new HashMap<>();
    }

    /**
     * Converts grid coordinates into a string key for the hashmap
     * for example, getKey(5, 3) returns "5,3"
     */
    private String getKey(int tileX, int tileY) {
        return tileX + "," + tileY;
    }

    /**
     * Calculates tile size from a tile's position.
     * Since tiles are placed at (col * tileSize + tileSize/2),
     * we can find tileSize by looking at the x coordinate.
     * The smallest x coordinate should be tileSize/2.
     */
    private int getTileSizeFromTiles() {
        // Find the smallest x coordinate among all tiles
        int minX = Integer.MAX_VALUE;
        for (List<Tile> tiles : tileGrid.values()) {
            for (Tile tile : tiles) {
                if (tile.getX() < minX) {
                    minX = tile.getX();
                }
            }
        }
        // The smallest x is tileSize/2, so tileSize = minX * 2
        return minX == Integer.MAX_VALUE ? 50 : minX * 2;
    }

    /**
     * Returns all tiles whose grid cell matches the given pixel position.
     * Specifically, it takes a pixel position, converts to tile coords and looks up all
     * tiles at that grid cell
     *
     * @param position the pixel position to look up
     * @param dimensions the window dimensions for pixel-to-tile conversion
     * @return all tiles at that grid cell, empty list if no tiles found
     * @stage1
     */
    @Override
    public List<Tile> tilesAtPosition(Positionable position, Dimensions dimensions) {
        int tileSize = dimensions.tileSize();  // Use dimensions for lookup
        int tileX = position.getX() / tileSize;
        int tileY = position.getY() / tileSize;
        String key = getKey(tileX, tileY);
        return tileGrid.getOrDefault(key, new ArrayList<>());
    }

    /**
     * Returns whether any tile in the world is still toxic.
     *
     * @return true if any toxic field has toxicity > 0
     * @stage1
     */
    public boolean isToxic() {
        for (List<Tile> tiles : tileGrid.values()) { // iterates thru every tile in world
            for (Tile tile : tiles) {
                // checks if it's a ToxicField and toxicity > 0
                if (tile instanceof ToxicField && ((ToxicField) tile).isToxic()) {
                    return true; // found a toxic field
                }
            }
        }
        return false; // no toxic fields left
    }

    /**
     * Returns a copy of all tiles in the world.
     * Specifically, collects every tile from every grid cell into a single list
     *
     * @return a list of all tiles
     * @stage1
     */
    @Override
    public List<Tile> allTiles() {
        List<Tile> all = new ArrayList<>();
        for (List<Tile> tiles : tileGrid.values()) {
            all.addAll(tiles);
        }
        return all;
    }

    /**
     * Places a tile into the world at its encoded position.
     *
     * @param tile the tile to add
     * @stage1
     */
    @Override
    public void place(Tile tile) {
        int tileSize = getTileSizeFromTiles();

        if (tileGrid.isEmpty()) {
            // For the first tile, we can't calculate tileSize yet
            // We'll use a temporary method: infer from the tile's position
            // Since tile is at center, tileSize is at least (x * 2) for col=0
            // For col=0, x = tileSize/2, so tileSize = x * 2
            if (tile.getX() > 0) {
                tileSize = tile.getX() * 2;
            } else {
                tileSize = 50; // fallback
            }
        } else {
            tileSize = getTileSizeFromTiles();
        }
        int tileX = tile.getX() / tileSize;
        int tileY = tile.getY() / tileSize;
        String key = getKey(tileX, tileY);
        tileGrid.computeIfAbsent(key, k -> new ArrayList<>()).add(tile);
    }

    /**
     * Advances every tile in the world by one tick.
     *
     * @param state the engine state
     * @param game the game state
     * @stage1
     */
    @Override
    public void tick(EngineState state, GameState game) {
        for (List<Tile> tiles : tileGrid.values()) {
            for (Tile tile : tiles) {
                tile.tick(state, game); // each tile updates itself
            }
        }
    }

    /**
     * Returns all renderables for every tile in the world.
     *
     * @return list of renderables in draw order
     * @stage1
     */
    @Override
    public List<Renderable> render() {
        List<Renderable> all = new ArrayList<>();
        for (List<Tile> tiles : tileGrid.values()) {
            for (Tile tile : tiles) {
                all.addAll(tile.render()); // each tile returns itself and stacked entities
            }
        }
        return all;
    }
}