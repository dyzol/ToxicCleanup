package toxiccleanup.builder.player;

import toxiccleanup.builder.GameState;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Direction;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.timing.RepeatingTimer;
import toxiccleanup.engine.timing.TickTimer;

import java.util.List;

/**
 * Manages all aspects of the player's state and behaviour during the game. This class:
 *
 * <ul>
 *   <li>Holds and controls the {@link Cleaner} entity that is rendered on screen.</li>
 *   <li>Tracks the player's current HP (starts at 10, clamped to [0, 10]) and exposes it
 *       via the {@link Harmable} interface.</li>
 *   <li>Each tick, reads keyboard input (WASD) to move the player one tile per movement
 *       timer interval, updates the facing sprite accordingly, and enforces window boundaries.</li>
 *   <li>After moving, determines which tiles are currently under the player and calls
 *       {@link toxiccleanup.builder.entities.PlayerOverHook#playerOver} on each of them so tiles can react
 *       (e.g. a {@link toxiccleanup.builder.entities.tiles.Chasm} deals damage, a
 *       {@link toxiccleanup.builder.entities.tiles.Dirt} listens for build input).</li>
 *   <li>When the player's HP reaches 0, the dead sprite is shown and movement/interaction stops.</li>
 * </ul>
 *
 * @hint The player manager should hold an instance of {@link Cleaner}.
 * @multistage
 */
public class PlayerManager implements Player {
    private static final int MAX_HP = 10;
    private static final int SPEED = 10; // originally 1, change this back after testing
    private final Cleaner player;
    //used to track if players recently moved
    private final TickTimer movementTimer = new RepeatingTimer(10);
    private int hp;

    /**
     * Constructs a new {@link PlayerManager}, creating an internal {@link Cleaner} entity at
     * the given pixel position and initialising the player's HP to the maximum (10).
     *
     * @param position the pixel position at which to spawn the {@link Cleaner} entity.
     * @requires the position to be a valid position within the game window
     * (x and y &ge; 0 and &le; window size).
     *
     * @provided
     */
    public PlayerManager(Positionable position) {
        super();
        player = new Cleaner(position);
        hp = MAX_HP;
    }

    /**
     * Advances player state for one game tick.
     *
     * <p>The managed player entity is ticked first.</p>
     *
     * <p>If the player is alive and one or more movement keys are pressed, the player moves by
     * exactly one tile in a single direction. The movement keys are:</p>
     *
     * <table>
     * <caption>&nbsp;</caption>
     * <tr><th>Key</th><th>Direction</th></tr>
     * <tr><td>w</td><td>NORTH</td></tr>
     * <tr><td>s</td><td>SOUTH</td></tr>
     * <tr><td>a</td><td>WEST</td></tr>
     * <tr><td>d</td><td>EAST</td></tr>
     * </table>
     *
     * <p>If multiple movement keys are pressed, only one movement is applied. The priority order is
     * {@code w}, then {@code s}, then {@code a}, then {@code d}.</p>
     *
     * <p>After movement, this method processes interactions with any tiles currently overlapping the
     * player's position by invoking their player-over behaviour.</p>
     *
     * <p>If the player is not alive, the player does not move, and the dead sprite is shown.</p>
     *
     * <p><span style="color:#9B59B6;">Provided:</span> Starter code only; The method signature is provided without a body.
     *
     * <p><span style="color:#14CC2A;">Stage 0:</span> Movement is performed as a one-tile step (equivalent to {@code player.move(direction, 1)} when that helper is available)
     *
     *  <p><b>Hint:</b> Read movement input using {@code state.getKeys().isDown(char)}
     *  (for {@code 'w'}, {@code 's'}, {@code 'a'}, {@code 'd'}).
     *
     * <p><span style="color:#F55600;">Stage 3:</span> Ticks the managed player entity, checks if the
     * player is alive before moving, processes tile interactions by invoking their player-over behaviour,
     * and displays the dead sprite if the player is no longer alive.
     *
     * @param state the current state of the toxiccleanup.engine.
     * @param game  the current state of the game.
     * @ensures player moves within the game window boundaries, and only if alive.
     *
     * @provided
     * @stage0
     * @stage3
     */
    @Override
    public void tick(EngineState state, GameState game) {
        // first tick the player entity
        player.tick(state, game);

        // check if player is alive
        if (!isAlive()) {
            return;
        }

        // Priority order: w > s > a > d
        if (state.getKeys().isDown('w')) {
            player.move(Direction.NORTH, SPEED);
        } else if (state.getKeys().isDown('s')) {
            player.move(Direction.SOUTH, SPEED);
        } else if (state.getKeys().isDown('a')) {
            player.move(Direction.WEST, SPEED);
        } else if (state.getKeys().isDown('d')) {
            player.move(Direction.EAST, SPEED);
        }
        // find boundary limits (half-tile offset)
        // get window dimensions from state
        int windowWidth = state.getDimensions().windowSize();
        int windowHeight = state.getDimensions().windowSize();
        int tileSize = state.getDimensions().tileSize();

        // half a tile before edge to prevent half the player get offscreen
        int minX = tileSize / 2;
        int maxX = windowWidth - (tileSize / 2);
        int minY = tileSize / 2;
        int maxY = windowHeight - (tileSize / 2);

        int newX = player.getX();
        int newY = player.getY();

        if (newX < minX) player.setX(minX);
        if (newX > maxX) player.setX(maxX);
        if (newY < minY) player.setY(minY);
        if (newY > maxY) player.setY(maxY);
    }

