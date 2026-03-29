package toxiccleanup.builder.player;

import toxiccleanup.builder.Tickable;
import toxiccleanup.builder.ui.RenderableGroup;
import toxiccleanup.engine.game.Positionable;

/**
 * An interface to query information about the player in the game including position, hp,
 * render any sprites etc.
 *
 * @provided
 */
public interface Player extends Tickable, RenderableGroup, Harmable {
    /**
     * Returns the horizontal (x-axis) and vertical (y-axis) coordinate of the player entity.
     *
     * @return The horizontal (x-axis) and vertical (y-axis) coordinate.
     * @ensures \result >= 0
     * @ensures \result is less than the window width
     */
    Positionable getPosition();

    /**
     * Sets the horizontal (x-axis) and vertical (y-axis) coordinate of the player entity.
     *
     * @param mockPosition the position
     */
    void setPosition(Positionable mockPosition);
}
