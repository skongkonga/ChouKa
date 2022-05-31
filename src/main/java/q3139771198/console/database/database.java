package q3139771198.console.database;

import java.util.List;

public interface database {

    String getCardTimes(String name, String cardname);

    String getCard(String name, String card);

    boolean checkForAddColumn(List<String> enabled, String type);

    boolean isRegisteredTime(String name);

    boolean isRegisteredCard(String name);

    boolean updateTime(String Id, String cardname, Integer times);

    boolean updateCard(String Id, String card, Integer times);

    void closeConnection();

    void openConnection(List<String> enabled, String type);
}
