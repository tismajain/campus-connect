import Link from "next/link";
import Search from "@/components/Search";
import {LabRow} from "@/components/Resources";
import {api,Lab} from "@/lib/api";
export const dynamic="force-dynamic";
export default async function Home(){
 let labs:Lab[]=[];let error=false;
 try {labs=await api<Lab[]>("/labs?gpu=true");}catch{error=true;}
 return <div className="page"><section className="home-intro"><p className="eyebrow">The campus resource directory</p><h1>Find what you need<br/>on campus.</h1><p className="lead">Find a GPU lab, compare systems or look up software used by your subject.</p><Search/><div className="category-links" aria-label="Resource types"><Link href="/explore?type=labs">Hardware</Link><Link href="/explore?type=software">Software</Link><Link href="/explore?type=labs">Labs</Link><Link href="/explore?type=equipment">Equipment</Link></div></section>
 <section className="home-directory"><div><div className="section-heading"><div><p className="eyebrow">Start with a capability</p><h2>Find the right resource</h2></div><Link href="/explore">Explore all →</Link></div><div className="entry-list">
 <Link className="entry" href="/explore?type=labs&gpu=true"><span className="entry-number">01</span><div><h3>GPU computing</h3><p>HP G9 Tower · 16 GB RAM · 12 GB GPU</p></div><span className="entry-location">AB4 · 406 / 411 →</span></Link>
 <Link className="entry" href="/explore?type=labs&q=Apple"><span className="entry-number">02</span><div><h3>Apple M3 systems</h3><p>Apple M3 · 8 GB RAM</p></div><span className="entry-location">AB5 · 303 →</span></Link>
 <Link className="entry" href="/explore?type=equipment"><span className="entry-number">03</span><div><h3>Embedded systems</h3><p>Evaluation boards, interfaces and measurement equipment</p></div><span className="entry-location">Browse equipment →</span></Link>
 <Link className="entry" href="/explore?type=software"><span className="entry-number">04</span><div><h3>Software by subject</h3><p>Find tools used for Computer Vision, Embedded System and more</p></div><span className="entry-location">Browse software →</span></Link>
 </div></div><aside className="building-index"><h2>Browse by building</h2><p>Computer labs across two academic blocks</p><Link href="/explore?building=AB4&type=labs"><span>AB4</span>Browse labs →</Link><Link href="/explore?building=AB5&type=labs"><span>AB5</span>Browse labs →</Link><p className="caption">Software records describe subject use. They do not confirm installation in a lab.</p></aside></section>
 <section><div className="section-heading"><div><p className="eyebrow">Hardware directory</p><h2>GPU-capable labs</h2></div><span className="caption">Specifications from the campus inventory</span></div>{error?<div className="notice" role="alert">The lab directory is unavailable. <Link href="/">Try again</Link></div>:labs.length?<div className="resource-list">{labs.map(l=><LabRow key={l.id} lab={l}/>)}</div>:<p className="notice">No GPU-equipped systems are currently recorded</p>}</section></div>;
}
