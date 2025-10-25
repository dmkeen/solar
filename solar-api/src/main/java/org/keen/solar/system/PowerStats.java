package org.keen.solar.system;

/**
 * Statistics about power generation and consumption.
 *
 * @param totalGeneration  total power generated, in Watts
 * @param totalConsumption total power consumed, in Watts, represented as a negative number
 * @param totalExported    total power exported to the grid, in Watts
 * @param totalImported    total power imported from the grid, in Watts, represented as a negative number
 */
public record PowerStats(double totalGeneration, double totalConsumption,
                         double totalExported, double totalImported) {
}
