package com.mekongocop.mekongocopserver.util;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.TimeZone;

@Component
public class FlashSaleScheduler implements CommandLineRunner {

    @Autowired
    private Scheduler scheduler;

    @Override
    public void run(String... args) throws Exception {
        JobDetail flashSaleJob = JobBuilder.newJob(FlashSaleJob.class)
                .withIdentity("flashSaleJob", "group1")
                .build();

        JobDetail restorePriceJob = JobBuilder.newJob(RestorePriceJob.class)
                .withIdentity("restorePriceJob", "group1")
                .build();

        // Lên lịch cho các khung giờ flash sale
        scheduleFlashSale(scheduler, flashSaleJob, restorePriceJob, 9, 0);
        scheduleFlashSale(scheduler, flashSaleJob, restorePriceJob, 12, 0);
        scheduleFlashSale(scheduler, flashSaleJob, restorePriceJob, 15, 0);
        scheduleFlashSale(scheduler, flashSaleJob, restorePriceJob, 18, 0);
        scheduleFlashSale(scheduler, flashSaleJob, restorePriceJob, 21, 0);
        scheduleFlashSale(scheduler, flashSaleJob, restorePriceJob, 0, 0);

    }

    private void scheduleFlashSale(Scheduler scheduler, JobDetail flashSaleJob, JobDetail restorePriceJob, int hour, int minute) throws SchedulerException {
        JobKey flashSaleJobKey = flashSaleJob.getKey();
        JobKey restorePriceJobKey = restorePriceJob.getKey();

        if (!scheduler.checkExists(flashSaleJobKey)) {
            Trigger flashSaleTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("flashSaleTrigger" + hour, "group1")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour, minute)
                            .inTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")))
                    .build();

            scheduler.scheduleJob(flashSaleJob, flashSaleTrigger);
        }

        if (!scheduler.checkExists(restorePriceJobKey)) {
            Trigger restorePriceTrigger = TriggerBuilder.newTrigger()
                    .withIdentity("restorePriceTrigger" + hour, "group1")
                    .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(hour + 1, minute)
                            .inTimeZone(TimeZone.getTimeZone("Asia/Ho_Chi_Minh")))
                    .build();

            scheduler.scheduleJob(restorePriceJob, restorePriceTrigger);
        }
    }
}