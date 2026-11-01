"use client";
export default function ErrorPage({reset}:{reset:()=>void}){return <div className="page"><div className="notice error" role="alert"><h1>Unable to load the directory</h1><p>Check your connection and try again</p><button onClick={reset}>Try again</button></div></div>;}
