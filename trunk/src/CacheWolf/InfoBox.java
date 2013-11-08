/*
GNU General Public License
CacheWolf is a software for PocketPC, Win and Linux that
enables paperless caching.
It supports the sites geocaching.com and opencaching.de

Copyright (C) 2006  CacheWolf development team
See http://www.cachewolf.de/ for more information.
This program is free software; you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation; version 2 of the License.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package CacheWolf;

import ewe.ui.ControlEvent;
import ewe.ui.Event;
import ewe.ui.Form;
import ewe.ui.FormBase;
import ewe.ui.TextMessage;
import ewe.ui.mCheckBox;
import ewe.ui.mInput;
import ewe.ui.mLabel;

public class InfoBox extends Form {

    public final static int CHECKBOX = 1;
    public final static int INPUT = 2;
    public final static int DISPLAY_ONLY = 3;
    public final static int PROGRESS_WITH_WARNINGS = 4;

    private TextMessage msgArea;
    private TextMessage warnings;
    private mCheckBox checkBox;
    private boolean checkBoxState = false;
    private mInput feedback = new mInput();
    private ExecutePanel executePanel;

    private int type = 0;

    /**
     * This variable is set to true (by canExit()), if the user closed the Info window by clicking the "close" button.
     * It can be used to check if a lengthy task needs to be aborted (i.e. spidering)
     */
    private boolean isClosed = false;

    public InfoBox(String title, String info) {
	this(title, info, DISPLAY_ONLY);
    }

    public InfoBox(String title, String info, int type) {
	this(title, info, type, true);
    }

    public InfoBox(String title, String info, int type, boolean autoWrap) {
	// Resize InfoBox
	int psx = Global.pref.fontSize * 14;
	int psy = Global.pref.fontSize * 6;
	if (Global.pref.useBigIcons) {
	    psx = Math.min(psx + 48, MyLocale.getScreenWidth());
	    psy = Math.min(psy + 16, MyLocale.getScreenHeight());
	} else {
	    psx = Math.min(psx, MyLocale.getScreenWidth());
	    psy = Math.min(psy, MyLocale.getScreenHeight());
	}
	this.setPreferredSize(psx, psy);

	switch (type) {
	case CHECKBOX:
	    checkBox = new mCheckBox(info);
	    this.addLast(checkBox, STRETCH, FILL);
	    executePanel = new ExecutePanel(this);
	    break;
	case INPUT:
	    mLabel mL = new mLabel(info);
	    this.addNext(mL, STRETCH, FILL);
	    this.addLast(feedback, STRETCH, FILL);
	    executePanel = new ExecutePanel(this);
	    break;
	case DISPLAY_ONLY:
	    msgArea = new TextMessage(info);
	    msgArea.autoWrap = autoWrap;
	    msgArea.alignment = CENTER;
	    msgArea.anchor = CENTER;
	    this.addLast(msgArea.getScrollablePanel(), STRETCH, FILL);
	    this.setPreferredSize(psx + 100, psy);
	    break;
	case PROGRESS_WITH_WARNINGS:
	    msgArea = new TextMessage(info);
	    msgArea.autoWrap = autoWrap;
	    msgArea.alignment = CENTER;
	    msgArea.anchor = CENTER;
	    msgArea.setPreferredSize(psx - 20, psy);
	    this.addLast(msgArea.getScrollablePanel(), HEXPAND | HGROW, HEXPAND | HGROW);
	    warnings = new TextMessage("");
	    warnings.autoWrap = autoWrap;
	    this.addLast(warnings.getScrollablePanel(), HEXPAND | VEXPAND | VGROW, HEXPAND | VEXPAND | VGROW);
	    executePanel = new ExecutePanel(this);
	    executePanel.hide(ExecutePanel.APPLY);
	    executePanel.hide(ExecutePanel.CANCEL);
	    break;
	}
	this.title = title;
	this.type = type;
	relayout(false);
    }

    public final int wait(int doButtons)
    //===================================================================
    {
	if (type == DISPLAY_ONLY) {
	    if (executePanel == null)
		executePanel = new ExecutePanel(this, doButtons);
	}
	exec();
	return waitUntilClosed();
    }

    public void setInfoHeight(int heighti) {
	msgArea.setPreferredSize(getPreferredSize(null).width, heighti);
    }

    public void setInfoWidth(int widthi) {
	msgArea.setPreferredSize(widthi, getPreferredSize(null).height);
    }

    public String getInfo() {
	return msgArea.getText();
    }

    public void setInfo(String info) {
	msgArea.setText(info);
	this.repaintNow();
    }

    public void addInfo(String t) {
	msgArea.setText(msgArea.text + t);
	this.repaintNow();
    }

    public String getInput() {
	return feedback.getText();
    }

    public void addWarning(String w) {
	warnings.setText(warnings.text + w);
    }

    public boolean isCheckBoxState() {
	return checkBoxState;
    }

    public void setCheckBoxState(boolean checkBoxState) {
	this.checkBoxState = checkBoxState;
    }

    public mInput getFeedback() {
	return feedback;
    }

    public void setFeedback(mInput feedback) {
	this.feedback = feedback;
    }

    public void addOkButton() {
	executePanel.show(ExecutePanel.APPLY);
    }

    public boolean isClosed() {
	return isClosed;
    }

    // Overrides
    protected boolean canExit(int exitCode) {
	isClosed = true;
	return true;
    }

    public void onEvent(Event ev) {
	if (ev instanceof ControlEvent && ev.type == ControlEvent.PRESSED) {
	    if (ev.target == executePanel.applyButton) {
		if (type == CHECKBOX)
		    checkBoxState = checkBox.getState();
		this.close(FormBase.IDOK);
	    } else if (ev.target == executePanel.cancelButton) {
		this.close(FormBase.IDCANCEL);
	    } else if (ev.target == executePanel.refuseButton) {
		this.close(FormBase.IDNO);
	    }
	}
	super.onEvent(ev);
    }

}