package com.openmethods.openvxml.platforms.genesys.services;

import org.eclipse.vtp.framework.core.IExecutionContext;

public class GenesysVoicePlatform8CTI extends GenesysVoicePlatform8
{
	public GenesysVoicePlatform8CTI(IExecutionContext context)
	{
		super(context);
		setIsCtiC("true");
		context.info("Use CtiC = true (GVP8CTI)");
	}
}
