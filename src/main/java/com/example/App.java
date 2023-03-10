package com.example;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.server.BackgroundJobServerConfiguration;
import org.jobrunr.storage.InMemoryStorageProvider;

public class App {
  
  static Logic logic = new Logic();

  public static void main(String[] args) throws IOException {
   
    JobRunr.configure()
        .useStorageProvider(new InMemoryStorageProvider())
        .useBackgroundJobServer(
          BackgroundJobServerConfiguration
          .usingStandardBackgroundJobServerConfiguration()
          .andPollIntervalInSeconds(5)
        )
        .useBackgroundJobServer()
        .initialize();

    BackgroundJob.enqueue(() -> logic.work("enqueued"));

    var job1 = UUID.randomUUID();
    BackgroundJob.schedule(job1, Instant.now().plusMillis(500), () -> logic.work("scheduled"));

    BackgroundJob.scheduleRecurrently("job2", Duration.ofSeconds(5), () -> logic.work("recurrent/Duration"));

    BackgroundJob.scheduleRecurrently("job3", "*/5 * * * * *", () -> logic.work("recurrent/Cron"));

    System.in.read();

    BackgroundJob.delete("job3");
    BackgroundJob.delete("job2");
    BackgroundJob.delete(job1);
    System.out.println("END");
  }

  public static class Logic {
    public void work(String type) {
      System.out.println("%s scheduleRecurrently(%s)".formatted(Instant.now(), type));
    }
  }
}
