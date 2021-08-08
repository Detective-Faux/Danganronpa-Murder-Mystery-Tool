package Danganronpa.Helpers.Discord;

import Danganronpa.Helpers.GameItems.Player;
import Danganronpa.Helpers.Other.Settings;
import Danganronpa.Helpers.Other.SpreadsheetHandler;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.util.*;

public class DiscordHandler {
    private static final GatewayIntent[] INTENTS = new GatewayIntent[]{
            GatewayIntent.GUILD_MEMBERS,
            GatewayIntent.GUILD_MESSAGES,
            GatewayIntent.DIRECT_MESSAGES
    };
    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordHandler.class);
    private static Guild mainGuild;

    public static void buildDiscordInstance(){
        try{
            LOGGER.info("Booting Discord...");
            JDABuilder.createDefault(Settings.getInst().getDiscordToken(), Arrays.asList(INTENTS))
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.EMOTE)
                    .setBulkDeleteSplittingEnabled(false)
                    .setAutoReconnect(true)
                    .addEventListeners(new Capsule())
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setActivity(Activity.watching("over "+ Settings.getInst().getUsername()+"'s class..."))
                    .build();
        } catch (LoginException e) {
            LOGGER.error("Exception in Discord Login!");
        }
    }

    public static void sendDM(Player p, String message){
        Objects.requireNonNull(mainGuild.getMemberById(p.getID())).getUser().openPrivateChannel().queue(ch -> ch.sendMessage(message).queue());
    }

    public static void afterDiscordStartup(Guild guild) {
        mainGuild = guild;
        SpreadsheetHandler.buildList(Settings.getInst().getPlayerRange());
        checkNewUsers();
        LOGGER.info("Finished Initialization");
    }

    public static void checkNewUsers(){
        boolean found;
        ArrayList<Player> temp = new ArrayList<>();
        for(Member m: mainGuild.getMembers()){
            found = false;
            if(m.getUser().isBot()) continue;
            for(Player s: SpreadsheetHandler.USER_LIST) {
                if(s.getID().equals(m.getId())) {
                    found = true;
                    break;
                }
            }
            if(!found) temp.add(new Player(m));
        }
        if(temp.isEmpty()) return;
        SpreadsheetHandler.USER_LIST.addAll(temp);
        List<List<Object>> toAppend = new ArrayList<>();
        for(Player s: temp) toAppend.add(s.getPlayerSheetVariable());
        SpreadsheetHandler.getInstance().appendSpreadsheet(Settings.getInst().getPlayerRange(), toAppend);
        LOGGER.info("{} new users have been added to the user list!", toAppend.size());
    }

    public static void updateUser(Player p){
        //FIXME The updated user isn't displayed instantly once the update is sent
        Member m = mainGuild.getMemberById(p.getID());
        if(m == null) return;
        int x = 2;
        for(List<Object> row: SpreadsheetHandler.getInstance().getRange(Settings.getInst().getPlayerRange())){
            if(row.get(0).toString().equals(p.getID())){
                String name = m.getUser().getName();
                if(!row.get(1).toString().equals(name)){
                    row.set(1,name);
                    SpreadsheetHandler.getInstance().updateSingleEntry(Settings.getInst().getPlayerRange()+"!A"+x+":P", Collections.singletonList(row));
                    p.setName(name);
                }
                break;
            }
            x++;
        }
    }
}
