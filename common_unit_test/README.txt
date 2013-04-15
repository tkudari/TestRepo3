GMUSICK (03/07):

Notes from first attempt to get unit tests on common with Robolectric:

1) To run this from the command line, you can just type "ant test" as normal although
I am not sure why this works at the moment.

2) If you run this through the IDE, you will need to downlevel the target sdk to 16. There is a
bug in Robolectric where it chokes on level 17 stuff.