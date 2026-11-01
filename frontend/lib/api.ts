export type System = {id:number;systemSummary:string;minRamGb:number|null;maxRamGb:number|null;hasGpu:boolean;gpuMemoryGb:number|null;sourceConfiguration:string};
export type Lab = {id:number;building:{id:number;code:string;name:string};roomNumber:string;name:string|null;description:string|null;status:string;systems:System[]};
export type Software = {id:number;name:string;category:string|null;description:string|null;subjects:{id:number;code:string;name:string}[]};
export type Equipment = {id:number;labId:number|null;location:string|null;name:string;category:string;quantity:number|null;description:string|null;status:string};
export type Results = {labs:Lab[];software:Software[];equipment:Equipment[]};
const base=(process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080").replace(/\/$/, "");
export async function api<T>(path:string, options:RequestInit = {}):Promise<T> {
 const controller=new AbortController();const timeout=setTimeout(()=>controller.abort(),15000);
 try {
  const response=await fetch(`${base}/api${path}`,{...options,cache:"no-store",credentials:"omit",signal:options.signal || controller.signal});
  if(!response.ok) {
   if(response.status===401 || response.status===403) throw new Error("Invalid credentials");
   if(response.status===404) throw new Error("Resource not found");
   if(response.status===400) throw new Error("Check the field values and limits, then try again");
   if(response.status===409) throw new Error("This entry conflicts with existing data");
   throw new Error("The campus directory is unavailable. Please try again");
  }
  return response.status===204 ? undefined as T : await response.json() as T;
 } catch(error) {
  if(error instanceof Error && error.name==="AbortError") throw new Error("The directory took too long to respond. Please try again");
  if(error instanceof TypeError) throw new Error("Unable to reach the campus directory. Check your connection and try again");
  throw error;
 } finally {clearTimeout(timeout);}
}
export function ram(system:System) {
 if(system.minRamGb===null && system.maxRamGb===null)return "RAM not recorded";
 return `${system.minRamGb ?? system.maxRamGb}${system.maxRamGb!==null && system.minRamGb!==null && system.minRamGb!==system.maxRamGb ? `–${system.maxRamGb}` : ""} GB RAM`;
}
export function statusLabel(status:string){return status.toLowerCase().replaceAll("_"," ");}
