package com.campusconnect;
import jakarta.persistence.*;
@Entity @Table(name="subjects")
public class Subject {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String code; public String name;
}
