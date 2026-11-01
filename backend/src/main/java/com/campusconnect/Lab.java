package com.campusconnect;
import jakarta.persistence.*;
@Entity @Table(name="labs")
public class Lab {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne(fetch=FetchType.EAGER) @JoinColumn(name="building_id") public Building building;
 public String roomNumber; public String departmentCode; public String name; public Integer floor;
 public String description; public String status = "AVAILABLE";
 @OneToMany(mappedBy="lab", fetch=FetchType.EAGER) @OrderBy("id") public java.util.List<LabSystem> systems = new java.util.ArrayList<>();
}
