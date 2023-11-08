package com.goglotek.examhelper.listeners;

import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseInputListener;
import com.goglotek.examhelper.exception.ExamHelperException;

public interface EventListener extends NativeKeyListener, NativeMouseInputListener {

  public void listenForEvents() throws ExamHelperException;

  public void destroyEventListener() throws ExamHelperException;

}
