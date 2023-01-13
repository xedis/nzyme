package app.nzyme.core.distributed;

import com.google.auto.value.AutoValue;
import com.google.common.base.Joiner;
import org.joda.time.DateTime;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.software.os.OSProcess;
import oshi.software.os.OperatingSystem;


public class NodeInformation {

    public Info collect() {
        SystemInfo s = new SystemInfo();

        OperatingSystem os = s.getOperatingSystem();
        OperatingSystem.OSVersionInfo osV = os.getVersionInfo();
        String osVersion =  os.getFamily() + " " + osV.getVersion() +
                " (" + osV.getBuildNumber() + ")";

        GlobalMemory memory = s.getHardware().getMemory();
        long memoryUsed = memory.getTotal()-memory.getAvailable();

        CentralProcessor cpu = s.getHardware().getProcessor();
        double cpuSystemLoad = cpu.getSystemCpuLoad(500);

        OSProcess currentProcess = s.getOperatingSystem().getCurrentProcess();

        return Info.create(
                memory.getTotal(),
                memory.getAvailable(),
                memoryUsed,
                (memoryUsed*100.0)/ memory.getTotal(),
                cpuSystemLoad,
                cpu.getLogicalProcessorCount(),
                new DateTime(s.getOperatingSystem().getSystemBootTime()*1000),
                currentProcess.getVirtualSize(),
                Joiner.on(", ").join(currentProcess.getArguments()),
                osVersion
        );
    }

    @AutoValue
    public static abstract class Info {

        // Memory.
        public abstract long memoryTotal();
        public abstract long memoryAvailable();
        public abstract long memoryUsed();
        public abstract double memoryUsedPercent();

        // CPU.
        public abstract double cpuSystemLoad();
        public abstract int cpuThreadCount();

        // Process.
        public abstract DateTime processStartTime();
        public abstract long processVirtualSize();
        public abstract String processArguments();

        // OS.
        public abstract String osInformation();

        public static Info create(long memoryTotal, long memoryAvailable, long memoryUsed, double memoryUsedPercent, double cpuSystemLoad, int cpuThreadCount, DateTime processStartTime, long processVirtualSize, String processArguments, String osInformation) {
            return builder()
                    .memoryTotal(memoryTotal)
                    .memoryAvailable(memoryAvailable)
                    .memoryUsed(memoryUsed)
                    .memoryUsedPercent(memoryUsedPercent)
                    .cpuSystemLoad(cpuSystemLoad)
                    .cpuThreadCount(cpuThreadCount)
                    .processStartTime(processStartTime)
                    .processVirtualSize(processVirtualSize)
                    .processArguments(processArguments)
                    .osInformation(osInformation)
                    .build();
        }

        public static Builder builder() {
            return new AutoValue_NodeInformation_Info.Builder();
        }

        @AutoValue.Builder
        public abstract static class Builder {
            public abstract Builder memoryTotal(long memoryTotal);

            public abstract Builder memoryAvailable(long memoryAvailable);

            public abstract Builder memoryUsed(long memoryUsed);

            public abstract Builder memoryUsedPercent(double memoryUsedPercent);

            public abstract Builder cpuSystemLoad(double cpuSystemLoad);

            public abstract Builder cpuThreadCount(int cpuThreadCount);

            public abstract Builder processStartTime(DateTime processStartTime);

            public abstract Builder processVirtualSize(long processVirtualSize);

            public abstract Builder processArguments(String processArguments);

            public abstract Builder osInformation(String osInformation);

            public abstract Info build();
        }
    }

}
