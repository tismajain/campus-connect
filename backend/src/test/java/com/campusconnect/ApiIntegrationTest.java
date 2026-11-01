package com.campusconnect;
import com.fasterxml.jackson.databind.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.*;
import org.springframework.transaction.annotation.Transactional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
@SpringBootTest @AutoConfigureMockMvc @Transactional
class ApiIntegrationTest {
 @Autowired MockMvc mvc; @Autowired ObjectMapper json; @Autowired LabSystemRepository systems;
 @Autowired jakarta.persistence.EntityManager entityManager;
 String user=System.getenv("ADMIN_USERNAME"), password=System.getenv("ADMIN_PASSWORD");
 JsonNode read(String path) throws Exception{return json.readTree(mvc.perform(get(path)).andExpect(status().isOk()).andReturn().getResponse().getContentAsString());}
 @Test void sourceDataAndGpuDiscovery() throws Exception {
  assertThat(read("/api/labs").size()).isEqualTo(28);
  assertThat(read("/api/equipment").size()).isEqualTo(7);
  JsonNode gpu=read("/api/labs?gpu=true");assertThat(gpu.size()).isEqualTo(2);
  assertThat(gpu.findValuesAsText("roomNumber")).containsExactly("406","411");
  assertThat(read("/api/search?q=GPU").get("labs").findValuesAsText("roomNumber")).containsExactly("406","411");
  assertThat(read("/api/labs?q=Apple").get(0).get("building").get("code").asText()).isEqualTo("AB5");
  JsonNode software=read("/api/software");int mappings=0;java.util.Set<String> subjects=new java.util.HashSet<>();
  for(JsonNode s:software){mappings+=s.get("subjects").size();for(JsonNode subject:s.get("subjects"))subjects.add(subject.get("code").asText());assertThat(s.has("labs")).isFalse();}
  assertThat(mappings).isEqualTo(60);assertThat(subjects.size()).isEqualTo(36);
  assertThat(read("/api/software?q=Computer Vision").size()).isEqualTo(3);
 }
 @Test void everyAdminMutationAndAdminReadRequiresRole() throws Exception {
  for(String path:new String[]{"labs","lab-systems","software","equipment"}){
   mvc.perform(post("/api/admin/"+path).contentType("application/json").content("{}")).andExpect(status().isUnauthorized());
   mvc.perform(put("/api/admin/"+path+"/1").contentType("application/json").content("{}")).andExpect(status().isUnauthorized());
   mvc.perform(patch("/api/admin/"+path+"/1").contentType("application/json").content("{}")).andExpect(status().isUnauthorized());
   mvc.perform(delete("/api/admin/"+path+"/1")).andExpect(status().isUnauthorized());
  }
  mvc.perform(get("/api/admin/session")).andExpect(status().isUnauthorized());
  mvc.perform(post("/api/labs").contentType("application/json").content("{}")).andExpect(status().isUnauthorized());
  mvc.perform(post("/api/admin/software").with(httpBasic(user,"incorrect")).contentType("application/json").content("{\"name\":\"Wrong credentials\"}")).andExpect(status().isUnauthorized());
  mvc.perform(post("/api/admin/software").with(user("student").roles("STUDENT")).contentType("application/json").content("{\"name\":\"No access\"}")).andExpect(status().isForbidden());
 }
 @Test void authenticatedSystemEditPersistsAndIsPubliclyReadable() throws Exception {
  JsonNode lab=read("/api/labs?gpu=true").get(0),s=lab.get("systems").get(0);long id=s.get("id").asLong();
  var body=json.createObjectNode().put("labId",lab.get("id").asLong()).put("systemSummary",s.get("systemSummary").asText()).put("minRamGb",32).put("maxRamGb",32).put("hasGpu",true).put("gpuMemoryGb",12).put("sourceConfiguration",s.get("sourceConfiguration").asText());
  mvc.perform(put("/api/admin/lab-systems/"+id).with(httpBasic(user,password)).contentType("application/json").content(body.toString())).andExpect(status().isOk());
  entityManager.flush();entityManager.clear();assertThat(systems.findById(id).orElseThrow().maxRamGb).isEqualTo(32);
  assertThat(read("/api/labs/"+lab.get("id").asLong()).get("systems").get(0).get("maxRamGb").asInt()).isEqualTo(32);
 }
 @Test void softwareAndEquipmentCrudAndPlainText() throws Exception {
  String html="<script>alert('xss')</script>";
  String body=json.createObjectNode().put("name","  "+html+"  ").put("description","<b>Plain text</b>").toString();
  var response=mvc.perform(post("/api/admin/software").with(httpBasic(user,password)).contentType("application/json").content(body)).andExpect(status().isCreated()).andReturn().getResponse();
  JsonNode created=json.readTree(response.getContentAsString());assertThat(created.get("name").asText()).isEqualTo(html);long id=created.get("id").asLong();
  mvc.perform(put("/api/admin/software/"+id).with(httpBasic(user,password)).contentType("application/json").content("{\"name\":\"Updated software\"}")).andExpect(status().isOk());
  mvc.perform(delete("/api/admin/software/"+id).with(httpBasic(user,password))).andExpect(status().isNoContent());
  String equipment="{\"name\":\"Test measurement board\",\"category\":\"Embedded Systems\",\"quantity\":0,\"status\":\"AVAILABLE\"}";
  long equipmentId=json.readTree(mvc.perform(post("/api/admin/equipment").with(httpBasic(user,password)).contentType("application/json").content(equipment)).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("id").asLong();
  mvc.perform(put("/api/admin/equipment/"+equipmentId).with(httpBasic(user,password)).contentType("application/json").content(equipment.replace("AVAILABLE","MAINTENANCE"))).andExpect(status().isOk());
  mvc.perform(delete("/api/admin/equipment/"+equipmentId).with(httpBasic(user,password))).andExpect(status().isNoContent());
 }
 @Test void rejectsInvalidInputAndReturnsSafeErrors() throws Exception {
  for(String body:new String[]{"{\"name\":\" \"}","{\"name\":\"a\",\"id\":99}","{\"name\":\""+"x".repeat(121)+"\"}","{broken"})
   mvc.perform(post("/api/admin/software").with(httpBasic(user,password)).contentType("application/json").content(body)).andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").exists());
  for(String body:new String[]{"{\"name\":\"a\",\"category\":\"b\",\"quantity\":-1,\"status\":\"AVAILABLE\"}","{\"name\":\"a\",\"category\":\"b\",\"status\":\"UNSAFE\"}","{\"name\":\"a\",\"category\":\"b\",\"quantity\":1.5,\"status\":\"AVAILABLE\"}"})
   mvc.perform(post("/api/admin/equipment").with(httpBasic(user,password)).contentType("application/json").content(body)).andExpect(status().isBadRequest());
  mvc.perform(get("/api/labs/not-an-id")).andExpect(status().isBadRequest());
  mvc.perform(get("/api/labs/999999")).andExpect(status().isNotFound());
  mvc.perform(get("/api/search").param("q","x".repeat(101))).andExpect(status().isBadRequest());
  mvc.perform(get("/api/search").param("q","   ")).andExpect(status().isOk()).andExpect(jsonPath("$.labs").isEmpty());
  mvc.perform(get("/api/search").param("q","' OR 1=1; DROP TABLE labs;--")).andExpect(status().isOk()).andExpect(jsonPath("$.labs").isEmpty());
  assertThat(read("/api/labs").size()).isEqualTo(28);
  mvc.perform(post("/api/admin/software").with(httpBasic(user,password)).contentType("application/json").content("{\"name\":\"Vivado\"}")).andExpect(status().isConflict()).andExpect(jsonPath("$.message").value("Resource conflicts with existing data"));
 }
 @Test void exactOriginStatelessHeadersAndPreflight() throws Exception {
  mvc.perform(options("/api/admin/software").header("Origin","http://localhost:3000").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","authorization,content-type"))
   .andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","http://localhost:3000"));
  mvc.perform(options("/api/admin/software").header("Origin","https://other.example").header("Access-Control-Request-Method","POST")).andExpect(status().isForbidden()).andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
  mvc.perform(get("/api/labs").header("Origin","https://other.example")).andExpect(status().isForbidden());
  mvc.perform(get("/api/admin/session").with(httpBasic(user,password))).andExpect(status().isOk()).andExpect(header().doesNotExist("Set-Cookie"));
  mvc.perform(get("/api/labs")).andExpect(status().isOk()).andExpect(header().string("X-Content-Type-Options","nosniff")).andExpect(header().string("X-Frame-Options","DENY")).andExpect(header().string("Referrer-Policy","no-referrer")).andExpect(header().exists("Content-Security-Policy"));
 }
 @Test void labAndSystemLifecycleAndBodyLimits() throws Exception {
  long building=read("/api/labs").get(0).get("building").get("id").asLong();
  var labBody=json.createObjectNode().put("buildingId",building).put("roomNumber","test-999").put("name","Test lab").put("status","RESTRICTED");
  long labId=json.readTree(mvc.perform(post("/api/admin/labs").with(httpBasic(user,password)).contentType("application/json").content(labBody.toString())).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("id").asLong();
  mvc.perform(put("/api/admin/labs/"+labId).with(httpBasic(user,password)).contentType("application/json").content(labBody.put("status","MAINTENANCE").toString())).andExpect(status().isOk());
  assertThat(read("/api/labs/"+labId).get("status").asText()).isEqualTo("MAINTENANCE");
  var systemBody=json.createObjectNode().put("labId",labId).put("systemSummary","Test system").put("minRamGb",8).put("maxRamGb",16).put("hasGpu",false).put("sourceConfiguration","Test fixture");
  long systemId=json.readTree(mvc.perform(post("/api/admin/lab-systems").with(httpBasic(user,password)).contentType("application/json").content(systemBody.toString())).andExpect(status().isCreated()).andReturn().getResponse().getContentAsString()).get("id").asLong();
  mvc.perform(put("/api/admin/lab-systems/"+systemId).with(httpBasic(user,password)).contentType("application/json").content(systemBody.put("minRamGb",32).toString())).andExpect(status().isBadRequest());
  mvc.perform(put("/api/admin/lab-systems/"+systemId).with(httpBasic(user,password)).contentType("application/json").content(systemBody.put("minRamGb",8).put("gpuMemoryGb",4).toString())).andExpect(status().isBadRequest());
  mvc.perform(delete("/api/admin/lab-systems/"+systemId).with(httpBasic(user,password))).andExpect(status().isNoContent());
  mvc.perform(post("/api/admin/software").with(httpBasic(user,password)).contentType("application/json").content(" ".repeat(65537))).andExpect(status().isPayloadTooLarge());
  mvc.perform(get("/api/labs").secure(true)).andExpect(status().isOk()).andExpect(header().string("Strict-Transport-Security",org.hamcrest.Matchers.allOf(org.hamcrest.Matchers.containsString("max-age=31536000"),org.hamcrest.Matchers.containsString("includeSubDomains"))));
 }

}
