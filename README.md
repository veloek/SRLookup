SRLookup
========

Simple tool to lookup norwegian words in the dictionary used at sprakrad.no.

Build
-----

To build the jar make sure you have ant installed and then run:
    $ ant -f build.xml jar

Run
---

Whith the jar built you can either use the SRLookup.jar CLI or GUI. To lookup a word using CLI, type `java -jar SRLookup.jar [word]`. If you omit the word the GUI client will be run.

Disclaimer
-------

All the information provided in this application is fetched from sprakrad.no. It's not some open API, so the resources was found by reverse engineering the web site. I am not aware of any legal issues this may cause. I take no responsibility for anything harmful that this application may inflict on sprakrad.no by misuse or exploitation of it's functionality.

Credits
-------

This application was written mainly for personal use, but I could see that it may have value also for others and therefore I will publish it online.

The author is Vegard Løkken <vegard@loekken.org>.

