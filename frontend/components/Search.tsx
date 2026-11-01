export default function Search({query=""}:{query?:string}) {
 return <form action="/explore" className="search" role="search">
  <label className="sr-only" htmlFor="directory-search">Search labs, software or equipment</label>
  <input id="directory-search" name="q" type="search" maxLength={100} defaultValue={query} placeholder="Search GPU, software, lab, equipment..." />
  <button type="submit">Search</button>
 </form>;
}
