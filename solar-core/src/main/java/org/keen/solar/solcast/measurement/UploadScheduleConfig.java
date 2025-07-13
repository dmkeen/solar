package org.keen.solar.solcast.measurement;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;

@Profile("!test")
@Configuration
@ConditionalOnProperty(name = "app.solcast.measurement-upload.enable", havingValue = "true")
public class UploadScheduleConfig {

    @Autowired
    private MeasurementUploader uploader;

    @Async
    @Scheduled(cron = "${app.solcast.measurement-upload-cron}")
    public void uploadAll() {
        uploader.uploadAll();
    }
}
