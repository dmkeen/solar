package org.keen.solar.financial.dal;

import org.keen.solar.financial.domain.Tariff;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalTime;

@Component
public class TariffDaoSpringDataImpl implements TariffDao {

    private final TariffRepository repository;

    public TariffDaoSpringDataImpl(TariffRepository repository) {
        this.repository = repository;
    }

    @Override
    public Tariff getEffectiveFeedInTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime) {
        return repository.findEffectiveFeedInTariff(dayOfWeek, localTime, epochTime);
    }

    @Override
    public Tariff getEffectiveUsageTariff(DayOfWeek dayOfWeek, LocalTime localTime, long epochTime) {
        return repository.findEffectiveUsageTariff(dayOfWeek, localTime, epochTime);
    }
}
