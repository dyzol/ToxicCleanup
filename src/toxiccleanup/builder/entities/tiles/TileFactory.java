package toxiccleanup.builder.entities.tiles;

import toxiccleanup.engine.game.Positionable;

/**
 * A tile factory constructs new tile instances from a symbol encoding.
 *
 * @stage1
 */
public class TileFactory {

    /**
     * Constructs a new Tile based on the given symbol.
     *
     * | Symbol | Tile |
     * |--------|------|
     * | 'g'    | Grass |
     * | 'd'    | Dirt |
     * | 't'    | ToxicField |
     * | 'c'    | Chasm (fallable) |
     * | 'l'    | Chasm (left edge) |
     * | 'L'    | Chasm (left slope) |
     * | 'r'    | Chasm (right edge) |
     * | 'R'    | Chasm (right slope) |
     *
     *
     * @param position the position to place the tile at
     * @param symbol the symbol representing the tile type
     * @return a new Tile instance
     * @throws IllegalArgumentException if the symbol is not recognized
     * @stage1
     */
    public static Tile fromSymbol(Positionable position, char symbol) {
        switch (symbol) {
            case 'g':
                return new Grass(position);
            case 'd':
                return new Dirt(position);
            case 't':
                return new ToxicField(position);
            case 'c':
                return new Chasm(position);
            case 'l':
                return new Chasm(position, "left");
            case 'L':
                return new Chasm(position, "leftslope");
            case 'r':
                return new Chasm(position, "right");
            case 'R':
                return new Chasm(position, "rightslope");
            default:
                throw new IllegalArgumentException("Unknown tile symbol: " + symbol);
        }
    }
}