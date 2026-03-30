package toxiccleanup.builder;

import toxiccleanup.builder.machines.Machines;
import toxiccleanup.builder.player.Player;
import toxiccleanup.builder.player.PlayerManager;
import toxiccleanup.builder.world.World;

/**
 * The ToxicCleanup-specific implementation of the GameState interface.
 * This class bundles together the three core components of the
 * game(the World, the PlayerManager, and the Machines system)
 * into a single snapshot that is passed to every
 * Tickable.tick(toxiccleanup.engine.EngineState, toxiccleanup.builder.GameState)
 * call each frame. Components that receive a GameState can use it to read or modify the world,
 * query the player's position and HP, and interact with the power and machine system.
 */
public class ToxicCleanupGameState implements GameState {
    // fields
    private final World world;
    private final Player player;
    private final Machines machines;

    /**
     * Constructs a new ToxicCleanupGameState wrapping only the player manager.
     * Use this constructor when only player-related state is needed and world or machine
     * access is not required.
     * @param player - the player manager, used to query position, HP, and move the player.
     */
    public ToxicCleanupGameState(PlayerManager player) {
        this.world = null;
        this.player = player;
        this.machines = null;
    }
    /**
     * Constructs a new ToxicCleanupGameState wrapping the three core game components.
     *
     * @param world the game world
     * @param player the player manager
     * @param machines the machine manager
     */
    public ToxicCleanupGameState(World world, PlayerManager player, Machines machines) {
        this.world = world;
        this.player = player;
        this.machines = machines;
    }

    /**
     * Returns the current state of the game world.
     * The returned world is mutable, that is, calling mutator methods such as World.place(Tile)
     * will modify the world.
     * Specified by:
     * getWorld in interface GameState
     * Returns:
     * The game world.
     */
    @Override
    public World getWorld() {
        return world;
    }

    /**
     * Returns the current state of the player. Useful for retrieving the player's location.
     * Specified by:
     * getPlayer in interface GameState
     * Returns:
     * The player of the game.
     */
    @Override
     public Player getPlayer() {
        return player;
     }

    /**
     * Returns the current state of the machine system.
     * Specified by:
     * getMachines in interface GameState
     * Returns:
     * the Machines instance, providing access to machine spawning, teleporter locations, and the power system.
     */
    @Override
    public Machines getMachines() {
        return machines;
    }
}
