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

    private final Map<String, List<Tile>> tileGrid;

    public ToxicWorld() {
        this.tileGrid = new HashMap<>();
    }

    /**
     * Returns the key for a given grid cell position.
     */
    private String getKey(int tileX, int tileY) {
        return tileX + "," + tileY;
    }

    /**
     * Returns all tiles whose grid cell matches the given pixel position.
     *
     * @param position the pixel position to look up
     * @param dimensions the window dimensions for pixel-to-tile conversion
     * @return all tiles at that grid cell
     * @stage1
     */
    @Override
    public List<Tile> tilesAtPosition(Positionable position, Dimensions dimensions) {
        int tileSize = dimensions.tileSize();
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
        for (List<Tile> tiles : tileGrid.values()) {
            for (Tile tile : tiles) {
                if (tile instanceof ToxicField && ((ToxicField) tile).isToxic()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns a copy of all tiles in the world.
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
        int tileSize = 32; // Assuming standard tile size, or get from dimensions?
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
                tile.tick(state, game);
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
                all.addAll(tile.render());
            }
        }
        return all;
    }
}