package toxiccleanup.builder.player;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.tiles.Tile;
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
 * Holds and controls the Cleaner entity that is rendered on screen.
 * Tracks the player's current HP (starts at 10, clamped to [0, 10]) and exposes it via the Harmable interface.
 * Each tick, reads keyboard input (WASD) to move the player one tile per movement timer interval,
 * updates the facing sprite accordingly, and enforces window boundaries.
 * After moving, determines which tiles are currently under the player and calls
 * PlayerOverHook.playerOver(toxiccleanup.engine.EngineState, toxiccleanup.builder.GameState)
 * on each of them so tiles can react (e.g. a Chasm deals damage, a Dirt listens for build input).
 * When the player's HP reaches 0, the dead sprite is shown and movement/interaction stops.
 * hint The player manager should hold an instance of Cleaner.
 * @multistage
 */
public class PlayerManager implements Player {
    private static final int MAX_HP = 10;
    private static final int SPEED = 1;
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
     * w NORTH
     * s SOUTH
     * a WEST
     * d EAST
     *
     * <p>If multiple movement keys are pressed, only one movement is applied. The priority order
     * is
     * w, s, a, d</p>
     *
     * After movement, this method processes interactions with any tiles currently overlapping the
     * player's position by invoking their player-over behaviour.
     * If the player is not alive, the player does not move, and the dead sprite is shown.
     *
     * @stage0 Movement is performed as a one-tile step (equivalent to
     * {player.move(direction, 1)} when that helper is available)
     *
     *  <p>Hint: Read movement input using {@code state.getKeys().isDown(char)}
     *  (for 'w' 's' 'a' 'd').
     *
     * @stage3 Ticks the managed player entity, checks if the
     * player is alive before moving, processes tile interactions by invoking their player-over
     * behaviour,
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

        // ensure player is alive
        if (!isAlive()) {
            return;
        }

        int tileSize = state.getDimensions().tileSize();

        movementTimer.tick(); // counts down from 10

        // only move when timer is finished (cooldown is over)
        if (movementTimer.isFinished()) {
            // Priority order: w > s > a > d
            if (state.getKeys().isDown('w')) {
                player.move(Direction.NORTH, tileSize);
                // timer resets automatically (RepeatingTimer resets after isFinished)
            } else if (state.getKeys().isDown('s')) {
                player.move(Direction.SOUTH, tileSize);
            } else if (state.getKeys().isDown('a')) {
                player.move(Direction.WEST, tileSize);
            } else if (state.getKeys().isDown('d')) {
                player.move(Direction.EAST, tileSize);
            }
        }

        // find boundary limits (half-tile offset)
        // get window dimensions from state
        int windowWidth = state.getDimensions().windowSize();
        int windowHeight = state.getDimensions().windowSize();

        // ensure player moves within the game window boundaries
        int minX = tileSize / 2; // half a tile before edge
        int maxX = windowWidth - (tileSize / 2);
        int minY = tileSize / 2;
        final int maxY = windowHeight - (tileSize / 2);

        int newX = player.getX();
        int newY = player.getY();

        if (newX < minX) {
            player.setX(minX);
        }
        if (newX > maxX) {
            player.setX(maxX);
        }
        if (newY < minY) {
            player.setY(minY);
        }
        if (newY > maxY) {
            player.setY(maxY);
        }

        // Stage 3: process tile interactions AFTER movement
        // list of tiles at player's position
        List<Tile> tiles = game.getWorld().tilesAtPosition(getPosition(), state.getDimensions());
        // call playerOver on each
        for (Tile tile : tiles) {
            tile.playerOver(state, game);
        }
    }

    /**
     * Returns whether the player is currently alive. Used by
     * toxiccleanup.builder.ToxicCleanup tick
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
     * convention (from {@link toxiccleanup.builder.machines.Adjustable}) uses positive values
     * to reduce HP.
     * HP cannot go below 0 or above the maximum.
     *
     * <p><span style="color:#9B59B6;">Provided:</span> The method signature is provided
     * without a body.
     *
     * @param amount amount to subtract from the player's HP (positive = damage).
     * @provided
     */
    @Override
    public void adjust(int amount) {
        hp -= amount;
        hp = Math.clamp(hp, 0, MAX_HP);

        // use dead sprite when HP reaches 0
        if (hp == 0) {
            player.setDeadSprite();
        }
    }
}
