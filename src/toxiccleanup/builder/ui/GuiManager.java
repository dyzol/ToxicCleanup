package toxiccleanup.builder.ui;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all HUD (heads-up display) elements shown during gameplay.
 *
 * @stage2
 */
public class GuiManager implements Overlay {

    private static final int MAX_POWER = 14;
    private static final int GAME_DURATION_TICKS = 18000; // 5 minutes at 60 ticks/sec
    private static final int TICKS_PER_SECOND = 60;

    private List<Renderable> currentRenderables;
    private String endGameMessage; // "YOU WIN" or "GAME OVER", null if game ongoing

    /**
     * Constructs a new GuiManager.
     *
     * @stage2
     */
    public GuiManager() {
        this.currentRenderables = new ArrayList<>();
        this.endGameMessage = null;
    }

    /**
     * Called each game tick to rebuild all HUD elements from the current game state.
     *
     * @param state the engine state
     * @param game the game state
     * @stage2
     */
    @Override
    public void tick(EngineState state, GameState game) {
        // If game is over, don't rebuild normal HUD (just keep win/lose message)
        if (endGameMessage != null) {
            return;
        }

        List<Renderable> renderables = new ArrayList<>();
        int tileSize = state.getDimensions().tileSize();
        int windowSize = state.getDimensions().windowSize();

        // Calculate positions
        int powerIconX = tileSize / 2;
        int powerIconY = tileSize / 2;

        int heartIconX = windowSize - tileSize / 2;
        int heartIconY = tileSize / 2;

        // Add power icon
        renderables.add(new PowerIcon(new Position(powerIconX, powerIconY)));

        // Add power bars (vertical column below power icon)
        int currentPower = game.getMachines().getPower();
        for (int i = 0; i < MAX_POWER; i++) {
            int barY = powerIconY + (i + 1) * tileSize;
            boolean charged = i < currentPower;
            renderables.add(new PowerBar(new Position(powerIconX, barY), charged));
        }

        // Add hearts (vertical column on right side)
        int currentHp = game.getPlayer().getHp();
        for (int i = 0; i < currentHp; i++) {
            int heartY = heartIconY + i * tileSize;
            renderables.add(new Heart(new Position(heartIconX, heartY)));
        }

        // Add countdown timer at bottom-left
        int ticksRemaining = GAME_DURATION_TICKS - state.currentTick();
        int secondsRemaining = Math.max(0, ticksRemaining / TICKS_PER_SECOND);
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        String timeText = String.format("%d %02d", minutes, seconds);

        // Create text renderable for countdown (using letters sprite group)
        // Position at bottom-left corner
        int textX = tileSize / 2;
        int textY = windowSize - tileSize / 2;
        // Note: You'll need to create a TextRenderable or similar using SpriteGallery.letters
        // For now, this is a placeholder - actual text rendering depends on engine

        this.currentRenderables = renderables;
    }

    /**
     * Switches the GUI to display a centred "YOU WIN" message.
     *
     * @param state the engine state
     * @stage2
     */
    public void win(EngineState state) {
        this.endGameMessage = "YOU WIN";
        // Create centred text message
        // This will be rendered in render() instead of normal HUD
    }

    /**
     * Switches the GUI to display a centred "GAME OVER" message.
     *
     * @param state the engine state
     * @stage2
     */
    public void lose(EngineState state) {
        this.endGameMessage = "GAME OVER";
        // Create centred text message
    }

    /**
     * Returns all HUD renderables for the current frame.
     *
     * @return list of renderables to display
     * @stage2
     */
    @Override
    public List<Renderable> render() {
        if (endGameMessage != null) {
            // Return win/lose message renderables
            // This will need to be implemented with SpriteGallery.letters
            return new ArrayList<>();
        }
        return new ArrayList<>(currentRenderables);
    }
}