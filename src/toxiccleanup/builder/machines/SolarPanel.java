package toxiccleanup.builder.machines;

import toxiccleanup.builder.SpriteGallery;

/**
 * A {@link SolarPanel} is a machine that passively generates power for the game's shared power
 * system. Once placed on a paved {toxiccleanup.builder.entities.tiles.Dirt} tile, it increments the
 * power in the {MachinesManager} by 1 every 120 game ticks (approximately every 2 seconds
 * at 60 ticks per second). Power is capped at the machine manager's maximum (14 by default).
 *
 * <p>Costs {COST} power units to build. Rendered using {@link SpriteGallery#solarPanel}.
 *
 * <p><span style="color:#9B59B6;">Provided:</span> The class is provided without
 * {@code extends} or {@code implements} clauses, and with no fields or methods.
 *
 * @provided
 * @stage3
 */
public class SolarPanel {

}
