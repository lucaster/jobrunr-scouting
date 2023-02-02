package com.example;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.storage.InMemoryStorageProvider;

public class App
{
    public static void main( String[] args ) throws IOException
    {

      JobRunr.configure()
      .useStorageProvider(new InMemoryStorageProvider())
      // .useJobDetailsGenerator(new JobDetailsAsmGenerator())
      .useBackgroundJobServer()
      .initialize();

      BackgroundJob.enqueue(() -> new Logic().work("enqueued"));

      var job1 = UUID.randomUUID();
      BackgroundJob.schedule(job1, Instant.now().plusMillis(500), () -> new Logic().work("scheduled"));

      BackgroundJob.scheduleRecurrently("job2", Duration.ofSeconds(5), () -> new Logic().work("recurrent/Duration"));

      BackgroundJob.scheduleRecurrently("job3", "*/5 * * * * *", () -> new Logic().work("recurrent/Cron"));

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
