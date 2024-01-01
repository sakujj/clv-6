package by.sakujj.context;

import by.sakujj.config.SpringConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class AppContext {
    private static ApplicationContext instance;

    public static ApplicationContext getInstance() {
        if (instance == null) {
            synchronized (AppContext.class) {
                instance = new AnnotationConfigApplicationContext(SpringConfig.class);
            }
        }

        return instance;
    }
}
