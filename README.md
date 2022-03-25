SRLookup
========

Simple tool to lookup norwegian words in the dictionary used at [ordbøkene.no](https://ordbøkene.no).

Build
-----

To build the jar make sure you have ant installed and then run `ant -f build.xml jar`.

Run
---

Whith the jar built you can either use the SRLookup.jar CLI or GUI. To lookup a word using CLI, type `java -jar SRLookup.jar [word]`. If you omit the word the GUI client will be run.

Usage
-----

The CLI client is described above. The GUI client is fairly simple. You just start typing the word and at the 2nd character the application will start fetching suggestions that are presented in the list below. If you press enter the focus will be moved from the input field to the list so you can select the word you're looking for. Pressing enter while selecting a word will open a web browser directed to ordbøkene.no presenting the definition of the word.

Disclaimer
-------

All the information provided in this application is fetched from [ord.uib.no](https://ord.uib.no). I take no responsibility for the availablility nor the content received and presented from their API.

Credits
-------

This application was written mainly for personal use, but I could see that it may have value also for others and therefore I will publish it online.

Copyright 2022 Vegard Løkken <vegard@loekken.org>

