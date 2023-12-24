package by.sakujj.util;

import lombok.experimental.UtilityClass;
import org.apache.ibatis.jdbc.ScriptRunner;

import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;

@UtilityClass
public class SQLScriptRunner {
    public static void runScript(Connection conn, Reader scriptReader) {
        ScriptRunner scriptRunner = new ScriptRunner(conn);
        scriptRunner.setStopOnError(true);
        scriptRunner.setErrorLogWriter(new PrintWriter(System.err));
        scriptRunner.runScript(scriptReader);
    }
}
