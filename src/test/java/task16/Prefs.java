package task16;

import lombok.Data;

import java.util.List;
@Data
public class Prefs {
    private String permissionLevel;
    private boolean hideVotes;
    private String voting;
    private String comments;
    private String invitations;
    private boolean selfJoin;
    private boolean cardCovers;
    private boolean showCompleteStatus;
    private boolean cardCounts;
    private boolean isTemplate;
    private String cardAging;
    private boolean calendarFeedEnabled;
    private List<String> hiddenPluginBoardButtons;
    private List<SwitcherView> switcherViews;
    private Object autoArchive;
    private String background;
    private String backgroundColor;
    private Object backgroundDarkColor;
    private Object backgroundImage;
    private Object backgroundDarkImage;
    private Object backgroundImageScaled;
    private boolean backgroundTile;
    private String backgroundBrightness;
    private Object sharedSourceUrl;
    private String backgroundBottomColor;
    private String backgroundTopColor;
    private boolean canBePublic;
    private boolean canBeEnterprise;
    private boolean canBeOrg;
    private boolean canBePrivate;
    private boolean canInvite;

    public boolean getIsTemplate() {
        return isTemplate;
    }

    public void setIsTemplate(boolean template) {
        isTemplate = template;
    }
}
