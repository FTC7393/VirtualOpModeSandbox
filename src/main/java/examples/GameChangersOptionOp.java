package examples;

import external.opmode.AbstractOptionsOpMode;
import external.util.OptionEntries;

public class GameChangersOptionOp extends AbstractOptionsOpMode {

    public GameChangersOptionOp(String optionsFilePath) {
        super(optionsFilePath + "-gamechangers", GameChangersOptions.class);
    }

}

enum GameChangersOptions implements OptionEntries {
    TEAM_COLOR(TypeData
            .enumType(TeamColor.class)
            .withFallback(TeamColor.RED)
    ),
    INITIAL_AUTO_DELAY(TypeData
            .integerType(1, 0, 15)
            .withFallback(0)
    ),
    STARTING_POSITION(TypeData
            .enumType(StartingPosition.class)
            .withFallback(StartingPosition.LEFT)
    ),
    COLLECT_MORE_RINGS(TypeData
            .booleanType()
            .withFallback(false)
    ),
    PARK_CLOSE(TypeData
            .booleanType()
            .withFallback(false)
    ),
    ;

    TypeData<?> data;

    GameChangersOptions(TypeData<?> data) {
        this.data = data;
    }

    @Override
    public TypeData<?> getData() {
        return data;
    }
}

enum TeamColor {
    RED,
    BLUE,
}

enum StartingPosition {
    RIGHT,
    LEFT,
}