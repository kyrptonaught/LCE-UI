package net.kyrptonaught.lceui.creativeInv;

import java.util.List;

public interface ItemGroupExpander {

    void replaceGroups(List<CustomItemGroup> customItemGroupList);

    void clearGroups();

    void shrinkGroups();
}
