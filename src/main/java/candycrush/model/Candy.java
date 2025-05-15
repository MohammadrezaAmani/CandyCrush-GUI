package candycrush.model;

import java.awt.Color;
import java.io.Serializable;

/**
 * Represents a candy in the game.
 * Candies have various properties such as type, color, and score value.
 * They can also have special abilities based on their type.
 */
public class Candy implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Type {
        SIMPLE("Simple", 5, "\u2299"),
        ROW_STRIPED("Row Striped", 10, "\u2296"),
        COLUMN_STRIPED("Column Striped", 10, "\u211A"),
        WRAPPED("Wrapped", 15, "\u229B");

        private final String name;
        private final int score;
        private final String symbol;

        Type(String name, int score, String symbol) {
            this.name = name;
            this.score = score;
            this.symbol = symbol;
        }

        public String getName() {
            return name;
        }

        public int getScore() {
            return score;
        }

        public String getSymbol() {
            return symbol;
        }
    }

    public enum CandyColor {
        RED(Color.RED, "R"),
        GREEN(Color.GREEN, "G"),
        BLUE(Color.BLUE, "B"),
        YELLOW(Color.YELLOW, "Y");

        private final Color color;
        private final String code;

        CandyColor(Color color, String code) {
            this.color = color;
            this.code = code;
        }

        public Color getColor() {
            return color;
        }

        public String getCode() {
            return code;
        }

        public static CandyColor fromCode(String code) {
            for (CandyColor color : values()) {
                if (color.code.equals(code)) {
                    return color;
                }
            }

            return RED;
        }
    }

    private Type type;
    private CandyColor color;
    private boolean isSelected;
    private boolean isExploding;
    private int row;
    private int column;
    private float animationOffset;

    /**
     * Create a simple candy with the specified color
     *
     * @param color Candy color
     */
    public Candy(CandyColor color) {
        this(Type.SIMPLE, color);
    }

    /**
     * Create a candy with the specified type and color
     *
     * @param type Candy type
     * @param color Candy color
     */
    public Candy(Type type, CandyColor color) {
        this.type = type;
        this.color = color;
        this.isSelected = false;
        this.isExploding = false;
        this.animationOffset = 0f;
    }

    /**
     * Get the position info
     */
    public void setPosition(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    /**
     * Get candy type
     *
     * @return Candy type
     */
    public Type getType() {
        return type;
    }

    /**
     * Set candy type
     *
     * @param type New candy type
     */
    public void setType(Type type) {
        this.type = type;
    }

    /**
     * Get candy color
     *
     * @return Candy color
     */
    public CandyColor getCandyColor() {
        return color;
    }

    /**
     * Set candy color
     *
     * @param color New candy color
     */
    public void setCandyColor(CandyColor color) {
        this.color = color;
    }

    /**
     * Check if candy is selected
     *
     * @return true if selected
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * Set candy selected state
     *
     * @param selected true to select, false to deselect
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    /**
     * Check if candy is exploding (part of animation)
     *
     * @return true if exploding
     */
    public boolean isExploding() {
        return isExploding;
    }

    /**
     * Set candy exploding state
     *
     * @param exploding true if exploding
     */
    public void setExploding(boolean exploding) {
        this.isExploding = exploding;
    }

    /**
     * Get animation offset (for smooth movements)
     *
     * @return Animation offset value
     */
    public float getAnimationOffset() {
        return animationOffset;
    }

    /**
     * Set animation offset
     *
     * @param offset Animation offset value
     */
    public void setAnimationOffset(float offset) {
        this.animationOffset = offset;
    }

    /**
     * Get score value for this candy
     *
     * @return Score value
     */
    public int getScore() {
        return type.getScore();
    }

    /**
     * Get symbol representing this candy
     *
     * @return Candy symbol
     */
    public String getSymbol() {
        return type.getSymbol();
    }

    /**
     * Check if two candies have the same color
     *
     * @param other Other candy to compare with
     * @return true if colors match
     */
    public boolean isSameColor(Candy other) {
        return other != null && this.color == other.color;
    }

    /**
     * Convert candy to string representation
     *
     * @return String representation (used for saving)
     */
    public String toFileString() {
        String typeCode;
        if (type == Type.SIMPLE) {
            typeCode = "SC";
        } else if (type == Type.ROW_STRIPED) {
            typeCode = "LR";
        } else if (type == Type.COLUMN_STRIPED) {
            typeCode = "LC";
        } else {
            typeCode = "RC";
        }

        return typeCode + color.getCode();
    }

    /**
     * Create candy from file string
     *
     * @param fileString String representation from save file
     * @return New candy instance
     */
    public static Candy fromFileString(String fileString) {
        if (fileString == null || fileString.length() < 3) {
            return new Candy(CandyColor.RED);
        }

        String typeCode = fileString.substring(0, 2);
        String colorCode = fileString.substring(2, 3);

        Type type;
        switch (typeCode) {
            case "SC":
                type = Type.SIMPLE;
                break;
            case "LR":
                type = Type.ROW_STRIPED;
                break;
            case "LC":
                type = Type.COLUMN_STRIPED;
                break;
            case "RC":
                type = Type.WRAPPED;
                break;
            default:
                type = Type.SIMPLE;
        }

        CandyColor color = CandyColor.fromCode(colorCode);

        return new Candy(type, color);
    }

    @Override
    public String toString() {
        return (
            "Candy{" +
            "type=" +
            type +
            ", color=" +
            color +
            ", row=" +
            row +
            ", column=" +
            column +
            '}'
        );
    }
}
