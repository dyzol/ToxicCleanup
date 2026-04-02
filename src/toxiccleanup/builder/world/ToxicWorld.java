package toxiccleanup.builder.world;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.Tickable;
import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.entities.tiles.ToxicField; // for checking toxicity
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
 * A world consists of a grid of tiles. Each tick, the world progresses every tile's state,
 * and via the render method collects all renderables from each tile and its stacked entities
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
     *
     * @param tileX the tile horizontal number
     * @param tileY the tile's vertical number
     * @return the key, a string format of tile grid coordinates
     */
    private String getKey(int tileX, int tileY) {
        return tileX + "," + tileY;
    }

    /**
     * Calculates tile size from a tile's position.
     * This is needed in place() but it does not receive Dimensions (which has tileSize)
     *
     * @return tile size
     */
    private int getTileSizeFromTiles() {
        // find the smallest x coordinate among all tiles
        int minX = Integer.MAX_VALUE;
        for (List<Tile> tiles : tileGrid.values()) {
            for (Tile tile : tiles) {
                if (tile.getX() < minX) {
                    minX = tile.getX();
                }
            }
        }
        // the smallest x is tileSize/2, so tileSize = minX * 2
        return minX == Integer.MAX_VALUE ? 50 : minX * 2;
    }

    /**
     * Given a pixel position, returns all tiles in that position's grid cell
     * Specifically, it takes a pixel position, converts to tile coords and looks up all
     * tiles at that grid cell
     *
     * @param position the pixel position to look up (e.g. the player's current position)
     * @param dimensions the window dimensions for pixel-to-tile conversion
     * @return all tiles at that grid cell, empty list if no tiles found
     * @stage1
     */
    @Override
    public List<Tile> tilesAtPosition(Positionable position, Dimensions dimensions) {
        int tileSize = dimensions.tileSize();
        int tileX = position.getX() / tileSize;
        int tileY = position.getY() / tileSize;

        // create map key using grid coordinates
        String key = getKey(tileX, tileY);
        // getOrDefault() maps key to list of tiles at that cell
        // if key does NOT exist, return new empty list
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
     * Returns a copy of all tiles in the world. Modifying the returned list will
     * not affect the world's internal tile collection (although mutating tile objects
     * within it will).
     * Specifically, collects every tile from every grid cell into a single list
     *
     * @return a list of all tiles
     * @stage1
     */
    @Override
    public List<Tile> allTiles() {
        // defensive copy which creates a new list each time allTiles() is called
        List<Tile> all = new ArrayList<>();

        for (List<Tile> tiles : tileGrid.values()) {
            // add the same tile objects to new list
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
        int tileSize;

        if (tileGrid.isEmpty()) {
            // infer tileSize from its position
            if (tile.getX() > 0) {
                tileSize = tile.getX() * 2;
            } else {
                tileSize = 50; // fallback
            }
        } else {
            tileSize = getTileSizeFromTiles(); // getTileSize needs existing tiles
        }
        int tileX = tile.getX() / tileSize;
        int tileY = tile.getY() / tileSize;
        String key = getKey(tileX, tileY);

        List<Tile> tiles = tileGrid.get(key);
        if (tiles == null) {
            tiles = new ArrayList<>();
            tileGrid.put(key, tiles);
        }
        tiles.add(tile);
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