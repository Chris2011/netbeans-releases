/*
 * Sun Public License Notice
 * 
 * The contents of this file are subject to the Sun Public License Version
 * 1.0 (the "License"). You may not use this file except in compliance with
 * the License. A copy of the License is available at http://www.sun.com/
 * 
 * The Original Code is the Jemmy library.
 * The Initial Developer of the Original Code is Alexandre Iline.
 * All Rights Reserved.
 * 
 * Contributor(s): Alexandre Iline.
 * 
 * $Id$ $Revision$ $Date$
 * 
 */

package org.netbeans.jemmy.operators;

import org.netbeans.jemmy.CharBindingMap;
import org.netbeans.jemmy.ComponentChooser;
import org.netbeans.jemmy.ComponentSearcher;
import org.netbeans.jemmy.EventDispatcher;
import org.netbeans.jemmy.JemmyProperties;
import org.netbeans.jemmy.Outputable;
import org.netbeans.jemmy.TestOut;
import org.netbeans.jemmy.Timeoutable;
import org.netbeans.jemmy.TimeoutExpiredException;
import org.netbeans.jemmy.Timeouts;
import org.netbeans.jemmy.Waitable;
import org.netbeans.jemmy.Waiter;

import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MenuComponent;
import java.awt.Point;
import java.awt.PopupMenu;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;

import java.awt.dnd.DropTarget;

import java.awt.event.ComponentListener;
import java.awt.event.FocusListener;
import java.awt.event.InputEvent;
import java.awt.event.InputMethodListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.im.InputContext;
import java.awt.im.InputMethodRequests;

import java.awt.image.ColorModel;
import java.awt.image.ImageObserver;
import java.awt.image.ImageProducer;

import java.beans.PropertyChangeListener;

import java.io.PrintStream;
import java.io.PrintWriter;

import java.util.Hashtable;
import java.util.Locale;

/**
 * Root class for all component operators.
 *
 * Provides basic methods to operate with mouse and keyboard.<BR>
 * <BR>
 * All user input methods (pushKey, moveMouse, ...) use dispatching model defined by
 * setDispatchingModel(int) model method.
 * Almost all input methods can throw JemmyInputException or its subclass.<BR>
 *
 * ComponentOperator and its subclasses has a lot of methods which name and parameters just like
 * consistent component has. In this case operator class just invokes consistent component method
 * through AWT Event Queue (invokeAndWait method).
 *
 * <BR><BR>Timeouts used: <BR>
 * ComponentOperator.PushKeyTimeout - time between key pressing and releasing <BR>
 * ComponentOperator.MouseClickTimeout - time between mouse pressing and releasing <BR>
 * ComponentOperator.WaitComponentTimeout - time to wait component displayed <BR>
 * ComponentOperator.WaitComponentEnabledTimeout - time to wait component enabled <BR>
 * ComponentOperator.BeforeDragTimeout - time to sleep before grag'n'drop operations <BR>
 * ComponentOperator.AfterDragTimeout - time to sleep after grag'n'drop operations <BR>
 * ComponentOperator.WaitFocusTimeout - time to wait component focus <BR>
 *
 * @see org.netbeans.jemmy.Timeouts
 * @see #setDispatchingModel(int)
 *
 * @author Alexandre Iline (alexandre.iline@sun.com)
 */

