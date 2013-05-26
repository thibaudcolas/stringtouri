# StringToURI — Basic RDF dataset interlinking

> StringToURI is a simple link generation framework which helps creating links between RDF datasets.

------------------------------------------------------------

## Overview

As stated by its name, StringToURI looks for specified strings inside a data set and tries to find matches inside another data set.
If two strings are equal, the string in the "target / goal / destination / update" data set is replaced by a link to the entity described by the string inside the "source / reference / origin" data set.

This process is delivered in three different fashions :

- Through a library by using its main components : DataSet, Linkage, Output.
- In a much more abstract way using the App class of the library.
- By using one of the ready-to-use tools inside the app package.


## Structure

- The src and test folders contain source code respectively for source files and test files.
  - The util package contains everything needed in order to link datasets together.
  - The app package contains examples of apps ready to be used in a real world context.
- The doc folder contains the javadoc.
- import contains every external library used by StringToURI
- log contains examples of log files as logged by the application.
- rdf contains rdfxml examples.

## Changelog

v1.0.0, 2012-04-30
------------------------

- Initial release

v1.1.0, 2012-07-11
------------------------

- New package architecture
- Now using sesame 2.6.7
- SesameOutput is no longer supported
- RDFOutput is no longer supported
- Datasets can now specify a context to use.
- Linkages can now use contexts inside their queries.
- Output can now display the new tuples as lists of strings.

## System requirements :

- Sesame 2.6.7
  - Commons httpclient 3.1
  - Commons codec 1.4
  - Commons logging 1.1.1
  - slf4j api 1.6.4
  - An slf4j implementation 1.6.4
- Commons CLI 1.2 for the CLI apps
- Log4j
- JUnit 4 for the unit tests 

## Origin

StringToURI was written during a school research project at the UM2 university in Montpellier. It was previously hosted on [Assembla](http://www.assembla.com/) and has now been moved to GitHub.

© 2012-2013 ThibWeb
