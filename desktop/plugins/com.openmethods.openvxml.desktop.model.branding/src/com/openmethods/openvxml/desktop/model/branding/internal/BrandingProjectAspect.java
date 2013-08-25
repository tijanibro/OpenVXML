package com.openmethods.openvxml.desktop.model.branding.internal;

import java.util.List;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.vtp.desktop.model.core.IOpenVXMLProject;
import org.eclipse.vtp.desktop.model.core.spi.OpenVXMLProjectAspect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.openmethods.openvxml.desktop.model.branding.BrandManager;
import com.openmethods.openvxml.desktop.model.branding.IBrand;
import com.openmethods.openvxml.desktop.model.branding.IBrandingProjectAspect;

public class BrandingProjectAspect extends OpenVXMLProjectAspect implements
		IBrandingProjectAspect
{
	private DefaultBrandManager brandManager = null;

	public BrandingProjectAspect(IOpenVXMLProject project, Element aspectConfiguration)
	{
		super(project);
		brandManager = new DefaultBrandManager();
		if(aspectConfiguration != null)
		{
			NodeList nl = aspectConfiguration.getChildNodes();
			for(int i = 0; i < nl.getLength(); i++)
			{
				if(nl.item(i).getNodeType() != Node.ELEMENT_NODE)
					continue;
				Element brandElement = (Element)nl.item(i);
				String brandId = brandElement.getAttribute("id");
				Brand defaultBrand = new Brand(brandId, brandElement.getAttribute("name"));
				brandManager.setDefaultBrand(defaultBrand);
				NodeList subBrandList = brandElement.getChildNodes();
				addBrands(defaultBrand, subBrandList);
			}
		}
	}

	private void addBrands(Brand parent, NodeList brandList)
	{
		for(int i = 0; i < brandList.getLength(); i++)
		{
			if(brandList.item(i).getNodeType() != Node.ELEMENT_NODE)
				continue;
			Element brandElement = (Element)brandList.item(i);
			String brandId = brandElement.getAttribute("id");
			Brand brand = new Brand(brandId, brandElement.getAttribute("name"));
			brand.setParent(parent);
			addBrands(brand, brandElement.getChildNodes());
		}
	}

	@Override
	public String getAspectId()
	{
		return ASPECT_ID;
	}

	@Override
	public BrandManager getBrandManager()
	{
		return brandManager;
	}

	@Override
	public void writeConfiguration(Element aspectElement)
	{
		Document document = aspectElement.getOwnerDocument();
		Element brandsElement = document.createElement("brands");
		aspectElement.appendChild(brandsElement);
		IBrand defaultBrand = brandManager.getDefaultBrand();
		writeBrand(brandsElement, defaultBrand);
	}
	
	/**
	 * @param parentElement
	 * @param brand
	 */
	private void writeBrand(Element parentElement, IBrand brand)
	{
		Element brandElement = parentElement.getOwnerDocument().createElement("brand");
		parentElement.appendChild(brandElement);
		brandElement.setAttribute("id", brand.getId());
		brandElement.setAttribute("name", brand.getName());
		List<IBrand> children = brand.getChildBrands();
		for(IBrand child : children)
		{
			writeBrand(brandElement, child);
		}
	}
	
	@Override
	public void removeProjectLayout()
	{
	}

	@Override
	public boolean removeProjectConfiguration(IProjectDescription description)
	{
		return false;
	}

}
