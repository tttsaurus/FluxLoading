package com.tttsaurus.fluxloading.function;

public abstract class Func<TReturn> extends FuncBase
{
    public abstract TReturn invoke();
}
