# fileAccessSynchronization


###Requirments for the service

 * Service has to be designed to allow access of multiple users simultaneously
 * Service has to use JAX-RS, but any web-server is allowed
 * Tests have to be written for the service
 * **Access for the resoures has to be synchronized**
 
 
###Task

 File system has 2 modifiable files:
  1. Input file *f1* contains a set of numbers in csv format
  2. File with intermediate results f2 also contains a set of numbers in csv format
  
 2 types of queries should be allowed:
  1. *Get(k)* - get k-th result from file f2 and change it: *if f2[k] > 10 then f2[k] = f2[k] - 10 else leave it as is*
     Result should be returned to the client as xml. Parameters shiuld be in url.
  
  2. Post(a,b,c) makes calculation:
      *if f1[b] + a < 10 then f2[c] = f1[b] + a + 10
      else  f2[c] = f1[b] + a*
        
      Return 0 to client if the condition was holded and 1 if not(xml)
      Parameters of post request should be in xml.
   