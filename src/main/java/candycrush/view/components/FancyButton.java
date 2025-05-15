package candycrush.view.components;

import candycrush.audio.AudioManager;
import candycrush.util.ThemeManager;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * A customized JButton with modern styling and animation effects.
 * Features hover animations, click effects, and consistent styling.
 */
public class FancyButton extends JButton {

    private static final int CORNER_RADIUS = 15;
    private static final int ANIMATION_DURATION = 150;

    private Color normalColor;
    private Color hoverColor;
    private Color pressedColor;
    private Color textColor;
    private Color shadowColor;

    private boolean isHovered = false;
    private boolean isPressed = false;
    private float animationProgress = 0.0f;
    private Timer animationTimer;

    /**
     * Create a new fancy button with default styling
     */
    public FancyButton() {
        this("");
    }

    /**
     * Create a new fancy button with the specified text
     *
     * @param text Button text
     */
    public FancyButton(String text) {
        super(text);
        ThemeManager themeManager = ThemeManager.getInstance();
        normalColor = themeManager.getColor("PRIMARY");
        if (normalColor == null) {
            normalColor = new Color(220, 70, 120);
        }

        hoverColor = normalColor.brighter();
        pressedColor = normalColor.darker();
        textColor = Color.WHITE;
        shadowColor = new Color(0, 0, 0, 100);

        setForeground(textColor);
        setFont(new Font("Arial", Font.BOLD, 16));
        setBorderPainted(false);
        setContentAreaFilled(false);
        setFocusPainted(false);
        setOpaque(false);

        setPreferredSize(new Dimension(200, 50));

        addMouseListener(
            new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!isEnabled()) return;
                    isHovered = true;
                    startAnimation();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    if (!isEnabled()) return;
                    isHovered = false;
                    isPressed = false;
                    startAnimation();
                }

                @Override
                public void mousePressed(MouseEvent e) {
                    if (!isEnabled()) return;
                    isPressed = true;
                    startAnimation();
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    if (!isEnabled()) return;
                    isPressed = false;
                    startAnimation();

                    if (isHovered) {
                        AudioManager.getInstance()
                            .playSound(AudioManager.SOUND_BUTTON_CLICK);
                    }
                }
            }
        );
    }

    /**
     * Start the animation timer to handle transitions
     */
    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        animationTimer = new Timer(16, e -> {
            float targetProgress = isPressed ? 1.0f : (isHovered ? 0.5f : 0.0f);

            if (Math.abs(animationProgress - targetProgress) < 0.01f) {
                animationProgress = targetProgress;
                ((Timer) e.getSource()).stop();
            } else {
                animationProgress +=
                    (targetProgress - animationProgress) * 0.2f;
            }

            repaint();
        });

        animationTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        int width = getWidth();
        int height = getHeight();

        Color currentColor;
        if (isEnabled()) {
            if (animationProgress > 0.5f) {
                float ratio = (animationProgress - 0.5f) * 2;
                currentColor = interpolateColor(
                    hoverColor,
                    pressedColor,
                    ratio
                );
            } else {
                float ratio = animationProgress * 2;
                currentColor = interpolateColor(normalColor, hoverColor, ratio);
            }
        } else {
            currentColor = new Color(150, 150, 150);
        }

        g2d.setColor(shadowColor);
        g2d.fill(
            new RoundRectangle2D.Float(
                3,
                3,
                width - 6,
                height - 6,
                CORNER_RADIUS,
                CORNER_RADIUS
            )
        );

        g2d.setColor(currentColor);
        g2d.fill(
            new RoundRectangle2D.Float(
                0,
                0,
                width - 3,
                height - 3,
                CORNER_RADIUS,
                CORNER_RADIUS
            )
        );

        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.draw(
            new RoundRectangle2D.Float(
                0,
                0,
                width - 4,
                height - 4,
                CORNER_RADIUS,
                CORNER_RADIUS
            )
        );

        GradientPaint highlight = new GradientPaint(
            0,
            0,
            new Color(255, 255, 255, 120),
            0,
            height / 2,
            new Color(255, 255, 255, 0)
        );
        g2d.setPaint(highlight);
        g2d.fill(
            new RoundRectangle2D.Float(
                2,
                2,
                width - 8,
                height / 2 - 2,
                CORNER_RADIUS,
                CORNER_RADIUS
            )
        );

        g2d.dispose();

        int yOffset = isPressed ? 1 : 0;

        g2d = (Graphics2D) g.create();
        g2d.translate(0, yOffset);

        super.paintComponent(g2d);
        g2d.dispose();
    }

    /**
     * Interpolate between two colors
     *
     * @param c1 First color
     * @param c2 Second color
     * @param ratio Ratio between colors (0.0 to 1.0)
     * @return Interpolated color
     */
    private Color interpolateColor(Color c1, Color c2, float ratio) {
        int red = (int) (c1.getRed() + ratio * (c2.getRed() - c1.getRed()));
        int green = (int) (c1.getGreen() +
            ratio * (c2.getGreen() - c1.getGreen()));
        int blue = (int) (c1.getBlue() + ratio * (c2.getBlue() - c1.getBlue()));
        return new Color(
            Math.min(255, Math.max(0, red)),
            Math.min(255, Math.max(0, green)),
            Math.min(255, Math.max(0, blue))
        );
    }

    /**
     * Set custom button colors
     *
     * @param normal Normal state color
     * @param hover Hover state color
     * @param pressed Pressed state color
     * @param text Text color
     */
    public void setButtonColors(
        Color normal,
        Color hover,
        Color pressed,
        Color text
    ) {
        this.normalColor = normal;
        this.hoverColor = hover;
        this.pressedColor = pressed;
        this.textColor = text;
        setForeground(textColor);
        repaint();
    }
}
