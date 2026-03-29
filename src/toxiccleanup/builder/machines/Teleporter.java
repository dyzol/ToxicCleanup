package toxiccleanup.builder.machines;

import toxiccleanup.builder.SpriteGallery;

/**
 * A {@link Teleporter} is a machine that allows the player to instantly travel between teleporter
 * locations on the map. When the player stands on a teleporter and presses the use key ('e'),
 * they are moved to a randomly chosen other teleporter's position — provided the shared power
 * system has at least {COST} power units available. Power is NOT consumed on use;
 * it is only required to be present.
 *
 * <p>Costs {COST} power units to build. When powered, cycles through a sprite animation
 * every 12 ticks. If power drops below the requirement, the animation pauses. Rendered using
 * {@link SpriteGallery#teleporter}.
 *
 * <p><span style="color:#9B59B6;">Provided:</span> The class is provided without
 * {@code extends} or {@code implements} clauses, and with no fields or methods.
 *
 * @provided
 * @stage3
 */
public class Teleporter {

}
