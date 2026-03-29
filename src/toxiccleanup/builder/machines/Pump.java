package toxiccleanup.builder.machines;

import toxiccleanup.builder.SpriteGallery;

/**
 * A {@link Pump} is a machine that removes toxicity from a {toxiccleanup.builder.entities.tiles.ToxicField}
 * over time. Every 100 game ticks it calls {@link Adjustable#adjust(int)} on
 * its target with {@code 1}, reducing the field's toxicity by 1. The pump only operates when
 * the shared power system has at least 2 power units available - if power drops below 2, both
 * the animation and the pumping pause until power is restored.
 *
 * <p>The pump stops and is removed from the field once the field's toxicity reaches 0 (i.e.
 * when the field is fully cleaned up).
 *
 * <p>Costs {COST} power units to build. The pump cycles through a sprite animation every
 * 4 ticks while powered. Rendered using {@link SpriteGallery#pump}.
 *
 * <p><span style="color:#9B59B6;">Provided:</span> The class is provided without
 * {@code extends} or {@code implements} clauses, and with no fields or methods.
 *
 * @provided
 * @stage3
 */
public class Pump {

}
