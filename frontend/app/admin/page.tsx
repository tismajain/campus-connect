"use client";
import {useState,FormEvent} from "react";
import {api,Lab,Software,Equipment} from "@/lib/api";
type Kind="labs"|"lab-systems"|"software"|"equipment";
type Field={key:string;label:string;kind?:"number"|"text"|"textarea"|"checkbox"|"status"|"lab"|"building";required?:boolean;max?:number};
type Draft=Record<string,string|boolean>;
type Row={id:number;label:string;detail:string;values:Draft};
const fields:Record<Kind,Field[]>={
 labs:[{key:"buildingId",label:"Building",kind:"building",required:true},{key:"roomNumber",label:"Room number",required:true,max:20},{key:"name",label:"Lab name",max:120},{key:"description",label:"Description",kind:"textarea",max:4000},{key:"status",label:"Directory status",kind:"status",required:true}],
 "lab-systems":[{key:"labId",label:"Lab",kind:"lab",required:true},{key:"systemSummary",label:"System summary",required:true,max:200},{key:"minRamGb",label:"Minimum RAM (GB)",kind:"number",max:4096},{key:"maxRamGb",label:"Maximum RAM (GB)",kind:"number",max:4096},{key:"hasGpu",label:"GPU equipped",kind:"checkbox"},{key:"gpuMemoryGb",label:"GPU memory (GB)",kind:"number",max:1024},{key:"sourceConfiguration",label:"Source configuration",kind:"textarea",required:true,max:4000}],
 software:[{key:"name",label:"Software name",required:true,max:120},{key:"category",label:"Category",max:80},{key:"description",label:"Description",kind:"textarea",max:4000}],
 equipment:[{key:"name",label:"Equipment name",required:true,max:200},{key:"category",label:"Category",required:true,max:80},{key:"labId",label:"Verified lab (leave blank if unknown)",kind:"lab"},{key:"quantity",label:"Quantity (leave blank if unknown)",kind:"number",max:100000},{key:"description",label:"Description",kind:"textarea",max:4000},{key:"status",label:"Directory status",kind:"status",required:true}]
};
const labels:Record<Kind,string>={labs:"Labs","lab-systems":"Lab systems",software:"Software",equipment:"Equipment"};
function draft(kind:Kind):Draft {return Object.fromEntries(fields[kind].map(f=>[f.key,f.kind==="checkbox"?false:f.key==="status"?"AVAILABLE":f.key==="category"&&kind==="equipment"?"Embedded Systems":""]));}
function values(record:object,kind:Kind):Draft {const r=record as Record<string,unknown>;return Object.fromEntries(fields[kind].map(f=>[f.key,f.kind==="checkbox"?Boolean(r[f.key]):r[f.key]==null?"":String(r[f.key])]));}
export default function Admin(){
 const [auth,setAuth]=useState("");const [labs,setLabs]=useState<Lab[]>([]);const [software,setSoftware]=useState<Software[]>([]);const [equipment,setEquipment]=useState<Equipment[]>([]);
 const [buildings,setBuildings]=useState<{id:number;code:string}[]>([]);const [kind,setKind]=useState<Kind>("labs");
 const [editor,setEditor]=useState<{id?:number;values:Draft}|null>(null);const [pendingDelete,setPendingDelete]=useState<Row|null>(null);
 const [busy,setBusy]=useState(false);const [loading,setLoading]=useState(false);const [error,setError]=useState("");const [feedback,setFeedback]=useState("");const [fieldError,setFieldError]=useState("");
 async function load(token:string){
  setLoading(true);
  try{const [l,s,e,b]=await Promise.all([api<Lab[]>("/labs"),api<Software[]>("/software"),api<Equipment[]>("/equipment"),api<{id:number;code:string}[]>("/admin/buildings",{headers:{Authorization:token}})]);setLabs(l);setSoftware(s);setEquipment(e);setBuildings(b);}
  finally{setLoading(false);}
 }
 // Only tab memory holds credentials. Navigation away, refresh and logout discard them.
 async function login(event:FormEvent<HTMLFormElement>){
  event.preventDefault();setBusy(true);setError("");const form=event.currentTarget;const data=new FormData(form);
  const username=String(data.get("username")||"");const password=String(data.get("password")||"");
  if(username.includes(":")){setError("Username cannot contain a colon");setBusy(false);return;}
  const token=`Basic ${btoa(String.fromCharCode(...new TextEncoder().encode(`${username}:${password}`)))}`;
  try{await api("/admin/session",{headers:{Authorization:token}});await load(token);form.reset();setAuth(token);}
  catch(e){setError(e instanceof Error?e.message:"Unable to sign in");}finally{setBusy(false);}
 }
 function logout(){setAuth("");setEditor(null);setPendingDelete(null);setError("");setFeedback("");}
 let rows:Row[]=[];
 if(kind==="labs") rows=labs.map(l=>({id:l.id,label:`${l.building.code} · ${l.roomNumber}`,detail:l.name||l.systems[0]?.systemSummary||"Computer lab",values:values({...l,buildingId:l.building.id},kind)}));
 if(kind==="lab-systems")rows=labs.flatMap(l=>l.systems.map(s=>({id:s.id,label:`${l.building.code} · ${l.roomNumber}`,detail:s.systemSummary,values:values({...s,labId:l.id},kind)})));
 if(kind==="software")rows=software.map(s=>({id:s.id,label:s.name,detail:`${s.subjects.length} subject mappings`,values:values(s,kind)}));
 if(kind==="equipment")rows=equipment.map(e=>({id:e.id,label:e.name,detail:e.category,values:values(e,kind)}));
 async function save(event:FormEvent<HTMLFormElement>){
  event.preventDefault();if(!editor)return;setFieldError("");setError("");setFeedback("");
  const payload:Record<string,unknown>={};
  for(const f of fields[kind]){const value=editor.values[f.key];payload[f.key]=f.kind==="checkbox"?!!value:f.kind==="number"||f.kind==="lab"||f.kind==="building"?(value===""?null:Number(value)):String(value||"").trim();}
  if(kind==="lab-systems"){
   if(payload.minRamGb!==null&&payload.maxRamGb!==null&&Number(payload.minRamGb)>Number(payload.maxRamGb)){setFieldError("Minimum RAM must not exceed maximum RAM");return;}
   if(!payload.hasGpu && payload.gpuMemoryGb!==null){setFieldError("Clear GPU memory when GPU equipped is unchecked");return;}
  }
  setBusy(true);
  try{await api(`/admin/${kind}${editor.id?`/${editor.id}`:""}`,{method:editor.id?"PUT":"POST",headers:{Authorization:auth,"Content-Type":"application/json"},body:JSON.stringify(payload)});setEditor(null);setFeedback("Saved to the campus directory");await load(auth);}
  catch(e){const message=e instanceof Error?e.message:"Unable to save";if(message==="Invalid credentials"){logout();setError(message);}else setFieldError(message);}
  finally{setBusy(false);}
 }
 async function remove(){if(!pendingDelete)return;setBusy(true);setError("");setFeedback("");try{await api(`/admin/${kind}/${pendingDelete.id}`,{method:"DELETE",headers:{Authorization:auth}});setPendingDelete(null);setFeedback("Entry deleted");await load(auth);}catch(e){setError(e instanceof Error?e.message:"Unable to delete");}finally{setBusy(false);}}
 return <div className="page admin-page"><div className="section-heading"><div><p className="eyebrow">Directory maintenance</p><h1>Admin</h1></div>{auth&&<button className="secondary" disabled={busy} onClick={logout}>Sign out</button>}</div><p className="lead">Update verified resource records. Changes appear in the student directory after refresh.</p>
 {!auth?<section className="login-panel"><h2>Admin sign in</h2><p className="muted">Use the administrator account configured on the API. Credentials stay in this tab’s memory.</p><form onSubmit={login}><label>Username<input name="username" autoComplete="username" required maxLength={100}/></label><label>Password<input name="password" type="password" autoComplete="current-password" required maxLength={200}/></label>{error&&<p className="form-error" role="alert">{error}</p>}<button disabled={busy}>{busy?"Signing in…":"Sign in"}</button></form></section>:<>
 <div className="admin-tabs" role="group" aria-label="Manage resource type">{(Object.keys(labels) as Kind[]).map(k=><button key={k} className={kind===k?"selected":"secondary"} disabled={busy} aria-pressed={kind===k} onClick={()=>{setKind(k);setEditor(null);setPendingDelete(null);setFieldError("");setFeedback("");setError("");}}>{labels[k]}</button>)}</div>
 {feedback&&<p className="notice success" role="status">{feedback}</p>}{error&&<p className="notice error" role="alert">{error}</p>}
 <div className="section-heading"><h2>{labels[kind]} <span className="caption">{rows.length} records</span></h2><button disabled={busy} onClick={()=>{setEditor({values:draft(kind)});setFieldError("");}}>Add {kind==="lab-systems"?"system":kind==="labs"?"lab":kind==="software"?"software":"equipment"}</button></div>
 {editor&&<section className="editor"><h2>{editor.id?"Edit record":"Add record"}</h2><form onSubmit={save}><div className="form-grid">{fields[kind].map(f=><label key={f.key} className={f.kind==="textarea"?"full-width":f.kind==="checkbox"?"checkbox":""}>{f.label}{f.required&&<span className="caption"> (required)</span>}{f.kind==="textarea"?<textarea maxLength={f.max} required={f.required} rows={3} value={String(editor.values[f.key])} onChange={e=>setEditor({...editor,values:{...editor.values,[f.key]:e.target.value}})}/>:f.kind==="status"?<select required value={String(editor.values[f.key])} onChange={e=>setEditor({...editor,values:{...editor.values,[f.key]:e.target.value}})}>{["AVAILABLE",...(kind==="labs"?["IN_CLASS"]:[]),"RESTRICTED","MAINTENANCE"].map(s=><option key={s}>{s}</option>)}</select>:f.kind==="building"||f.kind==="lab"?<select required={f.required} value={String(editor.values[f.key])} onChange={e=>setEditor({...editor,values:{...editor.values,[f.key]:e.target.value}})}><option value="">{f.required?"Select a location":"Not recorded"}</option>{f.kind==="building"?buildings.map(b=><option key={b.id} value={b.id}>{b.code}</option>):labs.map(l=><option key={l.id} value={l.id}>{l.building.code} · {l.roomNumber}</option>)}</select>:f.kind==="checkbox"?<input type="checkbox" checked={Boolean(editor.values[f.key])} onChange={e=>setEditor({...editor,values:{...editor.values,[f.key]:e.target.checked}})}/>:<input type={f.kind==="number"?"number":"text"} min={f.kind==="number"?0:undefined} max={f.kind==="number"?f.max:undefined} step={f.kind==="number"?1:undefined} maxLength={f.kind!=="number"?f.max:undefined} required={f.required} value={String(editor.values[f.key])} onChange={e=>setEditor({...editor,values:{...editor.values,[f.key]:e.target.value}})}/>}</label>)}</div>{fieldError&&<p role="alert" className="form-error">{fieldError}</p>}<div className="actions"><button disabled={busy}>{busy?"Saving…":"Save changes"}</button><button type="button" className="secondary" disabled={busy} onClick={()=>setEditor(null)}>Cancel</button></div></form></section>}
 {pendingDelete&&<section role="alertdialog" aria-modal="false" aria-labelledby="delete-title" className="delete-confirm"><h2 id="delete-title">Delete {pendingDelete.label}?</h2><p>This permanently removes the entry{kind==="software"?" and its subject mappings":""} from the directory.</p><div className="actions"><button className="danger" disabled={busy} onClick={remove}>{busy?"Deleting…":"Confirm delete"}</button><button className="secondary" disabled={busy} onClick={()=>setPendingDelete(null)}>Keep entry</button></div></section>}
 {loading?<p className="notice" role="status">Loading current records…</p>:<div className="table-wrap"><table><caption className="sr-only">{labels[kind]} records</caption><thead><tr><th scope="col">Resource</th><th scope="col">Record detail</th><th scope="col">Actions</th></tr></thead><tbody>{rows.map(r=><tr key={r.id}><td>{r.label}</td><td>{r.detail}</td><td><div className="table-actions"><button className="text-button" disabled={busy} aria-label={`Edit ${r.label}`} onClick={()=>{setEditor({id:r.id,values:r.values});setFieldError("");}}>Edit</button>{kind!=="labs"&&<button className="text-button destructive" disabled={busy} aria-label={`Delete ${r.label}`} onClick={()=>setPendingDelete(r)}>Delete</button>}</div></td></tr>)}</tbody></table>{!rows.length&&<p className="notice">No entries yet. Add the first verified record above.</p>}</div>}
 <p className="caption section-note">Lab deletion is disabled to protect related records. Source configuration should remain faithful to the original inventory.</p>
 </> }</div>;
}
