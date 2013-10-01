/*--------------------------------------------------------------------------
 * Copyright (c) 2004, 2006-2007 OpenMethods, LLC
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Trip Gilman (OpenMethods), Lonnie G. Pryor (OpenMethods)
 *    - initial API and implementation
 -------------------------------------------------------------------------*/
package com.openmethods.openvxml.desktop.model.branding.internal;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.openmethods.openvxml.desktop.model.branding.BrandManager;
import com.openmethods.openvxml.desktop.model.branding.BrandManagerListener;
import com.openmethods.openvxml.desktop.model.branding.IBrand;

public class DefaultBrandManager implements BrandManager
{
	private Brand defaultBrand;
	private List<BrandManagerListener> listeners = new LinkedList<BrandManagerListener>();
	private Map<String, IBrand> brandNameMap = new HashMap<String, IBrand>();
	private Map<String, IBrand> brandIdMap = new HashMap<String, IBrand>();

	public DefaultBrandManager()
	{
		super();
	}

	public void fireBrandAdded(IBrand brand)
	{
		brandNameMap.put(brand.getName(), brand);
		brandIdMap.put(brand.getId(), brand);
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandAdded(brand);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void fireBrandIdChanged(IBrand brand, String oldId)
	{
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandNameChanged(brand, oldId);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void fireBrandNameChanged(IBrand brand, String oldName)
	{
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandNameChanged(brand, oldName);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void fireBrandParentChanged(IBrand brand, IBrand oldParent)
	{
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandParentChanged(brand, oldParent);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public void fireBrandRemoved(IBrand brand)
	{
		if(brandNameMap.get(brand.getName()).getId().equals(brand.getId()))
			brandNameMap.remove(brand.getName());
		brandIdMap.remove(brand.getId());
		for(BrandManagerListener listener : listeners)
		{
			try
			{
				listener.brandRemoved(brand);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	public IBrand getDefaultBrand()
	{
		return defaultBrand;
	}
	
	public void setDefaultBrand(IBrand defaultBrand)
	{
		if(this.defaultBrand != null)
			brandNameMap.remove(this.defaultBrand.getName());
		this.defaultBrand = (Brand)defaultBrand;
		this.defaultBrand.setManager(this);
		brandNameMap.put(defaultBrand.getName(), defaultBrand);
		brandIdMap.put(defaultBrand.getId(), defaultBrand);
	}
	
	public void addListener(BrandManagerListener listener)
	{
		listeners.remove(listener);
		listeners.add(listener);
	}

	public void removeListener(BrandManagerListener listener)
	{
		listeners.remove(listener);
	}

	public boolean checkBrandName(IBrand parent, String name)
	{
		for(IBrand child : parent.getChildBrands())
		{
			if(child.getName().equals(name))
				return false;
		}
		return true;
	}

	public Brand getBrandByName(String name)
	{
		return (Brand)brandNameMap.get(name);
	}

	public IBrand getBrandById(String id)
	{
		return brandIdMap.get(id);
	}
	
	public IBrand getBrandByPath(String path)
	{
		String[] parts = path.split("/");
		int s = 0;
		if(path.startsWith("/"))
			s = 1;
		IBrand brand = defaultBrand;
		if(!brand.getName().equals(parts[s]))
			return null;
		++s;
		for(int i = s; i < parts.length; i++)
		{
			boolean found = false;
			for(IBrand child : brand.getChildBrands())
			{
				if(child.getName().equals(parts[i]))
				{
					brand = child;
					found = true;
					break;
				}
			}
			if(!found)
				return null;
		}
		return brand;
	}
	
}
