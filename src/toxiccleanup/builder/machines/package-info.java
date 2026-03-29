/**
 * Machines are powered game entities that the player builds to interact with the world.
 * The power system ({@link toxiccleanup.builder.machines.Machines} / {@link toxiccleanup.builder.machines.MachinesManager})
 * tracks a shared pool of power units consumed when machines are built. Machines include:
 *
 * <ul>
 *   <li>{@link toxiccleanup.builder.machines.SolarPanel} — generates power over time.</li>
 *   <li>{@link toxiccleanup.builder.machines.Teleporter} — teleports the player between teleporter locations.</li>
 *   <li>{@link toxiccleanup.builder.machines.Pump} — reduces the toxicity of a {@link toxiccleanup.builder.entities.tiles.ToxicField}.</li>
 * </ul>
 */
package toxiccleanup.builder.machines;
