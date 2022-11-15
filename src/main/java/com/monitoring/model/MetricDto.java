package com.monitoring.model;

public class MetricDto {

    private long time;
    private float cpu;
    private float memory;

    public MetricDto() {
    }

    public MetricDto(Builder builder) {
        this.time = builder.time;
        this.cpu = builder.cpu;
        this.memory = builder.memory;
    }


    public float getCpu() {
        return cpu;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setCpu(float cpu) {
        this.cpu = cpu;
    }

    public float getMemory() {
        return memory;
    }

    public void setMemory(float memory) {
        this.memory = memory;
    }

    public static class Builder {
        private long time;
        private float cpu;
        private float memory;

        public Builder time(long time) {
            this.time = time;
            return this;
        }

        public Builder cpuUsage(float cpu) {
            this.cpu = cpu;
            return this;
        }

        public Builder memoryUsage(float memory) {
            this.memory = memory;
            return this;
        }

        public MetricDto build() {
            return new MetricDto(this);
        }
    }
}
