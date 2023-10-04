# Rationale

At the end of 2022, after releasing a stable version of the SDS core library, the 
idea of supporting XPath to query SDA data lodged in my brain and refused to go 
away. 

This was not the first time I entertained that thought. Back in 2008, I jokingly 
called  it "S-Path" in the SDA documentation, as if I envisioned some simplified 
form of XPath that could be applied to SDA. And maybe I did, at the time.

Now, almost 15 years later, the idea of writing even a simple XPath engine from 
scratch seemed utterly ridiculous. Not only would it be hard and time-consuming, 
it would also be like re-inventing a perfectly fine wheel, even more so than with 
SDA and SDS.

So, the sensible thing to do was to find an *existing* XPath library that was 
able to adapt to the SDA object model. And it wasn't long before I came across 
Jaxen, which could do exactly that. Because Jaxen is well-thought-out, and SDA 
rather simple, I literally had a proof of concept within a few hours.

This allowed me to ~~re-invent XSLT~~ start working on Structured (Simple) Data 
Transformation, which is why I needed XPath in the first place.

**Harold C.L. Baur, April 2023**
