/*
 * OpenXES
 * 
 * The reference implementation of the XES meta-model for event 
 * log data management.
 * 
 * Copyright (c) 2016 Christian W. Guenther (christian@deckfour.org)
 * 
 * 
 * LICENSE:
 * 
 * This code is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA
 * 
 * EXEMPTION:
 * 
 * The use of this software can also be conditionally licensed for
 * other programs, which do not satisfy the specified conditions. This
 * requires an exemption from the general license, which may be
 * granted on a per-case basis.
 * 
 * If you want to license the use of this software with a program
 * incompatible with the LGPL, please contact the author for an
 * exemption at the following email address: 
 * christian@deckfour.org
 * 
 */
package org.deckfour.xes.extension.std;

import java.net.URI;

import org.deckfour.xes.extension.XExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryRegistry;
import org.deckfour.xes.id.XID;
import org.deckfour.xes.info.XGlobalAttributeNameMap;
import org.deckfour.xes.model.XAttributable;
import org.deckfour.xes.model.XAttribute;
import org.deckfour.xes.model.XAttributeDiscrete;
import org.deckfour.xes.model.XAttributeID;
import org.deckfour.xes.model.XEvent;

/**
 * @author Eric Verbeek (h.m.w.verbeek@tue.nl)
 *
 */
public class XMicroExtension extends XExtension {

	/**
	 * 
	 */
	private static final long serialVersionUID = -173374654036723348L;

	/**
	 * Unique URI of this extension.
	 */
	public static final URI EXTENSION_URI = URI.create("http://www.xes-standard.org/micro.xesext");
	/**
	 * Key for the level attribute.
	 */
	public static final String KEY_LEVEL = "micro:level";
	/**
	 * Key for the parentID attribute.
	 */
	public static final String KEY_PID = "micro:parentId";
	/**
	 * Key for the length attribute.
	 */
	public static final String KEY_LENGTH = "micro:length";

	/**
	 * Level attribute prototype
	 */
	public static XAttributeDiscrete ATTR_LEVEL;
	/**
	 * ParentID attribute prototype
	 */
	public static XAttributeID ATTR_PID;
	/**
	 * Length attribute prototype
	 */
	public static XAttributeDiscrete ATTR_LENGTH;

	/**
	 * Singleton instance of this extension.
	 */
	private transient static XMicroExtension singleton = new XMicroExtension();

	/**
	 * Provides access to the singleton instance.
	 * 
	 * @return Singleton extension.
	 */
	public static XMicroExtension instance() {
		return singleton;
	}

	private Object readResolve() {
		return singleton;
	}

	/**
	 * Private constructor
	 */
	private XMicroExtension() {
		super("Micro", "micro", EXTENSION_URI);
		XFactory factory = XFactoryRegistry.instance().currentDefault();
		ATTR_LEVEL = factory.createAttributeDiscrete(KEY_LEVEL, -1, this);
		ATTR_PID = factory.createAttributeID(KEY_PID, new XID(), this);
		ATTR_LENGTH = factory.createAttributeDiscrete(KEY_LENGTH, -1, this);
		this.eventAttributes.add((XAttribute) ATTR_LEVEL.clone());
		this.eventAttributes.add((XAttribute) ATTR_PID.clone());
		this.eventAttributes.add((XAttribute) ATTR_LENGTH.clone());
		// register aliases
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_LEVEL,
				"Micro level of this event");
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_PID,
				"Id of parent event of this event");
		XGlobalAttributeNameMap.instance().registerMapping(XGlobalAttributeNameMap.MAPPING_ENGLISH, KEY_LENGTH,
				"Number of child events for this event");
	}

	/**
	 * Retrieves the level of an event, if set by this extension's level
	 * attribute.
	 * 
	 * @param event
	 *            Event to extract level from.
	 * @return The requested event level, -1 if not set.
	 */
	public long extractLevel(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_LEVEL);
		if (attribute == null) {
			return -1;
		} else {
			return ((XAttributeDiscrete) attribute).getValue();
		}
	}

	/**
	 * Assigns any event its level, as defined by this extension's level
	 * attribute.
	 * 
	 * @param event
	 *            Event to assign level to.
	 * @param level
	 *            The level to be assigned. Should be a positive integer.
	 */
	public void assignLevel(XAttributable event, long level) {
		if (level > 0) {
			XAttributeDiscrete attr = (XAttributeDiscrete) ATTR_LEVEL.clone();
			attr.setValue(level);
			event.getAttributes().put(KEY_LEVEL, attr);
		}
	}

	/**
	 * Removes the level from an event.
	 * 
	 * @param event
	 *            The event to remove th elevel from.
	 */
	public void removeLevel(XAttributable event) {
		event.getAttributes().remove(KEY_LEVEL);
	}

	/**
	 * Retrieves the parent Id of an event, if set by this extension's parentId
	 * attribute.
	 * 
	 * @param event
	 *            Event to extract parent Id from.
	 * @return The requested event parent Id, null if not set.
	 */
	public XID extractParentID(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_PID);
		if (attribute == null) {
			return null;
		} else {
			return ((XAttributeID) attribute).getValue();
		}
	}

	/**
	 * Assigns any event its parent Id, as defined by this extension's parentId
	 * attribute.
	 * 
	 * @param event
	 *            Event to assign parent Id to.
	 * @param parentId
	 *            The parent Id to be assigned. May not be null.
	 */
	public void assignParentID(XAttributable event, XID parentId) {
		if (parentId != null) {
			XAttributeID attr = (XAttributeID) ATTR_PID.clone();
			attr.setValue(parentId);
			event.getAttributes().put(KEY_PID, attr);
		} else {
			event.getAttributes().remove(KEY_PID);
		}
	}

	/**
	 * Removes the parent Id from an event.
	 * 
	 * @param event
	 *            The event to remove the parent Id from.
	 */
	public void removeParentID(XAttributable event) {
		event.getAttributes().remove(KEY_PID);
	}

	/**
	 * Retrieves the stated number of children of an event, if set by this
	 * extension's length attribute.
	 * 
	 * Note that this simply returns the value of the "micro:legnth" attribute,
	 * and -1 if not present. This does not count the children, it simply
	 * returns the number as found in the event.
	 * 
	 * @param event
	 *            Event to extract stated number of children from.
	 * @return The requested number for this event, -1 if not set.
	 */
	public long extractLength(XEvent event) {
		XAttribute attribute = event.getAttributes().get(KEY_LENGTH);
		if (attribute == null) {
			return -1;
		} else {
			return ((XAttributeDiscrete) attribute).getValue();
		}
	}

	/**
	 * Assigns any event its state number of children, as defined by this
	 * extension's length attribute.
	 * 
	 * @param event
	 *            Event to assign number of children to.
	 * @param length
	 *            The number to be assigned. Should be a non-negative integer.
	 */
	public void assignLength(XAttributable event, long length) {
		if (length >= 0) {
			XAttributeDiscrete attr = (XAttributeDiscrete) ATTR_LENGTH.clone();
			attr.setValue(length);
			event.getAttributes().put(KEY_LENGTH, attr);
		}
	}

	/**
	 * Removes the stated number of children from an event.
	 * 
	 * @param event
	 *            The event to remove the number from.
	 */
	public void removeLength(XAttributable event) {
		event.getAttributes().remove(KEY_LENGTH);
	}

}
