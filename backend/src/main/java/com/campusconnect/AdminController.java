package com.campusconnect;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Map;
@RestController @RequestMapping("/api/admin") @Transactional
public class AdminController {
 final LabRepository labs; final BuildingRepository buildings; final LabSystemRepository systems;
 final SoftwareRepository software; final EquipmentRepository equipment;
 public AdminController(LabRepository l,BuildingRepository b,LabSystemRepository y,SoftwareRepository s,EquipmentRepository e){labs=l;buildings=b;systems=y;software=s;equipment=e;}
 static <T> T get(JpaRepository<T,Long> repo,Long id){return repo.findById(id).orElseThrow(()->new ResponseStatusException(HttpStatus.NOT_FOUND));}
 @GetMapping("/session") public Map<String,String> session(){return Map.of("role","ADMIN");}
 @GetMapping("/buildings") public Object buildings(){return buildings.findAll();}
 @PostMapping("/labs") @ResponseStatus(HttpStatus.CREATED) public Lab createLab(@Valid @RequestBody Requests.LabInput r){return saveLab(new Lab(),r);}
 @PutMapping("/labs/{id}") public Lab updateLab(@PathVariable Long id,@Valid @RequestBody Requests.LabInput r){return saveLab(get(labs,id),r);}
 Lab saveLab(Lab l,Requests.LabInput r){l.building=get(buildings,r.buildingId());l.roomNumber=r.roomNumber();l.name=r.name();l.description=r.description();l.status=r.status();return labs.saveAndFlush(l);}
 @PostMapping("/lab-systems") @ResponseStatus(HttpStatus.CREATED) public LabSystem createSystem(@Valid @RequestBody Requests.SystemInput r){return saveSystem(new LabSystem(),r);}
 @PutMapping("/lab-systems/{id}") public LabSystem updateSystem(@PathVariable Long id,@Valid @RequestBody Requests.SystemInput r){return saveSystem(get(systems,id),r);}
 LabSystem saveSystem(LabSystem s,Requests.SystemInput r){
  if((r.minRamGb()!=null&&r.maxRamGb()!=null&&r.minRamGb()>r.maxRamGb())||(!r.hasGpu()&&r.gpuMemoryGb()!=null))throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
  s.lab=get(labs,r.labId());s.systemSummary=r.systemSummary();s.minRamGb=r.minRamGb();s.maxRamGb=r.maxRamGb();s.hasGpu=r.hasGpu();s.gpuMemoryGb=r.gpuMemoryGb();s.sourceConfiguration=r.sourceConfiguration();return systems.saveAndFlush(s);
 }
 @DeleteMapping("/lab-systems/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteSystem(@PathVariable Long id){systems.delete(get(systems,id));systems.flush();}
 @PostMapping("/software") @ResponseStatus(HttpStatus.CREATED) public Software createSoftware(@Valid @RequestBody Requests.SoftwareInput r){return saveSoftware(new Software(),r);}
 @PutMapping("/software/{id}") public Software updateSoftware(@PathVariable Long id,@Valid @RequestBody Requests.SoftwareInput r){return saveSoftware(get(software,id),r);}
 Software saveSoftware(Software s,Requests.SoftwareInput r){s.name=r.name();s.category=r.category();s.description=r.description();return software.saveAndFlush(s);}
 @DeleteMapping("/software/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteSoftware(@PathVariable Long id){software.delete(get(software,id));software.flush();}
 @PostMapping("/equipment") @ResponseStatus(HttpStatus.CREATED) public Equipment createEquipment(@Valid @RequestBody Requests.EquipmentInput r){return saveEquipment(new Equipment(),r);}
 @PutMapping("/equipment/{id}") public Equipment updateEquipment(@PathVariable Long id,@Valid @RequestBody Requests.EquipmentInput r){return saveEquipment(get(equipment,id),r);}
 Equipment saveEquipment(Equipment e,Requests.EquipmentInput r){e.lab=r.labId()==null?null:get(labs,r.labId());e.name=r.name();e.category=r.category();e.quantity=r.quantity();e.description=r.description();e.status=r.status();return equipment.saveAndFlush(e);}
 @DeleteMapping("/equipment/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void deleteEquipment(@PathVariable Long id){equipment.delete(get(equipment,id));equipment.flush();}
}