    /**
     * Returns whether the player is currently alive. Used by {@link toxiccleanup.builder.ToxicCleanup#tick}
     * to decide whether to display the game-over screen and stop gameplay, and by
     * {@link #tick} to decide whether to show the dead sprite and skip movement.
     *
     * @return {@code true} if the player's HP is greater than 0; {@code false} if HP is 0.
     *
     * @provided
     */
    public boolean isAlive() {
        return hp > 0;
    }

    /**
     * Returns the current pixel position of the {@link Cleaner} entity as a new
     * {@link Position} snapshot. Used by {toxiccleanup.builder.world.ToxicWorld} to
     * determine which tiles are currently under the player, and by other components that
     * need to know where the player is.
     *
     * @return a new {@link Positionable} containing the player's current x and y coordinates.
     * @provided
     */
    public Positionable getPosition() {
        return new Position(this.player.getX(), this.player.getY());
    }

    /**
     * Moves the internal {@link Cleaner} entity to the given pixel position by directly setting
     * its x and y coordinates. Used by {toxiccleanup.builder.machines.Teleporter#playerOver} to
     * teleport the player to a new location instantly.
     *
     * @param mockPosition the x and y pixel coordinates to move the player to.
     * @provided
     */
    public void setPosition(Positionable mockPosition) {
        this.player.setX(mockPosition.getX());
        this.player.setY(mockPosition.getY());
    }

    /**
     * Returns the player's current HP. HP starts at 10 and decreases when the player
     * stands on a {toxiccleanup.builder.entities.tiles.Chasm} or when the game's periodic damage
     * timer fires. HP is always in the range [0, {@link #getMaxHp()}].
     *
     * @return the current HP value.
     * @provided
     */
    public int getHp() {
        return hp;
    }

    /**
     * Returns the player's maximum HP. The player starts with this value and cannot exceed it.
     * The number of {toxiccleanup.builder.ui.Heart} icons shown in the HUD equals this value when
     * the player is at full health.
     *
     * @return {@code 10}, the maximum HP the player can have.
     * @provided
     */
    public int getMaxHp() {
        return PlayerManager.MAX_HP;
    }

    /**
     * Returns the renderables that represent the player for the current frame. The player
     * manager only renders the {@link Cleaner} entity itself - a single-element list containing
     * the cleaner, which the toxiccleanup.engine draws at the cleaner's current pixel position.
     *
     * @return a single-element list containing the {@link Cleaner} entity to be rendered.
     *
     * @provided
     */
    @Override
    public List<Renderable> render() {
        return List.of(player);
    }

    /**
     * Subtracts the given amount from the player's HP score, then clamps the result to the
     * range [0, {@link #getMaxHp()}]. A positive {@code amount} causes damage; the interface
     * convention (from {@link toxiccleanup.builder.machines.Adjustable}) uses positive values to reduce HP.
     * HP cannot go below 0 or above the maximum.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> The method signature is provided without a body.
     *
     * @param amount amount to subtract from the player's HP (positive = damage).
     * @provided
     */
    @Override
    public void adjust(int amount) {
        hp -= amount;
        hp = Math.clamp(hp, 0, MAX_HP);
    }
}
