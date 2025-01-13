package files.util;

public enum MapDirection {

    NORTH(0), NORTH_EAST(1), EAST(2), SOUTH_EAST(3), SOUTH(4), SOUTH_WEST(5), WEST(6), NORTH_WEST(7);

    @Override
    public String toString() {
        return switch (this) {
            case NORTH_EAST -> "Północny-Wschód";
            case EAST -> "Wschód";
            case WEST -> "Zachód";
            case NORTH -> "Północ";
            case SOUTH_EAST -> "Południowy-Wschód";
            case SOUTH -> "Południe";
            case SOUTH_WEST -> "Południowy-Zachód";
            case NORTH_WEST -> "Północny-Zachód";
        };
    }

    public MapDirection next() {
        return (getMapDirection((this.getValue()+1)%8));
    }

    public MapDirection previous() {
        return (getMapDirection((this.getValue()+7)%8));
    }

    public MapDirection reverse() {
        return (getMapDirection((this.getValue()+4)%8));
    }

    public Vector2d toUnitVector() {
        return switch (this) {
            case NORTH_WEST ->  new Vector2d(-1,1);
            case NORTH ->       new Vector2d(0,1);
            case NORTH_EAST ->  new Vector2d(1,1);
            case EAST ->        new Vector2d(1,0);
            case SOUTH_EAST ->  new Vector2d(1,-1);
            case SOUTH ->       new Vector2d(0,-1);
            case SOUTH_WEST ->  new Vector2d(-1,-1);
            case WEST ->        new Vector2d(-1,0);
        };
    }

    private final int code;
    MapDirection(int code) { this.code = code;}
    public int getValue() { return code; }
    public static MapDirection getMapDirection(int code) {
        return switch (code) {
            case 0 -> NORTH;
            case 1 -> NORTH_EAST;
            case 2 -> EAST;
            case 3 -> SOUTH_EAST;
            case 4 -> SOUTH;
            case 5 -> SOUTH_WEST;
            case 6 -> WEST;
            case 7 -> NORTH_WEST;
            default -> null;
        };
    }
}