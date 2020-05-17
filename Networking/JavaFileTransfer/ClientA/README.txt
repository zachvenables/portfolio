#File Transfer Platform Lab2

This is a simple file transfer client where users can post a file to a server and then another user download that file

##Installation

Included in this folder is a Client.java and Client.class file.  You can compile the Client.java file if you need to, or just issue the command
"java Client" to start

##Usage
Run the client AFTER the server is running.

You will be prompted to enter the IP address of the the server and then which action you would like to perform.

If you are performing a post, you will be asked for the name of the file. Then the client will be waiting for some other client to
request the file.

If you are performing a download, you will be prompted to enter your own IP address and the file that you wish to download.  Make sure youre download
and post clients are running in different folders on STDLinux, because the download will clear a file with the same name in the same location.

Once the download request is sent to the server, the download client waits for the datagrams to start coming in.  10 seconds after the last datagram is received 
the client will close.


