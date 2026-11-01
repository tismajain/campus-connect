import Link from "next/link";
import {notFound} from "next/navigation";
import {api,Lab,ram,statusLabel} from "@/lib/api";
import {Location} from "@/components/Resources";
export const dynamic="force-dynamic";
export default async function LabDetail({params}:{params:Promise<{id:string}>}){
 const {id}=await params;if(!/^\d+$/.test(id))notFound();
 let lab:Lab;
 try{lab=await api<Lab>(`/labs/${id}`);}catch(e){if(e instanceof Error&&e.message==="Resource not found")notFound();throw e;}
 return <div className="page detail-page"><Link className="breadcrumb" href="/explore?type=labs">← Explore / Labs</Link><div className="detail-heading"><div><Location lab={lab}/><h1>{lab.name||`Computer lab ${lab.roomNumber}`}</h1><p className="lead">{lab.building.code} · Room {lab.roomNumber}</p></div><div><span className="status">{statusLabel(lab.status)}</span><p className="caption">Directory status · Not live occupancy</p></div></div>{lab.description&&<p className="lab-description">{lab.description}</p>}<section><div className="section-heading"><h2>System configuration</h2><span className="caption">Campus equipment record</span></div>{!lab.systems.length?<p className="notice">No system configuration is recorded for this lab</p>:lab.systems.map(s=><article className="spec-card" key={s.id}><h3>{s.systemSummary}</h3><dl className="spec-grid"><div><dt>Memory</dt><dd>{ram(s)}</dd></div><div><dt>Minimum RAM</dt><dd>{s.minRamGb===null?"Not recorded":`${s.minRamGb} GB`}</dd></div><div><dt>Maximum RAM</dt><dd>{s.maxRamGb===null?"Not recorded":`${s.maxRamGb} GB`}</dd></div><div><dt>Graphics</dt><dd>{s.hasGpu?(s.gpuMemoryGb===null?"GPU equipped":`${s.gpuMemoryGb} GB GPU`):"GPU not specified in source"}</dd></div></dl><details><summary>Source configuration</summary><p className="source-text">{s.sourceConfiguration}</p></details></article>)}</section><p className="notice">Software installation locations are not recorded in the source data. <Link href="/explore?type=software">Browse software by subject →</Link></p></div>;
}
