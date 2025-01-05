package files.util;

import java.util.ArrayList;
import java.util.List;

public class OptionsParser {

    public static List<MoveDirection> parse(String[] args) throws Exception{
        List<MoveDirection> output = new ArrayList<>();

        for (String arg : args) {
            MoveDirection to_add = switch (arg) {
                case "f", "forward" -> MoveDirection.FORWARD;
                case "b", "backward" -> MoveDirection.BACKWARD;
                case "l", "left" -> MoveDirection.LEFT;
                case "r", "right" -> MoveDirection.RIGHT;
                default -> throw new IllegalArgumentException(arg + " is not legal move specification");
            };


            output.add(to_add);

        }

        return output;
    }

}
