package nl.runnable.spring.fileupload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.Assert;

import java.util.Date;

/**
 * Background job for cleaning up expired files from {@link nl.runnable.spring.fileupload.MultipartFileStorage}.
 *
 * @author Laurens Fridael
 */
public class CleanupExpiredFilesJob implements Runnable, InitializingBean, DisposableBean {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  /* Dependencies */

  @Autowired(required = false)
  @Qualifier("spring-file-upload-storage")
  private TaskScheduler taskScheduler;

  @Autowired
  private MultipartFileStorage multipartFileStorage;

  /* Configuration */

  @Value("${spring-file-upload-storage.database.cleanupInterval:60}")
  private int intervalInSeconds = 60;

  /* State*/

  private boolean destroyScheduler = false;

  /* Main operations */

  public void run() {
    multipartFileStorage.deleteExpired();
  }

  /* Utility */

  @Override
  public void afterPropertiesSet() throws Exception {
    scheduleTask();
  }

  private void scheduleTask() {
    if (taskScheduler == null) {
      taskScheduler = createDefaultTaskScheduler();
      destroyScheduler = true;
    }
    int delay = intervalInSeconds * 1000;
    Date startingAt = new Date(new Date().getTime() + delay);
    logger.info("Scheduling expired files clean-up for every {} seconds, starting at {}.", intervalInSeconds,
        startingAt);
    taskScheduler.scheduleAtFixedRate(this, startingAt, delay);
  }

  private TaskScheduler createDefaultTaskScheduler() {
    ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
    taskScheduler.afterPropertiesSet();
    return taskScheduler;
  }

  @Override
  public void destroy() throws Exception {
    if (destroyScheduler) {
      try {
        ((DisposableBean) taskScheduler).destroy();
      } finally {
        destroyScheduler = false;
      }
    }
  }

  /* Dependencies */

  public void setTaskScheduler(TaskScheduler taskScheduler) {
    Assert.notNull(taskScheduler);
    this.taskScheduler = taskScheduler;
  }

  public void setMultipartFileStorage(MultipartFileStorage multipartFileStorage) {
    Assert.notNull(multipartFileStorage);
    this.multipartFileStorage = multipartFileStorage;
  }

  /* Configuration */

  public void setIntervalInSeconds(int intervalInSeconds) {
    Assert.isTrue(intervalInSeconds > 0, "Interval must be greater than 0.");
    this.intervalInSeconds = intervalInSeconds;
  }

}
