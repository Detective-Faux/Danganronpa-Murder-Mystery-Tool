package Danganronpa.Helpers.Discord;

import Danganronpa.Helpers.GameItems.Player;
import Danganronpa.Helpers.Other.Settings;
import Danganronpa.Helpers.Other.SpreadsheetHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Objects;

public class Capsule extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(Capsule.class);
    public Capsule(){}

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        LOGGER.info("{} is ready", event.getJDA().getSelfUser().getAsTag());
        Guild g = Objects.requireNonNull(event.getJDA().getGuildById(Settings.getInst().getPreferredGuild()));
        g.loadMembers().onSuccess(t -> DiscordHandler.afterDiscordStartup(g)); //Fixes "Get member" problem
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        //User is not a Bot
        if(event.getAuthor().isBot()) return;
        //User is Messaging the bot
        String raw = event.getMessage().getContentRaw();
        if(!raw.startsWith(Settings.getInst().getPrefix())) return;
        raw = raw.replaceFirst(Settings.getInst().getPrefix(), "").trim();

        if(raw.startsWith("ping")){
            event.getTextChannel().sendMessage("Ping: ...").queue(m -> {
                long ping = event.getMessage().getTimeCreated().until(m.getTimeCreated(), ChronoUnit.MILLIS);
                m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getGatewayPing() + "ms").queue();
            });
            LOGGER.warn(event.getAuthor().getName()+" Used command \"ping\" in "+event.getTextChannel().getName());
        }

        else if(raw.startsWith("status")){
            event.getTextChannel().sendMessage("The bot is currently logged in under: `"+Settings.getInst().getUsername()+"`").queue();
            LOGGER.warn(event.getAuthor().getName()+" Used command \"status\" in "+event.getTextChannel().getName());
        }
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        //==Update Spreadsheet==//
        if(event.getGuild().getId().equals(Settings.getInst().getPreferredGuild())) {
            Player s = new Player(event.getMember());
            for(Player plr : SpreadsheetHandler.USER_LIST) if(plr.getID().equals(s.getID())) return;
            SpreadsheetHandler.USER_LIST.add(s);
            SpreadsheetHandler.getInstance().appendSpreadsheet(Settings.getInst().getPlayerRange(), Collections.singletonList(s.getPlayerSheetVariable()));
            LOGGER.info("A new user joined just joined the server! They have been added to the user list!");
        }
    }
}