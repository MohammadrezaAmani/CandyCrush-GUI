package candycrush.view.components;

import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import javax.swing.*;

/**
 * A customized JPanel with a semi-transparent glassmorphism effect.
 * Provides a modern, frosted glass appearance for UI elements.
 */
public class GlassPanel extends JPanel {

    private static final int CORNER_RADIUS = 20;
    private static final float ALPHA = 0.7f;
    private static final Color GLASS_COLOR = new Color(255, 255, 255, 200);
    private static final Color BORDER_COLOR = new Color(255, 255, 255, 100);

    /**
     * Create a new glass panel with default layout
     */
    public GlassPanel() {
        this(new BorderLayout());
    }

    /**
     * Create a new glass panel with specified layout
     *
     * @param layout Layout manager to use
     */
    public GlassPanel(LayoutManager layout) {
        super(layout);
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(
            RenderingHints.KEY_ANTIALIASING,
            RenderingHints.VALUE_ANTIALIAS_ON
        );

        RoundRectangle2D roundedRect = new RoundRectangle2D.Float(
            0,
            0,
            getWidth(),
            getHeight(),
            CORNER_RADIUS,
            CORNER_RADIUS
        );

        g2d.setComposite(AlphaComposite.SrcOver.derive(ALPHA));

        g2d.setColor(GLASS_COLOR);
        g2d.fill(roundedRect);

        g2d.setStroke(new BasicStroke(1.5f));
        g2d.setColor(BORDER_COLOR);
        g2d.draw(roundedRect);

        GradientPaint gradient = new GradientPaint(
            0,
            0,
            new Color(255, 255, 255, 40),
            0,
            getHeight(),
            new Color(255, 255, 255, 10)
        );
        g2d.setPaint(gradient);
        g2d.fill(roundedRect);

        g2d.dispose();
    }
}
