package com.campusconnect;
import java.util.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
@RestController @RequestMapping("/api")
public class PublicController {
 final LabRepository labs; final SoftwareRepository software; final EquipmentRepository equipment;
 public PublicController(LabRepository l,SoftwareRepository s,EquipmentRepository e){labs=l;software=s;equipment=e;}
 static String query(String q) {String s=q==null?"":q.strip(); if(s.length()>100) throw new ResponseStatusException(HttpStatus.BAD_REQUEST); return s.toLowerCase(Locale.ROOT);}
 static boolean contains(String text,String q){return text!=null && text.toLowerCase(Locale.ROOT).contains(q);}
 static boolean matches(Lab lab,String q){
  if(q.isEmpty())return true;
  if(q.equals("gpu"))return lab.systems.stream().anyMatch(s->s.hasGpu);
  if(contains(lab.building.code+" "+lab.roomNumber+" "+lab.building.code+"-"+lab.roomNumber+" "+Objects.toString(lab.name,"")+" "+Objects.toString(lab.description,""),q))return true;
  return lab.systems.stream().anyMatch(s->contains(s.systemSummary,q)||contains(s.sourceConfiguration,q)||(q.equals("gpu")&&s.hasGpu));
 }
 List<Lab> findLabs(String q,String building,Boolean gpu,Integer minRam){
  if(building!=null&&!Set.of("AB4","AB5").contains(building))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
  if(minRam!=null&&(minRam<0||minRam>4096))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
  return labs.findAll(org.springframework.data.domain.Sort.by("id")).stream().filter(l->building==null||l.building.code.equals(building))
   .filter(l->gpu==null||!gpu||l.systems.stream().anyMatch(s->s.hasGpu))
   .filter(l->minRam==null||l.systems.stream().anyMatch(s->s.maxRamGb!=null&&s.maxRamGb>=minRam)).filter(l->matches(l,q)).toList();
 }
 List<Software> findSoftware(String q){return software.findAll(org.springframework.data.domain.Sort.by("name")).stream()
  .filter(s->q.isEmpty()||contains(s.name,q)||s.subjects.stream().anyMatch(u->contains(u.name,q)||contains(u.code,q))).toList();}
 List<Equipment> findEquipment(String q){return equipment.findAll(org.springframework.data.domain.Sort.by("id")).stream().filter(e->q.isEmpty()||contains(e.name,q)||contains(e.category,q)).toList();}
 @GetMapping("/labs") public List<Lab> labs(@RequestParam(required=false) String building,@RequestParam(required=false) Boolean gpu,@RequestParam(required=false) Integer minRam,@RequestParam(defaultValue="") String q){return findLabs(query(q),building,gpu,minRam);}
 @GetMapping("/labs/{id}") public Lab lab(@PathVariable Long id){return labs.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
 @GetMapping("/software") public List<Software> software(@RequestParam(defaultValue="") String q){return findSoftware(query(q));}
 @GetMapping("/equipment") public List<Equipment> equipment(@RequestParam(defaultValue="") String q){return findEquipment(query(q));}
 @GetMapping("/search") public Map<String,Object> search(@RequestParam String q){String term=query(q); if(term.isEmpty())return Map.of("labs",List.of(),"software",List.of(),"equipment",List.of()); return Map.of("labs",findLabs(term,null,null,null),"software",findSoftware(term),"equipment",findEquipment(term));}
}
