import Link from "next/link";
import {Lab, Software, Equipment, ram, statusLabel} from "@/lib/api";
export function Location({lab}:{lab:Lab}){return <span className="location">{lab.building.code} · {lab.roomNumber}</span>;}
export function LabRow({lab}:{lab:Lab}) {
 return <article className="resource lab-row"><Location lab={lab}/><div className="resource-body">
  <h3><Link href={`/labs/${lab.id}`}>{lab.name || lab.systems[0]?.systemSummary || "Computer lab"}</Link></h3>
  {lab.systems.map(s=><p className="specs" key={s.id}>{ram(s)}{s.hasGpu && <> · {s.gpuMemoryGb===null ? "GPU equipped" : `${s.gpuMemoryGb} GB GPU`}</>}</p>)}
  {lab.systems.length===0 && <p className="muted">System configuration not recorded</p>}
 </div><span className="status">{statusLabel(lab.status)}</span><Link className="row-link" aria-label={`View ${lab.building.code} ${lab.roomNumber}`} href={`/labs/${lab.id}`}>View lab →</Link></article>;
}
export function SoftwareRow({item}:{item:Software}) {return <article className="resource"><div className="resource-body"><h3>{item.name}</h3><p className="muted">{item.subjects.length ? `Used by ${item.subjects.map(s=>s.name===s.code?s.code:`${s.name} (${s.code})`).join(", ")}` : "No subject mapping recorded"}</p>{item.description && <p>{item.description}</p>}<p className="caption">Lab installation locations are not recorded</p></div></article>;}
export function EquipmentRow({item}:{item:Equipment}) {return <article className="resource"><div className="resource-body"><h3>{item.name}</h3><p className="muted">{item.category}{item.quantity!==null && ` · Quantity ${item.quantity}`}</p>{item.description && <p>{item.description}</p>}<p className="caption">{item.location ? `Verified location: ${item.location}` : "Location not provided in this directory"}</p></div><span className="status">{statusLabel(item.status)}</span></article>;}
