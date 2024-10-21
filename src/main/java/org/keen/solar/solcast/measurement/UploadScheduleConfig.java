package org.keen.solar.solcast.measurement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class UploadScheduleConfig {

    @Autowired
    private MeasurementUploader uploader;

    @EventListener(classes = ApplicationReadyEvent.class)
    @Scheduled(cron = "${app.solcast.measurement-upload-cron}")
    public void uploadAll() {
        // TODO: Don't upload if we've uploaded recently (configurable)
        uploader.uploadAll();
    }
}
