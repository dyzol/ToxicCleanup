package toxiccleanup.builder.ui;

import toxiccleanup.builder.GameState;
import toxiccleanup.builder.SpriteGallery;
import toxiccleanup.builder.entities.GameEntity;
import toxiccleanup.engine.EngineState;
import toxiccleanup.engine.game.Position;
import toxiccleanup.engine.game.Positionable;
import toxiccleanup.engine.renderer.Renderable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages all HUD (heads-up display) elements shown during gameplay.
 * Each tick, the GuiManager rebuilds the following elements:
 * Power icon: a lightning-bolt icon in the top-left corner.
 * Power bar: a vertical column of PowerBar segments below the power icon, showing how much
 * of the maximum power (14) is currently available; filled segments represent available power,
 * empty segments represent spent power.
 * Hearts: a vertical column of Heart icons in the top-right corner, one per remaining HP point.
 * Countdown timer: a text display at the bottom-left showing the remaining game time in
 * minutes and seconds.
 * When the game ends, win(toxiccleanup.engine.EngineState) or
 * lose(toxiccleanup.engine.EngineState) is called to overlay a win/lose message
 * Once set, this message persists until the game restarts.
 *
 * @stage2
 */
public class GuiManager implements Overlay {

    private static final int MAX_POWER = 14;
    private static final int GAME_DURATION_TICKS = 18000; // 5 minutes at 60 ticks/sec
    private static final int TICKS_PER_SECOND = 60;
    private static final int CHAR_WIDTH = 50;

    private List<Renderable> currentRenderables;
    private List<Renderable> messageRenderables;
    private boolean gameEnded;

    /**
     * Constructs a new GuiManager.
     *
     * @stage2
     */
    public GuiManager() {
        this.currentRenderables = new ArrayList<>();
        this.messageRenderables = new ArrayList<>();
        this.gameEnded = false;
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
        if (gameEnded) {
            return;
        }

        List<Renderable> renderables = new ArrayList<>();
        int tileSize = state.getDimensions().tileSize();
        int windowSize = state.getDimensions().windowSize();

        // Calculate positions
        int powerIconX = tileSize / 2;
        int powerIconY = tileSize / 2;

        // Power icon
        int heartIconX = windowSize - tileSize / 2;
        int heartIconY = tileSize / 2;
        renderables.add(new PowerIcon(new Position(powerIconX, powerIconY)));

        // Power bars
        int currentPower = game.getMachines().getPower();
        for (int i = 0; i < MAX_POWER; i++) {
            int barY = powerIconY + (i + 1) * tileSize;
            boolean charged = i < currentPower;
            renderables.add(new PowerBar(new Position(powerIconX, barY), charged));
        }

        // Hearts
        int currentHp = game.getPlayer().getHp();
        for (int i = 0; i < currentHp; i++) {
            int heartY = heartIconY + i * tileSize;
            renderables.add(new Heart(new Position(heartIconX, heartY)));
        }

        // Countdown timer at bottom-left
        int ticksRemaining = GAME_DURATION_TICKS - state.currentTick();
        int secondsRemaining = Math.max(0, ticksRemaining / TICKS_PER_SECOND);
        int minutes = secondsRemaining / 60;
        int seconds = secondsRemaining % 60;
        String timeText = String.format("%d %02d", minutes, seconds);

        // timer text
        int timerX = tileSize / 2;
        int timerY = windowSize - tileSize / 2;
        renderables.addAll(createTextRenderables(timeText, timerX, timerY));

        this.currentRenderables = renderables;
    }

    /**
     * Switches the GUI to display a centred "YOU WIN" message.
     *
     * @param state the engine state
     * @stage2
     */
    public void win(EngineState state) {
        gameEnded = true;
        int windowSize = state.getDimensions().windowSize();
        String message = "YOU WIN";
        int totalWidth = message.length() * CHAR_WIDTH;
        int x = (windowSize - totalWidth) / 2;
        int y = windowSize / 2;
        messageRenderables = createTextRenderables(message, x, y);
    }

    /**
     * Switches the GUI to display a centred "GAME OVER" message.
     *
     * @param state the engine state
     * @stage2
     */
    public void lose(EngineState state) {
        gameEnded = true;
        int windowSize = state.getDimensions().windowSize();
        String message = "GAME OVER";
        int x = (windowSize - (message.length() * CHAR_WIDTH)) / 2;
        int y = windowSize / 2;
        messageRenderables = createTextRenderables(message, x, y);
    }

    /**
     * Returns all HUD renderables for the current frame.
     *
     * @return list of renderables to display
     * @stage2
     */
    @Override
    public List<Renderable> render() {

        // always see normal HUD (hearts, power bars, timer)
        List<Renderable> allRenderables = new ArrayList<>(currentRenderables);

        // if game ended, add the win/lose message on top
        if (gameEnded) {
            allRenderables.addAll(messageRenderables);
        }

        return allRenderables;
    }

    /**
     * Creates renderables for each character in a text string.
     */
    private List<Renderable> createTextRenderables(String text, int startX, int startY) {
        List<Renderable> renderables = new ArrayList<>();
        int offset = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            String spriteName = getSpriteName(c);

            int x = startX + offset;

            TextChar character = new TextChar(new Position(x, startY), spriteName);
            renderables.add(character);
            offset += CHAR_WIDTH;
        }

        return renderables;
    }

    /**
     * Converts a character to the corresponding sprite name.
     */
    private String getSpriteName(char c) {
        if (c >= 'A' && c <= 'Z') {
            return String.valueOf(c);
        }
        if (c >= '0' && c <= '9') {
            return String.valueOf(c);
        }
        if (c == ' ') {
            return "space";
        }
        return "space";
    }

    /**
     * Inner class for a single text character.
     */
    private static class TextChar extends GameEntity {
        public TextChar(Positionable position, String spriteName) {
            super(position);
            setSprite(SpriteGallery.letters.getSprite(spriteName));
        }

        @Override
        public void tick(EngineState state, GameState game) {
            // Do nothing
        }
    }
}