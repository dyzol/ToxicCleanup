package toxiccleanup.builder.entities.tiles;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.entities.GameEntity; // added for adjust() when removing stacked ents
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.machines.Adjustable;
import toxiccleanup.builder.machines.Pump;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Positionable;

/**
 * A ToxicField represents a contaminated flower field that can be purified
 * using a Pump. Toxicity starts at 6 and decreases when a Pump adjusts it.
 *
 * @stage1
 */
public class ToxicField extends Tile implements Adjustable {

    private int toxicity;

    /**
     * Constructs a new toxic field tile with toxicity 6.
     *
     * @param position the position to place this tile at
     * @require position.getX() >= 0, position.getY() >= 0
     * @stage1
     */
    public ToxicField(Positionable position) {
        super(position, SpriteGallery.toxicField);
        this.toxicity = 6;
    }

    /**
     * Reduces the toxicity of this field by the given amount.
     * Updates the sprite based on the new toxicity level:
     * <ul>
     *   <li>Toxicity >= 3: default toxic appearance</li>
     *   <li>Toxicity = 2: cleanup started</li>
     *   <li>Toxicity = 1: cleanup nearly done</li>
     *   <li>Toxicity = 0: fully cleaned, marks pump for removal</li>
     * </ul>
     * Specified by adjust in interface Adjustable
     *
     * @param amount the amount to reduce toxicity by (typically 1)
     * @stage1
     */
    @Override
    public void adjust(int amount) {
        toxicity = Math.max(0, toxicity - amount);

        if (toxicity == 0) {
            // Mark all stacked entities (including pump) for removal
            for (GameEntity entity : getStackedEntities()) {
                entity.markForRemoval();
            }
            updateSprite("cleanupdone");
        } else if (toxicity == 2) {
            updateSprite("cleanupstart");
        } else if (toxicity == 1) {
            updateSprite("cleanupmid");
        }
        // toxicity >= 3 keeps the default sprite
    }

    /**
     * Returns whether this field still contains toxicity.
     *Used by ToxicWorld.isToxic() to determine whether the game has been won.
     * Also used by the pump-spawning logic to prevent placing a pump on an already-clean field
     *
     * @return true if toxicity > 0, false otherwise
     * @stage1
     */
    public boolean isToxic() {
        return toxicity > 0;
    }

    /**
     * Handles player interaction while on this tile.
     * - Left click: build a Pump (if toxic and no stacked entities)
     * Specified by Tile.playerOver
     *
     * @param state current engine input/state
     * @param game current game state
     * @stage3
     */
    @Override
    public void playerOver(EngineState state, GameState game) {
        super.playerOver(state, game); // let stacked entities react first

        // check if there's already a pump (or anything else)
        boolean isEmpty = getStackedEntities().isEmpty();

        // build condition: field is toxic, no pump, player left-clicked
        if (isToxic() && isEmpty && state.getMouse().isLeftPressed()) {
            // spawnPump returns new Pump at tile's position, cleaning 'this' toxic field itself
            // but only if has enough power
            Pump pump = game.getMachines().spawnPump(getPosition(), this);
            if (pump != null) { // if not enough power, pump's spawning fails
                placeOn(pump); // reaching here means spawning successful
            }
        }
    }
}