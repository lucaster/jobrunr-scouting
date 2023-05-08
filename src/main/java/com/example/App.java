package com.example;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.dashboard.JobRunrDashboardWebServer;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.storage.InMemoryStorageProvider;
import org.jobrunr.utils.mapper.gson.GsonJsonMapper;

public class App {

  static Logic logic = new Logic();

  public static void main(String[] args) throws Exception {

    final var storageProvider = new InMemoryStorageProvider();
	JobRunr.configure()
        .useStorageProvider(storageProvider)
        .useBackgroundJobServer(
          BackgroundJobServerConfiguration
          .usingStandardBackgroundJobServerConfiguration()
          // Cannot be less than 5 seconds.
          // Must be equal or shorter than the shortest interval of any recurrent job.
          .andPollIntervalInSeconds(5)
        )
        .initialize();

	var dashboard = new JobRunrDashboardWebServer(storageProvider, new GsonJsonMapper(), 8066);
	dashboard.start();

    BackgroundJob.enqueue(() -> logic.work("enqueued"));

    var job1 = UUID.randomUUID();
    BackgroundJob.schedule(job1, Instant.now().plusMillis(500), () -> logic.work("scheduled"));

    BackgroundJob.scheduleRecurrently("job2", Duration.ofSeconds(5), () -> logic.work("recurrent/Duration"));

    BackgroundJob.scheduleRecurrently("job3", "*/5 * * * * *", () -> logic.work("recurrent/Cron"));

    Thread.sleep(15000);

    System.out.println("Press a key to stop");
    System.in.read();

    BackgroundJob.delete("job3");
    BackgroundJob.delete("job2");
    BackgroundJob.delete(job1);

    Thread.sleep(5000);

    System.out.println("END");

    dashboard.stop();

    System.exit(0);
  }

  public static class Logic {
    public void work(String type) {
      System.out.println("%s scheduleRecurrently(%s)".formatted(Instant.now(), type));
    }
  }
}
