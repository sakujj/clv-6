package integration.listeners;

import integration.context.TestContext;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestPlan;


public class CleanupListener implements TestExecutionListener {
    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        try {
            TestContext.getInstance().close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
