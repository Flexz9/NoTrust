package flexzcraft.notrust.manager;

import com.mojang.logging.LogUtils;
import flexzcraft.notrust.entity.Log;
import flexzcraft.notrust.repository.LogRepository;
import org.slf4j.Logger;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LogManager {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static LogManager instance;
    private final LogRepository logRepository;
    private ConcurrentLinkedQueue<Log> incommingQueue = new ConcurrentLinkedQueue<>();
    private ConcurrentLinkedQueue<Log> writingQueue = new ConcurrentLinkedQueue<>();

    private LogManager() {
        this.logRepository = new LogRepository();
    }

    public synchronized static LogManager getInstance() {
        if (instance == null) {
            instance = new LogManager();
        }
        return instance;
    }

    public void switchAndProcessQueue() {
        if (writingQueue.isEmpty()) {
            ConcurrentLinkedQueue<Log> tempQueue = writingQueue;
            writingQueue = incommingQueue;
            incommingQueue = tempQueue;

            if (!writingQueue.isEmpty())
                new Thread(() -> {
                    try {
                        logRepository.save(writingQueue.stream().toList());
                    } catch (SQLException e) {
                        LOGGER.error(e.toString(), e);
                        incommingQueue.addAll(writingQueue);
                    }
                    writingQueue.clear();
                }).start();
        }
    }

    public void log(Log log) {
        incommingQueue.add(log);
    }

    public void disconnect() {
        logRepository.disconnect();
    }

    public List<Log> selectLogsByPositionAndPlayer(int x, int y, int z, String dimension, String playerToFind, int limit) {
        return logRepository.selectLogsByPositionAndPlayer(x, y, z, dimension, playerToFind, limit);
    }

    public List<Log> selectLogsByPositionAndPlayer(int x1, int y1, int z1, int x2, int y2, int z2, String dimension, String playerToFind, int limit) {
        return logRepository.selectLogsByPositionAndPlayer(x1, y1, z1, x2, y2, z2, dimension, playerToFind, limit);
    }

    public List<Log> selectLogsByPosition(int x, int y, int z, String dimension, int limit) {
        return logRepository.selectLogsByPosition(x, y, z, dimension, limit);
    }

    public List<Log> selectLogsByPosition(int x1, int y1, int z1, int x2, int y2, int z2, String dimension, int limit) {
        return logRepository.selectLogsByPosition(x1, y1, z1, x2, y2, z2, dimension, limit);
    }
}
