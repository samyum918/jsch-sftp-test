import lombok.extern.slf4j.Slf4j;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

@Slf4j
public class JschLogger implements com.jcraft.jsch.Logger {
    private Map<Integer, Consumer<String>> logMap = new HashMap<>();
    private HashMap<Integer, BooleanSupplier> enabledMap = new HashMap<>();
    {
        logMap.put(DEBUG, log::debug);
        logMap.put(INFO, log::info);
        logMap.put(WARN, log::warn);
        logMap.put(ERROR, log::error);
        logMap.put(FATAL, log::error);

        enabledMap.put(DEBUG, log::isDebugEnabled);
        enabledMap.put(INFO, log::isInfoEnabled);
        enabledMap.put(WARN, log::isWarnEnabled);
        enabledMap.put(ERROR, log::isErrorEnabled);
        enabledMap.put(FATAL, log::isErrorEnabled);
    }

    @Override
    public void log(int level, String message) {
        logMap.get(level).accept(message);
    }

    @Override
    public boolean isEnabled(int level) {
        return enabledMap.get(level).getAsBoolean();
    }
}
