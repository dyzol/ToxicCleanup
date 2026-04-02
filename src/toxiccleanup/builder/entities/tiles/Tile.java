package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState; // added
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.builder.entities.PlayerOverHook;
import toxiccleanup.builder.ui.RenderableGroup;
import toxiccleanup.engine.EngineState; // added
import toxiccleanup.engine.art.sprites.SpriteGroup;
import toxiccleanup.engine.game.HasTick;
import toxiccleanup.engine.game.Positionable; // added
import toxiccleanup.engine.renderer.Renderable;
import toxiccleanup.engine.art.ArtNotFoundException;

// added
import java.util.ArrayList;
import java.util.List;

/**
 * The abstract base class for all ground tiles in the game world. Each tile occupies one cell
 * in the grid and is responsible for:
 *
 * <ul>
 *   <li>Displaying itself using a SpriteGroup set at construction (or changed later
 *       via setArt).</li>
 *   <li>Maintaining a stack of {@link GameEntity}s that sit on top of it (e.g. a
 *       toxiccleanup.builder.machines.SolarPanel placed on a paved {@link Dirt} tile).</li>
 *   <li>Ticking itself and all stacked entities each frame, and removing any that are marked
 *       for removal.</li>
 *   <li>Implementing {@link PlayerOverHook playerOver} to react when the player stands on this
 *       cell, and forwarding the event to any stacked entities that also implement the hook.</li>
 *   <li>Collecting all {@link Renderable}s for itself and its stack via render.</li>
 * </ul>
 *
 * <p>Concrete tile types ({@link Dirt}, {@link Grass}, {@link Chasm}, {@link ToxicField})
 * extend this class and implement their specific mechanics.
 *
 * <p><span style="color:#9B59B6;">Provided:</span> Starter code only; this class initially has no
 * extends/implements, fields, or method bodies.
 *
 * <p><span style="color:#2E75B2;">Stage 1:</span> Extends {@link GameEntity} and implements
 * {@link PlayerOverHook}, {@link RenderableGroup}, and {@link HasTick}. Adds fields and methods
 * for managing tile art, stacked entities, ticking, rendering, and player-over interactions.
 *
 * @provided
 * @stage1
 */
public abstract class Tile extends GameEntity implements PlayerOverHook, RenderableGroup, HasTick {

    private SpriteGroup art; // group of sprites for tile art
    private final List<GameEntity> stackedEntities; // entities stacked on this tile

    /**
     * Constructs an instance of Tile at the given Positionable.
     * @param position The position we wish to place this newly constructed tile at.
     * @param art - The sprite group art to use for this tile, the tile will initially render as
     *            the 'default' sprite for this group.
     * @require position.getX() >= 0, position.getX() is less than the window width,
     *          position.getY() >= 0, position.getY() is less than the window height,
     *          The given sprite group must contain a 'default' sprite.
    */
    public Tile(Positionable position, SpriteGroup art) {
        // inherit position from GameEntity
        super(position);
        // initialise art to use and array of entities
        this.art = art;
        this.stackedEntities = new ArrayList<>();
        updateSprite("default");
    }

    /**
     * Replaces this tile's sprite group and immediately switches the displayed sprite to
     * the 'default' sprite within the new group. Used when a tile changes its visual appearance
     * entirely, for example when a Dirt tile is paved and switches from the dirt sprite group
     * to the paved sprite group.
     * @param art a sprite group to use for this tile's sprites going forward.
     * @require The given sprite group must contain a 'default' sprite.
     */
    public void setArt(SpriteGroup art) {
        this.art = art;
        updateSprite("default");
    }

    /**
     * Change the current sprite (see Entity.setSprite(Sprite)) to the given artwork name within
     * the tiles current art (i.e. the sprite group provided to the constructor or set by
     * setArt(SpriteGroup)).
     * @param artName The name of the art within the sprite group.
     * @throws ArtNotFoundException if the given name doesn't exist within the sprite group.
     */
    public void updateSprite(String artName) throws ArtNotFoundException {
        setSprite(art.getSprite(artName));
    }

    /**
     * Advances this tile by one game tick, removes any stacked entities marked for removal,
     * then advances each remaining stacked entity by one tick.
     * Specified by: tick in interface Tickable
     * Overrides: tick in class GameEntity
     * @param engine - The state of the toxiccleanup.engine, including the mouse, keyboard
     *               information and dimension.
     * @param game - The state of the game, including the player and world.
     */
    @Override
    public void tick(EngineState engine, GameState game) {
        // Tick each remaining stacked entity first to prevent pump remaining onscreen after win
        for (GameEntity entity : stackedEntities) {
            entity.tick(engine, game);
        }
        // remove entities marked for removal
        // removeIf handles concurrent modification
        stackedEntities.removeIf(GameEntity::isMarkedForRemoval);
    }

    /**
     * Returns the list of entities currently stacked on top of this tile
     * @return Any entities stacked onto this tile.
     */
    public List<GameEntity> getStackedEntities() {
        return new ArrayList<>(stackedEntities);
    }

    /**
     * Filters the stacked entity list to return only those that implement PlayerOverHook.
     * Used by subclasses in their playerOver(toxiccleanup.engine.EngineState,
     * toxiccleanup.builder.GameState) implementations to forward the event to relevant stacked
     * machines without needing to type-check manually.
     * @return a new List containing only the entities that implement PlayerOverHook;
     *         empty if no such entities exist.
     */
    public List<PlayerOverHook> getStackedEntitiesWithPlayerOverHook() {
        List<PlayerOverHook> hooks = new ArrayList<>();
        for (GameEntity entity : stackedEntities) {
            if (entity instanceof PlayerOverHook) {
                hooks.add((PlayerOverHook) entity);
            }
        }
        return hooks;
    }

    /**
     * Adds the given entity to this tile's stacked entity list. Used when a machine is built
     * on top of a tile (e.g. placing a SolarPanel on a paved Dirt tile). After this call the
     * entity will be ticked and rendered as part of this tile each frame.
     * @param tile - the GameEntity to place on top of this tile.
     * @ensures The entity is contained within getStackedEntities().
     */
    public void placeOn(GameEntity tile) {
        stackedEntities.add(tile); // add tile to list of stacked entities
    }

    /**
     * Called each tick the player occupies this tile's grid cell. The base implementation
     * forwards the event to every stacked entity that also implements PlayerOverHook,
     * allowing machines or other entities on the tile to react to the player's presence.
     * Subclasses should call this (or re-implement the forwarding) and add their own interaction
     * logic (e.g. dealing damage, building machines).
     * Specified by: playerOver in interface PlayerOverHook
     * @param state - The state of the toxiccleanup.engine, including the mouse,
     * keyboard information and dimension. Useful for processing keyboard presses or mouse movement
     * @param game - The state of the game, including the player and world. Can be used to query or
     * update the game state.
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        // forward the event to all stacked PlayerOverHook entities
        for (PlayerOverHook hook : getStackedEntitiesWithPlayerOverHook()) {
            hook.playerOver(state, game);
        }
    }

    /**
     * A collection of items to render, including the tile and any entities stacked on it.
     * This tile must be the first renderable in the list so that it is rendered behind each
     * stacked entity. The remaining list must match the order of getStackedEntities().
     * Specified by: render in interface RenderableGroup
     * @return The list of renderables required to draw this tile to the screen.
     */
    @Override
    public List<Renderable> render() {
        List<Renderable> renderables = new ArrayList<>();
        // this tile first (eg. grass, dirt, chasm)
        renderables.add(this);
        // add all stacked entities in order
        renderables.addAll(stackedEntities);
        return renderables;
    }
}
