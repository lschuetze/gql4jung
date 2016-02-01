[Crocopat](http://www.sosy-lab.org/~dbeyer/CrocoPat/) is an effective system to query and manipulate relations. In particular, the relations can be extracted from programs and Crocopat queries can be used to represent design patterns and anti patterns. Crocopat is based on Binary Decision Diagrams (BDDs), and known for its excellent performance. The GQL4JUNG standard queries can also be expressed in Crocopat (but it is not clear whether  CNS can be represented in Crocopat). Crocopats language (RML) is a full programming language with syntax elements such as conditionals and loops. It is based on first order logic. CrocoPat queries return facts for a certain predicate symbol. It is therefore not easily possible to query directly for paths of arbitrary length. For instance, this feature is needed when querying for circular dependencies between packages, and when we are interested in the actual dependency paths and not only in the packages that are the start and end points of these paths. Paths can not be directly represented in Crocopat. However, reasoning about paths is supported through the higher order transitive closure predicate TC.

The following CrocoPat query is the equivalent of the GQL4JUNG CD query:
```
       1. ref(x,y) := uses(x,y) | extends(x,y) | implements(x,y) ;
       2. refs(x,y) := TC(uses(x,y));
       3. sameNS(x,y) := EX(n1,namespace(x,n1) & EX(n2,namespace(y,n2) & =(n1,n2)));
       4. nscycle(x,y) := sameNS(x,y) & EX(z,ref(x,z) & !sameNS(x,z) & EX(w,refs(z,w) & ref(w,y) & !sameNS(x,w)));
       5. PRINT nscycle(x,y);
```

This query uses several existential quantifiers to compare name spaces. This is because the language is predicative. GQL4JUNG on the other hand follows an object-oriented model and functions can be used directly (nested in MVEL expressions, for instance property access is represented in expressions like in "class.namespace", representing the complex term namespace(class)). This is often more effective, since for the computation of the name space of a class the function can be directly invoked (through the associated java method), whereas in Crocopat a matching fact must be found in the list of facts for the namespace attribute.

As far as performance is concerned, we have used the largest graph in the qualitas corpus (Azureus3.0.3.4.jar, 5378 vertices and 29231 edges) and the computationally most expensive query (circular dependencies between packages) for benchmarking. Executing the query took 6 min 21 sec and returned 27421 results. On the other hand, executing the same query using GQL4JUNG to compute all variants (not using aggregation) took 11 min 45 sec and returned 611,566 instances. The larger number of instances results from the fact that motif instances with the same end nodes but different paths are treated as differently by GQL4JUNG. Therefore, GQL4JUNG has to deal with a much larger search space. However, when aggregation was used and only one path per package with circular dependency is computed, GQL4JUNG finished analysis in 53 sec and returned 309 results. Moreover, the GQL4JUNG has a different observer based API. It should also be noted that we had to increase memory settings in CrocoPat to 512 MB (starting CrocoPat with the -m 512 option, the application was still running out of memory with -m 450), whereas the GQL4JUNG engine was able to answer the query with the heap size of the JDK set to only 64 MB (-Xmx64m option) to compute all variants or all instances.  If the variants had to be recorded in memory, computing all variants required 192 MB of memory.

As far as usability is concerned, we assume that GQL4JUNG is easier to use for practitioners as it is based familiar technologies like XML and Java based expression languages. Being Java based and therefore platform independent, it is easier to integrate into many applications such IDEs like Eclipse and build tools like ANT.

The fact that CrocoPat has many procedural elements means that calculations can be expressed directly within the language. On the other hand, in GQL4JUNG the declarative aspect (of the queries) and the procedural aspect (the preprocessors used to annotate the language) are clearly separated. We believe that this makes the language easier to use.

## Appendix ##

### Platform used for experiment ###
OS: 2.6.28-13-generic (#44-Ubuntu SMP Tue Jun 2 07:57:31 UTC 2009)
CPU: Intel(R) Core(TM)2 CPU T5600  @ 1.83GHz
Memory: 2012 MiB
Java: OpenJDK Runtime Environment (IcedTea6 1.4.1) (6b14-1.4.1-0ubuntu7)

### Code used in experiment ###

  * Project URL: http://gql4jung.googlecode.com/svn/trunk/benchmarks/
  * Find all cd variants in Azureus3.0.3.4 using gql4jung: src/nz/ac/massey/cs/gql4jung/benchmarking/CountAzureusVariants.java
  * Find all cd instances in Azureus3.0.3.4 using gql4jung: src/nz/ac/massey/cs/gql4jung/benchmarking/CountAzureusInstances.java
  * Find whether there is one cd instance in Azureus3.0.3.4 using gql4jung: src/nz/ac/massey/cs/gql4jung/benchmarking/FindFirstAzureusVariant.java
  * Find all cd instance in Azureus3.0.3.4 using CrocoPat: crocopat/analyze-Azureus.sh
  * Test cd query with CrocoPat: crocopat/analyze-test1.sh , crocopat/analyze-test2.sh
  * Create CrocoPat input file (RSF) from Azureus3.0.3.4.jar: src/nz/ac/massey/cs/gql4jung/benchmarking/crocopat/Jung2RSF.java