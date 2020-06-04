import discord
import group
import user
import os
import procedurallearningmod
import ultraalert
from discord.ext import commands


client = commands.Bot(command_prefix = '.')

#On bot ready, build a list of all groups available to be messaged, items are group objects
group_list = []


#Helper method to get the group name out of a string
def get_group_name(msg_list):
	for item in msg_list:
		if '_boys' in item:
			for name in group_list:
				if item == name.name:
					return item

	return 'Error: groupname not found in grouplist'


#Boolean to check if a group is to be messaged
def is_group_message(msg_list, author):
	if 'new_group' not in msg_list and 'add_user' not in msg_list and 'remove_user' not in msg_list and 'delete_group' not in msg_list and author != 'zackbot69000':
		for item in msg_list:
			if contains_group(item):
				return True

	return False

#Boolean to see fi a message contains a group
def contains_group(msg):
	for item in group_list:
		if item.name == msg:
			return True

	return False


#gets the corresponding user id from the 'client list' from the discord server
def get_user_id(user):
	for item in client.users:
		if item.name.lower() == user:
			return str(item.id)


#gets the group object from the group list
def get_group(groupname):
	for item in group_list:
		if item.name.lower() == groupname.lower():
			return item

	return None

#boolean to check if a word is a valid username
def is_valid_username(username):
	for item in client.users:
		if item.name.lower() == username.lower():
			return True

	return False

#sends a message through the server to an entire group
async def message_group(groupname, channel):
	message = ''
	for item in group_list:
		print(item.name)
		if item.name == groupname:
			for user in item.users:
				message += '<@' + user + '> '
			break	

	if len(message) < 1:
		message += 'No users in group.'

	await channel.send(message)


@client.event
async def on_ready():
	for file in os.listdir():
		if '_boys' in file:
			group_name = file[:-4]
			current_group = group.Group(group_name)
			group_list.append(current_group)
			f = open(file, 'r')
			for line in f.read().splitlines():
				current_group.add_user(line)

			f.close()

	print('Bot is running.')


@client.event
async def on_message(msg):
	msg_list = msg.content.split(' ')
	channel = msg.channel

	#used to test if server is operating
	if msg.content == 'ping':
		await channel.send('pong')
	
	#checks to see if a group name is in the message content
	if is_group_message(msg_list, msg.author.name):
		await message_group(get_group_name(msg_list), channel)
	
	#command for creating a new group
	if msg_list[0] == 'new_group':
		if len(msg_list) > 1:
			if msg_list[1] != None and '_boys' in msg_list[1] and not contains_group(msg_list[1]):
				group_name = msg_list[1]
				group_list.append(group.Group(group_name))
				file = open(group_name + '.txt', 'w+')
				file.close()
				await channel.send('New group ' + group_name + ' added.')
			else:
				await channel.send('Must provide valid group name.')

	#command for adding a user to a group
	if msg_list[0] == 'add_user':
		addgroup = None
		if 'to' in msg_list and msg_list.index('to') != len(msg_list)-1:
			addgroup = get_group(msg_list[msg_list.index('to')+1])
		else:
			await channel.send('Must include keyword \'to\'')

		if addgroup != None:
			i=1
			while msg_list[i] != 'to':
				username = msg_list[i].lower()
				if is_valid_username(username):
					userid = get_user_id(username)
					if userid not in addgroup.users:
						addgroup.add_user(userid)
						file = open(addgroup.name + '.txt', 'a')
						file.write(userid + '\n')
						file.close()
						await channel.send(addgroup.name + ' updated.')
					else:
						await channel.send(username + ' is already in that group.')
				else:
					await channel.send(username + ' is an invalid username.')
				i += 1
		else:
			await channel.send('Not a valid group name')

	#command for removing a user from a group
	if msg_list[0] == 'remove_user':
		username = None
		if len(msg_list) > 1:
			username = msg_list[1]

		if username != None and is_valid_username(username):
			removegroup = None
			if 'from' in msg_list:
				removegroup = get_group(msg_list[msg_list.index('from') + 1])
			else:
				await channel.send('Missing keyword \'from\'.')

			if removegroup != None:
				userid = get_user_id(username)
				print(removegroup.users)
				removegroup.remove_user(userid)
				print(removegroup.users)

				with open(removegroup.name + '.txt', 'r') as f:
					lines = f.readlines()
				with open(removegroup.name + '.txt', 'w') as f:
					for line in lines:
						if line.strip('\n') != userid:
							f.write(line)

				await channel.send('User ' + username + ' was removed from group ' + removegroup.name)
			else:
				await channel.send('Invalid group name.')
		else:
			await channel.send('Invalid user name.')

	#command for deleting a group file
	if msg_list[0] == 'delete_group':
		deletegroup = None
		if len(msg_list) > 1:
			deletegroup = msg_list[1]

		if deletegroup != None and contains_group(deletegroup):
			group_list.remove(get_group(deletegroup))
			os.remove(deletegroup + '.txt')
			await channel.send('Group ' + deletegroup + ' removed.')
		else:
			await channel.send('Invalid group name.')

#######################################			
#TOKEN NEEDED TO RUN
#TOKEN ACQUIRED THROUGH DISCORD
#client.run(!INSERT TOKEN HERE!)
#######################################