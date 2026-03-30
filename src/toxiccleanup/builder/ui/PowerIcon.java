package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.game.Positionable;

/**
 * A decorative HUD icon displayed in the top-left corner to label the power bar.
 *
 * @stage2
 */
public class PowerIcon extends GameEntity {

    /**
     * Constructs a new PowerIcon at the given position.
     *
     * @param position the position to place the power icon
     * @stage2
     */
    public PowerIcon(Positionable position) {
        super(position);
        setSprite(SpriteGallery.power.getSprite("icon"));
    }
}