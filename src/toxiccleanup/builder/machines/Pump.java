package toxiccleanup.builder.machines;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

/**
 * A {@link Pump} is a machine that removes toxicity from a {toxiccleanup.builder.entities.tiles.ToxicField}
 * over time. Every 100 game ticks it calls {@link Adjustable#adjust(int)} on
 * its target with {@code 1}, reducing the field's toxicity by 1. The pump only operates when
 * the shared power system has at least 2 power units available - if power drops below 2, both
 * the animation and the pumping pause until power is restored.
 *
 * <p>The pump stops and is removed from the field once the field's toxicity reaches 0 (i.e.
 * when the field is fully cleaned up).
 *
 * <p>Costs {COST} power units to build. The pump cycles through a sprite animation every
 * 4 ticks while powered. Rendered using {@link SpriteGallery#pump}.
 *
 * <p><span style="color:#9B59B6;">Provided:</span> The class is provided without
 * {@code extends} or {@code implements} clauses, and with no fields or methods.
 *
 * @provided
 * @stage3
 */
public class Pump extends GameEntity implements Powered {

    public static final int COST = 5;
    private static final int POWER_REQUIREMENT = 2;
    private static final int TOTAL_FRAMES = 10;

    private final TickTimer animationTimer;
    private final TickTimer pumpTimer;
    private final Adjustable target;
    private int animationFrame;

    /**
     * Constructs a new Pump at the given position targeting the given Adjustable.
     *
     * @param position the position to place the pump
     * @param pumpTarget the Adjustable (ToxicField) to clean
     * @stage3
     */
    public Pump(Positionable position, Adjustable pumpTarget) {
        super(position);
        // start at frame 1
        setSprite(SpriteGallery.pump.getSprite("1"));
        this.target = pumpTarget;
        // change every 4 ticks
        this.animationTimer = new RepeatingTimer(4);
        // pump every 100 ticks
        this.pumpTimer = new RepeatingTimer(100);
        this.animationFrame = 0;
    }

    /**
     * Returns the minimum power required for this pump to operate.
     *
     * @return 2
     * @stage3
     */
    @Override
    public int getPowerRequirement() {
        return POWER_REQUIREMENT;
    }

    /**
     * Called every tick. Advances animation and pump timers when powered.
     *
     * @param state the engine state
     * @param game the game state
     * @stage3
     */
    @Override
    public void tick(EngineState state, GameState game) {
        if (!game.getMachines().hasRequiredPower(POWER_REQUIREMENT)) {
            return;  // Not enough power, do nothing
        }

        // Animation timer
        animationTimer.tick();
        if (animationTimer.isFinished()) {
            // cycle through frames
            animationFrame = (animationFrame + 1) % TOTAL_FRAMES;
            String frameNumber = String.valueOf(animationFrame + 1);
            setSprite(SpriteGallery.pump.getSprite(frameNumber));
        }

        // Pump timer - clean the field
        pumpTimer.tick();
        if (pumpTimer.isFinished()) {
            target.adjust(1);  // Reduce toxicity by 1
        }
    }
}
