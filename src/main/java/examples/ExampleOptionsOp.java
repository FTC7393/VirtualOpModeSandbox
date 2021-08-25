package examples;

import external.opmode.AbstractOptionsOpMode;
import external.util.OptionEntries;

public class ExampleOptionsOp extends AbstractOptionsOpMode {

    public ExampleOptionsOp(String optionsFilePath) {
        super(optionsFilePath + "-example", ExampleOptions.class);
    }

}

enum ExampleOptions implements OptionEntries {

    LIKE(TypeData
            .integerType(1, 0, 10)
            .withFallback(99)
    ),
    SUBSCRIBE(TypeData
            .booleanType()
            .withFallback(false)
    ),
    TESTENUM(TypeData
            .enumType(ForkOptions.class)
            .withFallback(ForkOptions.BRUH1)),
    ;

    TypeData<?> data;

    ExampleOptions(TypeData<?> data) {
        this.data = data;
    }

    @Override
    public TypeData<?> getData() {
        return data;
    }
}

enum ForkOptions {
    BRUH1,
    BRUH2,
    BRUH3,
    ;
}