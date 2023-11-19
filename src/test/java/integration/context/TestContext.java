package integration.context;

import by.sakujj.context.Context;

public class TestContext {
    private static final Context testContext = new Context("test.yaml");

    public static Context getInstance() {
        return testContext;
    }
}
