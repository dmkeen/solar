package org.keen.solar.financial;

import org.keen.solar.financial.dal.TariffDao;
import org.keen.solar.financial.domain.Tariff;
import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * TariffLoader is responsible for loading tariffs from a CSV file and persisting them to a data store.
 * The file is expected to have a header row and subsequent rows representing tariff records.
 * 
 * <p><b>Constraints:</b></p>
 * <ul>
 *     <li>Both usage and feed-in tariffs must be configured for each day of the week, covering the entire day
 *         from 00:00 to 23:59:59.</li>
 *     <li>Each day can have one or more time periods, but the periods must not overlap and must cover the entire day.</li>
 * </ul>
 * 
 * <p>Each row in the file must contain the following columns in order:</p>
 * <ol>
 *     <li><b>feedIn</b> (boolean): True if this is a feed-in tariff (price paid for electricity fed to the grid),
 *         false if this is a usage tariff.</li>
 *     <li><b>startEffectiveDateEpoch</b> (long or date string): The date when this tariff starts applying, 
 *         either as seconds since the epoch or as a date string formatted according to the 
 *         {@code app.tariff.date-format} property.</li>
 *     <li><b>endEffectiveDateEpoch</b> (Long or date string): The date when this tariff stops applying, 
 *         either as seconds since the epoch, as a date string, or empty if the tariff is ongoing.</li>
 *     <li><b>dayOfWeek</b> (DayOfWeek): The day of the week this tariff applies to (e.g., MONDAY, TUESDAY).</li>
 *     <li><b>startOfPeriod</b> (LocalTime): The start time of the period (inclusive) in HH:mm:ss format.</li>
 *     <li><b>endOfPeriod</b> (LocalTime): The end time of the period (exclusive) in HH:mm:ss format.</li>
 *     <li><b>pricePerKwh</b> (BigDecimal): The cost per kilowatt-hour as a decimal value.</li>
 * </ol>
 * 
 * <p>Example CSV file (a real file will need to specify values for each day of the week):</p>
 * <pre>
 * feedIn,startEffectiveDateEpoch,endEffectiveDateEpoch,dayOfWeek,startOfPeriod,endOfPeriod,pricePerKwh
 * true,1731196800,,SUNDAY,00:00:00,23:59:59,0.033
 * false,1731196800,,SUNDAY,00:00:00,15:00:00,0.2174
 * false,1731196800,,SUNDAY,15:00:00,21:00:00,0.4082
 * false,1731196800,,SUNDAY,21:00:00,23:59:59,0.2174
 * </pre>
 * 
 * <p>The loader supports parsing date strings for {@code startEffectiveDateEpoch} and {@code endEffectiveDateEpoch}
 * based on the {@code app.tariff.date-format} property and converts them to epoch seconds using the timezone
 * specified in {@code app.tariff.timezone}.</p>
 */
@Profile("!test")
@Service
@RestController
public class TariffLoader {
    
    private final Logger logger = LoggerFactory.getLogger(TariffLoader.class);
    private final TariffDao tariffDao;
    private final Path filePath;
    private final DateTimeFormatter dateFormatter;
    private final ZoneId timeZone;

    public TariffLoader(TariffDao tariffDao, 
                        @Value("${app.tariff.file-path}") String filePath,
                        @Value("${app.tariff.date-format}") String dateFormat,
                        @Value("${app.tariff.timezone}") String timeZone) {
        this.tariffDao = tariffDao;
        this.filePath = Path.of(filePath);
        this.dateFormatter = DateTimeFormatter.ofPattern(dateFormat);
        this.timeZone = ZoneId.of(timeZone);
    }

