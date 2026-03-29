package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.game.Positionable;

/**
 * A Dirt tile is a buildable ground tile with two visual states:
 * unpaved and paved. Tiles start unpaved.
 *
 * @stage1
 */
public class Dirt extends Tile {

    private boolean paved;

    /**
     * Constructs a new unpaved dirt tile at the given position.
     *
     * @param position the position to place this tile at
     * @require position.getX() &gt;= 0, position.getY() &gt;= 0
     * @stage1
     */
    public Dirt(Positionable position) {
        super(position, SpriteGallery.dirt);
        this.paved = false;
    }

    /**
     * Returns whether this dirt tile has been paved.
     *
     * @return true if paved, false otherwise
     * @stage3
     */
    public boolean isPaved() {
        return paved;
    }

    /**
     * Transitions this tile from unpaved to paved.
     * Switches the sprite group to paved art.
     *
     * @stage3
     */
    public void pave() {
        if (!paved) {
            paved = true;
            setArt(SpriteGallery.paved);
        }
    }
}