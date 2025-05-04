package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.Tariff;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.time.DayOfWeek;
import java.time.LocalTime;

public interface TariffRepository extends CrudRepository<Tariff, Long> {

    @Query("select * " +
            "from tariff t " +
            "where t.feed_in = true " +
            "and t.start_effective_date_epoch <= :epochTime " +
            "and (t.end_effective_date_epoch is null or t.end_effective_date_epoch > :epochTime)" +
            "and t.day_of_week = :dayOfWeek " +
            "and t.start_of_period <= :localTime " +
            "and t.end_of_period > :localTime")
    Tariff findEffectiveFeedInTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime);

    @Query("select * " +
            "from tariff t " +
            "where t.feed_in = false " +
            "and t.start_effective_date_epoch <= :epochTime " +
            "and (t.end_effective_date_epoch is null or t.end_effective_date_epoch > :epochTime)" +
            "and t.day_of_week = :dayOfWeek " +
            "and t.start_of_period <= :localTime " +
            "and t.end_of_period > :localTime")
    Tariff findEffectiveUsageTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime);

}
