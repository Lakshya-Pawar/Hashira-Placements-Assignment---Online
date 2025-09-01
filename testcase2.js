// secret.js
const fs = require("fs");

// paste your JSON here
const data = {
  "keys": { "n": 10, "k": 7 },
  "1": { "base": "6", "value": "13444211440455345511" },
  "2": { "base": "15", "value": "aed7015a346d635" },
  "3": { "base": "15", "value": "6aeeb69631c227c" },
  "4": { "base": "16", "value": "e1b5e05623d881f" },
  "5": { "base": "8", "value": "316034514573652620673" },
  "6": { "base": "3", "value": "2122212201122002221120200210011020220200" },
  "7": { "base": "3", "value": "20120221122211000100210021102001201112121" },
  "8": { "base": "6", "value": "20220554335330240002224253" },
  "9": { "base": "12", "value": "45153788322a1255483" },
  "10": { "base": "7", "value": "1101613130313526312514143" }
};

function gcd(a,b){ a=a<0n?-a:a; b=b<0n?-b:b; while(b){ [a,b]=[b,a%b]; } return a; }
class Fr{
  constructor(n=0n,d=1n){ if(d<0n){n=-n;d=-d;} const g=gcd(n<0n?-n:n,d); this.n=n/g; this.d=d/g; }
  add(b){ return new Fr(this.n*b.d+b.n*this.d,this.d*b.d); }
  sub(b){ return new Fr(this.n*b.d-b.n*this.d,this.d*b.d); }
  mul(b){ return new Fr(this.n*b.n,this.d*b.d); }
  div(b){ return new Fr(this.n*b.d,this.d*b.n); }
}

function parseBig(baseStr, valStr){
  const b = BigInt(parseInt(baseStr,10));
  let r=0n;
  for(let ch of valStr){
    let v;
    if(ch>='0'&&ch<='9') v=BigInt(ch.charCodeAt(0)-48);
    else v=BigInt(ch.toLowerCase().charCodeAt(0)-87);
    r=r*b+v;
  }
  return r;
}

const k = data.keys.k;
let pts=[];
for(const key in data){
  if(key==="keys") continue;
  pts.push([BigInt(key), parseBig(data[key].base, data[key].value)]);
}
pts=pts.slice(0,k); // pick first k points

// Interpolation
let coeffs = Array(k).fill(0).map(_=>new Fr(0n,1n));
for(let i=0;i<k;i++){
  const [xi,yi]=pts[i];
  let numer=[new Fr(1n,1n)], denom=new Fr(1n,1n);
  for(let j=0;j<k;j++){
    if(i===j) continue;
    const [xj]=pts[j];
    const term=[new Fr(-xj,1n), new Fr(1n,1n)];
    let tmp=Array(numer.length+term.length-1).fill(0).map(_=>new Fr(0n,1n));
    for(let p=0;p<numer.length;p++) for(let q=0;q<term.length;q++)
      tmp[p+q]=tmp[p+q].add(numer[p].mul(term[q]));
    numer=tmp;
    denom=denom.mul(new Fr(xi-xj,1n));
  }
  const factor=new Fr(yi,1n).div(denom);
  for(let t=0;t<numer.length;t++) coeffs[t]=coeffs[t].add(numer[t].mul(factor));
}

const integers=coeffs.map(c=>{
  if(c.d!==1n) throw new Error("non-integer coeff");
  return c.n.toString();
}).reverse();

console.log("Polynomial coeffs (highest→lowest):", integers);
console.log("Secret (constant term):", integers[integers.length-1]);
