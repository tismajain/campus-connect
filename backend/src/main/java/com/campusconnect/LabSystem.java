package com.campusconnect;
import jakarta.persistence.*;
@Entity @Table(name="lab_systems")
public class LabSystem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @com.fasterxml.jackson.annotation.JsonIgnore @ManyToOne @JoinColumn(name="lab_id") public Lab lab;
 public String systemSummary; public Integer minRamGb; public Integer maxRamGb;
 public boolean hasGpu; public Integer gpuMemoryGb; public String sourceConfiguration;
}
