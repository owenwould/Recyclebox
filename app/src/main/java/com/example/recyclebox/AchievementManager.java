package com.example.recyclebox;

import java.util.List;
import java.util.Map;

import com.example.recyclebox.Data_models.Achievement;

public class AchievementManager {
    List<Achievement> achievementList;
    AchivementManagerListner listner;
    final int POINT_BRONZE = 10, POINT_SILVER = 15, POINT_GOLD = 20;

    public void setList(Map<String, Integer> materialMap, int iUserTotal) {
        //Setup list with current values and check if already complete
        for (Achievement achivement : achievementList) {
            for (String material : materialMap.keySet()) {
                int type = Appconstants.returnMaterialType(material);

                if (achivement.getType() == type) {
                    achivement.setCurrentVal(materialMap.get(material));

                    if (achivement.getCurrentVal() >= achivement.getTotal())
                        achivement.setCompleted(true);

                } else if (achivement.getType() == Appconstants.ITYPE_ALL) {
                    achivement.setCurrentVal(iUserTotal);

                    if (achivement.getCurrentVal() >= achivement.getTotal())
                        achivement.setCompleted(true);
                }
            }
        }
    }

    public void checkAchievements(Integer iType) {
        //Call each time an item is recorded
        for (Achievement achievement : achievementList) {
            if (achievement.isCompleted())
                continue;
            //Use .equals otherwise it compares ref not values
            if (iType.equals(achievement.getType()))
                updateAchievement(achievement);
            else if (achievement.getType() == Appconstants.ITYPE_ALL)
                updateAchievement(achievement);
        }
        //Check complete, update achievement manager in main activity
        listner.achievementsChecked();
    }

    void updateAchievement(Achievement achievement) {
        int current = achievement.getCurrentVal();
        current += 1;
        achievement.setCurrentVal(current);

        if (current >= achievement.getTotal()) {
            listner.achievementUnlocked(achievement.getName(),
                    returnReward(achievement.getAchievementType()));
            achievement.setCompleted(true);
        }
    }

    public List<Achievement> getAchievementList() {
        return achievementList;
    }

    public void setAchievementList(List<Achievement> achievementList) {
        this.achievementList = achievementList;
    }

    public interface AchivementManagerListner {
        void achievementUnlocked(String name, int points);
        void achievementsChecked();
    }

    private int returnReward(int iAchievementType) {
        int point = 0;
        switch (iAchievementType) {
            case Appconstants.achievementTypeBronze:
                point = POINT_BRONZE;
                break;
            case Appconstants.achievementTypeSilver:
                point = POINT_SILVER;
                break;
            case Appconstants.achievementTypeGold:
                point = POINT_GOLD;
                break;
        }
        return point;
    }

    public AchievementManager(List<Achievement> achievementList) {
        this.achievementList = achievementList;
    }


    public void setListner(AchivementManagerListner listner) {
        this.listner = listner;
    }
}
