package com.tttsaurus.fluxloading.core.function;

public abstract class Func<TReturn> extends FuncBase
{
    public abstract TReturn invoke();
}
