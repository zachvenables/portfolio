# zackbot
This is a discord bot written in python 3.  It's main purpose is to create a simple, persistent user database for group messaging.
It is available to all users in the channel, not just the admins.

The zackbot current functiona:
	message groups - <groupname>
	create a new group	- new_group <groupname>
	add users to existing group - add_user <username> to <groupname>
	remove users from group - remove_user <username> from <groupname>
	delete group - delete_group <groupname>
	
SETUP INSTRUCTIONS:

-add a bot to a discord server
-generate a token for the bot to sign in with
-copy and paste token as a string into the last line of code in bot.py (Must have Python 3.7 installed)
-Double click bot.py to run, or navigate to the directory using CMD or Terminal then exectue with command 'py bot.py'


USER INSTRUCTIONS:
-by convention all groups must be titled <groupname>'_boys'.  This is so there are no accidental group messaging.
-to message a group just type the group titled
-to add users to a group use command 'add_user <username 1> <username 2> .... <username N> to <group title>'
-to create a new group use command 'new_group <group_title>'  After this you will be able to add users to the group