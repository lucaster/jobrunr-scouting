package com.example;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.jobrunr.configuration.JobRunr;
import org.jobrunr.jobs.details.JobDetailsAsmGenerator;
import org.jobrunr.scheduling.BackgroundJob;
import org.jobrunr.storage.InMemoryStorageProvider;

public class App
{
    public static void main( String[] args ) throws IOException
    {

      JobRunr.configure()
      .useStorageProvider(new InMemoryStorageProvider())
      .useJobDetailsGenerator(new JobDetailsAsmGenerator())
      .useBackgroundJobServer()
      .initialize();

      BackgroundJob.enqueue(() -> System.out.println("%s enqueue".formatted(Instant.now())));

      BackgroundJob.schedule(UUID.randomUUID(), Instant.now().plusMillis(500), () -> System.out.println("%s schedule".formatted(Instant.now())));

      BackgroundJob.scheduleRecurrently("a", Duration.ofSeconds(5), () -> System.out.println("%s scheduleRecurrently(Duration)".formatted(Instant.now())));

      BackgroundJob.scheduleRecurrently("b", "*/5 * * * * *", () -> System.out.println("%s scheduleRecurrently(Cron)".formatted(Instant.now())));

      System.in.read();
    }
}
