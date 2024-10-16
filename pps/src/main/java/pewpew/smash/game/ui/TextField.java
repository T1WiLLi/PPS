package pewpew.smash.game.ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import lombok.Getter;
import pewpew.smash.engine.Canvas;

public class TextField extends UiElement {

    @Getter
    private String text;
    @Getter
    private boolean focused;
    private int textOffset;
    private int cursorPosition;
    private long lastBlinkTime;
    private boolean cursorVisible;
    private Font font;
    private FontMetrics fontMetrics;

    public TextField(int x, int y, int w, int h) {
        super(x, y, w, h);
        this.text = "";
        this.focused = false;
        this.textOffset = 0;
        this.cursorPosition = 0;
        this.lastBlinkTime = System.currentTimeMillis();
        this.cursorVisible = true;
        this.font = new Font("Impact", Font.PLAIN, 24);
    }

    @Override
    protected void loadSprites(BufferedImage spriteSheet) {
        // No sprites to load for TextField
    }

    @Override
    public void update() {
        updateScaledBounds();
        updateCursorBlink();
    }

    @Override
    public void render(Canvas canvas) {
        Graphics2D g2d = canvas.getGraphics2D();
        fontMetrics = g2d.getFontMetrics(font);
        renderBackground(canvas);
        renderText(canvas);
        renderCursor(canvas);
    }

    private void renderBackground(Canvas canvas) {
        canvas.renderRectangle(xPos, yPos, width, height, Color.WHITE);
        canvas.renderRectangleBorder(xPos, yPos, width, height, 2, focused ? Color.GREEN : Color.BLACK);
    }

    private void renderText(Canvas canvas) {
        canvas.setFont(font);
        textOffset = calculateTextOffset();
        clipAndRenderText(canvas);
        canvas.resetFont();
    }

    private void clipAndRenderText(Canvas canvas) {
        Graphics2D g2d = canvas.getGraphics2D();
        g2d.setClip(xPos, yPos, width, height);
        canvas.renderString(text, xPos + 5 - textOffset, yPos + height / 2 + 10, Color.BLACK);
        g2d.setClip(null);
    }

    private void renderCursor(Canvas canvas) {
        if (focused && cursorVisible) {
            int cursorX = xPos + 5 - textOffset + fontMetrics.stringWidth(text.substring(0, cursorPosition));
            canvas.renderLine(cursorX, yPos + 5, cursorX, yPos + height - 5, 2, Color.BLACK);
        }
    }

    private void updateCursorBlink() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBlinkTime >= 500) {
            cursorVisible = !cursorVisible;
            lastBlinkTime = currentTime;
        }
    }

    private int calculateTextOffset() {
        int visibleWidth = width - 10;
        int cursorX = fontMetrics.stringWidth(text.substring(0, cursorPosition));
        if (cursorX > visibleWidth) {
            return cursorX - visibleWidth;
        } else {
            return 0;
        }
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
        if (focused) {
            cursorVisible = true;
            lastBlinkTime = System.currentTimeMillis();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (focused) {
            handleKeyEvent(e);
        }
    }

    private void handleKeyEvent(KeyEvent e) {
        switch (e.getKeyCode()) {
            case KeyEvent.VK_BACK_SPACE:
                handleBackspace();
                break;
            case KeyEvent.VK_DELETE:
                handleDelete();
                break;
            case KeyEvent.VK_LEFT:
                moveCursorLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moveCursorRight();
                break;
            default:
                handleCharacterInput(e);
                break;
        }
    }

    private void handleBackspace() {
        if (cursorPosition > 0) {
            text = removeCharacterAt(cursorPosition - 1);
            cursorPosition--;
            adjustTextOffsetAfterCursorMovement();
        }
    }

    private void handleDelete() {
        if (cursorPosition < text.length()) {
            text = removeCharacterAt(cursorPosition);
        }
    }

    private void moveCursorLeft() {
        if (cursorPosition > 0) {
            cursorPosition--;
            adjustTextOffsetAfterCursorMovement();
        }
    }

    private void moveCursorRight() {
        if (cursorPosition < text.length()) {
            cursorPosition++;
            adjustTextOffsetAfterCursorMovement();
        }
    }

    private void handleCharacterInput(KeyEvent e) {
        char c = e.getKeyChar();
        if (isValidCharacter(c)) {
            insertCharacterAtCursor(c);
            adjustTextOffsetAfterCursorMovement();
        }
    }

    private boolean isValidCharacter(char c) {
        return Character.isLetterOrDigit(c) || Character.isWhitespace(c) || isSpecialCharacter(c);
    }

    private boolean isSpecialCharacter(char c) {
        String specialCharacters = "!@#$%?&*()_+-=<>[]{}|;:'\",./`~";
        return specialCharacters.indexOf(c) >= 0;
    }

    private void insertCharacterAtCursor(char c) {
        text = text.substring(0, cursorPosition) + c + text.substring(cursorPosition);
        cursorPosition++;
    }

    private String removeCharacterAt(int index) {
        return text.substring(0, index) + text.substring(index + 1);
    }

    private void adjustTextOffsetAfterCursorMovement() {
        int cursorX = fontMetrics.stringWidth(text.substring(0, cursorPosition));
        int visibleWidth = width - 10;
        if (cursorX - textOffset > visibleWidth) {
            textOffset = cursorX - visibleWidth;
        } else if (cursorX < textOffset) {
            textOffset = cursorX;
        }
    }

    @Override
    protected void handleMouseInput() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleMouseInput'");
    }

    @Override
    protected void handleMouseMove() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'handleMouseMove'");
    }
}