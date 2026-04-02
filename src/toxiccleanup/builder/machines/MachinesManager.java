package toxiccleanup.builder.machines;

import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.util.RandomNumberGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Tracks the power level (starts and capped at 14)
 * Spending power when machine is built; SolarPanel Teleporter and Pump have their fixed cost
 * Constructing and returning new machine instances when there is sufficient power,
 * or returning null if the power cost cannot be met.
 * Tracking all teleporter positions so that the Teleporter can retrieve
 * a destination when the player activates one.
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
     * @param power the starting power level, between 0 and 14 inclusive
     * @stage2
     */
    public MachinesManager(int power) {
        this.power = Math.max(0, Math.min(MAX_POWER, power));
        this.teleporterPositions = new ArrayList<>();
    }

    /**
     * Returns whether the current power level is sufficient for a machine's operation
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
     * @param value the new power level to set
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
     * Returns the position of a random teleporter other than the excluded one,
     * chosen at random from registered teleporter locations. Used by Teleporter to determine
     * where to send the player.
     * If only one teleporter, its own position is returned
     *
     * @param excludedPosition the teleporter position to exclude
     * @return a random teleporter position
     * @stage2
     * @stage3
     */
    @Override
    public Positionable getNextTeleporterPosition(Positionable excludedPosition) {

        // just one teleporter, so teleport to itself
        if (teleporterPositions.isEmpty()) {
            return excludedPosition;
        }
        if (teleporterPositions.size() == 1) {
            return teleporterPositions.getFirst();
        }

        // list of candidate teleporters (excludes latest teleporter: excludedPosition)
        List<Positionable> candidates = new ArrayList<>();
        for (Positionable pos : teleporterPositions) {
            if (pos.getX() != excludedPosition.getX() || pos.getY() != excludedPosition.getY()) {
                candidates.add(pos);
            }
        }

        // If only the excluded position exists, return it
        if (candidates.isEmpty()) {
            return excludedPosition;
        }

        // Return a random candidate
        RandomNumberGenerator num = new RandomNumberGenerator();
        int randomIndex = num.nextInt(candidates.size());
        return candidates.get(randomIndex);
    }

    /**
     * Attempts to spawn a SolarPanel at the given position.
     *  If the current power is at least 3 (the solar panel's cost),
     *  deducts 3 power and returns the new instance.
     *  Returns null if there is insufficient power.
     *
     * @param position the position to spawn at
     * @return a new SolarPanel, or null if insufficient power
     * @stage2
     * @stage3
     */
    @Override
    public SolarPanel spawnSolarPanel(Positionable position) {
        // has sufficient power
        if (hasRequiredPower(SolarPanel.COST)) {
            adjust(-SolarPanel.COST);
            // spawn solar panel
            return new SolarPanel(position);
        }
        return null; // insufficient power
    }

    /**
     * Attempts to spawn a Teleporter at the given position.
     * If the current power is at least 2 (the teleporter's cost),
     * deducts 2 power, records the teleporter's position for future
     * getNextTeleporterPosition(toxiccleanup.engine.game.Positionable) calls,
     * and returns the new instance.
     * Returns null if there is insufficient power.
     *
     * @param position the position to spawn at
     * @return a new Teleporter, or null if insufficient power (< 2)
     * @stage2
     * @stage3
     */
    @Override
    public Teleporter spawnTeleporter(Positionable position) {
        if (hasRequiredPower(Teleporter.COST)) {
            adjust(-Teleporter.COST);
            Teleporter teleporter = new Teleporter(position);
            teleporterPositions.add(position);
            return teleporter;
        }
        return null;
    }

    /**
     * Attempts to spawn a Pump at the given position.
     * If the current power is at least 5 (the pump's cost), deducts 5 power
     * and returns a new Pump that will call Adjustable.adjust(int) on adjustable
     * every 100 ticks. Returns null if there is insufficient power.
     *
     * @param position the position to spawn Pump at
     * @param adjustable the target ToxicField to whose adjustable value is reduced each time
     *                   the pump fires
     * @return a new Pump, or null if insufficient power
     * @stage2
     * @stage3
     */
    @Override
    public Pump spawnPump(Positionable position, Adjustable adjustable) {
        if (hasRequiredPower(Pump.COST)) {
            adjust(-Pump.COST);
            return new Pump(position, adjustable);
        }
        return null;
    }
}