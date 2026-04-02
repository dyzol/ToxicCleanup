package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.machines.SolarPanel;
import toxiccleanup.builder.machines.Teleporter;
import toxiccleanup.engine.game.Positionable;
// added for stage 3
import toxiccleanup.builder.GameState;
import toxiccleanup.builder.machines.Machines;
import toxiccleanup.engine.EngineState;

/**
 * A Dirt tile is a buildable ground tile with two visual states:
 * unpaved and paved. Tiles start unpaved.
 * When the player stands on an unpaved dirt tile and presses the pave key ('f')
 * the tile transitions to the paved state. It is rendered using the default sprite
 * of SpriteGallery.dirt
 * Once paved and with no machines already on it, the player can build machines by clicking:
 *     Left-click: attempts to build a SolarPanel (costs 3 power).
 *     Right-click: attempts to build a Teleporter (costs 2 power).
 * Both actions delegate to Machines which checks whether sufficient power is
 * available before constructing the machine. If power is insufficient, nothing is built.
 * @stage1
 * @stage3
 */
public class Dirt extends Tile {

    private boolean paved;

    /**
     * Constructs a new unpaved dirt tile at the given position.
     *
     * @param position the position to place this tile at
     * @require position.getX() >= 0, position.getY() >= 0
     * @stage1
     */
    public Dirt(Positionable position) {
        super(position, SpriteGallery.dirt);
        this.paved = false;
    }

    /**
     * Returns whether this dirt tile has been paved.
     *
     * @return true if paved, false otherwise
     * @stage3
     */
    public boolean isPaved() {
        return paved;
    }

    /**
     * Transitions this tile from unpaved to paved.
     * Sets paved flag to true
     * Switches and renders the sprite group to paved art.
     *
     * @stage3
     */
    public void pave() {
        if (!paved) {
            paved = true;
            setArt(SpriteGallery.paved);
        }
    }

    /**
     * Asks the machine system to build a SolarPanel at this tile's position.
     * Succeeds if sufficient power, with new solar panel placedOn current tile
     * if insufficient power, nothing happens
     *
     * @param spawner the Machines instance
     * @stage3
     */
    public void attemptSpawnSolarPanel(Machines spawner) {
        SolarPanel panel = spawner.spawnSolarPanel(getPosition());
        if (panel != null) {
            placeOn(panel);
        }
    }

    /**
     * Asks the machine system to build a Teleporter at this tile's position.
     * Spawn if successful (sufficient power), nothing happens otherwise
     * @param spawner the Machines instance
     * @stage3
     */
    public void attemptSpawnTeleporter(Machines spawner) {
        Teleporter teleporter = spawner.spawnTeleporter(getPosition());
        if (teleporter != null) {
            placeOn(teleporter);
        }
    }

    /**
     * Handles player interaction while on this tile.
     * - 'f' key: pave the tile (if unpaved)
     * - Left click: build SolarPanel (if paved and no stacked entities)
     * - Right click: build Teleporter (if paved and no stacked entities)
     * Overrides playerOver in class Tile
     * @param state the engine state
     * @param game the game state
     * @stage3
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        super.playerOver(state, game);

        // Check if tile has no machines
        // getStackedEntities() is list
        // isEmpty() checks if that list is empty
        boolean isEmpty = getStackedEntities().isEmpty();

        // Pave on 'f' key if unpaved
        if (!paved && state.getKeys().isDown('f')) {
            pave();
            return; // prevent building on same tick as paving
        }

        // Handle building on paved tiles with no machines
        if (paved && isEmpty) {
            // left click = build solar panel
            if (state.getMouse().isLeftPressed()) {
                attemptSpawnSolarPanel(game.getMachines());
            } else if (state.getMouse().isRightPressed()) { // right click = build teleporter
                attemptSpawnTeleporter(game.getMachines());
            }
        }
    }
}