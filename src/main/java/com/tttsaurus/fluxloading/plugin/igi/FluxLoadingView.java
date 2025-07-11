package com.tttsaurus.fluxloading.plugin.igi;

import com.tttsaurus.ingameinfo.common.core.mvvm.view.View;

public final class FluxLoadingView extends View
{
    @Override
    public String getIxmlFileName()
    {
        return "fluxloading";
    }

    @Override
    public String getDefaultIxml()
    {
        return """
                <Text text = "This is an example!">
                """;
    }
}
