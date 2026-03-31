package toxiccleanup.builder;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.ToxicCleanupGameState;
import toxiccleanup.builder.entities.tiles.Dirt;
import toxiccleanup.builder.entities.tiles.Tile; // for stage 4
import toxiccleanup.builder.machines.MachinesManager;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.builder.player.PlayerManager;
import toxiccleanup.builder.ui.GuiManager;
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
    private static final int TICKS_PER_SECOND = 60;
    private static final int GAME_DURATION_TICKS = 18000; // 5 minutes at 60 ticks/sec

    private final PlayerManager playerManager; // stage 0
    private ToxicWorld world; // stage 2
    private MachinesManager machinesManager;
    private GuiManager guiManager;

    private int damageTimer;  // Countdown for periodic damage
    private boolean gameOver;  // Prevent further updates after win/loss

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

        // stage 2: initialise machines manager and GUI manager
        this.machinesManager = new MachinesManager();
        this.guiManager = new GuiManager();

        // stage 4: initialise damage timer and game over flag
        this.damageTimer = 0;
        this.gameOver = false;

        // Stage 4: Spawn starter teleporter at the given position
        Teleporter starterTeleporter = machinesManager.spawnTeleporter(starterTeleporterPosition);
        if (starterTeleporter != null) {
            // Find the tile at that position and place the teleporter on it
            List<Tile> tiles = world.tilesAtPosition(starterTeleporterPosition, dimensions);
            for (Tile tile : tiles) {
                // If it's dirt, pave it first
                if (tile instanceof Dirt) {
                    Dirt dirtTile = (Dirt) tile;
                    if (!dirtTile.isPaved()) {
                        dirtTile.pave();
                    }
                }
                tile.placeOn(starterTeleporter);
            }
        }
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
        // If game is over, don't process any more ticks
        if (gameOver) {
            return;
        }

        // stage 2: create full gamestate with world, player, machines
        GameState gameState = new ToxicCleanupGameState(world, playerManager, machinesManager);

        // stage 0: tick the player manager
        playerManager.tick(engine, gameState);

        // stage 2: tick world and gui
        world.tick(engine, gameState);
        guiManager.tick(engine, gameState);

        checkWinLoseConditions(engine, gameState);

        // Stage 4: Apply periodic damage if game is still ongoing
        if (!gameOver) {
            applyPeriodicDamage(engine, gameState);
        }
    }

    /**
     * Checks win/lose conditions and displays appropriate messages.
     *
     * @param engine the engine state
     * @param game the game state
     * @stage4
     */
    private void checkWinLoseConditions(EngineState engine, GameState game) {
        // Lose condition: player HP <= 0
        if (!playerManager.isAlive()) {
            gameOver = true;
            guiManager.lose(engine);
            return;
        }

        // Win condition: no toxic fields remain
        if (!world.isToxic()) {
            gameOver = true;
            guiManager.win(engine);
        }
        // Stage 4: Lose condition - time runs out
        int currentTick = engine.currentTick();
        int ticksRemaining = GAME_DURATION_TICKS - currentTick;
        if (ticksRemaining <= 0) {
            gameOver = true;
            guiManager.lose(engine);
        }
    }

    /**
     * Applies periodic damage to the player every DAMAGE_INTERVAL ticks
     * if any toxic fields still exist.
     *
     * @param engine the engine state
     * @param game the game state
     * @stage4
     */
    private void applyPeriodicDamage(EngineState engine, GameState game) {
        // no need to damage if game is over
        if (gameOver) {
            return;
        }

        // Only apply damage if there are toxic fields
        if (world.isToxic()) {
            damageTimer++;

            if (damageTimer >= DAMAGE_INTERVAL) {
                // Deal 1 damage to player
                playerManager.adjust(1);
                damageTimer = 0;  // Reset timer
            }
        } else {
            // No toxic fields, reset timer
            damageTimer = 0;
        }
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

        // stage 2: guiManager
        renderables.addAll(guiManager.render());

        return renderables;
    }
}
