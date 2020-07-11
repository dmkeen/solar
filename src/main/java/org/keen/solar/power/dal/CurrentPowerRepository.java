package org.keen.solar.power.dal;

import org.keen.solar.power.domain.CurrentPower;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CurrentPowerRepository extends CrudRepository<CurrentPower, Long> {

    @Query(value = "select * " +
            "from current_power " +
            "where uploaded = :uploaded")
    List<CurrentPower> findByUploaded(@Param("uploaded") boolean uploaded);
}
