package com.campusconnect;
import jakarta.persistence.*;
@Entity @Table(name="software")
public class Software {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) public Long id;
 public String name; public String category; public String description;
 @ManyToMany(fetch=FetchType.EAGER) @JoinTable(name="subject_software",joinColumns=@JoinColumn(name="software_id"),inverseJoinColumns=@JoinColumn(name="subject_id")) @OrderBy("code") public java.util.Set<Subject> subjects = new java.util.LinkedHashSet<>();
}
