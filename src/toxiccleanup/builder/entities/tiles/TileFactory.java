package toxiccleanup.builder.entities.tiles;

import toxiccleanup.engine.game.Positionable;

/**
 * A tile factory constructs new tile instances from a symbol encoding.
 *
 * @stage1
 */
public class TileFactory {

    /**
     * Constructs a new Tile based on the given symbol at given position
     * | Character | Tile
     * | g    | Grass
     * | d    | Dirt
     * | t    | ToxicField
     * | c    | Chasm (fallable)
     * | l    | Chasm (left edge)
     * | L    | Chasm (left slope)
     * | r    | Chasm (right edge)
     * | R    | Chasm (right slope)
     *
     * @param position the position to place next tile at
     * @param symbol symbol to identify the tile type
     * @return a new Tile instance
     * @throws IllegalArgumentException if symbol does not correspond to a tile
     * @requires position.getX() >= 0, position.getY() >= 0
     * @stage1
     */
    public static Tile fromSymbol(Positionable position, char symbol) {
        return switch (symbol) {
            case 'g' -> new Grass(position);
            case 'd' -> new Dirt(position);
            case 't' -> new ToxicField(position);
            case 'c' -> new Chasm(position);
            case 'l' -> new Chasm(position, "left");
            case 'L' -> new Chasm(position, "leftslope");
            case 'r' -> new Chasm(position, "right");
            case 'R' -> new Chasm(position, "rightslope");
            default -> throw new IllegalArgumentException("Unknown tile symbol: " + symbol);
        };
    }
}