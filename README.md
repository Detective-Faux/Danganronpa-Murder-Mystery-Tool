# Danganronpa Murder Mystery Tool (DMMT)

![Current Version](https://img.shields.io/badge/dynamic/json.svg?color=brightgreen&label=Latest%20Version&query=tag_name&url=https%3A%2F%2Fgithub.com%2FShadow-Spade%2FDanganronpa-Murder-Mystery-Tool%2Freleases%2Flatest)

## About

The Danganronpa Murder Mystery Tool (DMMT) is an application that was developed in necessity for an ease toward the game's startup time.
This project not only was something that I decided to do of my own volition, but I started it to get more familiar with external libraries and JavaFX.

If you would like more information on the project, feel free to contact me on Discord **@Shadow Spade#1335**
or you can DM me on Twitter **@ShadowSpadeTV**

Otherwise, I hope you find the tool useful in your own way!

## Where and What should I download?

If you haven't already, go and grab the
[latest stable release](https://github.com/Shadow-Spade/Danganronpa-Murder-Mystery-Tool/releases/latest)

If you're unfamiliar with Java I recommend downloading the Zip file, otherwise, if you're just updating an older version, all you'll need to replace is the Jar file.

## What do I need?

This program revolves around a few things:
* Access to a Discord bot
* A main Discord server to focus on
* Access to a Google Spreadsheet with game data

If you have access to these things, then you can skip to the next section!  
If you don't I will go over how to get these.

### Spreadsheet

First, you'll want to create a spreadsheet in your Google Drive.  
Here's an example of how you should format you new spreadsheet:

https://docs.google.com/spreadsheets/d/1QyhbDICpzsgfMZGyjWeL8AcxVOp5e_geoT-70gmRPKY/edit?usp=sharing

Once you have your spreadsheet ready to be used, you'll need its ID. 
This can be found between the **spreadsheets/d/** and the **/edit** parts of your spreadsheet's URL.

### Discord Items

Next, let's talk about using a Discord Bot.
If you would like to use your own bot for your server, you'll need to go to
[Discord's developer portal](https://discordapp.com/developers/applications/)  
Create yourself a new application, make it a discord bot, and copy down the **Discord Token**.  
Now would also be a good time to connect your new bot to your discord account if you haven't done so already.  
To do so, edit the following link to your desire: 

https://discordapp.com/oauth2/authorize?&client_id=Your_Bots_ID_Here&scope=bot&permissions=8

Replace the "Your_Bots_ID_Here" with the ID of your bot, and the number at the end of "permissions=" with the permissions you'd like.  
(This may change in the future when the bot gets more helpful features, but for now, any or no permissions work for the bot)

Finally, you'll lastly need your Discord server's ID.
To find this, you'll need to enable developer options, which can be found in your Appearance Settings in Discord.  
Once enabled, right click on your server's icon, and click Copy ID.

## How do I use this program?
### First Run
If you have all the necessary information, run the program either through the command line,
or (if you had downloaded the Zip file), double clicking the "run.bat" file.

Your first time running the program you may see something like this:

![alert](https://i.imgur.com/iwrD9Mz.png)

You'll need to enter the information we gathered earlier into these field before you can run the program.

### Roles

Once you boot up the program you'll be greeted with a screen that looks something like this:

![first screen](https://i.imgur.com/wpOFSzJ.png)

To get started, you'll need a list of roles you want to dish out to your players.
Start by Clicking the big **GO** button, and around 12 different roles (if available) will appear in the center.

![starting](https://i.imgur.com/QlTfdTO.png)

If there's a role you don't like, or you'd like to customize the list to your liking,
you can _Right Click_ the center list, and choose to add a role,
add a tag or remove the currently selected role.

### Players

Once you've got a list of Roles you like, Click over to the **User Select** tab at the top.
You should end up with a screen that looks like this:

![players](https://i.imgur.com/NJcLmgn.png)

The **List of Users** section will show you a list of all the users on the spreadsheet.
To add a user to the **Who's Playing** field, just click on the user you want to add in the **List of Users**
and use the right arrow to move them over to the list.
If you have any moderators (probably yourself) who would like to know all the finalized data,
use the up arrow on after selecting a user to move them to the **Moderators** field.

**Notice**: You will not be able to send out your role selection until you sync up your list of Roles and Players lengths so that they match.

![Sync](https://i.imgur.com/UTgudUu.png)

Once everything's set up correctly, click the **Send Roles** button at the bottom,
and everyone you have selected in your lists should receive a discord message with their role,
and moderators should get a quick list of who all has what role/tag.

![user message](https://i.imgur.com/qxm7qOE.png)

![moderator message](https://i.imgur.com/gJlhXzN.png)

### Other Features

There are a few other simple features that the program has.  
There's a simple Dice roller, and a simple Countdown Timer for the needs of the moderator.

![Dice Roll](https://i.imgur.com/u3p9Ib1.png)

![Countdown](https://i.imgur.com/TroSEfJ.png)


## Thank You <3

This concludes the Help section.
I want to thank those for inspiring me to go through with this project,
and I want to thank the people who decide to use it.
It means a lot that my work is getting used for things like this,
so from the bottom of my heart,
thank you for your support, and I hope you enjoy as I continue development! 