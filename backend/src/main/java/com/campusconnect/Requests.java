package com.campusconnect;
import jakarta.validation.constraints.*;
public final class Requests {
 private Requests() {}
 public record LabInput(@NotNull @Positive Long buildingId, @NotBlank @Size(max=20) String roomNumber,
  @Size(max=120) String name, @Size(max=4000) String description,
  @NotNull @Pattern(regexp="AVAILABLE|IN_CLASS|RESTRICTED|MAINTENANCE") String status) {}
 public record SystemInput(@NotNull @Positive Long labId, @NotBlank @Size(max=200) String systemSummary,
  @Min(0) @Max(4096) Integer minRamGb, @Min(0) @Max(4096) Integer maxRamGb,
  boolean hasGpu, @Min(0) @Max(1024) Integer gpuMemoryGb,
  @NotBlank @Size(max=4000) String sourceConfiguration) {}
 public record SoftwareInput(@NotBlank @Size(max=120) String name, @Size(max=80) String category,
  @Size(max=4000) String description) {}
 public record EquipmentInput(@Positive Long labId, @NotBlank @Size(max=200) String name,
  @NotBlank @Size(max=80) String category, @Min(0) @Max(100000) Integer quantity,
  @Size(max=4000) String description,
  @NotNull @Pattern(regexp="AVAILABLE|RESTRICTED|MAINTENANCE") String status) {}
}
