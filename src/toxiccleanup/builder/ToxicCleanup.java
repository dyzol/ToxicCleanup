package toxiccleanup.builder;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.ToxicCleanupGameState;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.builder.player.PlayerManager;
import toxiccleanup.builder.world.WorldBuilder;
import toxiccleanup.builder.world.WorldLoadException;
import toxiccleanup.builder.world.ToxicWorld;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Game;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Dimensions;
import toxiccleanup.engine.renderer.Renderable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link ToxicCleanup} is a tile-based game in which the player cleans up toxic waste fields
 * using machines powered by solar panels and transported via teleporters.
 *
 * <p>The game ends when either all toxic fields are cleared (win) or the player's HP reaches
 * zero (lose). Player HP is periodically reduced while any toxic fields remain.
 *
 * @multistage
 */
public class ToxicCleanup implements Game {
    private static final int DAMAGE_INTERVAL = 1800; // 1 HP every 30 seconds at 60 ticks/s
    private final PlayerManager playerManager; // stage 0
    private ToxicWorld world; // stage 2


    /**
     * Constructs an instance of {@link ToxicCleanup} using default settings
     * for player location, map loading, and starting power, and spawns a starter
     * {@link Teleporter} at the given position.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Calculates the player's starting pixel
     * position based on tile coordinates (5, 5) and initialises the
     * {@link PlayerManager} with that position.
     *
     * <p><span style="color:#2E75B2;">Stage 1:</span> Loads the game world from the map file using
     * {WorldBuilder#fromFile} with the given dimensions.
     *
     * <p><span style="color:#F5D000;">Stage 2:</span> Initialises the {MachinesManager} and {GuiManager}.
     *
     * <p><span style="color:#D02F83;">Stage 4:</span> spawns a starter
     * {@link Teleporter} at the given position.
     *
     * @param dimensions                the dimensions of the game window, used to calculate tile
     *                                  positions and place entities on the grid.
     * @param starterTeleporterPosition the tile position at which to spawn the initial
     *                                  {@link Teleporter}.
     * @throws IOException        if the target map file could not be read.
     * @throws WorldLoadException if the target map file failed to parse.
     * @provided
     * @stage1
     * @stage2
     * @stage4
     */
    public ToxicCleanup(Dimensions dimensions, Positionable starterTeleporterPosition)
            throws IOException, WorldLoadException {
        // calculate player's starting position
        final int playerX = 5 * dimensions.tileSize() + dimensions.tileSize() / 2;
        final int playerY = 5 * dimensions.tileSize() + dimensions.tileSize() / 2;

        // stage 0: initialise player manager
        this.playerManager = new PlayerManager(new Position(playerX, playerY));

        // stage 1: load world from map file
        this.world = WorldBuilder.fromFile(dimensions, "resources/wasteland.map");
    }

    /**
     *Advances the game by one frame.
     *
     * <p>Each call updates active game systems (world, player, and GUI), applies end-state checks,
     * and enforces periodic toxicity damage during ongoing play.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Starter code only; No method body is provided.
     *
     * <p><span style="color:#14CC2A;">Stage 0:</span> Ticks the player by creating a new
     * {@link ToxicCleanupGameState} from the {@link PlayerManager} and passing it along with the engine
     * state to {@link PlayerManager#tick}.
     *
     * <p><span style="color:#F5D000;">Stage 2:</span> Updates the {@link ToxicCleanupGameState} to include the
     * {@link ToxicWorld} and {@link MachinesManager}. Ticks the {@link ToxicWorld} and {@link GuiManager} each frame.
     *
     * <p><span style="color:#D02F83;">Stage 4:</span> After ticking the world, checks if the player is no longer alive.
     * and displays the game-over screen, checks if no toxic fields remain and displays the win screen,
     * and advances the damage timer dealing 1 damage to the player every 1800 ticks.
     *
     * @param engine current engine input/state.
     * @ensures If the player is dead at end-state evaluation, the lose overlay is shown.
     * @ensures If no toxic fields remain at end-state evaluation, the win overlay is shown.
     * @ensures If neither end condition holds, gameplay progresses normally and periodic damage
     *          is applied when the damage timer finishes.
     * @provided
     * @stage0
     * @stage2
     * @stage4
     */
    public void tick(EngineState engine) {
        // stage 0: create GameState and tick player
        GameState gameState = new ToxicCleanupGameState(playerManager);

        // Tick the player manager
        playerManager.tick(engine, gameState);


    }

    /**
     * Returns all renderables for the current frame in back-to-front draw order.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Creates a new list of renderables and adds
     * the player's renderables from {@link PlayerManager#render()} to it, then returns the list.
     *
     * <p><span style="color:#2E75B2;">Stage 1:</span> Adds the world's renderables from
     * {@link ToxicWorld#render()} to the list.
     *
     * <p><span style="color:#F5D000;">Stage 2:</span> Adds the GUI renderables from
     * {@link GuiManager#render()} to the list.
     *
     * @return A list of renderables to draw, in back-to-front order.
     * @provided
     * @stage1
     * @stage2
     */
    @Override
    public List<Renderable> render() {
        final List<Renderable> renderables = new ArrayList<>();

        // stage 1: add world tiles first
        renderables.addAll(world.render());

        // stage 0: add player on top of world
        renderables.addAll(playerManager.render());

        return renderables;
    }
}
