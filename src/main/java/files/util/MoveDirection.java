package files.util;

public enum MoveDirection {
    FORWARD(0), HALF_RIGHT(1), RIGHT(2), HALF_RIGHT_BACK(3),
    BACKWARD(4), HALF_LEFT_BACK(5), LEFT(6), HALF_LEFT(7);

    MoveDirection(int code) {}
    //public int getValue() { return code; }
    public static MoveDirection getMoveDirection(int code) {
        return switch (code) {
            case 0 -> FORWARD;
            case 1 -> HALF_RIGHT;
            case 2 -> RIGHT;
            case 3 -> HALF_RIGHT_BACK;
            case 4 -> BACKWARD;
            case 5 -> HALF_LEFT_BACK;
            case 6 -> LEFT;
            case 7 -> HALF_LEFT;
            default -> null;
        };
    }
}
