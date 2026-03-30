package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.game.Positionable;

/**
 * A HUD icon representing one unit of the player's HP.
 *
 * @stage2
 */
public class Heart extends GameEntity {

    /**
     * Constructs a new Heart at the given position.
     *
     * @param position the position to place the heart
     * @stage2
     */
    public Heart(Positionable position) {
        super(position);
        setSprite(SpriteGallery.heart.getSprite("default"));
    }
}