public class ComponentOperator extends Operator
    implements Timeoutable, Outputable {

    private final static long PUSH_KEY_TIMEOUT = 0;
    private final static long MOUSE_CLICK_TIMEOUT = 0;
    private final static long BEFORE_DRAG_TIMEOUT = 0;
    private final static long AFTER_DRAG_TIMEOUT = 0;
    private final static long WAIT_COMPONENT_TIMEOUT = 10000;
    private final static long WAIT_COMPONENT_ENABLED_TIMEOUT = 1000;
    private final static long WAIT_FOCUS_TIMEOUT = 10000;
    private final static long WAIT_STATE_TIMEOUT = 5000;

    private Component source;
    private Timeouts timeouts;
    private TestOut output;
    private EventDispatcher dispatcher;

    /**
     * Constructor.
     */
    public ComponentOperator(Component comp) {
	super();
	source = comp;
	setEventDispatcher(new EventDispatcher(comp));
    }

    /**
     * Constructor.
     * Waits for a component in a container to show. The component is
     * iis the <code>index+1</code>'th <code>java.awt.Component</code>
     * that shows and that lies below the container in the display
     * containment hierarchy.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont Operator for a java.awt.Container.
     * @throws TimeoutExpiredException
     */
    public ComponentOperator(ContainerOperator cont, int index) {
	this(waitComponent((Container)cont.getSource(), 
			   ComponentSearcher.getTrueChooser("Any component"),
			   index, cont.getTimeouts(), cont.getOutput()));
	copyEnvironment(cont);
    }

    /**
     * Constructor.
     * Waits for a component in a container to show. The component is
     * is the first <code>java.awt.Component</code>
     * that shows and that lies below the container in the display
     * containment hierarchy.
     * Uses cont's timeout and output for waiting and to init operator.
     * @param cont Operator for a java.awt.Container.
     * @throws TimeoutExpiredException
     */
    public ComponentOperator(ContainerOperator cont) {
	this(cont, 0);
	copyEnvironment(cont);
    }

    /**
     * Searches Component in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return Component instance or null if component was not found.
     */
    public static Component findComponent(Container cont, ComponentChooser chooser, int index) {
	return(findComponent(cont, chooser, index, false));
    }

    /**
     * Searches Component in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return Component instance or null if component was not found.
     */
    public static Component findComponent(Container cont, ComponentChooser chooser) {
	return(findComponent(cont, chooser, 0));
    }

    /**
     * Waits Component in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @param index Ordinal component index.
     * @return Component instance or null if component was not found.
     * @throws TimeoutExpiredException
     */
    public static Component waitComponent(Container cont, ComponentChooser chooser, int index) {
	return(waitComponent(cont, chooser, index, 
			     JemmyProperties.getCurrentTimeouts(),
			     JemmyProperties.getCurrentOutput()));
    }

    /**
     * Waits Component in container.
     * @param cont Container to search component in.
     * @param chooser org.netbeans.jemmy.ComponentChooser implementation.
     * @return Component instance or null if component was not found.
     * @throws TimeoutExpiredException
     */
    public static Component waitComponent(Container cont, ComponentChooser chooser) {
	return(waitComponent(cont, chooser, 0));
    }

    protected static Component waitComponent(ContainerOperator contOper, 
					     ComponentChooser chooser, int index) {
	return(waitComponent((Container)contOper.getSource(),
			     chooser, index,
			     contOper.getTimeouts(), 
			     contOper.getOutput()));
    }

    protected static Component waitComponent(final Container cont, 
					     final ComponentChooser chooser, 
					     final int index,
					     Timeouts timeouts, final TestOut output) {
	try {
	    Waiter waiter = new Waiter(new Waitable() {
		    public Object actionProduced(Object obj) {
			return(findComponent(cont, new VisibleComponentFinder(chooser), index, 
					     output.createErrorOutput()));
		    }
		    public String getDescription() {
			return("Wait " + chooser.getDescription() + " loaded");
		    }
		});
	    Timeouts times = timeouts.cloneThis();
	    times.setTimeout("Waiter.WaitingTime", times.getTimeout("ComponentOperator.WaitComponentTimeout"));
	    waiter.setTimeouts(times);
	    waiter.setOutput(output);
	    return((Component)waiter.waitAction(null));
	} catch(InterruptedException e) {
	    return(null);
	}
    }

    private static Component findComponent(Container cont, ComponentChooser chooser, int index, TestOut output) {
	ComponentSearcher searcher= new ComponentSearcher(cont);
	searcher.setOutput(output);
	return(searcher.findComponent(new VisibleComponentFinder(chooser), index));
    }

    private static Component findComponent(Container cont, ComponentChooser chooser, int index, boolean supressOutout) {
	return(findComponent(cont, chooser, index, JemmyProperties.getCurrentOutput().createErrorOutput()));
    }

    static {
	Timeouts.initDefault("ComponentOperator.PushKeyTimeout", PUSH_KEY_TIMEOUT);
	Timeouts.initDefault("ComponentOperator.MouseClickTimeout", MOUSE_CLICK_TIMEOUT);
	Timeouts.initDefault("ComponentOperator.BeforeDragTimeout", BEFORE_DRAG_TIMEOUT);
	Timeouts.initDefault("ComponentOperator.AfterDragTimeout", AFTER_DRAG_TIMEOUT);
	Timeouts.initDefault("ComponentOperator.WaitComponentTimeout", WAIT_COMPONENT_TIMEOUT);
	Timeouts.initDefault("ComponentOperator.WaitComponentEnabledTimeout", WAIT_COMPONENT_ENABLED_TIMEOUT);
	Timeouts.initDefault("ComponentOperator.WaitStateTimeout", WAIT_STATE_TIMEOUT);
    }

    /**
     * Returns component.
     */
    public Component getSource() {
	return(source);
    }

    /**
     * Returnes org.netbeans.jemmy.EventDispatcher instance which is
     * used to dispatch events.
     * @see org.netbeans.jemmy.EventDispatcher
     */
    public EventDispatcher getEventDispatcher() {
	return(dispatcher);
    }

    ////////////////////////////////////////////////////////
    //Environment                                         //
    ////////////////////////////////////////////////////////

    /**
     * Defines print output streams or writers.
     * @param out Identify the streams or writers used for print output.
     * @see org.netbeans.jemmy.Outputable
     * @see org.netbeans.jemmy.TestOut
     */
    public void setOutput(TestOut out) {
	super.setOutput(out);
	this.output = out;
	if(dispatcher != null) {
	    dispatcher.setOutput(output.createErrorOutput());
	}
    }
    
    /**
     * Returns print output streams or writers.
     * @return an object that contains references to objects for
     * printing to output and err streams.
     * @see org.netbeans.jemmy.Outputable
     * @see org.netbeans.jemmy.TestOut
     */
    public TestOut getOutput() {
	return(output);
    }

    /**
     * Force operator to use dispatching model.
     * @param m New model value.
     * @see org.netbeans.jemmy.JemmyProperties#setCurrentDispatchingModel(int)
     */
    public void setDispatchingModel(int m) {
	super.setDispatchingModel(m);
	if(dispatcher != null) {
	    dispatcher.setDispatchingModel(m);
	}
    }
    /**
     * Defines current timeouts.
     * @param t A collection of timeout assignments.
     * @see org.netbeans.jemmy.Timeoutable
     * @see org.netbeans.jemmy.Timeouts
     */
    public void setTimeouts(Timeouts timeouts) {
	super.setTimeouts(timeouts);
	this.timeouts = timeouts;
	if(dispatcher != null) {
	    dispatcher.setTimeouts(getTimeouts());
	}
    }

    /**
     * Return current timeouts.
     * @return the collection of current timeout assignments.
     * @see org.netbeans.jemmy.Timeoutable
     * @see org.netbeans.jemmy.Timeouts
     */
    public Timeouts getTimeouts() {
	return(timeouts);
    }

    ////////////////////////////////////////////////////////
    //Mouse operations
    ////////////////////////////////////////////////////////
    
    /**
     * Makes mouse click.
     * @param x Horizontal click coordinate
     * @param y Vertical click coordinate
     * @param clickCount Click count
     * @param mouseButton Mouse button (InputEvent.BUTTON1/2/3_MASK value)
     * @param modifiers Modifiers (combination of InputEvent.*_MASK values)
     * @param forPopup
     */
    public void clickMouse(int x, int y, int clickCount, int mouseButton, int modifiers, boolean forPopup) {
	moveMouse(x, y);
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotPressMouse(mouseButton, modifiers);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_ENTERED ,               modifiers,          0, x, y, false);
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_PRESSED , mouseButton | modifiers,          1, x, y, forPopup);
	}
	for(int i = 1; i < clickCount; i++) {
	    if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
		getEventDispatcher().robotReleaseMouse(mouseButton, modifiers);
		getEventDispatcher().robotPressMouse(mouseButton, modifiers);
	    } else {
		getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_RELEASED, mouseButton | modifiers, i    , x, y, forPopup);
		getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_CLICKED , mouseButton | modifiers, i    , x, y, forPopup);
		getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_PRESSED , mouseButton | modifiers, i + 1, x, y, forPopup);
	    }
	}
	timeouts.sleep("ComponentOperator.MouseClickTimeout");
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotReleaseMouse(mouseButton, modifiers);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_RELEASED, mouseButton | modifiers, clickCount, x, y, forPopup);
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_CLICKED , mouseButton | modifiers, clickCount, x, y, forPopup);
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_EXITED  ,               modifiers,          0, x, y, false);
	}
    }

    /**
     * Makes mouse click.
     * @param x Horizontal click coordinate
     * @param y Vertical click coordinate
     * @param clickCount Click count
     * @param mouseButton Mouse button (InputEvent.BUTTON1/2/3_MASK value)
     * @param modifiers Modifiers (combination of InputEvent.*_MASK values)
     * @see #clickMouse(int, int, int, int, int, boolean)
     */
    public void clickMouse(int x, int y, int clickCount, int mouseButton, int modifiers) {
	clickMouse(x, y, clickCount, mouseButton, modifiers, false);
    }

    /**
     * Makes mouse click with 0 modifiers.
     * @param x Horizontal click coordinate
     * @param y Vertical click coordinate
     * @param clickCount Click count
     * @param mouseButton Mouse button (InputEvent.BUTTON1/2/3_MASK value)
     * @see #clickMouse(int, int, int, int, int)
     */
    public void clickMouse(int x, int y, int clickCount, int mouseButton) {
	clickMouse(x, y, clickCount, mouseButton, 0);
    }

    /**
     * Makes mouse click by default mouse button with 0 modifiers.
     * @param x Horizontal click coordinate
     * @param y Vertical click coordinate
     * @param clickCount Click count
     * @see #clickMouse(int, int, int, int)
     * @see #getDefaultMouseButton()
     */
    public void clickMouse(int x, int y, int clickCount) {
	clickMouse(x, y, clickCount, getDefaultMouseButton());
    }

    /**
     * Press mouse.
     */
    public void pressMouse(int x, int y) {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotPressMouse(getDefaultMouseButton(), 0);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_PRESSED, getDefaultMouseButton(), 1, x, y, false);
	}
    }

    /**
     * Releases mouse.
     */
    public void releaseMouse(int x, int y) {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotReleaseMouse(getDefaultMouseButton(), 0);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_RELEASED, getDefaultMouseButton(), 1, x, y, false);
	}
    }

    /**
     * Move mouse over the component.
     * @param x Horisontal destination coordinate.
     * @param y Vertical destination coordinate.
     */
    public void moveMouse(int x, int y) {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotMoveMouse(x, y);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_MOVED, 0, 0, x, y, false);
	}
    }

    /**
     * Drag mouse over the component.
     * @param x Horisontal destination coordinate.
     * @param y Vertical destination coordinate.
     * @param mouseButton Mouse button
     * @param modifiers Modifiers
     */
    public void dragMouse(int x, int y, int mouseButton, int modifiers) {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotMoveMouse(x, y);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_DRAGGED, mouseButton | modifiers, 1, x, y, false);
	}
    }

    /**
     * Drag mouse over the component with 0 modifiers.
     * @param x Horisontal destination coordinate.
     * @param y Vertical destination coordinate.
     * @param mouseButton Mouse button
     * @see #dragMouse(int, int, int, int)
     */
    public void dragMouse(int x, int y, int mouseButton) {
	dragMouse(x, y, mouseButton, 0);
    }

    /**
     * Drag mouse over the component with 0 modifiers and default mose button pressed.
     * @param x Horisontal destination coordinate.
     * @param y Vertical destination coordinate.
     * @see #dragMouse(int, int, int)
     * @see #getDefaultMouseButton()
     */
    public void dragMouse(int x, int y) {
	dragMouse(x, y, getDefaultMouseButton());
    }

    /**
     * Makes drag'n'drop operation.
     * @param start_x Start horizontal coordinate
     * @param start_y Start vertical coordinate
     * @param end_x End horizontal coordinate
     * @param end_y End vertical coordinate
     * @param mouseButton Mouse button
     * @param modifiers Modifiers
     */
    public void dragNDrop(int start_x, int start_y, int end_x, int end_y, int mouseButton, int modifiers) {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotMoveMouse(start_x, start_y);
	    getEventDispatcher().robotPressMouse(mouseButton, modifiers);
	    timeouts.sleep("ComponentOperator.BeforeDragTimeout");
	    getEventDispatcher().robotMoveMouse(end_x, end_y);
	    timeouts.sleep("ComponentOperator.AfterDragTimeout");
	    getEventDispatcher().robotReleaseMouse(mouseButton, modifiers);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_ENTERED,                        0, 0, start_x, start_y, false);
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_PRESSED,  mouseButton | modifiers, 1, start_x, start_y, false);
	    timeouts.sleep("ComponentOperator.BeforeDragTimeout");
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_DRAGGED,  mouseButton | modifiers, 0, end_x,   end_y,   false);
	    timeouts.sleep("ComponentOperator.AfterDragTimeout");
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_RELEASED, mouseButton | modifiers, 1, end_x,   end_y,   false);
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_EXITED,                         0, 0, end_x,   end_y,   false);
	}
    }

    /**
     * Makes drag'n'drop operation with 0 modifiers.
     * @param start_x Start horizontal coordinate
     * @param start_y Start vertical coordinate
     * @param end_x End horizontal coordinate
     * @param end_y End vertical coordinate
     * @param mouseButton Mouse button
     * @see #dragNDrop(int, int, int, int, int, int)
     */
    public void dragNDrop(int start_x, int start_y, int end_x, int end_y, int mouseButton) {
	dragNDrop(start_x, start_y, end_x, end_y, mouseButton, 0);
    }

    /**
     * Makes drag'n'drop operation by default mouse buttons with 0 modifiers.
     * @param start_x Start horizontal coordinate
     * @param start_y Start vertical coordinate
     * @param end_x End horizontal coordinate
     * @param end_y End vertical coordinate
     * @see #dragNDrop(int, int, int, int, int)
     * @see #getDefaultMouseButton()
     */
    public void dragNDrop(int start_x, int start_y, int end_x, int end_y) {
	dragNDrop(start_x, start_y, end_x, end_y, getDefaultMouseButton(), 0);
    }

    /** 
     * Clicks for popup.
     * @param x Horizontal click coordinate.
     * @param y Vertical click coordinate.
     * @param mouseButton Mouse button.
     * @see #clickMouse(int, int, int, int, int, boolean)
     */
    public void clickForPopup(int x, int y, int mouseButton) {
	clickMouse(x, y, 1, mouseButton, 0, true);
    }

    /** 
     * Clicks for popup by popup mouse button.
     * @param x Horizontal click coordinate.
     * @param y Vertical click coordinate.
     * @see #clickForPopup(int, int, int)
     * @see #getPopupMouseButton()
     */
    public void clickForPopup(int x, int y) {
	clickForPopup(x, y, getPopupMouseButton());
    }


    /**
     * Makes mouse click on the component center with 0 modifiers.
     * @param clickCount Click count
     * @param mouseButton Mouse button (InputEvent.BUTTON1/2/3_MASK value)
     * @see #clickMouse(int, int, int, int)
     */
    public void clickMouse(int clickCount, int mouseButton) {
	clickMouse(getCenterXForClick(), getCenterYForClick(), clickCount, mouseButton);
    }

    /**
     * Makes mouse click on the component center by default mouse button with 0 modifiers.
     * @param clickCount Click count
     * @see #clickMouse(int, int)
     * @see #getDefaultMouseButton()
     */
    public void clickMouse(int clickCount) {
	clickMouse(clickCount, getDefaultMouseButton());
    }

    /**
     * Makes siple mouse click on the component center by default mouse button with 0 modifiers.
     * @see #clickMouse(int)
     * @see #getDefaultMouseButton()
     */
    public void clickMouse() {
	clickMouse(1);
    }

    /**
     * Move mouse inside the component.
     */
    public void enterMouse() {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotMoveMouse(getCenterXForClick(), getCenterYForClick());
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_ENTERED, 0, 0, getCenterXForClick(), getCenterYForClick(), false);
	}
    }

    /**
     * Move mouse outside the component.
     */
    public void exitMouse() {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotMoveMouse(-1, -1);
	} else {
	    getEventDispatcher().dispatchMouseEvent(MouseEvent.MOUSE_EXITED, 0, 0, getCenterXForClick(), getCenterYForClick(), false);
	}
    }

    /**
     * Press mouse.
     */
    public void pressMouse() {
	pressMouse(getCenterXForClick(), getCenterYForClick());
    }

    /**
     * Releases mouse.
     */
    public void releaseMouse() {
	releaseMouse(getCenterXForClick(), getCenterYForClick());
    }

    /** 
     * Clicks for popup at the component center.
     * @param mouseButton Mouse button.
     * @see #clickForPopup(int, int)
     */
    public void clickForPopup(int mouseButton) {
	clickForPopup(getCenterXForClick(), getCenterYForClick(), mouseButton);
    }

    /** 
     * Clicks for popup by popup mouse button at the component center.
     * @param mouseButton Mouse button.
     * @see #clickForPopup(int)
     * @see #getPopupMouseButton()
     */
    public void clickForPopup() {
	clickForPopup(getPopupMouseButton());
    }

    ////////////////////////////////////////////////////////
    //Keyboard operations
    ////////////////////////////////////////////////////////

    /**
     * Press key.
     * @param keyCode Key code (KeyEvent.VK_* value)
     * @param modifiers Modifiers (combination of InputEvent.*_MASK fields)
     */
    public void pressKey(int keyCode, int modifiers) {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotPressKey(keyCode, modifiers);
	} else {
	    getEventDispatcher().dispatchKeyEvent(KeyEvent.KEY_PRESSED , modifiers, keyCode);
	}
    }

    /**
     * Press key with no modifiers.
     * @param keyCode Key code (KeyEvent.VK_* value)
     */
    public void pressKey(int keyCode) {
	pressKey(keyCode, 0);
    }

    /**
     * Releases key.
     * @param keyCode Key code (KeyEvent.VK_* value)
     * @param modifiers Modifiers (combination of InputEvent.*_MASK fields)
     */
    public void releaseKey(int keyCode, int modifiers) {
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	    getEventDispatcher().robotReleaseKey(keyCode, modifiers);
	} else {
	    getEventDispatcher().dispatchKeyEvent(KeyEvent.KEY_RELEASED, modifiers, keyCode);
	}
    }

    /**
     * Releases key with no modifiers.
     * @param keyCode Key code (KeyEvent.VK_* value)
     */
    public void releaseKey(int keyCode) {
	releaseKey(keyCode, 0);
    }

    /**
     * Pushs key.
     * @param keyCode Key code (KeyEvent.VK_* value)
     * @param modifiers Modifiers (combination of InputEvent.*_MASK fields)
     */
    public void pushKey(int keyCode, int modifiers) {
	pressKey(keyCode, modifiers);
	timeouts.sleep("ComponentOperator.PushKeyTimeout");
	releaseKey(keyCode, modifiers);
    }

    /**
     * Pushs key.
     * @param keyCode Key code (KeyEvent.VK_* value)
     */
    public void pushKey(int keyCode) {
	pushKey(keyCode, 0);
    }

    /**
     * Types one char.
     * @param keyCode Key code (KeyEvent.VK_* value)
     * @param keyChar Char to be typed.
     * @param modifiers Modifiers (combination of InputEvent.*_MASK fields)
     */
    public void typeKey(int keyCode, char keyChar, int modifiers) {
	pressKey(keyCode, modifiers);
	timeouts.sleep("ComponentOperator.PushKeyTimeout");
	if((getDispatchingModel() & JemmyProperties.ROBOT_MODEL_MASK) != 0) {
	} else {
	    getEventDispatcher().dispatchKeyEvent(KeyEvent.KEY_TYPED   , modifiers, KeyEvent.VK_UNDEFINED, keyChar);
	}
	releaseKey(keyCode, modifiers);
    }

    /**
     * Types one char.
     * Uses map defined by setCharBindingMap(CharBindingMap) method to find a key should be pressed.
     * @param keyChar Char to be typed.
     * @param modifiers Modifiers (combination of InputEvent.*_MASK fields)
     * @see org.netbeans.jemmy.CharBindingMap
     * @see #setCharBindingMap(CharBindingMap)
     * @see #typeKey(int, char, int)
     */
    public void typeKey(char keyChar, int modifiers) {
	typeKey(getCharKey(keyChar), keyChar, modifiers | getCharModifiers(keyChar));
    }

    /**
     * Types one char.
     * Uses map defined by setCharBindingMap(CharBindingMap) method 
     * to find a key and modifiers should be pressed.
     * @param keyChar Char to be typed.
     * @param modifiers Modifiers (combination of InputEvent.*_MASK fields)
     * @see #setCharBindingMap(CharBindingMap)
     * @see #typeKey(char, int)
     */
    public void typeKey(char keyChar) {
	typeKey(keyChar, 0);
    }

    ////////////////////////////////////////////////////////
    //Util
    ////////////////////////////////////////////////////////

    /**
     * @deprecated Use makeComponentVisible() instead.
     * @see #makeComponentVisible()
     */
    public void activateWindow() {
	getVisualizer().makeVisible((ComponentOperator)this);
    }

    /**
     * Prepares component for user input.
     * Uses visualizer defined by setVisualiser() method.
     */
    public void makeComponentVisible() {
	getVisualizer().makeVisible((ComponentOperator)this);
    }

    /**
     * Return the center x coordinate.
     */
    public int getCenterX() {
	return(getWidth() / 2);
    }

    /**
     * Return the center y coordinate.
     */
    public int getCenterY() {
	return(getHeight() / 2);
    }

    /**
     * Return the x coordinate which should be used
     * for mouse operations by default.
     */
    public int getCenterXForClick() {
	return(getCenterX());
    }

    /**
     * Return the y coordinate which should be used
     * for mouse operations by default.
     */
    public int getCenterYForClick() {
	return(getCenterY());
    }

    /**
     * Waits for component enabled.
     * @throws TimeoutExpiredException
     */
    public void waitComponentEnabled() throws InterruptedException{
	Waiter waiter = new Waiter(new Waitable() {
	    public Object actionProduced(Object obj) {
		if(((Component)obj).isEnabled()) {
		    return(obj);
		} else {
		    return(null);
		}
	    }
	    public String getDescription() {
		return("Component enabled: " + 
		       getSource().getClass().toString());
	    }
	});
	waiter.setOutput(output);
	Timeouts times = timeouts.cloneThis();
	times.setTimeout("Waiter.WaitingTime",
			 times.getTimeout("ComponentOperator.WaitComponentEnabledTimeout"));
	waiter.setTimeouts(times);
	waiter.waitAction(getSource());
    }

    /**
     * Returns an array of containers for this component.
     */
    public Container[] getContainers() {
	int counter = 0;
	Container cont = getSource().getParent();
	if(cont == null) {
	    return(new Container[0]);
	}
	do {
	    counter++;
	} while((cont = cont.getParent()) != null);
	Container[] res = new Container[counter];
	cont = getSource().getParent();
	counter = 0;
	do {
	    counter++;
	    res[counter - 1] = cont;
	} while((cont = cont.getParent()) != null);
	return(res);
    }

    /**
     * Searches a container.
     */
    public Container getContainer(ComponentChooser chooser) {
	Container[] conts = getContainers();
	for(int i = 0; i < conts.length; i++) {
	    if(chooser.checkComponent(conts[i])) {
		return(conts[i]);
	    }
	}
	return(null);
    }

    /**
     * Searches the window under component.
     */
    public Window getWindow() {
	Window comp = (Window)getContainer(new ComponentChooser() {
	    public boolean checkComponent(Component comp) {
		return(comp instanceof Window);
	    }
	    public String getDescription() {
		return("");
	    }
	});
	if(comp == null && getSource() instanceof Window) {
	    return((Window)getSource());
	} else {
	    return(comp);
	}
    }

    /**
     * Waits for this Component has the keyboard focus.
     * @throws TimeoutExpiredException
     */
    public void waitHasFocus() {
	Timeouts times = timeouts.cloneThis();
	times.setTimeout("Waiter.WaitingTime", 
			 times.getTimeout("ComponentOperator.WaitFocusTimeout"));
	Waiter focusWaiter = new Waiter(new Waitable() {
	    public Object actionProduced(Object obj) {
		return(hasFocus() ? "" : null);
	    }
	    public String getDescription() {
		return("Wait component has focus");
	    }
	});
	focusWaiter.setTimeouts(times);
	focusWaiter.setOutput(output.createErrorOutput());
	try {
	    focusWaiter.waitAction(null);
	} catch(InterruptedException e) {
	    output.printStackTrace(e);
	}
    }

    /**
     * Returns information about component.
     */
    public Hashtable getDump() {
	Hashtable result = super.getDump();
	result.put("Visible", new Boolean(getSource().isVisible()).toString());
	result.put("Showing", new Boolean(getSource().isShowing()).toString());
	result.put("X", Integer.toString(getSource().getX()));
	result.put("Y", Integer.toString(getSource().getY()));
	result.put("Width", Integer.toString(getSource().getWidth()));
	result.put("Height", Integer.toString(getSource().getHeight()));
	return(result);
    }

    ////////////////////////////////////////////////////////
    //Mapping                                             //

    /**Maps <code>Component.add(PopupMenu)</code> through queue*/
    public void add(final PopupMenu popupMenu) {
	runMapping(new MapVoidAction("add") {
		public void map() {
		    ((Component)getSource()).add(popupMenu);
		}});}

    /**Maps <code>Component.addComponentListener(ComponentListener)</code> through queue*/
    public void addComponentListener(final ComponentListener componentListener) {
	runMapping(new MapVoidAction("addComponentListener") {
		public void map() {
		    ((Component)getSource()).addComponentListener(componentListener);
		}});}

    /**Maps <code>Component.addFocusListener(FocusListener)</code> through queue*/
    public void addFocusListener(final FocusListener focusListener) {
	runMapping(new MapVoidAction("addFocusListener") {
		public void map() {
		    ((Component)getSource()).addFocusListener(focusListener);
		}});}

    /**Maps <code>Component.addInputMethodListener(InputMethodListener)</code> through queue*/
    public void addInputMethodListener(final InputMethodListener inputMethodListener) {
	runMapping(new MapVoidAction("addInputMethodListener") {
		public void map() {
		    ((Component)getSource()).addInputMethodListener(inputMethodListener);
		}});}

    /**Maps <code>Component.addKeyListener(KeyListener)</code> through queue*/
    public void addKeyListener(final KeyListener keyListener) {
	runMapping(new MapVoidAction("addKeyListener") {
		public void map() {
		    ((Component)getSource()).addKeyListener(keyListener);
		}});}

    /**Maps <code>Component.addMouseListener(MouseListener)</code> through queue*/
    public void addMouseListener(final MouseListener mouseListener) {
	runMapping(new MapVoidAction("addMouseListener") {
		public void map() {
		    ((Component)getSource()).addMouseListener(mouseListener);
		}});}

    /**Maps <code>Component.addMouseMotionListener(MouseMotionListener)</code> through queue*/
    public void addMouseMotionListener(final MouseMotionListener mouseMotionListener) {
	runMapping(new MapVoidAction("addMouseMotionListener") {
		public void map() {
		    ((Component)getSource()).addMouseMotionListener(mouseMotionListener);
		}});}

    /**Maps <code>Component.addNotify()</code> through queue*/
    public void addNotify() {
	runMapping(new MapVoidAction("addNotify") {
		public void map() {
		    ((Component)getSource()).addNotify();
		}});}

    /**Maps <code>Component.addPropertyChangeListener(PropertyChangeListener)</code> through queue*/
    public void addPropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
	runMapping(new MapVoidAction("addPropertyChangeListener") {
		public void map() {
		    ((Component)getSource()).addPropertyChangeListener(propertyChangeListener);
		}});}

    /**Maps <code>Component.addPropertyChangeListener(String, PropertyChangeListener)</code> through queue*/
    public void addPropertyChangeListener(final String string, final PropertyChangeListener propertyChangeListener) {
	runMapping(new MapVoidAction("addPropertyChangeListener") {
		public void map() {
		    ((Component)getSource()).addPropertyChangeListener(string, propertyChangeListener);
		}});}

    /**Maps <code>Component.checkImage(Image, int, int, ImageObserver)</code> through queue*/
    public int checkImage(final Image image, final int i, final int i1, final ImageObserver imageObserver) {
	return(runMapping(new MapIntegerAction("checkImage") {
		public int map() {
		    return(((Component)getSource()).checkImage(image, i, i1, imageObserver));
		}}));}

    /**Maps <code>Component.checkImage(Image, ImageObserver)</code> through queue*/
    public int checkImage(final Image image, final ImageObserver imageObserver) {
	return(runMapping(new MapIntegerAction("checkImage") {
		public int map() {
		    return(((Component)getSource()).checkImage(image, imageObserver));
		}}));}

    /**Maps <code>Component.contains(int, int)</code> through queue*/
    public boolean contains(final int i, final int i1) {
	return(runMapping(new MapBooleanAction("contains") {
		public boolean map() {
		    return(((Component)getSource()).contains(i, i1));
		}}));}

    /**Maps <code>Component.contains(Point)</code> through queue*/
    public boolean contains(final Point point) {
	return(runMapping(new MapBooleanAction("contains") {
		public boolean map() {
		    return(((Component)getSource()).contains(point));
		}}));}

    /**Maps <code>Component.createImage(int, int)</code> through queue*/
    public Image createImage(final int i, final int i1) {
	return((Image)runMapping(new MapAction("createImage") {
		public Object map() {
		    return(((Component)getSource()).createImage(i, i1));
		}}));}

    /**Maps <code>Component.createImage(ImageProducer)</code> through queue*/
    public Image createImage(final ImageProducer imageProducer) {
	return((Image)runMapping(new MapAction("createImage") {
		public Object map() {
		    return(((Component)getSource()).createImage(imageProducer));
		}}));}

    /**Maps <code>Component.dispatchEvent(AWTEvent)</code> through queue*/
    public void dispatchEvent(final AWTEvent aWTEvent) {
	runMapping(new MapVoidAction("dispatchEvent") {
		public void map() {
		    ((Component)getSource()).dispatchEvent(aWTEvent);
		}});}

    /**Maps <code>Component.doLayout()</code> through queue*/
    public void doLayout() {
	runMapping(new MapVoidAction("doLayout") {
		public void map() {
		    ((Component)getSource()).doLayout();
		}});}

    /**Maps <code>Component.enableInputMethods(boolean)</code> through queue*/
    public void enableInputMethods(final boolean b) {
	runMapping(new MapVoidAction("enableInputMethods") {
		public void map() {
		    ((Component)getSource()).enableInputMethods(b);
		}});}

    /**Maps <code>Component.getAlignmentX()</code> through queue*/
    public float getAlignmentX() {
	return(runMapping(new MapFloatAction("getAlignmentX") {
		public float map() {
		    return(((Component)getSource()).getAlignmentX());
		}}));}

    /**Maps <code>Component.getAlignmentY()</code> through queue*/
    public float getAlignmentY() {
	return(runMapping(new MapFloatAction("getAlignmentY") {
		public float map() {
		    return(((Component)getSource()).getAlignmentY());
		}}));}

    /**Maps <code>Component.getBackground()</code> through queue*/
    public Color getBackground() {
	return((Color)runMapping(new MapAction("getBackground") {
		public Object map() {
		    return(((Component)getSource()).getBackground());
		}}));}

    /**Maps <code>Component.getBounds()</code> through queue*/
    public Rectangle getBounds() {
	return((Rectangle)runMapping(new MapAction("getBounds") {
		public Object map() {
		    return(((Component)getSource()).getBounds());
		}}));}

    /**Maps <code>Component.getBounds(Rectangle)</code> through queue*/
    public Rectangle getBounds(final Rectangle rectangle) {
	return((Rectangle)runMapping(new MapAction("getBounds") {
		public Object map() {
		    return(((Component)getSource()).getBounds(rectangle));
		}}));}

    /**Maps <code>Component.getColorModel()</code> through queue*/
    public ColorModel getColorModel() {
	return((ColorModel)runMapping(new MapAction("getColorModel") {
		public Object map() {
		    return(((Component)getSource()).getColorModel());
		}}));}

    /**Maps <code>Component.getComponentAt(int, int)</code> through queue*/
    public Component getComponentAt(final int i, final int i1) {
	return((Component)runMapping(new MapAction("getComponentAt") {
		public Object map() {
		    return(((Component)getSource()).getComponentAt(i, i1));
		}}));}

    /**Maps <code>Component.getComponentAt(Point)</code> through queue*/
    public Component getComponentAt(final Point point) {
	return((Component)runMapping(new MapAction("getComponentAt") {
		public Object map() {
		    return(((Component)getSource()).getComponentAt(point));
		}}));}

    /**Maps <code>Component.getComponentOrientation()</code> through queue*/
    public ComponentOrientation getComponentOrientation() {
	return((ComponentOrientation)runMapping(new MapAction("getComponentOrientation") {
		public Object map() {
		    return(((Component)getSource()).getComponentOrientation());
		}}));}

    /**Maps <code>Component.getCursor()</code> through queue*/
    public Cursor getCursor() {
	return((Cursor)runMapping(new MapAction("getCursor") {
		public Object map() {
		    return(((Component)getSource()).getCursor());
		}}));}

    /**Maps <code>Component.getDropTarget()</code> through queue*/
    public DropTarget getDropTarget() {
	return((DropTarget)runMapping(new MapAction("getDropTarget") {
		public Object map() {
		    return(((Component)getSource()).getDropTarget());
		}}));}

    /**Maps <code>Component.getFont()</code> through queue*/
    public Font getFont() {
	return((Font)runMapping(new MapAction("getFont") {
		public Object map() {
		    return(((Component)getSource()).getFont());
		}}));}

    /**Maps <code>Component.getFontMetrics(Font)</code> through queue*/
    public FontMetrics getFontMetrics(final Font font) {
	return((FontMetrics)runMapping(new MapAction("getFontMetrics") {
		public Object map() {
		    return(((Component)getSource()).getFontMetrics(font));
		}}));}

    /**Maps <code>Component.getForeground()</code> through queue*/
    public Color getForeground() {
	return((Color)runMapping(new MapAction("getForeground") {
		public Object map() {
		    return(((Component)getSource()).getForeground());
		}}));}

    /**Maps <code>Component.getGraphics()</code> through queue*/
    public Graphics getGraphics() {
	return((Graphics)runMapping(new MapAction("getGraphics") {
		public Object map() {
		    return(((Component)getSource()).getGraphics());
		}}));}

    /**Maps <code>Component.getHeight()</code> through queue*/
    public int getHeight() {
	return(runMapping(new MapIntegerAction("getHeight") {
		public int map() {
		    return(((Component)getSource()).getHeight());
		}}));}

    /**Maps <code>Component.getInputContext()</code> through queue*/
    public InputContext getInputContext() {
	return((InputContext)runMapping(new MapAction("getInputContext") {
		public Object map() {
		    return(((Component)getSource()).getInputContext());
		}}));}

    /**Maps <code>Component.getInputMethodRequests()</code> through queue*/
    public InputMethodRequests getInputMethodRequests() {
	return((InputMethodRequests)runMapping(new MapAction("getInputMethodRequests") {
		public Object map() {
		    return(((Component)getSource()).getInputMethodRequests());
		}}));}

    /**Maps <code>Component.getLocale()</code> through queue*/
    public Locale getLocale() {
	return((Locale)runMapping(new MapAction("getLocale") {
		public Object map() {
		    return(((Component)getSource()).getLocale());
		}}));}

    /**Maps <code>Component.getLocation()</code> through queue*/
    public Point getLocation() {
	return((Point)runMapping(new MapAction("getLocation") {
		public Object map() {
		    return(((Component)getSource()).getLocation());
		}}));}

    /**Maps <code>Component.getLocation(Point)</code> through queue*/
    public Point getLocation(final Point point) {
	return((Point)runMapping(new MapAction("getLocation") {
		public Object map() {
		    return(((Component)getSource()).getLocation(point));
		}}));}

    /**Maps <code>Component.getLocationOnScreen()</code> through queue*/
    public Point getLocationOnScreen() {
	return((Point)runMapping(new MapAction("getLocationOnScreen") {
		public Object map() {
		    return(((Component)getSource()).getLocationOnScreen());
		}}));}

    /**Maps <code>Component.getMaximumSize()</code> through queue*/
    public Dimension getMaximumSize() {
	return((Dimension)runMapping(new MapAction("getMaximumSize") {
		public Object map() {
		    return(((Component)getSource()).getMaximumSize());
		}}));}

    /**Maps <code>Component.getMinimumSize()</code> through queue*/
    public Dimension getMinimumSize() {
	return((Dimension)runMapping(new MapAction("getMinimumSize") {
		public Object map() {
		    return(((Component)getSource()).getMinimumSize());
		}}));}

    /**Maps <code>Component.getName()</code> through queue*/
    public String getName() {
	return((String)runMapping(new MapAction("getName") {
		public Object map() {
		    return(((Component)getSource()).getName());
		}}));}

    /**Maps <code>Component.getParent()</code> through queue*/
    public Container getParent() {
	return((Container)runMapping(new MapAction("getParent") {
		public Object map() {
		    return(((Component)getSource()).getParent());
		}}));}

    /**Maps <code>Component.getPreferredSize()</code> through queue*/
    public Dimension getPreferredSize() {
	return((Dimension)runMapping(new MapAction("getPreferredSize") {
		public Object map() {
		    return(((Component)getSource()).getPreferredSize());
		}}));}

    /**Maps <code>Component.getSize()</code> through queue*/
    public Dimension getSize() {
	return((Dimension)runMapping(new MapAction("getSize") {
		public Object map() {
		    return(((Component)getSource()).getSize());
		}}));}

    /**Maps <code>Component.getSize(Dimension)</code> through queue*/
    public Dimension getSize(final Dimension dimension) {
	return((Dimension)runMapping(new MapAction("getSize") {
		public Object map() {
		    return(((Component)getSource()).getSize(dimension));
		}}));}

    /**Maps <code>Component.getToolkit()</code> through queue*/
    public Toolkit getToolkit() {
	return((Toolkit)runMapping(new MapAction("getToolkit") {
		public Object map() {
		    return(((Component)getSource()).getToolkit());
		}}));}

    /**Maps <code>Component.getTreeLock()</code> through queue*/
    public Object getTreeLock() {
	return((Object)runMapping(new MapAction("getTreeLock") {
		public Object map() {
		    return(((Component)getSource()).getTreeLock());
		}}));}

    /**Maps <code>Component.getWidth()</code> through queue*/
    public int getWidth() {
	return(runMapping(new MapIntegerAction("getWidth") {
		public int map() {
		    return(((Component)getSource()).getWidth());
		}}));}

    /**Maps <code>Component.getX()</code> through queue*/
    public int getX() {
	return(runMapping(new MapIntegerAction("getX") {
		public int map() {
		    return(((Component)getSource()).getX());
		}}));}

    /**Maps <code>Component.getY()</code> through queue*/
    public int getY() {
	return(runMapping(new MapIntegerAction("getY") {
		public int map() {
		    return(((Component)getSource()).getY());
		}}));}

    /**Maps <code>Component.hasFocus()</code> through queue*/
    public boolean hasFocus() {
	return(runMapping(new MapBooleanAction("hasFocus") {
		public boolean map() {
		    return(((Component)getSource()).hasFocus());
		}}));}

    /**Maps <code>Component.imageUpdate(Image, int, int, int, int, int)</code> through queue*/
    public boolean imageUpdate(final Image image, final int i, final int i1, final int i2, final int i3, final int i4) {
	return(runMapping(new MapBooleanAction("imageUpdate") {
		public boolean map() {
		    return(((Component)getSource()).imageUpdate(image, i, i1, i2, i3, i4));
		}}));}

    /**Maps <code>Component.invalidate()</code> through queue*/
    public void invalidate() {
	runMapping(new MapVoidAction("invalidate") {
		public void map() {
		    ((Component)getSource()).invalidate();
		}});}

    /**Maps <code>Component.isDisplayable()</code> through queue*/
    public boolean isDisplayable() {
	return(runMapping(new MapBooleanAction("isDisplayable") {
		public boolean map() {
		    return(((Component)getSource()).isDisplayable());
		}}));}

    /**Maps <code>Component.isDoubleBuffered()</code> through queue*/
    public boolean isDoubleBuffered() {
	return(runMapping(new MapBooleanAction("isDoubleBuffered") {
		public boolean map() {
		    return(((Component)getSource()).isDoubleBuffered());
		}}));}

    /**Maps <code>Component.isEnabled()</code> through queue*/
    public boolean isEnabled() {
	return(runMapping(new MapBooleanAction("isEnabled") {
		public boolean map() {
		    return(((Component)getSource()).isEnabled());
		}}));}

    /**Maps <code>Component.isFocusTraversable()</code> through queue*/
    public boolean isFocusTraversable() {
	return(runMapping(new MapBooleanAction("isFocusTraversable") {
		public boolean map() {
		    return(((Component)getSource()).isFocusTraversable());
		}}));}

    /**Maps <code>Component.isLightweight()</code> through queue*/
    public boolean isLightweight() {
	return(runMapping(new MapBooleanAction("isLightweight") {
		public boolean map() {
		    return(((Component)getSource()).isLightweight());
		}}));}

    /**Maps <code>Component.isOpaque()</code> through queue*/
    public boolean isOpaque() {
	return(runMapping(new MapBooleanAction("isOpaque") {
		public boolean map() {
		    return(((Component)getSource()).isOpaque());
		}}));}

    /**Maps <code>Component.isShowing()</code> through queue*/
    public boolean isShowing() {
	return(runMapping(new MapBooleanAction("isShowing") {
		public boolean map() {
		    return(((Component)getSource()).isShowing());
		}}));}

    /**Maps <code>Component.isValid()</code> through queue*/
    public boolean isValid() {
	return(runMapping(new MapBooleanAction("isValid") {
		public boolean map() {
		    return(((Component)getSource()).isValid());
		}}));}

    /**Maps <code>Component.isVisible()</code> through queue*/
    public boolean isVisible() {
	return(runMapping(new MapBooleanAction("isVisible") {
		public boolean map() {
		    return(((Component)getSource()).isVisible());
		}}));}

    /**Maps <code>Component.list()</code> through queue*/
    public void list() {
	runMapping(new MapVoidAction("list") {
		public void map() {
		    ((Component)getSource()).list();
		}});}

    /**Maps <code>Component.list(PrintStream)</code> through queue*/
    public void list(final PrintStream printStream) {
	runMapping(new MapVoidAction("list") {
		public void map() {
		    ((Component)getSource()).list(printStream);
		}});}

    /**Maps <code>Component.list(PrintStream, int)</code> through queue*/
    public void list(final PrintStream printStream, final int i) {
	runMapping(new MapVoidAction("list") {
		public void map() {
		    ((Component)getSource()).list(printStream, i);
		}});}

    /**Maps <code>Component.list(PrintWriter)</code> through queue*/
    public void list(final PrintWriter printWriter) {
	runMapping(new MapVoidAction("list") {
		public void map() {
		    ((Component)getSource()).list(printWriter);
		}});}

    /**Maps <code>Component.list(PrintWriter, int)</code> through queue*/
    public void list(final PrintWriter printWriter, final int i) {
	runMapping(new MapVoidAction("list") {
		public void map() {
		    ((Component)getSource()).list(printWriter, i);
		}});}

    /**Maps <code>Component.paint(Graphics)</code> through queue*/
    public void paint(final Graphics graphics) {
	runMapping(new MapVoidAction("paint") {
		public void map() {
		    ((Component)getSource()).paint(graphics);
		}});}

    /**Maps <code>Component.paintAll(Graphics)</code> through queue*/
    public void paintAll(final Graphics graphics) {
	runMapping(new MapVoidAction("paintAll") {
		public void map() {
		    ((Component)getSource()).paintAll(graphics);
		}});}

    /**Maps <code>Component.prepareImage(Image, int, int, ImageObserver)</code> through queue*/
    public boolean prepareImage(final Image image, final int i, final int i1, final ImageObserver imageObserver) {
	return(runMapping(new MapBooleanAction("prepareImage") {
		public boolean map() {
		    return(((Component)getSource()).prepareImage(image, i, i1, imageObserver));
		}}));}

    /**Maps <code>Component.prepareImage(Image, ImageObserver)</code> through queue*/
    public boolean prepareImage(final Image image, final ImageObserver imageObserver) {
	return(runMapping(new MapBooleanAction("prepareImage") {
		public boolean map() {
		    return(((Component)getSource()).prepareImage(image, imageObserver));
		}}));}

    /**Maps <code>Component.print(Graphics)</code> through queue*/
    public void print(final Graphics graphics) {
	runMapping(new MapVoidAction("print") {
		public void map() {
		    ((Component)getSource()).print(graphics);
		}});}

    /**Maps <code>Component.printAll(Graphics)</code> through queue*/
    public void printAll(final Graphics graphics) {
	runMapping(new MapVoidAction("printAll") {
		public void map() {
		    ((Component)getSource()).printAll(graphics);
		}});}

    /**Maps <code>Component.remove(MenuComponent)</code> through queue*/
    public void remove(final MenuComponent menuComponent) {
	runMapping(new MapVoidAction("remove") {
		public void map() {
		    ((Component)getSource()).remove(menuComponent);
		}});}

    /**Maps <code>Component.removeComponentListener(ComponentListener)</code> through queue*/
    public void removeComponentListener(final ComponentListener componentListener) {
	runMapping(new MapVoidAction("removeComponentListener") {
		public void map() {
		    ((Component)getSource()).removeComponentListener(componentListener);
		}});}

    /**Maps <code>Component.removeFocusListener(FocusListener)</code> through queue*/
    public void removeFocusListener(final FocusListener focusListener) {
	runMapping(new MapVoidAction("removeFocusListener") {
		public void map() {
		    ((Component)getSource()).removeFocusListener(focusListener);
		}});}

    /**Maps <code>Component.removeInputMethodListener(InputMethodListener)</code> through queue*/
    public void removeInputMethodListener(final InputMethodListener inputMethodListener) {
	runMapping(new MapVoidAction("removeInputMethodListener") {
		public void map() {
		    ((Component)getSource()).removeInputMethodListener(inputMethodListener);
		}});}

    /**Maps <code>Component.removeKeyListener(KeyListener)</code> through queue*/
    public void removeKeyListener(final KeyListener keyListener) {
	runMapping(new MapVoidAction("removeKeyListener") {
		public void map() {
		    ((Component)getSource()).removeKeyListener(keyListener);
		}});}

    /**Maps <code>Component.removeMouseListener(MouseListener)</code> through queue*/
    public void removeMouseListener(final MouseListener mouseListener) {
	runMapping(new MapVoidAction("removeMouseListener") {
		public void map() {
		    ((Component)getSource()).removeMouseListener(mouseListener);
		}});}

    /**Maps <code>Component.removeMouseMotionListener(MouseMotionListener)</code> through queue*/
    public void removeMouseMotionListener(final MouseMotionListener mouseMotionListener) {
	runMapping(new MapVoidAction("removeMouseMotionListener") {
		public void map() {
		    ((Component)getSource()).removeMouseMotionListener(mouseMotionListener);
		}});}

    /**Maps <code>Component.removeNotify()</code> through queue*/
    public void removeNotify() {
	runMapping(new MapVoidAction("removeNotify") {
		public void map() {
		    ((Component)getSource()).removeNotify();
		}});}

    /**Maps <code>Component.removePropertyChangeListener(PropertyChangeListener)</code> through queue*/
    public void removePropertyChangeListener(final PropertyChangeListener propertyChangeListener) {
	runMapping(new MapVoidAction("removePropertyChangeListener") {
		public void map() {
		    ((Component)getSource()).removePropertyChangeListener(propertyChangeListener);
		}});}

    /**Maps <code>Component.removePropertyChangeListener(String, PropertyChangeListener)</code> through queue*/
    public void removePropertyChangeListener(final String string, final PropertyChangeListener propertyChangeListener) {
	runMapping(new MapVoidAction("removePropertyChangeListener") {
		public void map() {
		    ((Component)getSource()).removePropertyChangeListener(string, propertyChangeListener);
		}});}

    /**Maps <code>Component.repaint()</code> through queue*/
    public void repaint() {
	runMapping(new MapVoidAction("repaint") {
		public void map() {
		    ((Component)getSource()).repaint();
		}});}

    /**Maps <code>Component.repaint(int, int, int, int)</code> through queue*/
    public void repaint(final int i, final int i1, final int i2, final int i3) {
	runMapping(new MapVoidAction("repaint") {
		public void map() {
		    ((Component)getSource()).repaint(i, i1, i2, i3);
		}});}

    /**Maps <code>Component.repaint(long)</code> through queue*/
    public void repaint(final long l) {
	runMapping(new MapVoidAction("repaint") {
		public void map() {
		    ((Component)getSource()).repaint(l);
		}});}

    /**Maps <code>Component.repaint(long, int, int, int, int)</code> through queue*/
    public void repaint(final long l, final int i, final int i1, final int i2, final int i3) {
	runMapping(new MapVoidAction("repaint") {
		public void map() {
		    ((Component)getSource()).repaint(l, i, i1, i2, i3);
		}});}

    /**Maps <code>Component.requestFocus()</code> through queue*/
    public void requestFocus() {
	runMapping(new MapVoidAction("requestFocus") {
		public void map() {
		    ((Component)getSource()).requestFocus();
		}});}

    /**Maps <code>Component.setBackground(Color)</code> through queue*/
    public void setBackground(final Color color) {
	runMapping(new MapVoidAction("setBackground") {
		public void map() {
		    ((Component)getSource()).setBackground(color);
		}});}

    /**Maps <code>Component.setBounds(int, int, int, int)</code> through queue*/
    public void setBounds(final int i, final int i1, final int i2, final int i3) {
	runMapping(new MapVoidAction("setBounds") {
		public void map() {
		    ((Component)getSource()).setBounds(i, i1, i2, i3);
		}});}

    /**Maps <code>Component.setBounds(Rectangle)</code> through queue*/
    public void setBounds(final Rectangle rectangle) {
	runMapping(new MapVoidAction("setBounds") {
		public void map() {
		    ((Component)getSource()).setBounds(rectangle);
		}});}

    /**Maps <code>Component.setComponentOrientation(ComponentOrientation)</code> through queue*/
    public void setComponentOrientation(final ComponentOrientation componentOrientation) {
	runMapping(new MapVoidAction("setComponentOrientation") {
		public void map() {
		    ((Component)getSource()).setComponentOrientation(componentOrientation);
		}});}

    /**Maps <code>Component.setCursor(Cursor)</code> through queue*/
    public void setCursor(final Cursor cursor) {
	runMapping(new MapVoidAction("setCursor") {
		public void map() {
		    ((Component)getSource()).setCursor(cursor);
		}});}

    /**Maps <code>Component.setDropTarget(DropTarget)</code> through queue*/
    public void setDropTarget(final DropTarget dropTarget) {
	runMapping(new MapVoidAction("setDropTarget") {
		public void map() {
		    ((Component)getSource()).setDropTarget(dropTarget);
		}});}

    /**Maps <code>Component.setEnabled(boolean)</code> through queue*/
    public void setEnabled(final boolean b) {
	runMapping(new MapVoidAction("setEnabled") {
		public void map() {
		    ((Component)getSource()).setEnabled(b);
		}});}

    /**Maps <code>Component.setFont(Font)</code> through queue*/
    public void setFont(final Font font) {
	runMapping(new MapVoidAction("setFont") {
		public void map() {
		    ((Component)getSource()).setFont(font);
		}});}

    /**Maps <code>Component.setForeground(Color)</code> through queue*/
    public void setForeground(final Color color) {
	runMapping(new MapVoidAction("setForeground") {
		public void map() {
		    ((Component)getSource()).setForeground(color);
		}});}

    /**Maps <code>Component.setLocale(Locale)</code> through queue*/
    public void setLocale(final Locale locale) {
	runMapping(new MapVoidAction("setLocale") {
		public void map() {
		    ((Component)getSource()).setLocale(locale);
		}});}

    /**Maps <code>Component.setLocation(int, int)</code> through queue*/
    public void setLocation(final int i, final int i1) {
	runMapping(new MapVoidAction("setLocation") {
		public void map() {
		    ((Component)getSource()).setLocation(i, i1);
		}});}

    /**Maps <code>Component.setLocation(Point)</code> through queue*/
    public void setLocation(final Point point) {
	runMapping(new MapVoidAction("setLocation") {
		public void map() {
		    ((Component)getSource()).setLocation(point);
		}});}

    /**Maps <code>Component.setName(String)</code> through queue*/
    public void setName(final String string) {
	runMapping(new MapVoidAction("setName") {
		public void map() {
		    ((Component)getSource()).setName(string);
		}});}

    /**Maps <code>Component.setSize(int, int)</code> through queue*/
    public void setSize(final int i, final int i1) {
	runMapping(new MapVoidAction("setSize") {
		public void map() {
		    ((Component)getSource()).setSize(i, i1);
		}});}

    /**Maps <code>Component.setSize(Dimension)</code> through queue*/
    public void setSize(final Dimension dimension) {
	runMapping(new MapVoidAction("setSize") {
		public void map() {
		    ((Component)getSource()).setSize(dimension);
		}});}

    /**Maps <code>Component.setVisible(boolean)</code> through queue*/
    public void setVisible(final boolean b) {
	runMapping(new MapVoidAction("setVisible") {
		public void map() {
		    ((Component)getSource()).setVisible(b);
		}});}

    /**Maps <code>Component.transferFocus()</code> through queue*/
    public void transferFocus() {
	runMapping(new MapVoidAction("transferFocus") {
		public void map() {
		    ((Component)getSource()).transferFocus();
		}});}

    /**Maps <code>Component.update(Graphics)</code> through queue*/
    public void update(final Graphics graphics) {
	runMapping(new MapVoidAction("update") {
		public void map() {
		    ((Component)getSource()).update(graphics);
		}});}

    /**Maps <code>Component.validate()</code> through queue*/
    public void validate() {
	runMapping(new MapVoidAction("validate") {
		public void map() {
		    ((Component)getSource()).validate();
		}});}

    //End of mapping                                      //
    ////////////////////////////////////////////////////////

    private void setEventDispatcher(EventDispatcher dispatcher) {
	dispatcher.setOutput(getOutput().createErrorOutput());
	dispatcher.setTimeouts(getTimeouts());
	dispatcher.setDispatchingModel(getDispatchingModel());
	this.dispatcher = dispatcher;
    }

    static class VisibleComponentFinder implements ComponentChooser {
	ComponentChooser subFinder;
	public VisibleComponentFinder(ComponentChooser sf) {
	    subFinder = sf;
	}
	public boolean checkComponent(Component comp) {
	    if(comp.isShowing()) {
		return(subFinder.checkComponent(comp));
	    }
	    return(false);
	}
	public String getDescription() {
	    return(subFinder.getDescription());
	}
    }

}

