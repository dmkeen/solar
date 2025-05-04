package org.keen.solar.solcast.measurement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

@Profile("!test")
@Configuration
public class UploadScheduleConfig {

    @Autowired
    private MeasurementUploader uploader;

    @Async
    @Scheduled(cron = "${app.solcast.measurement-upload-cron}")
    public void uploadAll() {
        uploader.uploadAll();
    }
}
