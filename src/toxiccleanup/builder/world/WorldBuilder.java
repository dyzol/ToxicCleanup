package toxiccleanup.builder.world;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.builder.entities.tiles.TileFactory;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.renderer.Dimensions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads an instance of a world from a string or file representation.
 *
 * @stage1
 */
public class WorldBuilder {

    /**
     * Reads the encoded world text and constructs the corresponding list of tiles.
     *
     * @param dimensions the dimensions of the world
     * @param text the text encoding of the world
     * @return a list of tiles loaded from the string
     * @throws WorldLoadException if the encoding is invalid
     * @stage1
     */
    public static List<Tile> fromString(Dimensions dimensions, String text) throws WorldLoadException {
        String[] lines = text.split("\\r?\\n");
        int tileSize = dimensions.tileSize();
        int expectedSize = dimensions.windowSize() / tileSize;

        // Check number of rows
        if (lines.length != expectedSize) {
            throw new WorldLoadException("Expected " + expectedSize + " rows, got " + lines.length);
        }

        List<Tile> tiles = new ArrayList<>();

        for (int row = 0; row < lines.length; row++) {
            String line = lines[row];

            // Check row length
            if (line.length() != expectedSize) {
                throw new WorldLoadException("Row " + row + " has incorrect length", row);
            }

            for (int col = 0; col < line.length(); col++) {
                char symbol = line.charAt(col);
                int x = col * dimensions.tileSize() + dimensions.tileSize() / 2;
                int y = row * dimensions.tileSize() + dimensions.tileSize() / 2;
                Position position = new Position(x, y);

                try {
                    Tile tile = TileFactory.fromSymbol(position, symbol);
                    tiles.add(tile);
                } catch (IllegalArgumentException e) {
                    throw new WorldLoadException("Invalid symbol '" + symbol + "'", row, col);
                }
            }
        }

        return tiles;
    }

    /**
     * Reads the provided file and creates a world from the tile encoding.
     *
     * @param dimensions the dimensions of the world
     * @param filepath the path to the map file
     * @return a new ToxicWorld containing all tiles from the file
     * @throws IOException if the file cannot be read
     * @throws WorldLoadException if the tile encoding is invalid
     * @stage1
     */
    public static ToxicWorld fromFile(Dimensions dimensions, String filepath)
            throws IOException, WorldLoadException {
        String content = Files.readString(Paths.get(filepath));
        List<Tile> tiles = fromString(dimensions, content);
        return fromTiles(tiles);
    }

    /**
     * Constructs a new ToxicWorld pre-populated with the given tiles.
     *
     * @param tiles the list of tiles to place in the world
     * @return a new ToxicWorld containing all given tiles
     * @stage1
     */
    public static ToxicWorld fromTiles(List<Tile> tiles) {
        ToxicWorld world = new ToxicWorld();
        // Add in reverse order so no test depends on insertion order
        for (Tile tile : tiles) {
            world.place(tile);
        }
        return world;
    }
}