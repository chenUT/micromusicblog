Micromix Blog
=============

_A Google App Engine project brought to you by Group 4, ECE 1779_

Members:
- Chen Liu                      1000278157
- Emin Tham                     995835716
- Priyamsa Maddila              1000998496


Dependence
----------

1. Eclipse
2. Eclipse Maven Plugin
3. Eclipse App Engine SDK
4. Eclipse Spring Plugin
5. Objectify

Note: Library Dependencies are handeld by Maven build system

Instructions
------------

### In Eclipse
1. Import into Eclipse as exsiting Maven Project.
2. Run maven install in the root directory.
3. In the micromusicblog-ear directory, run maven build -> appengine:devserver.
4. Within your browser (we recommend Firefox for best performance but Chrome 
   works as well), navigate to localhost:8080

### Command line
1. In micromusicblog, run `mvn clean install`.
2. In `micromusicblog/micromusicblog-ear`, run `mvn appengine:devserver`
3. Go to step 4 of the _In Elipse_ instructions.
