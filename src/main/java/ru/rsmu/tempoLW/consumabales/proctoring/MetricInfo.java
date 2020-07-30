package ru.rsmu.tempoLW.consumabales.proctoring;

/**
 * @author leonid.
 */
public class MetricInfo {
    private ProctoringMetric metric;
    private int value;

    public MetricInfo( String metricName, int value ) {
        try {
            this.metric = ProctoringMetric.valueOf( metricName );
        } catch (IllegalArgumentException e) {
            this.metric = ProctoringMetric.unknown;
        }
        this.value = value;
    }

    public ProctoringMetric getMetric() {
        return metric;
    }

    public int getValue() {
        return value;
    }
}
