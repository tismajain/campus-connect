package com.campusconnect;
import jakarta.persistence.*;
@Entity @Table(name="equipment")
public class Equipment {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 @ManyToOne @JoinColumn(name="lab_id") @com.fasterxml.jackson.annotation.JsonIgnore public Lab lab;
 public String name; public String category; public Integer quantity; public String description; public String status="AVAILABLE";
 public Long getLabId(){return lab==null?null:lab.id;}
 public String getLocation(){return lab==null?null:lab.building.code+" · "+lab.roomNumber;}
}
