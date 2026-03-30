package toxiccleanup.builder.machines;

import toxiccleanup.builder.entities.tiles.Tile;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.util.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the power system and machine spawning for the game.
 *
 * @stage2
 * @stage3
 */
public class MachinesManager implements Machines {

    private static final int MAX_POWER = 14;
    private int power;
    private final List<Positionable> teleporterPositions;

    /**
     * Constructs a new MachinesManager starting at full power (14).
     *
     * @stage2
     */
    public MachinesManager() {
        this(MAX_POWER);
    }

    /**
     * Constructs a new MachinesManager with the given starting power.
     *
     * @param power the starting power level
     * @stage2
     */
    public MachinesManager(int power) {
        this.power = Math.max(0, Math.min(MAX_POWER, power));
        this.teleporterPositions = new ArrayList<>();
    }

    /**
     * Returns whether the current power level is sufficient.
     *
     * @param powerRequirement the minimum power needed
     * @return true if current power >= powerRequirement
     * @stage2
     */
    @Override
    public boolean hasRequiredPower(int powerRequirement) {
        return power >= powerRequirement;
    }

    /**
     * Returns the current power level.
     *
     * @return current power
     * @stage2
     */
    @Override
    public int getPower() {
        return power;
    }

    /**
     * Sets the power level, clamped to [0, MAX_POWER].
     *
     * @param value the new power level
     * @stage2
     */
    @Override
    public void setPower(int value) {
        this.power = Math.max(0, Math.min(MAX_POWER, value));
    }

    /**
     * Returns the maximum power capacity.
     *
     * @return MAX_POWER (14)
     * @stage2
     */
    @Override
    public int getMaxPower() {
        return MAX_POWER;
    }

    /**
     * Adds the given amount to current power, clamped to [0, MAX_POWER].
     *
     * @param amount the amount to add (negative to subtract)
     * @stage2
     */
    @Override
    public void adjust(int amount) {
        setPower(power + amount);
    }

    /**
     * Returns the position of a random teleporter other than the excluded one.
     *
     * @param excludedPosition the teleporter position to exclude
     * @return a random teleporter position
     * @stage3
     */
    @Override
    public Positionable getNextTeleporterPosition(Positionable excludedPosition) {
        // Stage 2: Return the excluded position (no random selection yet)
        // Stage 3: Implement full random selection
        return excludedPosition;
    }

    /**
     * Attempts to spawn a SolarPanel at the given position.
     *
     * @param position the position to spawn at
     * @return a new SolarPanel, or null if insufficient power
     * @stage3
     */
    @Override
    public SolarPanel spawnSolarPanel(Positionable position) {
        // Stage 2: Return null
        // Stage 3: Implement spawning logic
        return null;
    }

    /**
     * Attempts to spawn a Teleporter at the given position.
     *
     * @param position the position to spawn at
     * @return a new Teleporter, or null if insufficient power
     * @stage3
     */
    @Override
    public Teleporter spawnTeleporter(Positionable position) {
        // Stage 2: Return null
        // Stage 3: Implement spawning logic
        return null;
    }

    /**
     * Attempts to spawn a Pump at the given position.
     *
     * @param position the position to spawn at
     * @param adjustable the target to adjust (ToxicField)
     * @return a new Pump, or null if insufficient power
     * @stage3
     */
    @Override
    public Pump spawnPump(Positionable position, Adjustable adjustable) {
        // Stage 2: Return null
        // Stage 3: Implement spawning logic
        return null;
    }
}