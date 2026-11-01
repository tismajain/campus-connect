import {test,expect} from "@playwright/test";
import {mkdirSync} from "node:fs";
const api="http://localhost:8080/api";
function authorization(){return `Basic ${Buffer.from(`${process.env.ADMIN_USERNAME}:${process.env.ADMIN_PASSWORD}`).toString("base64")}`;}
test("student discovery, specifications and subject-only software",async({page},testInfo)=>{
 await page.goto("/");await expect(page.getByRole("heading",{name:"Find what you need on campus."})).toBeVisible();
 await page.getByRole("searchbox").fill("GPU");await page.getByRole("button",{name:"Search",exact:true}).click();
 await expect(page.locator(".location")).toHaveText(["AB4 · 406","AB4 · 411"]);
 await page.getByRole("link",{name:"View AB4 406"}).click();await expect(page.getByRole("heading",{name:"System configuration",exact:true})).toBeVisible();
 await expect(page.getByText("12 GB GPU",{exact:true})).toBeVisible();
 await page.goto("/explore?type=software&q=Computer%20Vision");await expect(page.getByRole("heading",{name:"OpenCV",exact:true})).toBeVisible();await expect(page.getByText("Subject mappings do not confirm physical installation in any lab")).toBeVisible();
 await page.goto("/explore?type=labs&building=AB5");await expect(page.locator(".location").first()).toContainText("AB5");
 await page.goto("/explore?q=nonexistent-source-record");await expect(page.getByRole("heading",{name:"No matching resources"})).toBeVisible();
 for(const [name,path] of [["home","/"],["explore","/explore?q=GPU"],["detail","/labs/9"],["admin","/admin"]]){
  await page.goto(path);await page.locator("h1").waitFor();
  expect(await page.evaluate(()=>document.documentElement.scrollWidth<=window.innerWidth)).toBeTruthy();
  mkdirSync("test-results/screenshots",{recursive:true});await page.screenshot({path:`test-results/screenshots/${testInfo.project.name}-${name}.png`,fullPage:true});
  const preview=await page.screenshot({type:"jpeg",quality:65,fullPage:true});console.log(`UI_RENDER:${testInfo.project.name}-${name}:${preview.toString("base64")}`);
 }
});
test("admin system edit persists and resource text is escaped",async({page,request})=>{
 const labs=await (await request.get(`${api}/labs?gpu=true`)).json();const lab=labs[0],system=lab.systems[0];
 const body={labId:lab.id,systemSummary:system.systemSummary,minRamGb:system.minRamGb,maxRamGb:system.maxRamGb,hasGpu:system.hasGpu,gpuMemoryGb:system.gpuMemoryGb,sourceConfiguration:system.sourceConfiguration};
 let createdId:number|undefined;
 try{
  await page.goto("/admin");await page.getByLabel("Username").fill(process.env.ADMIN_USERNAME!);await page.getByLabel("Password").fill("incorrect");await page.getByRole("button",{name:"Sign in",exact:true}).click();await expect(page.getByRole("alert").filter({hasText:"Invalid credentials"})).toHaveText("Invalid credentials");
  await page.getByLabel("Password").fill(process.env.ADMIN_PASSWORD!);await page.getByRole("button",{name:"Sign in",exact:true}).click();await page.getByRole("button",{name:"Lab systems",exact:true}).click();
  const row=page.getByRole("row").filter({hasText:"AB4 · 406"});await row.getByRole("button",{name:"Edit AB4 · 406"}).click();
  await page.getByLabel("Minimum RAM (GB)").fill("32");await page.getByLabel("Maximum RAM (GB)").fill("32");await page.getByRole("button",{name:"Save changes"}).click();await expect(page.getByRole("status").filter({hasText:"Saved to the campus directory"})).toHaveText("Saved to the campus directory");
  await page.goto(`/labs/${lab.id}`);await page.reload();await expect(page.getByText("32 GB RAM",{exact:true})).toBeVisible();
  await page.goto("/admin");await expect(page.getByRole("heading",{name:"Admin sign in",exact:true})).toBeVisible();
  const text="<script>window.resourceXssExecuted=true</script>";
  const created=await request.post(`${api}/admin/software`,{headers:{Authorization:authorization()},data:{name:text,description:"<img src=x onerror=alert(1)>"}});expect(created.status()).toBe(201);createdId=(await created.json()).id;
  await page.goto("/explore?type=software&q="+encodeURIComponent(text));await expect(page.getByRole("heading",{name:text,exact:true})).toBeVisible();
  expect(await page.evaluate(()=>"resourceXssExecuted" in window)).toBe(false);expect(await page.locator(".resource-body script,.resource-body img").count()).toBe(0);
  expect(await page.evaluate(()=>Object.keys(localStorage))).toEqual([]);expect(await page.evaluate(()=>Object.keys(sessionStorage))).toEqual([]);
 }finally{
  expect((await request.put(`${api}/admin/lab-systems/${system.id}`,{headers:{Authorization:authorization()},data:body})).ok()).toBeTruthy();
  if(createdId)expect((await request.delete(`${api}/admin/software/${createdId}`,{headers:{Authorization:authorization()}})).ok()).toBeTruthy();
 }
});
