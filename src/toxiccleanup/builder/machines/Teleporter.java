package toxiccleanup.builder.machines;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

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
public class Teleporter extends GameEntity implements PlayerOverHook, Powered {

    public static final int COST = 2;
    private static final int POWER_REQUIREMENT = 2;
    private final TickTimer animationTimer;
    private int animationFrame; //

    /**
     * Constructs a new Teleporter at the given position.
     *
     * @param position the position to place the teleporter
     * @stage3
     */
    public Teleporter(Positionable position) {
        super(position);
        // start at frame 1
        setSprite(SpriteGallery.teleporter.getSprite("1"));
        // change frame every 12 ticks
        this.animationTimer = new RepeatingTimer(12);
        this.animationFrame = 0;
    }

    /**
     * Returns the minimum power required for this teleporter to operate.
     *
     * @return 2
     * @stage3
     */
    @Override
    public int getPowerRequirement() {
        return POWER_REQUIREMENT;
    }

    /**
     * Called every tick. Advances the animation timer and updates the sprite
     * when the timer fires and power is sufficient.
     *
     * @param engine the engine state
     * @param game the game state
     * @stage3
     */
    @Override
    public void tick(EngineState engine, GameState game) {
        animationTimer.tick();
        if (animationTimer.isFinished() && game.getMachines().hasRequiredPower(POWER_REQUIREMENT)) {
            // Cycle through animation frames
            animationFrame = (animationFrame + 1) % 7;
            String frameNumber = String.valueOf(animationFrame + 1);
            setSprite(SpriteGallery.teleporter.getSprite(frameNumber));
        }
    }

    /**
     * Called when the player stands on this teleporter. If 'e' is pressed and
     * sufficient power is available, teleports the player to another teleporter.
     *
     * @param state the engine state
     * @param game the game state
     * @stage3
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        if (state.getKeys().isDown('e') && game.getMachines().hasRequiredPower(POWER_REQUIREMENT)) {
            Positionable newPosition = game.getMachines().getNextTeleporterPosition(getPosition());
            game.getPlayer().setPosition(newPosition);
        }
    }
}
