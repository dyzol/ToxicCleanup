package toxiccleanup.builder.ui;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.game.Positionable;

/**
 * A HUD element representing one unit of the power bar.
 * Can be charged (power available) or uncharged (power spent).
 *
 * @stage2
 */
public class PowerBar extends GameEntity {

    /**
     * Constructs an uncharged PowerBar at the given position.
     *
     * @param position the position to place the power bar
     * @stage2
     */
    public PowerBar(Positionable position) {
        this(position, false);
    }

    /**
     * Constructs a PowerBar at the given position with the specified state.
     *
     * @param position the position to place the power bar
     * @param charged true for charged state, false for uncharged
     * @stage2
     */
    public PowerBar(Positionable position, boolean charged) {
        super(position);
        if (charged) {
            setSprite(SpriteGallery.power.getSprite("chargedbar"));
        } else {
            setSprite(SpriteGallery.power.getSprite("bar"));
        }
    }
}