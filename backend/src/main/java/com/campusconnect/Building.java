package com.campusconnect;
import jakarta.persistence.*;
@Entity @Table(name="buildings")
public class Building {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String code; public String name;
}
