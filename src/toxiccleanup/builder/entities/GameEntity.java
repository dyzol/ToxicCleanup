package toxiccleanup.builder.entities;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.Tickable;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Entity;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;

/**
 * The base class for all game objects in {@link toxiccleanup.builder.ToxicCleanup}. {@link GameEntity}
 * extends the toxiccleanup.engine's {@link Entity} (which provides x/y position and sprite rendering) and
 * additionally implements {@link Tickable} (so it is updated each game frame) and
 * {@link Positionable} (so its position can be retrieved as a {@link Position}).
 *
 * <p>All tiles, machines, the player entity, and UI items extend this class. Subclasses should
 * override {@link #tick(EngineState, GameState)} to add per-frame behavior;
 * by default, tick delegates to the toxiccleanup.engine's base {@link Entity#tick(EngineState)}.
 *
 * @provided
 */
public class GameEntity extends Entity implements Tickable, Positionable {
    /**
     * Constructs a {@link GameEntity} at the given position. Extracts the x and y pixel
     * coordinates from the {@link Positionable} and passes them to the toxiccleanup.engine's
     * {@link Entity} constructor, which stores them internally.
     *
     * @param position the position we wish the {@link GameEntity} to be spawned at.
     * @provided
     */
    public GameEntity(Positionable position) {
        super(position.getX(), position.getY());
        //convert from position to direct x, and y values at
        // the point we hit the toxiccleanup.engine
    }

    /**
     * Returns the current pixel position of this entity as a new {@link Position}
     * object. The returned object is a snapshot; changes to it do not affect this entity,
     * and future moves by this entity do not update the returned object.
     *
     * @return a new {@link Positionable} containing this entity's current x and y coordinates.
     * @provided
     */
    public Positionable getPosition() {
        return new Position(this.getX(), this.getY());
    }


    /**
     * Called once per game frame to advance this entity's state. The default implementation
     * delegates to the toxiccleanup.engine's base {@link Entity#tick(EngineState)} to handle any internal
     * toxiccleanup.engine bookkeeping (e.g. sprite animation). Subclasses should call {@code super.tick(state)}
     * and then add their own per-frame logic.
     *
     * @param state The state of the toxiccleanup.engine, including the mouse, keyboard information and
     *              dimension. Useful for processing keyboard presses or mouse movement.
     * @param game  The state of the game, including the player and world. Can be used to query or
     *              update the game state.
     * @provided
     */
    @Override
    public void tick(EngineState state, GameState game) {
        super.tick(state);
    }
}

