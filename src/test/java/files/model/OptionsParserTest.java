package files.model;
import files.util.OptionsParser;
import files.util.MoveDirection;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptionsParserTest {

    @Test
    void testValidInput() throws Exception {
        String[] args = {"f", "b", "forward", "l", "r", "backward"};
        List<MoveDirection> directions = OptionsParser.parse(args);

        assertEquals(6, directions.size());
        assertEquals(List.of(
                MoveDirection.FORWARD,
                MoveDirection.BACKWARD,
                MoveDirection.FORWARD,
                MoveDirection.LEFT,
                MoveDirection.RIGHT,
                MoveDirection.BACKWARD
        ), directions);
    }

    @Test
    void testInvalidInput() {
        String[] args = {"x", "invalid", "f"};
        Exception exception = assertThrows(IllegalArgumentException.class, () -> OptionsParser.parse(args));
        assertTrue(exception.getMessage().contains("is not legal move specification"));
    }

    @Test
    void testEmptyInput() throws Exception {
        String[] args = {};
        List<MoveDirection> directions = OptionsParser.parse(args);

        assertTrue(directions.isEmpty());
    }
}
