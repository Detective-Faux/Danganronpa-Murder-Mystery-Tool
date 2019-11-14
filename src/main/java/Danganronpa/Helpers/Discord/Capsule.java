package Danganronpa.Helpers.Discord;

import Danganronpa.Controller;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class Capsule extends ListenerAdapter {
    private String guildID, prefix;

    public Capsule(String guildID, String prefix){
        this.guildID = guildID;
        this.prefix = prefix;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        Controller.checkNewUsers(guildID);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().isBot() && event.getMessage().getContentRaw().startsWith(prefix)){
            if(event.getMessage().getContentRaw().equals(prefix+"ping")){
                event.getTextChannel().sendMessage("pong...").queue();
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(event.getGuild().getId().equals(guildID)) Controller.update(event.getMember());
    }
}