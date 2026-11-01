import type { Metadata } from "next";
import Link from "next/link";
import "./globals.css";
export const metadata:Metadata={title:{default:"Campus Connect",template:"%s · Campus Connect"},description:"Find campus labs, hardware, subject software and embedded equipment across AB4 and AB5"};
export default function RootLayout({children}:{children:React.ReactNode}) {
 return <html lang="en"><body><a className="skip" href="#main">Skip to content</a><header className="site-header"><div className="header-inner"><Link className="wordmark" href="/">Campus Connect<span className="brand-detail">Resource directory</span></Link><nav aria-label="Main navigation"><Link href="/">Home</Link><Link href="/explore">Explore</Link><Link href="/explore?type=labs">Labs</Link><Link href="/admin">Admin</Link></nav></div></header><main id="main">{children}</main><footer className="site-footer"><span>Campus Connect · AB4 / AB5</span><span>Source-backed specifications · No live occupancy data</span></footer></body></html>;
}
