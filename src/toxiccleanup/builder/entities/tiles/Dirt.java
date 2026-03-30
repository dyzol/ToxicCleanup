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
 *
 * @stage1
 */
public class Dirt extends Tile {

    private boolean paved;

    /**
     * Constructs a new unpaved dirt tile at the given position.
     *
     * @param position the position to place this tile at
     * @require position.getX() &gt;= 0, position.getY() &gt;= 0
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
     * Switches the sprite group to paved art.
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
     *
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
     *
     * @param state the engine state
     * @param game the game state
     * @stage3
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        super.playerOver(state, game);

        // Check if tile has no machines
        boolean isEmpty = getStackedEntities().isEmpty();

        // Pave on 'f' key
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