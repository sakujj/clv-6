package integration.context;

import by.sakujj.context.Context;

public class TestContext {
    static {
        Context.setSingletonPropertiesFileName("test.yaml");
    }

    private static final Context testContext = Context.getInstance();

    public static Context getInstance() {
        return testContext;
    }
}
