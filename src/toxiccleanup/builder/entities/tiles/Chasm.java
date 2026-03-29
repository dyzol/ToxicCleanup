package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;

/**
 * A Chasm tile represents a dangerous pit in the game world.
 * Fallable chasms deal 1 damage to the player each tick.
 * Edge chasms are decorative and do not deal damage.
 *
 * @stage1
 */
public class Chasm extends Tile {

    private final boolean fallable;

    /**
     * Constructs a fallable chasm tile at the given position.
     *
     * @param position the position to place this tile at
     * @stage1
     */
    public Chasm(Positionable position) {
        super(position, SpriteGallery.chasm);
        this.fallable = true;
    }

    /**
     * Constructs a chasm edge tile with the given facing sprite.
     * Edge tiles are NOT fallable (they don't deal damage).
     *
     * @param position the position to place this tile at
     * @param facing the facing sprite ('left', 'leftslope', 'right', 'rightslope')
     * @stage1
     */
    public Chasm(Positionable position, String facing) {
        super(position, SpriteGallery.chasm);
        this.fallable = false;
        updateSprite(facing);
    }

    /**
     * Called each tick the player occupies this tile.
     * If fallable, deals 1 damage to the player.
     *
     * @param engine the engine state
     * @param game the game state
     * @stage1
     */
    @Override
    public void playerOver(EngineState engine, GameState game) {
        super.playerOver(engine, game);
        if (fallable) {
            game.getPlayer().adjust(1);
        }
    }
}