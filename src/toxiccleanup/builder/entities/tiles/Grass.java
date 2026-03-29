package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.game.Positionable;

/**
 * A Grass tile is a purely decorative ground tile with no game mechanics.
 * The player can walk over it freely without triggering any interaction.
 *
 * @stage1
 */
public class Grass extends Tile {

    /**
     * Constructs a new grass tile at the given position.
     *
     * @param position the position to place this tile at
     * @requires position.getX() &gt;= 0, position.getY() &gt;= 0
     * @stage1
     */
    public Grass(Positionable position) {
        super(position, SpriteGallery.grass);
    }
}