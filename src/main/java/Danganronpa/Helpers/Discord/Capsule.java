package Danganronpa.Helpers.Discord;

import Danganronpa.Controller;
import Danganronpa.Helpers.Other.Settings;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class Capsule extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Capsule.class);
    public Capsule(){}

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
        Guild g = Objects.requireNonNull(event.getJDA().getGuildById(Settings.getInst().getPreferredGuild()));
        g.loadMembers().onSuccess(t -> Controller.afterDiscordStartup(g)); //Fixes "Get member" problem
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(!event.getAuthor().isBot() && event.getMessage().getContentRaw().startsWith(Settings.getInst().getPrefix())){
            if(event.getMessage().getContentRaw().equals(Settings.getInst().getPrefix()+"ping")){
                event.getTextChannel().sendMessage("pong...").queue();
            }
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        if(event.getGuild().getId().equals(Settings.getInst().getPreferredGuild())) Controller.update(event.getMember());
    }
}