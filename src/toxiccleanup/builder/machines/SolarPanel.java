package toxiccleanup.builder.machines;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

/**
 * A {@link SolarPanel} is a machine that passively generates power for the
 * game's shared power
 * system. Once placed on a paved {toxiccleanup.builder.entities.tiles.Dirt} tile,
 * it increments the
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
public class SolarPanel extends GameEntity {
    public static final int COST = 3;
    private final TickTimer generationTimer;

    /**
     * Constructs a new SolarPanel at the given position.
     *
     * @param position the position to place the solar panel
     * @stage3
     */
    public SolarPanel(Positionable position) {
        super(position);
        setSprite(SpriteGallery.solarPanel.getSprite("default"));
        this.generationTimer = new RepeatingTimer(120);
    }

    /**
     * Called every game tick to advance the solar panel's internal timer.
     * When the timer fires (every 120 ticks), adds 1 power to
     * the shared machine power system via Machines.adjust(int).
     *
     * @param state The state of the toxiccleanup.engine, including the mouse,
     *              keyboard information and dimension.
     *              Useful for processing keyboard presses or mouse movement.
     * @param game  state of the game, providing access to the machine power system
     * @stage3
     */
    @Override
    public void tick(EngineState state, GameState game) {
        generationTimer.tick();
        if (generationTimer.isFinished()) {
            game.getMachines().adjust(1);  // Add 1 power
        }
    }
}