    /**
     * Triggers the tariff update process.
     */
    @PostMapping("/update-tariffs")
    public void updateTariffs() throws IOException {
        if (!Files.exists(filePath)) {
            logger.warn("Tariff file does not exist: {}", filePath);
            throw new IllegalStateException("Tariff file not found: " + filePath);
        }

        Map<DayOfWeek, List<Tariff>> usageTariffs = new EnumMap<>(DayOfWeek.class);
        Map<DayOfWeek, List<Tariff>> feedInTariffs = new EnumMap<>(DayOfWeek.class);

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            reader.lines()
                    .skip(1) // Skip header row
                    .forEach(line -> {
                        try {
                            Tariff tariff = parseTariff(line);
                            if (tariff.feedIn()) {
                                feedInTariffs.computeIfAbsent(tariff.dayOfWeek(), k -> new ArrayList<>()).add(tariff);
                            } else {
                                usageTariffs.computeIfAbsent(tariff.dayOfWeek(), k -> new ArrayList<>()).add(tariff);
                            }
                        } catch (IllegalArgumentException e) {
                            logger.warn("Skipping invalid tariff record: {}. Reason: {}", line, e.getMessage());
                        }
                    });

            validateTariffs(usageTariffs, "Usage");
            validateTariffs(feedInTariffs, "Feed-in");

            // Apply tariffs by expiring existing ones and inserting new ones
            tariffDao.applyTariffs(
                    usageTariffs.values().stream().flatMap(List::stream).toList(),
                    feedInTariffs.values().stream().flatMap(List::stream).toList()
            );

            // Log the applied tariffs
            logger.info("Successfully applied usage tariffs: {}", usageTariffs.values().stream().flatMap(List::stream).toList());
            logger.info("Successfully applied feed-in tariffs: {}", feedInTariffs.values().stream().flatMap(List::stream).toList());
        }
    }

    /**
     * Validates that the tariffs for each day of the week are complete and do not overlap.
     *
     * @param tariffsByDay a map of tariffs grouped by day of the week
     * @param tariffType   the type of tariff (usage or feed-in)
     */
    private void validateTariffs(Map<DayOfWeek, List<Tariff>> tariffsByDay, String tariffType) {
        for (DayOfWeek day : DayOfWeek.values()) {
            List<Tariff> tariffs = tariffsByDay.getOrDefault(day, Collections.emptyList());
            if (tariffs.isEmpty()) {
                throw new IllegalArgumentException(tariffType + " tariffs are missing for " + day);
            }

            // Sort tariffs by startOfPeriod
            tariffs.sort(Comparator.comparing(Tariff::startOfPeriod));

            // Check that the entire day is covered
            LocalTime expectedStart = LocalTime.of(0, 0);
            for (Tariff tariff : tariffs) {
                if (!tariff.startOfPeriod().equals(expectedStart)) {
                    throw new IllegalArgumentException(tariffType + " tariffs for " + day + " do not cover the entire day. Missing period starting at " + expectedStart);
                }
                expectedStart = tariff.endOfPeriod();
            }

            if (!expectedStart.equals(LocalTime.of(23, 59, 59))) {
                throw new IllegalArgumentException(tariffType + " tariffs for " + day + " do not cover the entire day. Missing period ending at 23:59:59");
            }
        }
    }

    private Tariff parseTariff(String line) {
        String[] fields = line.split(",");
        if (fields.length != 7) {
            throw new IllegalArgumentException("Invalid number of fields");
        }

        boolean feedIn = Boolean.parseBoolean(fields[0].trim());
        long startEffectiveDateEpoch = parseEpoch(fields[1].trim());
        Long endEffectiveDateEpoch = fields[2].trim().isEmpty() ? null : parseEpoch(fields[2].trim());
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(fields[3].trim().toUpperCase());
        LocalTime startOfPeriod = LocalTime.parse(fields[4].trim());
        LocalTime endOfPeriod = LocalTime.parse(fields[5].trim());
        BigDecimal pricePerKwh = new BigDecimal(fields[6].trim());

        return new Tariff(feedIn, startEffectiveDateEpoch, endEffectiveDateEpoch, dayOfWeek, startOfPeriod, endOfPeriod, pricePerKwh);
    }

    private long parseEpoch(String value) {
        try {
            // Try parsing as a long (epoch seconds)
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            // If parsing as long fails, try parsing as a date string
            LocalDateTime dateTime = LocalDateTime.parse(value, dateFormatter);
            return dateTime.atZone(timeZone).toEpochSecond();
        }
    }

}
